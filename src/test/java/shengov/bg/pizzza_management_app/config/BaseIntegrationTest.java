package shengov.bg.pizzza_management_app.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestContainersConfig.class})
@AutoConfigureMockMvc
public abstract class BaseIntegrationTest {}
