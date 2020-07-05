package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Optional;

public class RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String INIT_STRING = "INITSTRING";

    private BufferedReader br;

    public static String handleRequest(BufferedReader br) throws IOException {
        RequestHandler requestHandler = new RequestHandler(br);

        String requestLine = requestHandler.handleRequestLine();

        requestHandler.readHeaders();

        return requestLine;

    }

    protected RequestHandler(BufferedReader br) {
        this.br = br;
    }

    protected String handleRequestLine() throws IOException {
        String requestLine = readRequestLine()
                .orElseThrow(IOException::new);

        String[] hostAndParams = splitByQuestionMark(requestLine)
                .orElseGet(() -> new String[] {});

        if (hostAndParams.length == 2) {
            modelMapping(hostAndParams[0], hostAndParams[1]);
        }

        return requestLine;
    }

    protected Optional<String> readRequestLine() throws IOException {
        String line = br.readLine();
//        log.info("request line : {}", line);
        System.out.println(line);
        if (line == null) {
            return Optional.empty();
        }
        String requestUrl = line.split(" ")[1];
        String index = "/index.html";
        return Optional.of(requestUrl.equals("/") ? index : requestUrl);
    }

    protected Optional<String[]> splitByQuestionMark(String requestUrl) {
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

    protected void readHeaders() throws IOException {
        String line = INIT_STRING;
        while (!line.equals("")) {
            line = br.readLine();
//            log.info("header : {}", line);
            System.out.println(line);
            if (line == null) break;
        }
    }
}
