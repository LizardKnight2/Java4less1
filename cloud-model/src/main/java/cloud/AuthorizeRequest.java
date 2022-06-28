package cloud;

import lombok.Data;

@Data
public class AuthorizeRequest implements CloudMessage{
    private final String login;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final String email;
}
