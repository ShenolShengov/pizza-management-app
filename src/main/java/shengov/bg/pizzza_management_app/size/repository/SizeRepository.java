package shengov.bg.pizzza_management_app.size.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shengov.bg.pizzza_management_app.size.model.SizeEntity;

@Repository
public interface SizeRepository extends JpaRepository<SizeEntity, UUID> {}
