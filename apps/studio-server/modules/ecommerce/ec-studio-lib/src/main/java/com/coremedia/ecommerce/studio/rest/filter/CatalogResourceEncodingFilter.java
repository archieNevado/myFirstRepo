package com.coremedia.ecommerce.studio.rest.filter;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static com.coremedia.ecommerce.studio.rest.AbstractCatalogResource.QUERY_ID;

/**
 * Filter detects and forwards catalog rest resource requests with special IDs containing "/".
 * The ID is then passed as query parameter instead of path parameter.
 *
 * See also {@link com.coremedia.ecommerce.studio.rest.CommerceBeanResourceWithEncodedId}
 */
@DefaultAnnotation(NonNull.class)
public class CatalogResourceEncodingFilter implements Filter {

  private static final String LIVECONTEXT_API_REQUEST_PATTERN = "api/livecontext/";

  private final List<RequestMatcher> includeRequestMatchers;

  public CatalogResourceEncodingFilter(List<RequestMatcher> includeRequestMatchers) {
    this.includeRequestMatchers = includeRequestMatchers;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    if (isRequestIncluded(request) && request.getRequestURI().contains(LIVECONTEXT_API_REQUEST_PATTERN)) {
      String id = extractIdFromRequestUri(request.getRequestURI());
      // only process resource urls with special chars
      if (isEncodingNeeded(id)) {
        String redirectUrl = getForwardUrl(request, id);
        RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(redirectUrl);
        dispatcher.forward(servletRequest, servletResponse);
        return;
      }
    }
    filterChain.doFilter(servletRequest, servletResponse);
  }

  String getForwardUrl(@NonNull HttpServletRequest request, @NonNull String id) {
    String redirectUri = request.getRequestURI()
            .replace("/" + id, "");
    UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString(redirectUri);
    ucb.queryParam(QUERY_ID, encode(id));
    return ucb.build().toString();
  }

  private static String encode(String input) {
    return input.replace("+", "%2B");
  }

  private static boolean isEncodingNeeded(String input) {
    return input.contains("/");
  }

  private static String extractIdFromRequestUri(String requestUri) {
    String requestUriTail = requestUri.substring(requestUri.indexOf(LIVECONTEXT_API_REQUEST_PATTERN) + LIVECONTEXT_API_REQUEST_PATTERN.length());
    int idStartIndex = StringUtils.ordinalIndexOf(requestUriTail, "/", 3) + 1;
    return requestUriTail.substring(idStartIndex);
  }

  private boolean isRequestIncluded(HttpServletRequest request) {
    return includeRequestMatchers.stream().anyMatch(matcher -> matcher.matches(request));
  }
}
