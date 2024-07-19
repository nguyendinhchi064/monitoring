package UserManagement.Resources;


import UserManagement.Model.LoginRequest;
import UserManagement.Model.RegisterRequest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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
}
