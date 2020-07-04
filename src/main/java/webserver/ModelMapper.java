package webserver;

import model.User;
import util.HttpRequestUtils;

import java.util.Map;

public class ModelMapper {
    private final String requestUrl;

    public static User userMapping(String requestUrl) {
        ModelMapper modelMapper = new ModelMapper(requestUrl);
        return modelMapper.mapping(requestUrl);
    }

    private ModelMapper(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    private User mapping(String queryParams) {
        Map<String, String> map = HttpRequestUtils.parseQueryString(queryParams);
        return new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));
    }
}
