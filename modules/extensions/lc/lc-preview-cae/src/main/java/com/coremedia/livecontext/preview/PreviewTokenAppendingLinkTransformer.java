package com.coremedia.livecontext.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.common.contentbeans.CMObject;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommercePropertyProvider;
import com.coremedia.livecontext.handler.LiveContextPageHandlerBase;
import com.coremedia.objectserver.web.links.LinkTransformer;
import com.coremedia.objectserver.web.links.ParameterAppendingLinkTransformer;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * LinkTransformer implementation that adds the previewToken request parameter to page links.
 */
public class PreviewTokenAppendingLinkTransformer implements LinkTransformer {

  public static final String SCHEME_RELATIVE = "//";
  public static final String HTTP_SCHEME = "http://";
  public static final String HTTPS_SCHEME = "https://";
  public static final String QUERY_PARAMETER_P13N_TEST = "p13n_test";
  public static final String QUERY_PARAMETER_PREVIEW_TOKEN = "previewToken";

  private final ParameterAppendingLinkTransformer parameterAppender;

  private CommercePropertyProvider previewTokenProvider;

  private Pattern includePattern;

  private boolean preview;

  @Required
  public void setPreviewTokenProvider(CommercePropertyProvider previewTokenProvider) {
    this.previewTokenProvider = previewTokenProvider;
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

  public PreviewTokenAppendingLinkTransformer() {
    parameterAppender = new ParameterAppendingLinkTransformer();
    parameterAppender.setParameterName(QUERY_PARAMETER_PREVIEW_TOKEN);
  }

  @PostConstruct
  void initialize() throws Exception {
    parameterAppender.afterPropertiesSet();
  }

  @Override
  public String transform(String source, Object bean, String view, HttpServletRequest request,
                          HttpServletResponse response, boolean forRedirect) {
    if (!isPreview()) {
      return source;
    }

    //if parameter is already available in current request append to all links
    if (request.getParameter(QUERY_PARAMETER_PREVIEW_TOKEN) != null) {
      return parameterAppender.transform(source, bean, view, request, response, forRedirect);
    }

    CommerceConnection commerceConnection = CurrentCommerceConnection.find().orElse(null);
    if (!isStoreContextAvailable(commerceConnection)) {
      return source;
    }

    if (!"IBM".equals(commerceConnection.getVendorName())) {
      return source;
    }

    if (isInitialStudioRequest()) {
      return appendPreviewToken(source, bean, view, request, response, forRedirect);
    }

    // On AIX-environments regexp-pattern matching is time consuming.
    // Therefor only check schemes via "startsWith" by default.
    // all external link targets in a commerce context will have a preview token added

    if ((bean instanceof CMObject || bean instanceof CommerceBean ||
         (source != null && (source.startsWith(SCHEME_RELATIVE) || source.startsWith(HTTP_SCHEME) || source.startsWith(HTTPS_SCHEME))))
        && isStudioPreviewRequest()) {//TODO: mbi LinkTransformer shall only be processed for ibm requests
      return appendPreviewToken(source, bean, view, request, response, forRedirect);
    }
    return source;
  }

  @VisibleForTesting
  boolean isInitialStudioRequest() {
    return LiveContextPageHandlerBase.isInitialStudioRequest();
  }

  @VisibleForTesting
  boolean isStudioPreviewRequest() {
    return LiveContextPageHandlerBase.isStudioPreviewRequest();
  }

  private String appendPreviewToken(String source, Object bean, String view, HttpServletRequest request,
                                    HttpServletResponse response, boolean forRedirect) {
    if (includePattern == null || includePattern.matcher(source).matches()) {
      String previewToken = (String) previewTokenProvider.provideValue(new HashMap<>());
      if (previewToken != null) {
        parameterAppender.setParameterValue(previewToken);
        return parameterAppender.transform(source, bean, view, request, response, forRedirect);
      }
    }
    return source;
  }

  private static boolean isStoreContextAvailable(CommerceConnection commerceConnection) {
    return commerceConnection != null && commerceConnection.getStoreContext() != null;
  }
}
