package com.kakawait.spring.security.cas.web.authentication;

import org.springframework.security.cas.ServiceProperties;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromContextPath;

/**
 * @author Thibaud Leprêtre
 */
public class RequestAwareCasLogoutSuccessHandler extends CasLogoutSuccessHandler {

    public RequestAwareCasLogoutSuccessHandler(URI casLogout, ServiceProperties serviceProperties) {
        super(casLogout, serviceProperties);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(casLogout);
        addLogoutServiceParameter(builder, fromContextPath(request).build().toUriString());
        return builder.build().toUriString();
    }
}
