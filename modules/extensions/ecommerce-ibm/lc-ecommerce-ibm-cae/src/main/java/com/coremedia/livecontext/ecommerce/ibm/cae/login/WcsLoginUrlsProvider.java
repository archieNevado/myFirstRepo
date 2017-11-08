package com.coremedia.livecontext.ecommerce.ibm.cae.login;

import com.coremedia.blueprint.base.links.UriConstants;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommercePropertyHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommercePropertyProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.catalog.CatalogServiceImpl;
import com.coremedia.livecontext.handler.NextURLRedirectHandler;
import com.coremedia.livecontext.web.taglib.LiveContextLoginUrlsProvider;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.coremedia.objectserver.web.links.TokenResolverHelper;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriUtils;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.coremedia.livecontext.handler.LiveContextPageHandlerBase.isStudioPreviewRequest;
import static java.util.Objects.requireNonNull;

/**
 * Provides URLs to WCS Login Form and logout handlers.
 */
public class WcsLoginUrlsProvider implements LiveContextLoginUrlsProvider {

  private static final String QUERY_PARAMETER_PREVIEW_TOKEN = "previewToken";

  private final CommercePropertyProvider previewTokenProvider;
  private final LinkFormatter linkFormatter;

  private String defaultStoreFrontUrl;
  private String previewStoreFrontUrl;
  private String loginFormUrlTemplate;
  private String logoutUrlTemplate;

  public WcsLoginUrlsProvider(@Nonnull CommercePropertyProvider previewTokenProvider,
                              @Nonnull LinkFormatter linkFormatter) {
    this.previewTokenProvider = requireNonNull(previewTokenProvider);
    this.linkFormatter = requireNonNull(linkFormatter);
  }

  @Required
  public void setDefaultStoreFrontUrl(@Nonnull String defaultStoreFrontUrl) {
    this.defaultStoreFrontUrl = requireNonNull(defaultStoreFrontUrl);
  }

  @Required
  public void setPreviewStoreFrontUrl(@Nonnull String previewStoreFrontUrl) {
    this.previewStoreFrontUrl = requireNonNull(previewStoreFrontUrl);
  }

  @Required
  public void setLoginFormUrlTemplate(@Nonnull String loginFormUrlTemplate) {
    this.loginFormUrlTemplate = requireNonNull(loginFormUrlTemplate);
  }

  @Required
  public void setLogoutUrlTemplate(@Nonnull String logoutUrlTemplate) {
    this.logoutUrlTemplate = requireNonNull(logoutUrlTemplate);
  }

  @Override
  public String buildLoginFormUrl() {
    return buildUrl(loginFormUrlTemplate);
  }

  @Override
  public String buildLogoutUrl() {
    return buildUrl(logoutUrlTemplate);
  }

  @Override
  public String transformLoginStatusUrl(String url) {
    return appendPreviewToken(url);
  }

  private String buildUrl(String urlTemplate) {
    CommerceConnection connection = CurrentCommerceConnection.get();
    StoreContext storeContext = connection.getStoreContext();
    CatalogServiceImpl catalogService = getCatalogService(connection);

    String commerceTokensReplacedUrl = CommercePropertyHelper.replaceTokens(urlTemplate, storeContext);
    String nexturl = buildNextUrl();
    Map<String, ?> parametersMap = ImmutableMap.of(
            "langId", catalogService.getLanguageId(storeContext.getLocale()),
            "storeId", storeContext.getStoreId(),
            "catalogId", storeContext.getCatalogId(),
            "nexturl", nexturl
    );
    String relativeUrl = TokenResolverHelper.replaceTokens(commerceTokensReplacedUrl, parametersMap, false, false);
    String storeFrontUrl = isStudioPreviewRequest() ? previewStoreFrontUrl : defaultStoreFrontUrl;

    String result = concatUrls(storeFrontUrl, relativeUrl);
    return appendPreviewToken(result);
  }

  @Nonnull
  private String buildNextUrl() {
    ServletRequestAttributes requestAttributes = requestAttributes();
    HttpServletRequest request = requestAttributes.getRequest();
    HttpServletResponse response = requestAttributes.getResponse();
    request.setAttribute(UriConstants.Links.ABSOLUTE_URI_KEY, true);
    return linkFormatter.formatLink(NextURLRedirectHandler.LinkTypeNextUrl.NEXTURL, null,
                                    request, response, false);
  }

  private ServletRequestAttributes requestAttributes() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    return (ServletRequestAttributes) requestAttributes;
  }

  private String appendPreviewToken(String url) {
    if (!isStudioPreviewRequest()) {
      return url;
    }
    String previewToken = (String) previewTokenProvider.provideValue(new HashMap<>());
    if (previewToken == null) {
      return url;
    }

    StringBuilder sb = new StringBuilder(url);
    if (url.indexOf('?') > -1 ) {
      // parameters already available
      sb.append('&');
    } else {
      // no parameter available yet
      sb.append('?');
    }
    try {
      sb.append(QUERY_PARAMETER_PREVIEW_TOKEN);
      sb.append('=');
      sb.append(UriUtils.encodeQueryParam(previewToken, "UTF-8"));
    } catch (UnsupportedEncodingException e) { //NOSONAR - ignore this exception.
    }
    return sb.toString();
  }

  private static String concatUrls(String baseUrl, String relativeUrl) {
    if (!baseUrl.endsWith("/")) {
      baseUrl += "/";
    }
    String relativeUrlPart = relativeUrl;
    if (relativeUrlPart.startsWith("/")) {
      relativeUrlPart = relativeUrlPart.substring(1);
    }
    return baseUrl + relativeUrlPart;
  }

  private static CatalogServiceImpl getCatalogService(CommerceConnection connection) {
    CatalogService cs = connection.getCatalogService();
    if (!(cs instanceof CatalogServiceImpl)) {
      throw new IllegalStateException("No CatalogService found for connection " + connection);
    }
    return (CatalogServiceImpl) cs;
  }
}
