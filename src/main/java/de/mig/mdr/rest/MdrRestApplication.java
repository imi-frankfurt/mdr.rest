package de.mig.mdr.rest;

import de.mig.mdr.dal.ResourceManager;
import de.mig.mdr.dal.jooq.tables.pojos.MdrUser;
import de.mig.mdr.db.migration.MigrationUtil;
import de.mig.mdr.model.handler.UserHandler;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootApplication
public class MdrRestApplication {

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

  /**
   * Start the rest application.
   */
  public static void main(String[] args) {
    SpringApplication.run(MdrRestApplication.class, args);
  }

  /**
   * Initialize MDR DAL ResourceManager for database access.
   */
  @Bean
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {
      ResourceManager.initialize(url, username, password);
      MigrationUtil.migrateDatabase();
    };
  }

  /**
   * Get the current user.
   */
  public static MdrUser getCurrentUser() {
    try (DSLContext ctx = ResourceManager.getDslContext()) {
      return getCurrentUser(ctx);
    }
  }

  /**
   * Get the current user with the given DSLContext.
   */
  public static MdrUser getCurrentUser(DSLContext ctx) {
    return UserHandler.getUserByIdentity(
        ctx, SecurityContextHolder.getContext().getAuthentication().getName());
  }

  /**
   * Loads the git.properties file for build information that can be displayed in the about section.
   */
  @Bean
  public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
    PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
    c.setLocation(new ClassPathResource("git.properties"));
    c.setIgnoreResourceNotFound(true);
    c.setIgnoreUnresolvablePlaceholders(true);
    return c;
  }
}
