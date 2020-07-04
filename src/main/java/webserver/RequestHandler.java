package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Optional;

import model.User;
import org.javatuples.LabelValue;
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
            String requestUrl = processInputRequest(in);

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = Files.readAllBytes(new File(DIRECTORY + requestUrl).toPath());
            response200Header(dos, body.length, requestUrl);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String processInputRequest(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String requestUrl = readRequestLine(br).orElseThrow(IOException::new);

        String[] hostAndParams = splitUrl(requestUrl).orElseGet(() -> new String[] {});

        if (hostAndParams.length == 2) {
            modelMapping(hostAndParams[0], hostAndParams[1]);
        }

        readHeaders(br, requestUrl);

        return requestUrl;
    }

    protected Optional<String[]> splitUrl(String requestUrl) {
        String[] split = requestUrl.split("\\?");
        if (split.length != 2) {
            return Optional.empty();
        }
        String host = split[0];
        String queryParams = split[1];
        return Optional.of(new String[] {host, queryParams});
    }


    protected Object modelMapping(String host, String queryParams) {
        if (host.equals("/user/create")) {
            User user = ModelMapper.userMapping(queryParams);
            log.info("user create : {}" , user);
            return user;
        }
        return Optional.empty();
    }

    private Optional<String> readRequestLine(BufferedReader br) throws IOException {
        String line = br.readLine();
        log.info("request line : {}", line);
        if (line == null) {
            return Optional.empty();
        }
        String requestUrl = line.split(" ")[1];
        String index = "/index.html";
        return Optional.of(requestUrl.equals("/") ? index : requestUrl);
    }

    private void readHeaders(BufferedReader br, String line) throws IOException {
        while (!line.equals("")) {
            line = br.readLine();
            log.info("header : {}", line);
            if (line == null) break;
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
