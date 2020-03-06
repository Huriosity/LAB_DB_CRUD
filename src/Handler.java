import java.net.Socket;

public class Handler extends Thread {

    private Socket socket;

    private String directory;

    Handler(Socket socket, String directory) {
        this.socket = socket;
        this.directory = directory;
    }
}
