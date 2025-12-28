package shengov.bg.pizzza_management_app.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureWebMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestContainersConfig.class})
@AutoConfigureWebMvc
public abstract class BaseIntegrationTest {}
