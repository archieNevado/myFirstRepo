package com.coremedia.livecontext.ecommerce.ibm.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.common.contentbeans.CMObject;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceConnectionImpl;
import com.coremedia.livecontext.ecommerce.ibm.login.LoginService;
import com.coremedia.livecontext.ecommerce.ibm.login.WcPreviewToken;
import com.coremedia.livecontext.handler.LiveContextPageHandlerBase;
import com.coremedia.objectserver.web.links.LinkTransformer;
import com.coremedia.objectserver.web.links.ParameterAppendingLinkTransformer;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

/**
 * LinkTransformer implementation that adds the previewToken request parameter to page links.
 */
public class PreviewTokenAppendingLinkTransformer implements LinkTransformer {

  public static final String SCHEME_RELATIVE = "//";
  public static final String HTTP_SCHEME = "http://";
  public static final String HTTPS_SCHEME = "https://";
  public static final String QUERY_PARAMETER_PREVIEW_TOKEN = "previewToken";

  private LoginService loginService;

  private Pattern includePattern;

  private boolean preview;

  @Required
  public void setLoginService(LoginService loginService) {
    this.loginService = loginService;
  }

  public boolean isPreview() {
    return preview;
  }

  @Value("${cae.is.preview}")
  public void setPreview(boolean preview) {
    this.preview = preview;
  }

  @SuppressWarnings("unused")
  public void setIncludeFilter(String includeFilter) {
    includePattern = Pattern.compile(includeFilter);
  }

  @Override
  public String transform(String source, Object bean, String view, HttpServletRequest request,
                          HttpServletResponse response, boolean forRedirect) {
    if (!isPreview()) {
      return source;
    }

    //if parameter is already available in current request append to all links
    if (request.getParameter(QUERY_PARAMETER_PREVIEW_TOKEN) != null) {
      ParameterAppendingLinkTransformer parameterAppender = createParameterAppenderForPreviewToken();
      return parameterAppender.transform(source, bean, view, request, response, forRedirect);
    }

    StoreContext storeContext = CurrentCommerceConnection.find()
            .filter(CommerceConnectionImpl.class::isInstance)
            .map(CommerceConnection::getStoreContext)
            .orElse(null);

    if (storeContext == null) {
      return source;
    }

    if (isInitialStudioRequest(request)) {
      return appendPreviewToken(source, bean, view, request, response, forRedirect, storeContext);
    }

    // On AIX-environments regexp-pattern matching is time consuming.
    // Therefor only check schemes via "startsWith" by default.
    // all external link targets in a commerce context will have a preview token added

    if ((isContentOrCommerceBean(bean) || isProtocolMatch(source)) && isStudioPreviewRequest(request)) {
      return appendPreviewToken(source, bean, view, request, response, forRedirect, storeContext);
    }
    return source;
  }

  private static boolean isProtocolMatch(String source) {
    return source != null && (source.startsWith(SCHEME_RELATIVE) || source.startsWith(HTTP_SCHEME) || source.startsWith(HTTPS_SCHEME));
  }

  private static boolean isContentOrCommerceBean(Object bean) {
    return bean instanceof CMObject || bean instanceof CommerceBean;
  }

  @VisibleForTesting
  boolean isInitialStudioRequest(@Nonnull HttpServletRequest request) {
    return LiveContextPageHandlerBase.isInitialStudioRequest(request);
  }

  @VisibleForTesting
  boolean isStudioPreviewRequest(@Nonnull HttpServletRequest request) {
    return LiveContextPageHandlerBase.isStudioPreviewRequest(request);
  }

  private String appendPreviewToken(String source, Object bean, String view, HttpServletRequest request,
                                    HttpServletResponse response, boolean forRedirect,
                                    StoreContext storeContext) {
    if (includePattern == null || includePattern.matcher(source).matches()) {
      WcPreviewToken wcPreviewToken = loginService.getPreviewToken(storeContext);
      String previewToken = wcPreviewToken != null ? wcPreviewToken.getPreviewToken() : null;
      if (previewToken != null) {
        ParameterAppendingLinkTransformer parameterAppender = createParameterAppenderForPreviewToken();
        parameterAppender.setParameterValue(previewToken);
        return parameterAppender.transform(source, bean, view, request, response, forRedirect);
      }
    }
    return source;
  }

  @Nonnull
  private static ParameterAppendingLinkTransformer createParameterAppenderForPreviewToken() {
    ParameterAppendingLinkTransformer parameterAppendingLinkTransformer = new ParameterAppendingLinkTransformer();
    parameterAppendingLinkTransformer.setParameterName(QUERY_PARAMETER_PREVIEW_TOKEN);
    return parameterAppendingLinkTransformer;
  }

}
