package UserManagement.Authentication.Resources;

import UserManagement.Authentication.Model.LoginRequest;
import UserManagement.Authentication.Model.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.Claims;
import org.jboss.logging.Logger;
import org.wildfly.security.password.Password;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.WildFlyElytronPasswordProvider;
import org.wildfly.security.password.interfaces.BCryptPassword;
import org.wildfly.security.password.util.ModularCrypt;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

@ApplicationScoped
public class AuthService {
    @Inject
    EntityManager em;

    private static final Logger LOG = Logger.getLogger(AuthService.class);

    public boolean verifyPassword(String bCryptPasswordHash, String passwordToVerify) {
        try {
            WildFlyElytronPasswordProvider provider = new WildFlyElytronPasswordProvider();
            PasswordFactory passwordFactory = PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT, provider);
            Password userPasswordDecoded = ModularCrypt.decode(bCryptPasswordHash);
            Password userPasswordRestored = passwordFactory.translate(userPasswordDecoded);
            return passwordFactory.verify(userPasswordRestored, passwordToVerify.toCharArray());
        } catch (Exception e) {
            LOG.errorf("Password verification failed: %s", e.getMessage());
            return false;
        }
    }

    public boolean isValidLogin(LoginRequest loginRequest) {
        List<User> userList = User.list("username", loginRequest.getUsername());
        if (!userList.isEmpty()) {
            User user = userList.get(0);
            return verifyPassword(user.getPassword(), loginRequest.getPassword());
        } else {
            LOG.warnf("No user found with username: %s", loginRequest.getUsername());
            return false;
        }
    }

    public User getUser(LoginRequest loginRequest) {
        List<User> userList = User.list("username", loginRequest.getUsername());
        return userList.isEmpty() ? null : userList.get(0);
    }

    public boolean isValidEmail(String email) {
        if (email == null) return false;
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(email).matches();
    }
    public boolean isValidPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*[!@#$%^&*()-+]).{5,}$";
        return Pattern.matches(regex, password);
    }
    public boolean userExists(String username) {
        return (User.count("username", username) > 0);
    }

    public boolean emailExists(String email) {
        return (User.count("email", email) > 0);
    }

    @Transactional
    public Response loginAndGenerateToken(LoginRequest loginRequest) {
        List<User> userList = User.list("username", loginRequest.getUsername());
        if (!isValidLogin(loginRequest)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid username or password").build();
        }

        User user = getUser(loginRequest);

        List<String> groups;
        if (user.isAdmin != null && user.isAdmin) {
            groups = Arrays.asList("User", "Admin");
        } else {
            groups = Arrays.asList("User");
        }

        long durationSeconds = 3600;
        long currentTimeInSeconds = System.currentTimeMillis() / 1000;
        long expirationTime = currentTimeInSeconds + durationSeconds;

        String token = Jwt.upn(user.getUserName())
                .groups(new HashSet<>(groups))
                .claim("username", user.getUserName())
                .claim(Claims.sub.name(), String.valueOf(user.userid))
                .expiresAt(expirationTime)
                .sign();

        user.accessToken = token;
        em.merge(user); //This one should be persist to database ?

        return Response.ok("{\"token\":\"" + token + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
