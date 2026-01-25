package shengov.bg.pizzza_management_app.size.service.impls;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import shengov.bg.pizzza_management_app.core.exception.ResourceNotFoundException;
import shengov.bg.pizzza_management_app.size.dto.SizeRequest;
import shengov.bg.pizzza_management_app.size.dto.SizeResponse;
import shengov.bg.pizzza_management_app.size.exception.SizeAlreadyExistsException;
import shengov.bg.pizzza_management_app.size.mapper.SizeMapper;
import shengov.bg.pizzza_management_app.size.model.SizeEntity;
import shengov.bg.pizzza_management_app.size.repository.SizeRepository;
import shengov.bg.pizzza_management_app.size.service.SizeService;

@Service
@RequiredArgsConstructor
public class SizeServiceImpl implements SizeService {

  private final SizeRepository sizeRepository;
  private final SizeMapper sizeMapper;

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public SizeResponse create(SizeRequest request) {
    validateUniqueName(request.name());
    SizeEntity toCreate = sizeMapper.requestToEntity(request);
    return sizeMapper.entityToResponse(sizeRepository.save(toCreate));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public SizeResponse update(UUID id, SizeRequest request) {
    SizeEntity toUpdate = byId(id);
    if (!request.name().equalsIgnoreCase(toUpdate.getName())) validateUniqueName(request.name());
    toUpdate.setName(request.name());
    return sizeMapper.entityToResponse(toUpdate);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(UUID id) {
    SizeEntity toDelete = byId(id);
    sizeRepository.delete(toDelete);
  }

  @Override
  public SizeResponse getById(UUID id) {
    return sizeMapper.entityToResponse(byId(id));
  }

  @Override
  public Page<SizeResponse> getAll(Pageable pageable) {
    return sizeRepository.findAll(pageable).map(sizeMapper::entityToResponse);
  }

  private SizeEntity byId(UUID id) {
    return sizeRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Size", "id", id.toString()));
  }

  private void validateUniqueName(String name) {
    if (sizeRepository.existsByNameIgnoreCase(name)) {
      throw new SizeAlreadyExistsException(name);
    }
  }
}
