package ru.kuznetsov.stories.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kuznetsov.stories.security.AuthenticationProviderImpl;
import ru.kuznetsov.stories.security.jwt.JwtConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtConfigurer jwtConfigurer;
    private final AuthenticationProviderImpl authenticationProvider;

    String[] authenticatedUrls = new String[]{"/api/mark/**","/api/mark",
            "/api/user/name","/api/user/*/refactor-stories", "/api/notifications","/api/notification/**","/api/auth/logout"};
    String[] anonymousUrls = new String[]{
            "/api/auth/login", "/api/auth/reg"
    };


    @Autowired
    public SecurityConfig(JwtConfigurer jwtConfigurer, AuthenticationProviderImpl authenticationProvider) {
        this.jwtConfigurer = jwtConfigurer;
        this.authenticationProvider = authenticationProvider;
    }
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(anonymousUrls).anonymous()
                .antMatchers(authenticatedUrls).authenticated()
                .antMatchers("/api/admin","/api/admin/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/api/moderator","/api/moderator/**").hasAuthority("ROLE_MODERATOR")
                .antMatchers(HttpMethod.POST,"/api/comments").authenticated()
                .antMatchers(HttpMethod.POST,"/api/stories").authenticated()
                .antMatchers(HttpMethod.DELETE,"/api/stories/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/comments/**").authenticated()
                .anyRequest().permitAll()
                .and().apply(jwtConfigurer).and().cors();
    }


}
