package netty;

import cloud.CloudMessage;
import lombok.Data;

@Data
public class FileDeleteRequest implements CloudMessage {
    private final String name;
}
