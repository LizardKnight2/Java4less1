package cloud;

import lombok.Data;

@Data
public class RequestPath implements CloudMessage{
    private final String login;
}
