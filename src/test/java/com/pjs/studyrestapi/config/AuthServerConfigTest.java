package com.pjs.studyrestapi.config;

import com.pjs.studyrestapi.accounts.Account;
import com.pjs.studyrestapi.accounts.AccountRole;
import com.pjs.studyrestapi.accounts.AccountService;
import com.pjs.studyrestapi.common.BaseControllerTest;
import com.pjs.studyrestapi.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Test
    @TestDescription("인증토큰 발급 테스트")
    public void getAuthToken() throws Exception {
        //Given
        String username = "test@mail.com";
        String password = "test";
        Account test = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(test);

        String clientId = "myApp";
        String clientSecret = "pass";

        this.mockMvc.perform(post("/oauth/token")
                    .with(httpBasic(clientId,clientSecret))
                    .param("username",username)
                    .param("password",password)
                    .param("grant_type", "password")
        )
                .andDo(print())
//                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());
    }

}