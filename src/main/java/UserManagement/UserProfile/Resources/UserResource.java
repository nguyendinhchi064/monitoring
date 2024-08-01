package UserManagement.UserProfile.Resources;

import UserManagement.Authentication.Model.User;
import UserManagement.UserProfile.Model.UpdatedRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/users")
@Tag(name = "User", description = "User Management REST APIs")
public class UserResource {

    @Inject
    EntityManager em;

    @Inject
    JsonWebToken jwt;

    @Inject
    UpdatedService updatedService;


//    @PUT
//    @Path("/updateUsername")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @RolesAllowed({"User", "Admin"})
//    @Transactional
//    public Response updateUsername(@QueryParam("newUsername") String newUsername) {
//        String username = jwt.getName();
//        User user = User.findByUsername(username);
//
//        if (user == null) {
//            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
//        }
//
//        user.setUserName(newUsername);
//        em.merge(user);
//        return Response.ok(user).build();
//    }
//
//    @PUT
//    @Path("/updatePhone")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @RolesAllowed({"User", "Admin"})
//    @Transactional
//    public Response updatePhone(@QueryParam("newPhone") String newPhone) {
//        String username = jwt.getName();
//        User user = User.findByUsername(username);
//
//        if (user == null) {
//            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
//        }
//
//        user.setPhone(newPhone);
//        em.merge(user);
//        return Response.ok(user).build();
//    }
//
//    @PUT
//    @Path("/updateFullName")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @RolesAllowed({"User", "Admin"})
//    @Transactional
//    public Response updateFullName(@QueryParam("newFullName") String newFullName) {
//        String username = jwt.getName();
//        User user = User.findByUsername(username);
//
//        if (user == null) {
//            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
//        }
//
//        user.setFullName(newFullName);
//        em.merge(user);
//        return Response.ok(user).build();
//    }
//
//    @PUT
//    @Path("/updateAddress")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @RolesAllowed({"User", "Admin"})
//    @Transactional
//    public Response updateAddress(@QueryParam("newAddress") String newAddress) {
//        String username = jwt.getName();
//        User user = User.findByUsername(username);
//
//        if (user == null) {
//            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
//        }
//
//        user.setAddress(newAddress);
//        em.merge(user);
//        return Response.ok(user).build();
//    }
//
//    @PUT
//    @Path("/updateEmail")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @RolesAllowed({"User", "Admin"})
//    @Transactional
//    public Response updateEmail(@QueryParam("newEmail") String newEmail) {
//        String username = jwt.getName();
//        User user = User.findByUsername(username);
//
//        if (user == null) {
//            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
//        }
//
//        user.setEmail(newEmail);
//        em.merge(user);
//        return Response.ok(user).build();
//    }

    @PUT
    @Path("/updatePassword")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    @Transactional
    public Response updatePassword(@QueryParam("newPassword") String newPassword) {
        String username = jwt.getName();
        User user = User.findByUsername(username);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }

        user.setPassword(newPassword);
        em.merge(user);
        return Response.ok(user).build();
    }
    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    public Response getUserDetails() {
        String username = jwt.getName();
        User user = User.findByUsername(username);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"message\":\"User not found\"}").build();
        }

        return Response.ok(user).build();
    }

    @PUT
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("User")
    @Transactional
    public Response updateUserInfo(UpdatedRequest updatedRequest) {
        return updatedService.UpdateUserProfile(updatedRequest);

    }
}
