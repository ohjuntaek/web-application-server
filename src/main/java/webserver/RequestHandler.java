package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private static final String DIRECTORY = "./webapp";

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String requestUrl = readRequestLine(br);
            printHeaders(br, requestUrl);

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = Files.readAllBytes(new File(DIRECTORY + requestUrl).toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String readRequestLine(BufferedReader br) throws IOException {
        String line = br.readLine();
        log.info("request line : {}", line);
        if (line == null) {
            throw new RuntimeException("connection exception happen");
        }
        String requestUrl = line.split(" ")[1];
        String index = "/index.html";
        return requestUrl.equals("/") ? index : requestUrl;
    }

    private void printHeaders(BufferedReader br, String line) throws IOException {
        while (!line.equals("")) {
            line = br.readLine();
            log.info("header : {}", line);
            if (line == null) break;
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
