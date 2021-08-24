package com.bpkim.studyrestapi.configs;

import com.bpkim.studyrestapi.accounts.Account;
import com.bpkim.studyrestapi.accounts.AccountRole;
import com.bpkim.studyrestapi.accounts.AccountService;
import com.bpkim.studyrestapi.common.AppProperties;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        //   인코딩된 패스워드 문자열 앞에 어떠한 방식으로 인코딩 되었는지 알 수 있도록 한다.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ApplicationRunner applicationRunner(){
        return new ApplicationRunner() {

            @Autowired
            AccountService accountService;

            @Autowired
            AppProperties appProperties;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                Account admin = Account.builder()
//                                    .email("admin")
//                                    .password("qwer")
                                    .email(appProperties.getAdminUsername())
                                    .password(appProperties.getAdminPassword())
                                    .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                                .build();
                accountService.saveAccount(admin);

                Account user = Account.builder()
//                                    .email("user")
//                                    .password("qwer")
                                    .email(appProperties.getUserUsername())
                                    .password(appProperties.getUserPassword())
                                    .roles(Set.of(AccountRole.USER))
                                .build();
                accountService.saveAccount(admin);
            }
        };
    }
}
