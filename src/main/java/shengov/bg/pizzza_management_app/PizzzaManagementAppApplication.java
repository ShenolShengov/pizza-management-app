package shengov.bg.pizzza_management_app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientRequest;
import shengov.bg.pizzza_management_app.ingredient.dto.IngredientResponse;
import shengov.bg.pizzza_management_app.ingredient.service.IngredientService;

@SpringBootApplication
public class PizzzaManagementAppApplication {

  public PizzzaManagementAppApplication(IngredientService ingredientService) {
    this.ingredientService = ingredientService;
  }

  public static void main(String[] args) {
    SpringApplication.run(PizzzaManagementAppApplication.class, args);
  }

  private final IngredientService ingredientService;

  @Bean
  public CommandLineRunner runner() {
    return new CommandLineRunner() {
      @Override
      public void run(String... args) throws Exception {
        System.out.println("----------------------");
        IngredientResponse cheese = ingredientService.create(new IngredientRequest("Cheese"));
        System.out.println("Message: " + cheese.message());
        System.out.println("Id: " + cheese.id());
        System.out.println("----------------------");
      }
    };
  }
}
