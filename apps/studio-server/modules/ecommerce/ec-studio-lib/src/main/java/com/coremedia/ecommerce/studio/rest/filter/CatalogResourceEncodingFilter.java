package com.coremedia.ecommerce.studio.rest.filter;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UriComponentsBuilder;

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
 * See also {@link com.coremedia.ecommerce.studio.rest.CategoryResourceEncodedId},
 * {@link com.coremedia.ecommerce.studio.rest.ProductResourceEncodedId},
 * {@link com.coremedia.ecommerce.studio.rest.ProductVariantResourceEncodedId}
 */
@DefaultAnnotation(NonNull.class)
public class CatalogResourceEncodingFilter extends GenericFilterBean {
  private static final String NO_WS = "NO_WS/";
  private static final int STR_LENGTH = NO_WS.length();

  private List<RequestMatcher> includeRequestMatchers = List.of();

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    if (isRequestIncluded(request)){
      String requestURI = request.getRequestURI();
      int i = requestURI.indexOf(NO_WS);
      String id = requestURI.substring(i + STR_LENGTH);

      // only process resource urls with special chars
      if (isEncodingNeeded(id)) {
        String redirectUrl = getForwardUrl(request, id);

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(redirectUrl);
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

  private boolean isRequestIncluded(HttpServletRequest request) {
    return includeRequestMatchers.stream().anyMatch(matcher -> matcher.matches(request));
  }

  public List<RequestMatcher> getIncludeRequestMatchers() {
    return includeRequestMatchers;
  }

  public void setIncludeRequestMatchers(List<RequestMatcher> includeRequestMatchers) {
    this.includeRequestMatchers = includeRequestMatchers;
  }
}
