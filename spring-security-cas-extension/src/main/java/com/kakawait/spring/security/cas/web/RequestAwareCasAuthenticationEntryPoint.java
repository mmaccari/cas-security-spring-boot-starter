package com.kakawait.spring.security.cas.web;

import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromContextPath;

/**
 * @author Thibaud Leprêtre
 */
public class RequestAwareCasAuthenticationEntryPoint extends CasAuthenticationEntryPoint {

    @SuppressWarnings("WeakerAccess")
    protected final URI loginPath;

    public RequestAwareCasAuthenticationEntryPoint(URI loginPath) {
        Assert.notNull(loginPath, "login path is required, it must not be null");
        this.loginPath = loginPath;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.hasLength(getLoginUrl(), "loginUrl must be specified");
        Assert.notNull(getServiceProperties(), "serviceProperties must be specified");
    }

    @Override
    protected String createServiceUrl(HttpServletRequest request, HttpServletResponse response) {
        String serviceUrl = buildUrl(request, loginPath).orElse(loginPath.toASCIIString());

        String service = getServiceProperties().getServiceParameter();
        if (service == null || service.isEmpty() || service.trim().isEmpty()) {
            // should not happen
            return response.encodeURL("service"); // TODO check this
        } else {
            return response.encodeURL(service);
        }
    }

    @SuppressWarnings("WeakerAccess")
    protected static Optional<String> buildUrl(HttpServletRequest request, URI path) {
        Assert.notNull(request, "request is required; it must not be null");
        if (!path.isAbsolute()) {
            return Optional.of(fromContextPath(request).path(path.toASCIIString()).toUriString());
        }
        return Optional.empty();
    }
}

