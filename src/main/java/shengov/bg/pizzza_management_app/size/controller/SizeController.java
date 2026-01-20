package shengov.bg.pizzza_management_app.size.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shengov.bg.pizzza_management_app.size.dto.SizeRequest;
import shengov.bg.pizzza_management_app.size.dto.SizeResponse;
import shengov.bg.pizzza_management_app.size.service.SizeService;

import java.util.UUID;

@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
public class SizeController {

  private final SizeService sizeService;

  @PostMapping
  public ResponseEntity<SizeResponse> create(@RequestBody @Valid SizeRequest request) {
    return ResponseEntity.ok(sizeService.create(request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<SizeResponse> get(@PathVariable UUID id) {
    return ResponseEntity.ok(sizeService.getById(id));
  }

  @GetMapping
    public ResponseEntity<PagedModel<SizeResponse>> getAll(@PageableDefault Pageable pageable) {
      Page<SizeResponse> response = sizeService.getAll(pageable);
      return ResponseEntity.ok(new PagedModel<>(response));
  }
}
