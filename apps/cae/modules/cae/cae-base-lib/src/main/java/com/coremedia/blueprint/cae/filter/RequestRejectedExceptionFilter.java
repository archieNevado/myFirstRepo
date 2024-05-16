package com.coremedia.blueprint.cae.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Catches {@link RequestRejectedException}s that are thrown by the
 * {@link org.springframework.security.web.firewall.StrictHttpFirewall StrictHttpFirewall}
 * and sends a {@link HttpStatus#BAD_REQUEST} response instead of letting Tomcat handle
 * them and pollute the logs with errors.
 * <p>
 * Can be removed when {@code spring-security}  improves its handling for
 * {@link org.springframework.security.web.firewall.StrictHttpFirewall StrictHttpFirewall}
 * rejections. See:
 * <ul>
 * <li><a href="https://github.com/spring-projects/spring-security/issues/5007">Issue 5007: Provide a way to handle RequestRejectedException</a></li>
 * <li><a href="https://github.com/spring-projects/spring-security/issues/7568">Issue 7568: RequestRejectedException should be 400 by default</a></li>
 * </ul>
 */
public class RequestRejectedExceptionFilter extends GenericFilterBean {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    try {
      chain.doFilter(request, response);
    } catch (RequestRejectedException e) {
      String reason = e.getMessage();
      if (LOG.isDebugEnabled()) {
        String requestUrl = UrlUtils.buildFullRequestUrl((HttpServletRequest) request);
        LOG.debug("Rejected request to {}: {}", requestUrl, reason);
      }
      ((HttpServletResponse) response).sendError(HttpStatus.BAD_REQUEST.value(), reason);
    }
  }
}
