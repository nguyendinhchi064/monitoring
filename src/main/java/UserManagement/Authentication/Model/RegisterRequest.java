package UserManagement.Authentication.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class RegisterRequest {
    @JsonProperty
    public String username;
    @JsonProperty
    public String password;
    @JsonProperty
    public String confirmPassword;
    @JsonProperty
    public String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterRequest that = (RegisterRequest) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(confirmPassword, that.confirmPassword) &&
                Objects.equals(email, that.email);
    }
    @Override
    public int hashCode() {
        return Objects.hash(username, password, confirmPassword, email);
    }
}
