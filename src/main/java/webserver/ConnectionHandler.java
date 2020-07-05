package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Optional;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(ConnectionHandler.class);

    private static final String DIRECTORY = "./webapp";

    private Socket connection;

    public ConnectionHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            String requestUrl = RequestHandler.handleRequest(new BufferedReader(new InputStreamReader(in)));

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = Files.readAllBytes(new File(DIRECTORY + requestUrl).toPath());
            response200Header(dos, body.length, requestUrl);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }



    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String requestUrl) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            String[] split = requestUrl.split("/");
            boolean isStaticRequest = false;
            if (split.length >= 2) {
                isStaticRequest = split[1].equals("css");
            }
            String contentType = isStaticRequest ? "text/css" : "text/html";
            dos.writeBytes(String.format("Content-Type: %s;charset=utf-8\r\n", contentType));
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
