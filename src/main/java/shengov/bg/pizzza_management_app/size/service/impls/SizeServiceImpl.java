package shengov.bg.pizzza_management_app.size.service.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import shengov.bg.pizzza_management_app.size.dto.SizeRequest;
import shengov.bg.pizzza_management_app.size.dto.SizeResponse;
import shengov.bg.pizzza_management_app.size.exception.SizeAlreadyExistsException;
import shengov.bg.pizzza_management_app.size.mapper.SizeMapper;
import shengov.bg.pizzza_management_app.size.model.SizeEntity;
import shengov.bg.pizzza_management_app.size.repository.SizeRepository;
import shengov.bg.pizzza_management_app.size.service.SizeService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SizeServiceImpl implements SizeService {

  private final SizeRepository sizeRepository;
  private final SizeMapper sizeMapper;

  @Override
  public SizeResponse create(SizeRequest request) {
    if(sizeRepository.existsByName(request.name())) {
        throw new SizeAlreadyExistsException(request.name());
    }
    SizeEntity toCreate = sizeMapper.requestToEntity(request);
    return sizeMapper.entityToResponse(sizeRepository.save(toCreate));
  }

  @Override
  public SizeResponse update(UUID id, SizeRequest request) {
    return null;
  }

  @Override
  public void delete(UUID id) {}

  @Override
  public SizeResponse getById(UUID id) {
    return null;
  }

  @Override
  public Page<SizeResponse> getAll(Pageable pageable) {
    return null;
  }
}
