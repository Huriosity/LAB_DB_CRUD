import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Handler extends Thread {

    private Socket socket;

    private String directory;

    private String method;

    private String requestURL;

    private String requestPayload;

    Handler(Socket socket, String directory) {
        this.socket = socket;
        this.directory = directory;
    }
    @Override
    public void run() {
        try (var input = this.socket.getInputStream(); var output = this.socket.getOutputStream()) {
            parseRequest(input);
            System.out.println("NOW we have method -  " + method);
            System.out.println("NOW we have url -  " + requestURL);
            switch (method){
                case "GET":{
                    break;
                }
                case "POST": {
                    break;
                }
                case "PUT": {
                    break;
                }
                case "DELETE": {
                    break;
                }

            }

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void parseRequest(InputStream input) throws IOException {
        InputStreamReader isReader = new InputStreamReader(input);
        BufferedReader br = new BufferedReader(isReader);
        //code to read and print headers
        String firstLine = br.readLine();
        method = firstLine.split(" ")[0];
        requestURL = firstLine.split(" ")[1];
        System.out.println("first = " + firstLine);
        String headerLine = null;
        while((headerLine = br.readLine()).length() != 0){
            System.out.println(headerLine);
        }

        StringBuilder payload = new StringBuilder();
        while(br.ready()){
            payload.append((char) br.read());
        }
        System.out.println("Payload data is: "+payload.toString());
        requestPayload = payload.toString();
        System.out.println("Request payload is: " + requestPayload);
    }
    
}
