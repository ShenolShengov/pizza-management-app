package shengov.bg.pizzza_management_app.size.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import shengov.bg.pizzza_management_app.size.dto.SizeRequest;
import shengov.bg.pizzza_management_app.size.dto.SizeResponse;
import shengov.bg.pizzza_management_app.size.service.SizeService;

@Tag(name = "Sizes", description = "Manage pizza sizes (e.g. Small, Medium, Large)")
@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
public class SizeController {

  private final SizeService sizeService;

  @Operation(
      summary = "Create size",
      description = "Creates a new pizza size. Requires ADMIN role.")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Size created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request body"),
    @ApiResponse(responseCode = "409", description = "Size with this name already exists")
  })
  @PostMapping
  public ResponseEntity<SizeResponse> create(@RequestBody @Valid SizeRequest request) {
    SizeResponse response = sizeService.create(request);
    return ResponseEntity.created(
            ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(response.id()))
        .body(response);
  }

  @Operation(summary = "Get size by ID", description = "Returns a single pizza size by its UUID")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Size found"),
    @ApiResponse(responseCode = "404", description = "Size not found")
  })
  @GetMapping("/{id}")
  public ResponseEntity<SizeResponse> getById(
      @Parameter(description = "UUID of the size") @PathVariable UUID id) {
    return ResponseEntity.ok(sizeService.getById(id));
  }

  @Operation(summary = "Get all sizes", description = "Returns a paginated list of all pizza sizes")
  @ApiResponse(responseCode = "200", description = "Sizes retrieved successfully")
  @GetMapping
  public ResponseEntity<PagedModel<SizeResponse>> getAll(@PageableDefault Pageable pageable) {
    Page<SizeResponse> response = sizeService.getAll(pageable);
    return ResponseEntity.ok(new PagedModel<>(response));
  }

  @Operation(
      summary = "Update size",
      description = "Updates an existing pizza size by its UUID. Requires ADMIN role.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Size updated successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request body"),
    @ApiResponse(responseCode = "404", description = "Size not found"),
    @ApiResponse(responseCode = "409", description = "Size with this name already exists")
  })
  @PutMapping("/{id}")
  public ResponseEntity<SizeResponse> update(
      @Parameter(description = "UUID of the size to update") @PathVariable UUID id,
      @RequestBody @Valid SizeRequest request) {
    return ResponseEntity.ok(sizeService.update(id, request));
  }

  @Operation(
      summary = "Delete size",
      description = "Deletes a pizza size by its UUID. Requires ADMIN role.")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Size deleted successfully"),
    @ApiResponse(responseCode = "404", description = "Size not found")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "UUID of the size to delete") @PathVariable UUID id) {
    sizeService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
