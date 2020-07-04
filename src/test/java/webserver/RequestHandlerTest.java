package webserver;

import model.User;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

public class RequestHandlerTest {

    @Test
    public void GET방식으로_회원가입() {
        // given
        String requestUrl = "/user/create?userId=javajigi&password=password&name=JaeSung&email=javajigi%40slipp.net";

        RequestHandler requestHandler = new RequestHandler(any());

        User expectUser = new User("javajigi", "password", "JaeSung", "javajigi%40slipp.net");

        // when
        String[] splitUrl = requestHandler.splitUrl(requestUrl).get();
        User actualUser = (User) requestHandler.modelMapping(splitUrl[0], splitUrl[1]);

        // then
        assertThat(actualUser).isEqualTo(expectUser);
    }

}