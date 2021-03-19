package de.mig.mdr.rest.configuration;

import de.mig.mdr.dal.ResourceManager;
import de.mig.mdr.dal.jooq.tables.pojos.MdrUser;
import de.mig.mdr.model.handler.UserHandler;
import java.util.Map;
import org.jooq.DSLContext;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests().antMatchers(HttpMethod.GET, "/**")
        .permitAll()
        .and().authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/**")
        .permitAll()
        .and().authorizeRequests().antMatchers(HttpMethod.HEAD, "/**")
        .permitAll()
        .and().authorizeRequests().anyRequest().fullyAuthenticated()
        .and().oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt).cors();
  }

  @Component
  public class AuthenticationSuccessListener
      implements ApplicationListener<AuthenticationSuccessEvent> {

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
      try (DSLContext ctx = ResourceManager.getDslContext()) {
        Map<String, Object> claims =
            ((JwtAuthenticationToken) event.getAuthentication()).getTokenAttributes();
        String sub = (String) claims.get("sub");
        String email = (String) claims.get("email");
        String name = (String) claims.get("name");
        MdrUser user = UserHandler.getUserByIdentity(ctx, sub);
        if (user == null) {
          UserHandler.createDefaultUser(ctx, sub, email, name);
        } else {
          user.setUserName(name);
          user.setEmail(email);
          UserHandler.updateUser(ctx, user);
        }
      }
    }
  }
}
