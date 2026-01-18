package shengov.bg.pizzza_management_app.size.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shengov.bg.pizzza_management_app.size.dto.SizeRequest;
import shengov.bg.pizzza_management_app.size.dto.SizeResponse;

public interface SizeService {
  SizeResponse create(SizeRequest request);

  SizeResponse update(UUID id, SizeRequest request);

  void delete(UUID id);

  SizeResponse getById(UUID id);

  Page<SizeResponse> getAll(Pageable pageable);
}
