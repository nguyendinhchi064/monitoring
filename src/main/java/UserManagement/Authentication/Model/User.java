package UserManagement.Authentication.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "user_db")
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long userid;

    @Column(nullable = false, unique = true)
    public String username;

    @Column
    public String phone;

    @Column
    public String FullName;

    @Column
    public String address;

    @JsonIgnore
    public Boolean isAdmin = false;

    @Column(nullable = false, unique = true)
    public String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String password;

    @Column(columnDefinition = "varchar(5000)")
    @JsonIgnore
    public String accessToken;

    @Column(nullable = false)
    @JsonIgnore
    public Boolean isInfoUpdated = false;

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = BcryptUtil.bcryptHash(password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static User findByUsername(String username) {
        return User.find("username", username).firstResult();
    }

    public long getUserid() {
        return userid;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setFullName(String FullName) {
        this.FullName = FullName;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(email, that.email);
    }

    public Boolean getInfoUpdated() {
        return isInfoUpdated;
    }

    @JsonIgnore
    public void setInfoUpdated(Boolean infoUpdated) {
        isInfoUpdated = infoUpdated;
    }
}
