package shengov.bg.pizzza_management_app.size.service.impls;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import shengov.bg.pizzza_management_app.core.exception.ResourceNotFoundException;
import shengov.bg.pizzza_management_app.size.dto.SizeRequest;
import shengov.bg.pizzza_management_app.size.dto.SizeResponse;
import shengov.bg.pizzza_management_app.size.exception.SizeAlreadyExistsException;
import shengov.bg.pizzza_management_app.size.mapper.SizeMapper;
import shengov.bg.pizzza_management_app.size.model.SizeEntity;
import shengov.bg.pizzza_management_app.size.repository.SizeRepository;

@ExtendWith(MockitoExtension.class)
class SizeServiceImplTest {

  private static final String TEST_NAME = "Large";

  @Mock private SizeRepository sizeRepository;
  @Mock private SizeMapper sizeMapper;

  @InjectMocks private SizeServiceImpl toTest;

  private SizeEntity createTestSize(String name) {
    SizeEntity size = new SizeEntity();
    ReflectionTestUtils.setField(size, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(size, "name", name);
    return size;
  }

  private SizeEntity createTestSize() {
    return createTestSize(TEST_NAME);
  }

  private SizeRequest createTestSizeRequest() {
    return new SizeRequest(TEST_NAME);
  }

  @Test
  void create_ShouldSaveSize_WhenNameIsUnique() {
    SizeEntity savedSize = createTestSize();
    SizeRequest request = createTestSizeRequest();
    SizeResponse expectedResponse = new SizeResponse(savedSize.getId(), savedSize.getName());

    when(sizeRepository.existsByNameIgnoreCase(TEST_NAME)).thenReturn(false);
    when(sizeMapper.requestToEntity(request)).thenReturn(savedSize);
    when(sizeRepository.save(savedSize)).thenReturn(savedSize);
    when(sizeMapper.entityToResponse(savedSize)).thenReturn(expectedResponse);

    SizeResponse response = toTest.create(request);

    assertNotNull(response);
    assertEquals(savedSize.getName(), response.name());
    assertEquals(savedSize.getId(), response.id());
    verify(sizeRepository, times(1)).save(savedSize);
  }

  @Test
  void create_ShouldThrowException_WhenSizeNameAlreadyExists() {
    SizeRequest request = createTestSizeRequest();

    when(sizeRepository.existsByNameIgnoreCase(TEST_NAME)).thenReturn(true);

    SizeAlreadyExistsException exception =
        assertThrows(SizeAlreadyExistsException.class, () -> toTest.create(request));

    assertTrue(exception.getMessage().contains(TEST_NAME));
    verify(sizeRepository, never()).save(any(SizeEntity.class));
  }

  @Test
  void getById_ShouldReturnSize_WhenExists() {
    SizeEntity size = createTestSize();
    SizeResponse expectedResponse = new SizeResponse(size.getId(), size.getName());

    when(sizeRepository.findById(size.getId())).thenReturn(Optional.of(size));
    when(sizeMapper.entityToResponse(size)).thenReturn(expectedResponse);

    SizeResponse response = toTest.getById(size.getId());

    assertEquals(size.getId(), response.id());
    assertEquals(size.getName(), response.name());
  }

  @Test
  void getById_ShouldThrowException_WhenSizeNotExist() {
    UUID notExistId = UUID.randomUUID();
    when(sizeRepository.findById(notExistId)).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(ResourceNotFoundException.class, () -> toTest.getById(notExistId));

    assertTrue(exception.getMessage().contains(notExistId.toString()));
    assertTrue(exception.getMessage().contains("Size"));
  }

  @Test
  void getAll_ShouldReturnCorrectResult() {
    List<SizeEntity> testSizes =
        List.of(createTestSize("Small"), createTestSize("Medium"), createTestSize("Large"));

    when(sizeRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(testSizes));
    when(sizeMapper.entityToResponse(any(SizeEntity.class)))
        .thenAnswer(
            inv -> {
              SizeEntity e = inv.getArgument(0);
              return new SizeResponse(e.getId(), e.getName());
            });

    Page<SizeResponse> result = toTest.getAll(Pageable.unpaged());

    assertEquals(testSizes.size(), result.getTotalElements());
  }

  @Test
  void update_ShouldThrow_WhenSizeNotExist() {
    UUID notExistId = UUID.randomUUID();
    when(sizeRepository.findById(notExistId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> toTest.update(notExistId, createTestSizeRequest()));
  }

  @Test
  void update_ShouldThrow_WhenNewNameAlreadyExists() {
    SizeEntity existingSize = createTestSize("Medium");
    SizeRequest request = createTestSizeRequest(); // "Large"

    when(sizeRepository.findById(existingSize.getId())).thenReturn(Optional.of(existingSize));
    when(sizeRepository.existsByNameIgnoreCase(TEST_NAME)).thenReturn(true);

    assertThrows(
        SizeAlreadyExistsException.class, () -> toTest.update(existingSize.getId(), request));
  }

  @Test
  void update_ShouldUpdate_WhenSizeExists() {
    String originalName = "Small";
    SizeEntity sizeToUpdate = createTestSize(originalName);
    SizeRequest request = createTestSizeRequest(); // "Large"
    SizeResponse expectedResponse = new SizeResponse(sizeToUpdate.getId(), TEST_NAME);

    when(sizeRepository.findById(sizeToUpdate.getId())).thenReturn(Optional.of(sizeToUpdate));
    when(sizeRepository.existsByNameIgnoreCase(TEST_NAME)).thenReturn(false);
    when(sizeMapper.entityToResponse(sizeToUpdate)).thenReturn(expectedResponse);

    SizeResponse response = toTest.update(sizeToUpdate.getId(), request);

    assertEquals(TEST_NAME, response.name());
    assertEquals(TEST_NAME, sizeToUpdate.getName());
  }

  @Test
  void update_ShouldNotValidateUniqueness_WhenNameIsUnchanged() {
    SizeEntity existingSize = createTestSize(TEST_NAME);
    SizeRequest request = new SizeRequest(TEST_NAME.toLowerCase());
    SizeResponse expectedResponse = new SizeResponse(existingSize.getId(), TEST_NAME);

    when(sizeRepository.findById(existingSize.getId())).thenReturn(Optional.of(existingSize));
    when(sizeMapper.entityToResponse(existingSize)).thenReturn(expectedResponse);

    toTest.update(existingSize.getId(), request);

    verify(sizeRepository, never()).existsByNameIgnoreCase(any());
  }

  @Test
  void delete_ShouldThrow_WhenSizeNotExist() {
    UUID notExistId = UUID.randomUUID();
    when(sizeRepository.findById(notExistId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> toTest.delete(notExistId));

    verify(sizeRepository, never()).delete(any(SizeEntity.class));
  }

  @Test
  void delete_ShouldDelete_WhenSizeExists() {
    SizeEntity size = createTestSize();
    when(sizeRepository.findById(size.getId())).thenReturn(Optional.of(size));

    toTest.delete(size.getId());

    verify(sizeRepository, times(1)).delete(size);
  }
}
