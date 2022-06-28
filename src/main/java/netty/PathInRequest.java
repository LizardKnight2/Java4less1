package netty;

import cloud.CloudMessage;
import lombok.Data;

@Data
public class PathInRequest implements CloudMessage {
    private final String name;
    private final String login;
}
