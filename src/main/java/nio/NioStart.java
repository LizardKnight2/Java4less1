package nio;

import java.io.IOException;

public class NioStart {
    public static void main(String[] args) throws IOException {
        NioServer ns = new NioServer();
        ns.start();
    }
}
