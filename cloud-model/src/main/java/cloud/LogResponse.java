package cloud;

import lombok.Data;

@Data
public class LogResponse implements CloudMessage{
    private final String login;
}
