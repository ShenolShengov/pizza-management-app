package shengov.bg.pizzza_management_app.size.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shengov.bg.pizzza_management_app.size.model.SizeEntity;

import java.util.UUID;

public interface SizeRepository extends JpaRepository<SizeEntity, UUID> {
}
