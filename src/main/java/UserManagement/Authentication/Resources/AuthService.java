package UserManagement.Authentication.Resources;

import UserManagement.Authentication.Model.LoginRequest;
import UserManagement.Authentication.Model.User;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
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

    @Inject
    UserService userService;

    @Inject
    MongoClient mongoClient;

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
        if (!isValidLogin(loginRequest)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"Invalid username or password\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        User user = getUser(loginRequest);

        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"User not found\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Create a MongoDB database named after the username and remember to have at least 1 collection to make the "username" database appear
        createMongoDatabaseForUser(user.getUserName());

        // JWT token generation and user authentication logic
        List<String> groups;
        if (user.isAdmin != null && user.isAdmin.equals(true)) {
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
        em.merge(user);

        if (!userService.isUserInfoUpdated(user.getUserName())) {
            return Response.status(Response.Status.PRECONDITION_REQUIRED)
                    .entity("{\"message\":\"User information update required\", \"token\":\"" + token + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        return Response.ok("{\"token\":\"" + token + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @Transactional
    public Response logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Invalid token format\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        String token = authHeader.substring("Bearer".length()).trim();

        // Log the token for debugging purposes
        LOG.info("Attempting to log out with token: " + token);

        User user = User.find("accessToken", token).firstResult();

        if (user == null) {
            LOG.warn("User not found or token invalid for token: " + token);
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"Invalid token or user not found\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Remove the token from the user record to invalidate it
        user.accessToken = null;
        em.merge(user);

        LOG.info("User logged out successfully with token: " + token);

        return Response.ok("{\"message\":\"Logged out successfully\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }


    private void createMongoDatabaseForUser(String username) {
        MongoDatabase userDatabase = mongoClient.getDatabase(username);
        System.out.println("Created MongoDB database for user: " + username);
    }
}
