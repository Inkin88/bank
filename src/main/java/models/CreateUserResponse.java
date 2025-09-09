package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserResponse extends BaseModel {

    private long id;
    private String password;
    private String username;
    private String name;
    private String role;
    private List<AccountResponse> accounts;
}
