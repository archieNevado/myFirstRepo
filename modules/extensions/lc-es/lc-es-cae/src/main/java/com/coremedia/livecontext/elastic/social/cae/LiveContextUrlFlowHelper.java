package com.coremedia.livecontext.elastic.social.cae;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Flow helper for the live context.
 */
public class LiveContextUrlFlowHelper {
  private static final Logger LOG = LoggerFactory.getLogger(LiveContextUrlFlowHelper.class);

  private String commerceUrl;

  public String getExternalRedirectUrl(RequestContext context) {
    HttpServletRequest request = getRequest(context);
    if(request.getParameterMap().containsKey("externalRedirect")) {
      try {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
        //double-decode the double-encoded next url
        String nextUrl = URLDecoder.decode(URLDecoder.decode(request.getParameter("nextUrl"), "utf8"), "utf8");
        String host = commerceUrl.replaceAll("//", "");
        builder.host(host);
        builder.path(nextUrl);
        return builder.build().toString();
      } catch (UnsupportedEncodingException e) {
        LOG.error("Error URL decoding 'nextUrl' parameter for registration flow", e);
      }
    }
    return null;
  }

  public void pingCommerce(RequestContext context) {
    CommerceConnection connection = Commerce.getCurrentConnection();
    UserSessionService userSessionService = connection!=null ? connection.getUserSessionService() : null;
    if (userSessionService!=null) {
      userSessionService.pingCommerce(getRequest(context), (HttpServletResponse) context.getExternalContext().getNativeResponse());
    }
  }

  public boolean isCheckoutFlow(RequestContext context) {
    FlowSession activeSession = context.getFlowExecutionContext().getActiveSession();
    return activeSession.getScope().contains("redirectUrl") && activeSession.getScope().get("redirectUrl") != null;
  }

  private static HttpServletRequest getRequest(RequestContext context) {
    return (HttpServletRequest) context.getExternalContext().getNativeRequest();
  }

  @Value("${livecontext.apache.wcs.url}")
  public void setCommerceUrl(String commerceUrl) {
    this.commerceUrl = commerceUrl;
  }
}
