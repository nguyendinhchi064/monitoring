package UserManagement.Resources;

import UserManagement.Model.RegisterRequest;
import UserManagement.Model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class RegisterService {

    @Inject
    AuthService authService;

    public Response registerUser(RegisterRequest registerRequest) {
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\":\"Passwords do not match\"}").build();
        }
        if (!authService.isValidEmail(registerRequest.getEmail())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\":\"Invalid email format\"}").build();
        }
        if (!authService.isValidPassword(registerRequest.getPassword())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\":\"Invalid Password\"}").build();
        }
        if (authService.userExists(registerRequest.getUsername())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\":\"User already exists\"}").build();
        }
        if (authService.emailExists(registerRequest.getEmail())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\":\"Email already Exists\"}").build();
        }
        User newUser = new User();
        newUser.setUserName(registerRequest.getUsername());
        newUser.setPassword(registerRequest.getPassword());
        newUser.setEmail(registerRequest.getEmail());
        newUser.persist();

        return Response.status(Response.Status.CREATED).entity("{\"message\":\"User registered successfully\"}").build();
    }
}
