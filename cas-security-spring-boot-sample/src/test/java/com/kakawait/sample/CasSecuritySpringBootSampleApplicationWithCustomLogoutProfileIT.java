package com.kakawait.sample;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.kakawait.sample.CasSecuritySpringBootSampleApplication.*;

@SpringBootTest
@ActiveProfiles("custom-logout")
class CasSecuritySpringBootSampleApplicationWithCustomLogoutProfileIT extends CasSecuritySpringBootSampleApplicationIT {

    @DisplayName("has static bean for custom-logout profile configured")
    @ParameterizedTest
    @ValueSource(classes = {SecurityConfiguration.class
            , OverrideDefaultCasSecurity.class
            //, LogoutConfiguration.class // In comment as custom-login profile is NOT active
            , ApiSecurityConfiguration.class
            , ApiSecurityConfiguration.CustomLogoutConfiguration.class
            , ApiSecurityConfiguration.WebMvcConfiguration.class
            , ApiSecurityConfiguration.BackwardSpringBoot1CasSecurityConfiguration.class
            , ApiSecurityConfiguration.IndexController.class
            , ApiSecurityConfiguration.HelloWorldController.class
    })
    void hasStaticBeanConfigured(Class clazz) {
        super.hasStaticBeanConfigured(clazz);
    }

    @DisplayName("has noSuchBeanDefinitionException thrown")
    @ParameterizedTest
    @ValueSource(classes = {LogoutConfiguration.class})
    void noSuchBeanDefinitionExceptionIsThrown(Class clazz) {
        super.noSuchBeanDefinitionExceptionIsThrown(clazz);
    }
}
