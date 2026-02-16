package shengov.bg.pizzza_management_app.pizza.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import shengov.bg.pizzza_management_app.ingredient.model.IngredientEntity;
import shengov.bg.pizzza_management_app.ingredient.model.IngredientEntity_;
import shengov.bg.pizzza_management_app.pizza.dto.PizzaFilterInput;
import shengov.bg.pizzza_management_app.pizza.model.PizzaEntity;
import shengov.bg.pizzza_management_app.pizza.model.PizzaEntity_;
import shengov.bg.pizzza_management_app.pizza.model.PizzaSize;
import shengov.bg.pizzza_management_app.pizza.model.PizzaSize_;
import shengov.bg.pizzza_management_app.size.model.SizeEntity;
import shengov.bg.pizzza_management_app.size.model.SizeEntity_;

public final class PizzaSpecification {

  private PizzaSpecification() {
    throw new IllegalStateException("Utility class");
  }

  public static Specification<PizzaEntity> from(PizzaFilterInput filter) {
    if (filter == null) {
      return (_, _, cb) -> cb.conjunction();
    }
    return Specification.where(nameLike(filter.name()))
        .and(descriptionLike(filter.description()))
        .and(hasAllIngredients(filter.ingredientNames()))
        .and(hasSizeFilters(filter.sizeName(), filter.minPrice(), filter.maxPrice()))
        .and(
            (_, query, _) -> {
              query.distinct(true);
              return null;
            });
  }

  private static Specification<PizzaEntity> nameLike(String name) {
    if (name == null || name.isBlank()) {
      return (_, _, cb) -> cb.conjunction();
    }
    String pattern = "%" + name.toLowerCase() + "%";
    return (root, _, cb) -> cb.like(cb.lower(root.get(PizzaEntity_.NAME)), pattern);
  }

  private static Specification<PizzaEntity> descriptionLike(String description) {
    if (description == null || description.isBlank()) {
      return (_, _, cb) -> cb.conjunction();
    }
    String pattern = "%" + description.toLowerCase() + "%";
    return (root, _, cb) -> cb.like(cb.lower(root.get(PizzaEntity_.DESCRIPTION)), pattern);
  }

  private static Specification<PizzaEntity> hasAllIngredients(List<String> ingredientNames) {
    if (ingredientNames == null || ingredientNames.isEmpty()) {
      return (_, _, cb) -> cb.conjunction();
    }
    List<String> lowerNames = ingredientNames.stream().map(String::toLowerCase).toList();
    return (root, _, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      for (String lowerName : lowerNames) {
        Join<PizzaEntity, IngredientEntity> join =
            root.join(PizzaEntity_.INGREDIENTS, JoinType.INNER);
        predicates.add(cb.equal(cb.lower(join.get(IngredientEntity_.NAME)), lowerName));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  private static Specification<PizzaEntity> hasSizeFilters(
      String sizeName, BigDecimal minPrice, BigDecimal maxPrice) {
    if ((sizeName == null || sizeName.isBlank()) && minPrice == null && maxPrice == null) {
      return (_, _, cb) -> cb.conjunction();
    }
    String lowerSizeName =
        (sizeName != null && !sizeName.isBlank()) ? sizeName.toLowerCase() : null;
    return (root, _, cb) -> {
      Join<PizzaEntity, PizzaSize> sizesJoin = root.join(PizzaEntity_.SIZES, JoinType.INNER);
      Join<PizzaSize, SizeEntity> sizeJoin = sizesJoin.join(PizzaSize_.SIZE, JoinType.INNER);

      List<Predicate> predicates = new ArrayList<>();
      if (lowerSizeName != null) {
        predicates.add(cb.equal(cb.lower(sizeJoin.get(SizeEntity_.NAME)), lowerSizeName));
      }
      if (minPrice != null) {
        predicates.add(cb.greaterThanOrEqualTo(sizesJoin.get(PizzaSize_.PRICE), minPrice));
      }
      if (maxPrice != null) {
        predicates.add(cb.lessThanOrEqualTo(sizesJoin.get(PizzaSize_.PRICE), maxPrice));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
