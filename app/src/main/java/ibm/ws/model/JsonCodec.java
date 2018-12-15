package ibm.ws.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * @authorHamed
 */
public class JsonCodec {
    public static final Charset UTF_8 = Charset.forName("utf-8");
    public static final ObjectMapper mapper = new ObjectMapper();
    private static boolean checkSchema = true;

    static {
//        try {
//            String check = IxProps.property("ix.microserver.check.schema", new Object[0]);
//            if(!Objects.equals(check, "ix.microserver.check.schema")) {
//                checkSchema = Boolean.parseBoolean(check);
//            }
//        } catch (Exception | NoClassDefFoundError var1) {
//            ;
//        }

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, checkSchema);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
    }

    public JsonCodec() {
    }

    public static <T> T toObject(byte[] bytes, Class<T> objClass) {
        try {
            return mapper.readValue(new String(bytes, UTF_8), objClass);
        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }
    }

    public static <T> T toObject(String json, Class<T> objClass) {
        try {
            return mapper.readValue(json, objClass);
        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }
    }

    public static <T> T toObject(Map map, Class<T> clazz) {
        return mapper.convertValue(map, clazz);
    }

    public static Map toMap(Object o) {
        return (Map) mapper.convertValue(o, Map.class);
    }

    public static byte[] toBytes(Object obj) {
        try {
            return toString(obj).getBytes(UTF_8);
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }

    public static String toString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }

}
