package com.bpkim.studyrestapi.common;

import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

// adoc 이쁘게 만들기
@TestConfiguration // 테스트에서만 사용하는 configuration
public class RestDocsConfiguration {

    @Bean
    public RestDocsMockMvcConfigurationCustomizer restDocsMockMvcConfigurationCustomizer(){
        return configurer -> configurer.operationPreprocessors()
                    .withRequestDefaults(prettyPrint())     // Request 이쁘게
                    .withResponseDefaults(prettyPrint());   // Response 이쁘게
    }
}
