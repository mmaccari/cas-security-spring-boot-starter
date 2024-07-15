package com.kakawait.spring.security.cas.web;

import com.kakawait.spring.security.cas.web.authentication.CasLogoutSuccessHandler;
import org.apereo.cas.client.util.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromContextPath;

/**
 * @author Thibaud Leprêtre
 */
public class RequestAwareCasAuthenticationEntryPoint extends CasAuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestAwareCasAuthenticationEntryPoint.class);

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

    // Convenience method copied from CommonUtils from jasig (older version)

    public static String constructServiceUrl(HttpServletRequest request, HttpServletResponse response, String service, String serverNames, String serviceParameterName, String artifactParameterName, boolean encode) {
        if (isNotBlank(service)) {
            return encode ? response.encodeURL(service) : service;
        } else {
            String serverName = findMatchingServerName(request, serverNames);
            URIBuilder originalRequestUrl = new URIBuilder(request.getRequestURL().toString(), encode);
            originalRequestUrl.setParameters(request.getQueryString());
            URIBuilder builder;
            if (!serverName.startsWith("https://") && !serverName.startsWith("http://")) {
                String scheme = request.isSecure() ? "https://" : "http://";
                builder = new URIBuilder(scheme + serverName, encode);
            } else {
                builder = new URIBuilder(serverName, encode);
            }

            if (builder.getPort() == -1 && !requestIsOnStandardPort(request)) {
                builder.setPort(request.getServerPort());
            }

            builder.setEncodedPath(builder.getEncodedPath() + request.getRequestURI());
            List<String> serviceParameterNames = Arrays.asList(serviceParameterName.split(","));
            if (!serviceParameterNames.isEmpty() && !originalRequestUrl.getQueryParams().isEmpty()) {
                Iterator var11 = originalRequestUrl.getQueryParams().iterator();

                label72:
                while(true) {
                    while(true) {
                        URIBuilder.BasicNameValuePair pair;
                        String name;
                        do {
                            do {
                                if (!var11.hasNext()) {
                                    break label72;
                                }

                                pair = (URIBuilder.BasicNameValuePair)var11.next();
                                name = pair.name();
                            } while(name.equals(artifactParameterName));
                        } while(serviceParameterNames.contains(name));

                        if (!name.contains("&") && !name.contains("=")) {
                            builder.addParameter(name, pair.value());
                        } else {
                            URIBuilder encodedParamBuilder = new URIBuilder();
                            encodedParamBuilder.setParameters(name);
                            Iterator var15 = encodedParamBuilder.getQueryParams().iterator();

                            while(var15.hasNext()) {
                                URIBuilder.BasicNameValuePair pair2 = (URIBuilder.BasicNameValuePair)var15.next();
                                String name2 = pair2.name();
                                if (!name2.equals(artifactParameterName) && !serviceParameterNames.contains(name2)) {
                                    builder.addParameter(name2, pair2.value());
                                }
                            }
                        }
                    }
                }
            }

            String result = builder.toString();
            String returnValue = encode ? response.encodeURL(result) : result;
            LOGGER.debug("serviceUrl generated: {}", returnValue);
            return returnValue;
        }
    }

    protected static String findMatchingServerName(HttpServletRequest request, String serverName) {
        String[] serverNames = serverName.split(" ");
        if (serverNames.length != 0 && serverNames.length != 1) {
            String host = request.getHeader("Host");
            String xHost = request.getHeader("X-Forwarded-Host");
            String comparisonHost = xHost != null ? xHost : host;
            if (comparisonHost == null) {
                return serverName;
            } else {
                String[] var6 = serverNames;
                int var7 = serverNames.length;

                for(int var8 = 0; var8 < var7; ++var8) {
                    String server = var6[var8];
                    String lowerCaseServer = server.toLowerCase();
                    if (lowerCaseServer.contains(comparisonHost)) {
                        return server;
                    }
                }

                return serverNames[0];
            }
        } else {
            return serverName;
        }
    }

}
