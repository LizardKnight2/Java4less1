package cloud;

import lombok.Data;

@Data
public class ListFilesRequest implements CloudMessage{
    private final String login;
}
