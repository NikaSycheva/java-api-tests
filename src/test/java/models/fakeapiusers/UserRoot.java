package models.fakeapiusers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoot{

    @JsonProperty("password")
    private String password;

    @JsonProperty("address")
    private Address address;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("name")
    private Name name;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("__v")
    private String v;

    @JsonProperty("email")
    private String email;

    @JsonProperty("username")
    private String username;
}