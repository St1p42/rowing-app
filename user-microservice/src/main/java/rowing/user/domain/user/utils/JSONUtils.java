package rowing.user.domain.user.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
