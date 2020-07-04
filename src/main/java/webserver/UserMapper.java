package webserver;

import model.User;
import util.HttpRequestUtils;

import java.util.Map;

public class UserMapper {
    private final String requestUrl;

    public static User userMapping(String requestUrl) {
        UserMapper userMapper = new UserMapper(requestUrl);
        return userMapper.mapping(requestUrl);
    }

    private UserMapper(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    private User mapping(String requestUrl) {
        String[] split = requestUrl.split("\\?");
        if (split.length >= 2) {
            Map<String, String> map = HttpRequestUtils.parseQueryString(split[1]);
            return new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));
        }
        return null;
    }
}
