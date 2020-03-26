import javax.swing.plaf.synth.SynthTextAreaUI;
import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;///////
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Handler extends Thread {

    private Socket socket;

    private String directory;

    private String method;

    private String requestURL;

    private String requestPayload;

    private static final Map<String, String> CONTENT_TYPES = new HashMap<>() {{
        put("jpg", "image/jpeg");
        put("html", "text/html");
        put("json", "application/json");
        put("txt", "text/plain");
        put("", "text/plain");
    }};

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
                    if(isRequestInDatabase(requestURL)){
                        System.out.println("is request in db");
                        var formTemplate = Path.of(this.directory, "formTemplate.html");
                        var form = Path.of(this.directory, "form.html");
                        if (!Files.exists(form)){
                            File _form = new File(form.toString());
                        }
                        Files.copy(formTemplate, form, StandardCopyOption.REPLACE_EXISTING);
                        ////////////////////////////////////
                        Database.getAllInfoFromDatabaseAndWriteInFile(form.toString());
                        ////////////////////////////////////
                        if (Files.exists(form) && !Files.isDirectory(form)) {
                            var extension = this.getFileExtension(form);
                            var type = CONTENT_TYPES.get(extension);
                            var fileBytes = Files.readAllBytes(form);
                            this.sendHeader(output, 202, "Accepted", type, fileBytes.length);
                            output.write(fileBytes);
                        }
                    } else {
                        var filePath = Path.of(this.directory, requestURL);
                        if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                            var extension = this.getFileExtension(filePath);
                            var type = CONTENT_TYPES.get(extension);
                            var fileBytes = Files.readAllBytes(filePath);
                            this.sendHeader(output, 200, "OK", type, fileBytes.length);
                            output.write(fileBytes);
                        } else {
                            var type = CONTENT_TYPES.get("text");
                            this.sendHeader(output, 404, "Not Found", type, HTTP_MESSAGE.NOT_FOUND_404.length());
                            output.write(HTTP_MESSAGE.NOT_FOUND_404.getBytes());
                        }

                    }
                    break;
                }
                case "POST": {
                    ArrayList<ArrayList<String>> keyValuePair = parseRequestPayload();
                    for(int i = 0; i < keyValuePair.get(0).size(); i++){
                        System.out.println("fist = " + keyValuePair.get(0).get(i));
                        System.out.println("second = " + keyValuePair.get(1).get(i));
                    }
                    Database.createNewRecordInTheDatabase(keyValuePair);
                    var formTemplate = Path.of(this.directory, "formTemplate.html");
                    var form = Path.of(this.directory, "form.html");
                    if (!Files.exists(form)){
                        File _form = new File(form.toString());
                    }
                    Files.copy(formTemplate, form, StandardCopyOption.REPLACE_EXISTING);
                    ////////////////////////////////////
                    Database.getAllInfoFromDatabaseAndWriteInFile(form.toString());
                    ////////////////////////////////////
                    if (Files.exists(form) && !Files.isDirectory(form)) {
                        var extension = this.getFileExtension(form);
                        var type = CONTENT_TYPES.get(extension);
                        var fileBytes = Files.readAllBytes(form);
                        this.sendHeader(output, 201, "CREATED", type, fileBytes.length);
                        output.write(fileBytes);
                    }
                    break;
                }
                case "PUT": {
                    System.out.println("this is put method");

                    ArrayList<ArrayList<String>> keyValuePair = parseRequestPayload();
                    for(int i = 0; i < keyValuePair.get(0).size(); i++){
                        System.out.println("fist = " + keyValuePair.get(0).get(i));
                        System.out.println("second = " + keyValuePair.get(1).get(i));
                    }
                    Database.updateRecordInTheDatabase(keyValuePair);
                    var formTemplate = Path.of(this.directory, "formTemplate.html");
                    var form = Path.of(this.directory, "form.html");
                    if (!Files.exists(form)){
                        File _form = new File(form.toString());
                    }
                    Files.copy(formTemplate, form, StandardCopyOption.REPLACE_EXISTING);
                    ////////////////////////////////////
                    Database.getAllInfoFromDatabaseAndWriteInFile(form.toString());
                    ////////////////////////////////////
                    if (Files.exists(form) && !Files.isDirectory(form)) {
                        var extension = this.getFileExtension(form);
                        var type = CONTENT_TYPES.get(extension);
                        var fileBytes = Files.readAllBytes(form);
                        this.sendHeader(output, 201, "CREATED", type, fileBytes.length);///
                        output.write(fileBytes);
                    }
                    /*var type = CONTENT_TYPES.get("text");
                    this.sendHeader(output, 204, HTTP_MESSAGE.NO_CONTENT_204, type, HTTP_MESSAGE.NO_CONTENT_204.length());
                    output.write(HTTP_MESSAGE.NO_CONTENT_204.getBytes());*/
                    break;
                }
                case "DELETE": {
                    System.out.println("this is delete method");
                    var type = CONTENT_TYPES.get("text");
                    this.sendHeader(output, 204, HTTP_MESSAGE.NO_CONTENT_204, type, HTTP_MESSAGE.NO_CONTENT_204.length());
                    output.write(HTTP_MESSAGE.NO_CONTENT_204.getBytes());
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
        System.out.println("firstLine = " + firstLine);
        String headerLine = null;
        while((headerLine = br.readLine()).length() != 0){
            System.out.println(headerLine);
        }

        StringBuilder payload = new StringBuilder();
        while(br.ready()){
            payload.append((char) br.read());
        }
        // System.out.println("Payload data is: "+payload.toString());
        requestPayload = payload.toString();
        System.out.println("check method = " + method);
        if (method.equals("POST")) {
            checkPayloadForMethodValue();
        }
        System.out.println("Request payload is: " + requestPayload);
    }

    private Boolean isRequestInDatabase(String str){
        return str.contains("?");
    }

    private String getFileExtension(Path path) {
        var name = path.getFileName().toString();
        var extensionStart = name.lastIndexOf(".");
        return extensionStart == -1 ? "" : name.substring(extensionStart + 1);
    }

    private void sendHeader(OutputStream output, int statusCode, String statusText, String type, long lenght) {
        var ps = new PrintStream(output);
        ps.printf("HTTP/1.1 %s %s%n", statusCode, statusText);
        ps.printf("Date: %s%n", LocalDate.now()); ////////////////
        ps.printf("Content-Type: %s%n", type);
        ps.printf("Content-Length: %s%n%n", lenght);
    }

    private ArrayList<ArrayList<String>> parseRequestPayload(){
        ArrayList<ArrayList<String>> HalvesOfParamenter = new ArrayList<ArrayList<String>>();
        for(int i = 0;i < 2; i++) {
            HalvesOfParamenter.add(new ArrayList<String>());
        }
        int i = 0;
        for (String retval : requestPayload.split("&")) {
            HalvesOfParamenter.get(0).add(retval.split("=",2)[0]);
            HalvesOfParamenter.get(1).add(retval.split("=",2)[1]);
            i++;
        }
        return HalvesOfParamenter;
    }

    private void checkPayloadForMethodValue(){
        // int startIndex = ;
        String result = "";
        int endIndex = requestPayload.indexOf("_isMethod") + "_isMethod".length();
        for(int i = endIndex + 1; i < requestPayload.length();i++){
            if(requestPayload.charAt(i) == '&'){
                break;
            }
            result += requestPayload.charAt(i);
        }
        if(result.equals("put")){
            method = "PUT";
            eraseMethodPart(result);
        } else if (result.equals("delete")) {
            method = "DELETE";
            eraseMethodPart(result);
        }
    }

    private void eraseMethodPart(String result) {
        requestPayload = requestPayload.replace("_isMethod="+result,"");
        if (requestPayload.charAt(requestPayload.length()-1) == '&'){
            System.out.println("should erase");
            requestPayload = removeLastChar(requestPayload);
        }
    }


    public static String removeLastChar(String s) {
        return (s == null || s.length() == 0)
                ? null
                : (s.substring(0, s.length() - 1));
    }
}
