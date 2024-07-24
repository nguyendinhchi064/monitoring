package UserManagement.Authentication.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class InformationForm {
    @JsonProperty
    private String fullName;

    @JsonProperty
    private String phone;

    @JsonProperty
    private String address;

    public String getAddress() {
        return address;
    }
    public String getPhone() {
        return phone;
    }
    public String getFullName() {
        return fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InformationForm that = (InformationForm) o;
        return Objects.equals(fullName, that.fullName) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(address, that.address);
    }
    @Override
    public int hashCode() {
        return Objects.hash(fullName, phone, address);
    }

}
