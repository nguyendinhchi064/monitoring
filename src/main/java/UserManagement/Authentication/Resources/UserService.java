package UserManagement.Authentication.Resources;

import UserManagement.Authentication.Model.InformationForm;
import UserManagement.Authentication.Model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class UserService {

    @Inject
    EntityManager em;

    @Inject
    JsonWebToken jwt;

    @Transactional
    public Response updateUserDetails(InformationForm infoForm) {
        String username = jwt.getName();
        User existingUser = User.findByUsername(username);

        if (existingUser != null) {
            existingUser.setFullName(infoForm.getFullName());
            existingUser.setPhone(infoForm.getPhone());
            existingUser.setAddress(infoForm.getAddress());
            existingUser.setInfoUpdated(true);
            em.merge(existingUser);
        } else {
            throw new IllegalArgumentException("User not found");
        }

        return Response.status(Response.Status.CREATED)
                .entity("{\"message\":\"User information updated successfully\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public boolean isUserInfoUpdated(String username) {
        User user = User.find("username", username).firstResult();
        return user != null && user.getInfoUpdated();
    }
}
