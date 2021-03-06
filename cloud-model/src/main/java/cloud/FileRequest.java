package cloud;

import lombok.Data;

@Data
public class FileRequest implements CloudMessage {
    private final boolean isClicked;
    private final String name;
    private final String login;
}
