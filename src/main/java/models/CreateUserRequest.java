package models;

import generators.GeneratingRule;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserRequest extends BaseModel {
    @GeneratingRule(regExp = "^[A-Za-z0-9]{3,15}$")
    private String username;
    @GeneratingRule(regExp = "^[A-Z]{3}[a-z]{4}[0-9]{3}[$%&]{2}$")
    private String password;
    @GeneratingRule(regExp = "USER$")
    private String role;
}
