package netty;

import cloud.CloudMessage;
import lombok.Data;

@Data
public class PathUpRequest implements CloudMessage {
    private final String name;
    private final String login;
}
