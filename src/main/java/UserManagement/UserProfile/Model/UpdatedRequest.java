package UserManagement.UserProfile.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdatedRequest {

    @JsonProperty
    public String email;

    @JsonProperty
    public String phone;

    @JsonProperty
    public String address;

    @JsonProperty
    public String FullName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
