package shengov.bg.pizzza_management_app.testutils;

import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

public final class ObjectMapperTestUtils {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private ObjectMapperTestUtils() {
    throw new IllegalCallerException("Utils class");
  }

  public static String toJson(Object object) throws JsonProcessingException {
    return MAPPER.writeValueAsString(object);
  }
}
