package webserver;

import junit.framework.TestCase;
import model.User;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;

public class RequestHandlerTest {

    @Test
    public void 요청_HTTP메세지_처리_테스트() {
        File requestMessage = new File("./src/main/resources/RequestMsg_defaultUrl.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(requestMessage))) {
            RequestHandler.handleRequest(br);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void 빈거들어옴() {
        File requestMessage = new File("./src/main/resources/RequestMsg_defaultUrl.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(requestMessage))) {
            RequestHandler.handleRequest(br);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void GET방식으로_회원가입() {
        // given
        File requestMessage = new File("./src/main/resources/RequestMsg_createUserUrl.txt");
        RequestHandler requestHandler;
        String requestLine;
        try (BufferedReader br = new BufferedReader(new FileReader(requestMessage))) {
            requestHandler = new RequestHandler(br);
            requestLine = requestHandler.handleRequestLine();
            User expectUser = new User("javajigi", "password", "ohjuntaek", "dimes12%40naver.com");

            // when
            String[] splitUrl = requestHandler.splitByQuestionMark(requestLine).get();
            User actualUser = (User) requestHandler.modelMapping(splitUrl[0], splitUrl[1]);

            // then
            assertThat(actualUser).isEqualTo(expectUser);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}