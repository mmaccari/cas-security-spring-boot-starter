package com.kakawait.spring.security.cas.web.authentication;

import org.springframework.security.cas.web.authentication.ServiceAuthenticationDetails;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Thibaud Leprêtre
 */
public interface ProxyCallbackAndServiceAuthenticationDetails extends ServiceAuthenticationDetails {  // TODO resolve deprecation ServiceAuthenticationDetails
    String getProxyCallbackUrl();

    void setContext(HttpServletRequest context);
}
