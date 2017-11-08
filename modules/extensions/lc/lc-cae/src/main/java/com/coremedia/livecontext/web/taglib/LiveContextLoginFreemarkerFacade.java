package com.coremedia.livecontext.web.taglib;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.handler.LoginStatusHandler;
import com.coremedia.objectserver.util.RequestServices;
import com.coremedia.objectserver.view.freemarker.FreemarkerUtils;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.function.Function;

/**
 * Provides URLs and parameter values for requests to the {@link LoginStatusHandler}.
 */
public class LiveContextLoginFreemarkerFacade {

  private final Map<String, LiveContextLoginUrlsProvider> urlProviderByVendor;

  public LiveContextLoginFreemarkerFacade(@Nonnull Map<String, LiveContextLoginUrlsProvider> urlProviderByVendor) {
    this.urlProviderByVendor = ImmutableMap.copyOf(urlProviderByVendor);
  }

  /**
   * Builds the url for the status handler to retrieve the actual state (logged in/logged out) of the user.
   *
   * @return an url to the cae handler.
   */
  public String getStatusUrl() {
    String link = buildLink(LoginStatusHandler.LinkType.STATUS);
    return getLiveContextLoginUrlsProvider().transformLoginStatusUrl(link);
  }

  /**
   * Builds the absolute url to the login formular of a commerce system.
   *
   * @return absolute url to a formular of a commerce system.
   */
  public String getLoginFormUrl() {
    return getUrl(LiveContextLoginUrlsProvider::buildLoginFormUrl);
  }

  /**
   * Builds a logout url of a commerce system to logout the current user.
   *
   * @return absolute url to logout the current user.
   */
  public String getLogoutUrl() {
    return getUrl(LiveContextLoginUrlsProvider::buildLogoutUrl);
  }

  private String getUrl(@Nonnull Function<LiveContextLoginUrlsProvider, String> urlProviderFunction) {
    LiveContextLoginUrlsProvider provider = getLiveContextLoginUrlsProvider();
    return urlProviderFunction.apply(provider);
  }

  private LiveContextLoginUrlsProvider getLiveContextLoginUrlsProvider() {
    CommerceConnection connection = CurrentCommerceConnection.get();
    LiveContextLoginUrlsProvider provider = urlProviderByVendor.get(connection.getVendorName());
    if (provider == null) {
      throw new IllegalStateException("No LiveContextLoginUrlsProvider configured for " + connection);
    }
    return provider;
  }

  private static String buildLink(LoginStatusHandler.LinkType bean) {
    HttpServletRequest request = FreemarkerUtils.getCurrentRequest();
    LinkFormatter linkFormatter = (LinkFormatter) request.getAttribute(RequestServices.LINK_FORMATTER);
    return linkFormatter.formatLink(bean, null, request, FreemarkerUtils.getCurrentResponse(), false);
  }
}
