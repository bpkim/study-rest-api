package com.bpkim.studyrestapi.configs;

import com.bpkim.studyrestapi.accounts.Account;
import com.bpkim.studyrestapi.accounts.AccountRole;
import com.bpkim.studyrestapi.accounts.AccountService;
import com.bpkim.studyrestapi.common.RestDocsConfiguration;
import com.bpkim.studyrestapi.common.TestDescription;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc // SpringBootTest 에서 moc 쓰기
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class) // 레스트 에이피아이 이쁘게 만들기 config 적용
@ActiveProfiles("test")
public class AuthServerConfigTest /*extends BaseControllerTest */{


    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Test
    @TestDescription("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception{


        String password = "password";
        String username = "bpkim@email.com";
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.USER))
                .build();

        this.accountService.saveAccount(account);

        String clientId = "myApp";
        String clientSecret = "pass";
        this.mockMvc.perform(post("/oauth/token")
                    .with(httpBasic(clientId, clientSecret))  // header 생성
                    .param("username", username)
                    .param("password", password)
                    .param("grant_type", "password")
                    )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());
    }
}