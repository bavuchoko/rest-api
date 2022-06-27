package com.pjs.studyrestapi.config;

import com.pjs.studyrestapi.accounts.Account;
import com.pjs.studyrestapi.accounts.AccountRole;
import com.pjs.studyrestapi.accounts.AccountService;
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
    public PasswordEncoder passwordEncoder() {
        //패스워드 인코딩 타입의 prefix 를 제공함.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

//    @Bean
//    public ApplicationRunner applicationRunner() {
//        return new ApplicationRunner() {
//            @Autowired
//            AccountService accountService;
//
//            @Override
//            public void run(ApplicationArguments args) throws Exception {
//                Account test = Account.builder()
//                        .email("test@mail.com")
//                        .password("test")
//                        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
//                        .build();
//                accountService.saveAccount(test);
//            }
//        };
//
//    }

}
