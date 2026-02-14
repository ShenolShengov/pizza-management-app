package shengov.bg.pizzza_management_app.testutils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shengov.bg.pizzza_management_app.size.dto.SizeRequest;
import shengov.bg.pizzza_management_app.size.model.SizeEntity;
import shengov.bg.pizzza_management_app.size.repository.SizeRepository;

@Component
public final class SizeTestUtils {

  @Autowired private SizeRepository sizeRepository;

  public static SizeRequest createTestSizeRequest(String name) {
    return new SizeRequest(name);
  }

  public static SizeEntity createTestSize(SizeRequest request) {
    SizeEntity size = new SizeEntity();
    size.setName(request.name());
    return size;
  }

  public SizeEntity saveTestSize(SizeRequest request) {
    return sizeRepository.save(createTestSize(request));
  }

  public void saveSizes(String[] names) {
    for (String name : names) {
      saveTestSize(createTestSizeRequest(name));
    }
  }
}
