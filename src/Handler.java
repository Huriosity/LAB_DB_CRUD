import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Handler extends Thread {

    private Socket socket;

    private String directory;

    private String method;

    private String requestURL;

    private String Host;

    private String UserAgent;

    private String requestPayload;

    private DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

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
                        var form = Path.of(this.directory, "index.html");
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
                            this.sendHeader(output, 202, HTTP_MESSAGE.ACCEPTED_202, type, fileBytes.length);

                            LogSystem.acces_log(Host, DTF.format(LocalDateTime.now()).toString(),method + " " +
                                    requestURL + "HTTP/1.1", 202,fileBytes.length, requestURL, UserAgent);

                            output.write(fileBytes);
                        }
                    } else {
                        var filePath = Path.of(this.directory, requestURL);
                        if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                            var extension = this.getFileExtension(filePath);
                            var type = CONTENT_TYPES.get(extension);
                            var fileBytes = Files.readAllBytes(filePath);
                            this.sendHeader(output, 200, "OK", type, fileBytes.length);

                            LogSystem.acces_log(Host, DTF.format(LocalDateTime.now()).toString(),method + " " +
                                    requestURL + "HTTP/1.1", 200,fileBytes.length,requestURL,UserAgent);

                            output.write(fileBytes);
                        } else {
                            var type = CONTENT_TYPES.get("text");
                            this.sendHeader(output, 404, "Not Found", type, HTTP_MESSAGE.NOT_FOUND_404.length());

                            LogSystem.acces_log(Host, DTF.format(LocalDateTime.now()).toString(),method + " " +
                                    requestURL + "HTTP/1.1", 404,HTTP_MESSAGE.NOT_FOUND_404.length(),
                                    requestURL, UserAgent);

                            output.write(HTTP_MESSAGE.NOT_FOUND_404.getBytes());
                        }

                    }
                    break;
                }
                case "POST": {
                    ArrayList<ArrayList<String>> keyValuePair = parseRequestPayload();
                    Database.createNewRecordInTheDatabase(keyValuePair);
                    var formTemplate = Path.of(this.directory, "formTemplate.html");
                    var form = Path.of(this.directory, "index.html");
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

                        LogSystem.acces_log(Host, DTF.format(LocalDateTime.now()).toString(),method + " " +
                                requestURL + "HTTP/1.1", 201,fileBytes.length, requestURL, UserAgent);

                        output.write(fileBytes);
                    }
                    break;
                }
                case "PUT": {
                    System.out.println("this is put method");

                    ArrayList<ArrayList<String>> keyValuePair = parseRequestPayload();
                    Database.updateRecordInTheDatabase(keyValuePair);
                    var formTemplate = Path.of(this.directory, "formTemplate.html");
                    var form = Path.of(this.directory, "index.html");
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
                        this.sendHeader(output, 200, HTTP_MESSAGE.OK_200, type, fileBytes.length);

                        LogSystem.acces_log(Host, DTF.format(LocalDateTime.now()).toString(),method + " " +
                                requestURL + "HTTP/1.1", 200,fileBytes.length, requestURL, UserAgent);

                        output.write(fileBytes);
                    }
                    break;
                }
                case "DELETE": {
                    System.out.println("this is delete method");

                    ArrayList<ArrayList<String>> keyValuePair = parseRequestPayload();
                    Database.deleteRecordInTheDatabase(keyValuePair);

                    var formTemplate = Path.of(this.directory, "formTemplate.html");
                    var form = Path.of(this.directory, "index.html");
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
                        this.sendHeader(output, 200, HTTP_MESSAGE.OK_200, type, fileBytes.length);

                        LogSystem.acces_log(Host, DTF.format(LocalDateTime.now()).toString(),method + " " +
                                requestURL + "HTTP/1.1", 200,fileBytes.length, requestURL, UserAgent);

                        output.write(fileBytes);
                    }
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
        Host = br.readLine().split(" ")[1];;
        System.out.println("Host = " + Host);
        System.out.println("firstLine = " + firstLine);
        String headerLine = null;
        while((headerLine = br.readLine()).length() != 0){
            // System.out.println(headerLine);
            if(headerLine.contains("User-Agent")){
                UserAgent = headerLine.split(" ",2)[1];
            }
        }

        StringBuilder payload = new StringBuilder();
        while(br.ready()){
            payload.append((char) br.read());
        }

        requestPayload = payload.toString();
        System.out.println("method = " + method);
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
        ps.printf("Date: %s%n", DTF.format(LocalDateTime.now())); ////////////////
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
            requestPayload = removeLastChar(requestPayload);
        }
    }


    public static String removeLastChar(String s) {
        return (s == null || s.length() == 0)
                ? null
                : (s.substring(0, s.length() - 1));
    }
}
