package cloud;

import lombok.Data;

@Data
public class AuthResponse implements CloudMessage{
    private final boolean isAuth;
    private final String login;
}
