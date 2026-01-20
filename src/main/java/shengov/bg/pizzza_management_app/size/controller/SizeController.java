package shengov.bg.pizzza_management_app.size.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shengov.bg.pizzza_management_app.size.dto.SizeRequest;
import shengov.bg.pizzza_management_app.size.dto.SizeResponse;
import shengov.bg.pizzza_management_app.size.service.SizeService;

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
}
