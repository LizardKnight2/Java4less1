import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ChatHandler implements Runnable {
    private final String serverDir = "server_files";

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public ChatHandler(Socket socket) throws IOException {
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client accepted");
        sendListOfFiles(serverDir);
    }
    private void sendListOfFiles(String dir) throws IOException {
        outputStream.writeUTF("#List#");
        List<String> files = getFiles(serverDir);
        outputStream.writeInt(files.size());
        for (String file : files) {
            outputStream.writeUTF(file);
        }
        outputStream.flush();
    }

    private List<String> getFiles(String dir){
        String[] list = new File(dir).list();
        assert list != null;
        return Arrays.asList(list);
    }

    @Override
    public void run() {
        byte[] buf = new byte[256];
        try {
            while (true) {
                String command = inputStream.readUTF();
                System.out.println("received: " + command);
                if (command.equals("#file#")) {
                    String fileName = inputStream.readUTF();
                    long len = inputStream.readLong();
                    File file = Path.of(serverDir).resolve(fileName).toFile();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        for (int i = 0; i < (len + 255) / 256; i++) {
                            int read = inputStream.read(buf);
                            fos.write(buf, 0, read);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sendListOfFiles(serverDir);
                }
            }
        } catch (Exception e) {
            System.err.println("Connection was broken");
        }
    }
}
