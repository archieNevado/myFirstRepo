package com.coremedia.livecontext.web.taglib;

import com.coremedia.blueprint.cae.web.FreemarkerEnvironment;
import com.coremedia.livecontext.ecommerce.link.CommerceLinkTemplateTypes;
import com.coremedia.livecontext.handler.LoginStatusHandler;
import com.coremedia.objectserver.util.RequestServices;
import com.coremedia.objectserver.web.links.LinkFormatter;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides URLs and parameter values for requests to the {@link LoginStatusHandler}.
 */
public class LiveContextLoginFreemarkerFacade {

  /**
   * Builds the url for the status handler to retrieve the actual state (logged in/logged out) of the user.
   *
   * @return an url to the cae handler.
   */
  public String getStatusUrl() {
    return buildLink(LoginStatusHandler.LinkType.STATUS);
  }

  /**
   * Builds the absolute url to the login formular of a commerce system.
   *
   * @return absolute url to a formular of a commerce system.
   */
  public String getLoginFormUrl() {
    return buildLink(CommerceLinkTemplateTypes.LOGIN_URL);
  }

  /**
   * Builds a logout url of a commerce system to logout the current user.
   *
   * @return absolute url to logout the current user.
   */
  public String getLogoutUrl() {
    return buildLink(CommerceLinkTemplateTypes.LOGOUT_URL);
  }

  private static String buildLink(Object bean) {
    HttpServletRequest request = FreemarkerEnvironment.getCurrentRequest();
    LinkFormatter linkFormatter = (LinkFormatter) request.getAttribute(RequestServices.LINK_FORMATTER);
    return linkFormatter.formatLink(bean, null, request, FreemarkerEnvironment.getCurrentResponse(), false);
  }
}
