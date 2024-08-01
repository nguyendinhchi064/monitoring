package UserManagement.UserProfile.Resources;

import UserManagement.Authentication.Model.User;
import UserManagement.UserProfile.Model.UpdatedRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class UpdatedService {

    @Inject
    EntityManager em;

    @Inject
    JsonWebToken JWT;

    public Response UpdateUserProfile(UpdatedRequest updatedRequest){

        String username = JWT.getName();
        User existingUser = User.findByUsername(username);

        if (existingUser == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"message\":\"User not found\"}").build();
        }

        existingUser.setFullName(updatedRequest.FullName);
        existingUser.setPhone(updatedRequest.phone);
        existingUser.setAddress(updatedRequest.address);
        existingUser.setEmail(updatedRequest.email);
        em.merge(existingUser);

        return Response.ok("{\"message\":\"User information updated successfully\"}").build();

    }
}
