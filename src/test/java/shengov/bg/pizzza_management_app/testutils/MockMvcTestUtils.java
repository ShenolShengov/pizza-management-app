package shengov.bg.pizzza_management_app.testutils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static shengov.bg.pizzza_management_app.testutils.ObjectMapperTestUtils.toJson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@Component
public class MockMvcTestUtils {

  @Autowired private MockMvc mockMvc;

  public ResultActions performPost(String uri, Object body) throws Exception {
    return mockMvc.perform(
        post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(toJson(body)));
  }

  public ResultActions performPut(String uri, Object body) throws Exception {
    return mockMvc.perform(
        put(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(toJson(body)));
  }

  public ResultActions performGet(String uri) throws Exception {
    return mockMvc.perform(get(uri));
  }
}
