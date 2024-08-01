package UserManagement.Authentication.Resources;


import UserManagement.Authentication.Model.InformationForm;
import UserManagement.Authentication.Model.LoginRequest;
import UserManagement.Authentication.Model.RegisterRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;


@Path("/Auth")
@Tag(name = "LoginRegister", description = "LoginRegister REST APIs")
public class AuthResource {

    @Inject
    AuthService authService;

    @Inject
    RegisterService registerService;

    @Inject
    UserService userService;

    @POST
    @Transactional
    @Path("/Register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(RegisterRequest registerRequest) {
        return registerService.registerUser(registerRequest);
    }

    @POST
    @Path("/Login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest loginRequest) {
        return authService.loginAndGenerateToken(loginRequest);
    }

    @POST
    @Path("/updateInfo")
    @RolesAllowed("User")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateInfo(InformationForm informationForm) {
        try {
            userService.updateUserDetails(informationForm);
            return Response.ok("{\"message\":\"User information updated successfully\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"" + e.getMessage() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\":\"An unexpected error occurred\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

}
