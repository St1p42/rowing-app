package rowing.commons.entities.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.time.LocalTime;

/**
 * The Json util for tests.
 */
public class JsonUtil {
    /**
     * Serialize object into a string.
     *
     * @param object The object to be serialized.
     * @return A serialized string.
     * @throws JsonProcessingException if an error occurs during serialization.
     */
    public static String serialize(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        // Create a custom serializer for the LocalTime class
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalTime.class, new JsonSerializer<LocalTime>() {
            @Override
            public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(value.toString());
            }
        });

        objectMapper.registerModule(module);
        return objectMapper.writeValueAsString(object);
    }

    /**
     * Deserializes a json string into an object.
     *
     * @param json The string to be deserialized.
     * @param type The type of the desired object.
     * @return The deserialized object.
     * @throws JsonProcessingException if an error occurs during deserialization.
     */
    public static <T> T deserialize(String json, Class<T> type) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        // Create a custom deserializer for the LocalTime class
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalTime.class, new JsonDeserializer<LocalTime>() {
            @Override
            public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return LocalTime.parse(p.getText());
            }
        });

        objectMapper.registerModule(module);
        return objectMapper.readValue(json, type);
    }
}