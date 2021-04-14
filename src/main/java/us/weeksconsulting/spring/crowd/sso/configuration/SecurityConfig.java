package us.weeksconsulting.spring.crowd.sso.configuration;

import com.atlassian.crowd.integration.springsecurity.RemoteCrowdAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;

/**
 *
 * @author Chris
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private RemoteCrowdAuthenticationProvider crowdAuthenticationProvider;
  @Autowired
  private CrowdProcessingFilter crowdProcessingFilter;

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(crowdAuthenticationProvider);
  }

  /**
   * Defines the web based security configuration.
   *
   * @param http It allows configuring web based security for specific http
   * requests.
   * @throws Exception
   */
  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http
        .httpBasic()
        .and()
        .addFilterBefore(crowdProcessingFilter, CsrfFilter.class)
        .authorizeRequests()
        .antMatchers("/").permitAll()
        .antMatchers("/favicon.ico").permitAll()
        .antMatchers("/saml/**").permitAll()
        .antMatchers("/css/**").permitAll()
        .antMatchers("/img/**").permitAll()
        .antMatchers("/js/**").permitAll()
        .anyRequest().authenticated()
        .and()
        .headers().contentSecurityPolicy("default-src 'none'")
        .and().and()
        .logout()
        .disable();	// The logout procedure is already handled by SAML filters.
  }
}
