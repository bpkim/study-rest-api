package com.bpkim.studyrestapi.configs;

import com.bpkim.studyrestapi.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public TokenStore tokenStore(){
        return new InMemoryTokenStore();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
    }

    // 스프링 시큐리티 필터에 걸린다.
    @Override
    public void configure(WebSecurity web) throws Exception {
        //   docs/index 시큐리티 미적용

        web.ignoring().mvcMatchers("/docs/index.html");
        //   기본 위치에는 시큐리티 미적용
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    // 시큐리티 필터 지나서 들어온것
    // http 로 거르면 스프링 시큐리티 안으로 들어온것임
    // 그 요청에 어노미 하면 아무나 접근할 수 있는 요청
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                    .mvcMatchers("/docs/index.html").anonymous()
//                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
//    }


    // 인증 로그인 화면
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.anonymous()
                .and()
            .formLogin()
                .and()
            .authorizeRequests().mvcMatchers(HttpMethod.GET, "/api/**").authenticated()
            .anyRequest().authenticated();

    }
}
