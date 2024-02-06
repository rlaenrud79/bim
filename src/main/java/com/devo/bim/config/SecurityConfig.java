package com.devo.bim.config;

import com.devo.bim.handler.CustomLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private AuthenticationFailureHandler customFailureHandler;

    @Autowired
    private AuthenticationSuccessHandler customSuccessHandler;

    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/account/resetAdminPassword/*", "/account/login", "/dist/**", "/plugins/**", "/mainApi/weather/**", "/chatApi/**","/mainApi/licenseReference/**", "/error/**")
            .permitAll()
            .anyRequest()
            .authenticated()
        .and()
            .formLogin()
            .loginPage("/account/login")
            .successHandler(customSuccessHandler)
            .failureHandler(customFailureHandler)
            .permitAll()
        .and()
            .logout()
            .logoutUrl("/logout") /* 로그아웃 url*/
            .logoutSuccessHandler(customLogoutSuccessHandler)
            .logoutSuccessUrl("/account/login") /* 로그아웃 성공시 이동할 url */
            .invalidateHttpSession(true) /*로그아웃시 세션 제거*/
            .deleteCookies("JSESSIONID") /*쿠키 제거*/
            .clearAuthentication(true) /*권한정보 제거*/
            .permitAll()
        .and()
            .sessionManagement()
            .maximumSessions(1) /* session 허용 갯수 */
            .expiredUrl("/account/login") /* session 만료시 이동 페이지*/
            .maxSessionsPreventsLogin(false)
            .sessionRegistry(sessionRegistry());

        http.cors().and().csrf().disable();
        http.headers().frameOptions().disable();
    }

    /* * 스프링 시큐리티 룰을 무시하게 하는 Url 규칙(여기 등록하면 규칙 적용하지 않음) */
    @Override public void configure(WebSecurity web) throws Exception {
//        web.ignoring()
//            .antMatchers("/costApi/**");
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {

        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder())
                .usersByUsernameQuery("select email as user_id, password, CASE WHEN enabled = 1 THEN true ELSE false END "
                        + "from account "
                        + "where email = ?")
                .authoritiesByUsernameQuery("select a.email as user_id, c.code "
                        + "from account a inner join account_role b on b.account_id=a.id  "
                        + "inner join role c on b.role_id = c.id "
                        + "where a.email = ? ")
                .groupAuthoritiesByUsername(
                        "select b.id, b.name, b.role_type "
                        + "from account a "
                        + "inner join company b on b.id = a.company_id "
                        + "where a.email = ?"
                );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher());
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}