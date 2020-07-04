package webserver;

import junit.framework.TestCase;
import model.User;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

public class RequestHandlerTest {

    @Test
    public void 파라미터를_파싱해서_유저모델에_넣는다() {
        // given
        String requestUrl = "/user/create?userId=javajigi&password=password&name=JaeSung&email=javajigi%40slipp.net";

        RequestHandler requestHandler = new RequestHandler(any());

        User expectUser = new User("javajigi", "password", "JaeSung", "javajigi%40slipp.net");

        // when
        User actualUser = requestHandler.userMapping(requestUrl);

        // then
        assertThat(actualUser).isEqualTo(expectUser);
    }

}