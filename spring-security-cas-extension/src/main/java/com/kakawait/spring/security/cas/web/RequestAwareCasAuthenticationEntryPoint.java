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

        // Use a reworked simplified version of org.jasig.cas.client.util.CommonUtils.constructServiceUrl which is no longer used
        var returnValue = RequestAwareCasAuthenticationEntryPoint.constructServiceUrl(null, response, serviceUrl, null,
                getServiceProperties().getServiceParameter(), getServiceProperties().getArtifactParameter(), true);

        // Logging of returValue can be inserted here for debugging purposes

        return returnValue;
    }

    @SuppressWarnings("WeakerAccess")
    protected static Optional<String> buildUrl(HttpServletRequest request, URI path) {
        Assert.notNull(request, "request is required; it must not be null");
        if (!path.isAbsolute()) {
            return Optional.of(fromContextPath(request).path(path.toASCIIString()).toUriString());
        }
        return Optional.empty();
    }

    // Dirty hack!!!
    // Convenience method copied from org.jasig.cas.client.util.CommonUtils.constructServiceUrl(...)
    //   <dependency>
    //      <groupId>org.jasig.cas.client</groupId>
    //      <artifactId>cas-client-core</artifactId>
    //      <version>3.6.4</version>
    //  </dependency>
    //
    // Remark: request and serverNames are always null and encode is always true in our calls
    // Moreover we only use https and the service is never blank
    // Therefor we removed the unnecessary logic from the original jasig code
    // which leaves us with:
    //   response.encodeURL(service)
    private static String constructServiceUrl(HttpServletRequest request, HttpServletResponse response, String service, String serverNames, String serviceParameterName, String artifactParameterName, boolean encode) {
        //if (isNotBlank(service)) {
        return /*encode ? */ response.encodeURL(service) /* : service */;
            /*
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
                                name = pair.getName();
                            } while(name.equals(artifactParameterName));
                        } while(serviceParameterNames.contains(name));

                        if (!name.contains("&") && !name.contains("=")) {
                            builder.addParameter(name, pair.getValue());
                        } else {
                            URIBuilder encodedParamBuilder = new URIBuilder();
                            encodedParamBuilder.setParameters(name);
                            Iterator var15 = encodedParamBuilder.getQueryParams().iterator();

                            while(var15.hasNext()) {
                                URIBuilder.BasicNameValuePair pair2 = (URIBuilder.BasicNameValuePair)var15.next();
                                String name2 = pair2.getName();
                                if (!name2.equals(artifactParameterName) && !serviceParameterNames.contains(name2)) {
                                    builder.addParameter(name2, pair2.getValue());
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
        */
    }

}
