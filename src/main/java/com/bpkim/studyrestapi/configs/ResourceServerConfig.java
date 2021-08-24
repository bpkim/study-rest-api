package com.bpkim.studyrestapi.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
// 리소스 서버 설정
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        // 리소스 id 설정
//        resources.accessDeniedHandler();// 접근권한이 없을때 어떻게 할지
        resources.resourceId("event");

    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.anonymous()
                .and()
             .authorizeRequests()
                .mvcMatchers(HttpMethod.POST, "/api/account").anonymous()// 회원가입 허용
                .mvcMatchers(HttpMethod.GET, "/api/**") // api 로 들어오는건 허용

                    .anonymous()
                .anyRequest()
                    .authenticated()
                .and()
             .exceptionHandling()
                .accessDeniedHandler(new OAuth2AccessDeniedHandler()); // OAuth2AccessDeniedHandler 403 으로 에러 보내는 핸들러
    }
}
