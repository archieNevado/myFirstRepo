package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.ecommerce.cae.AbstractCommerceContextInterceptor;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.objectserver.web.links.LinkTransformer;
import com.coremedia.objectserver.web.links.ParameterAppendingLinkTransformer;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.NO_WS_MARKER;

/**
 * LinkTransformer implementation that adds the workspaceId request parameter to page links.
 */
public class WorkspaceIdAppendingLinkTransformer implements LinkTransformer {

  private final ParameterAppendingLinkTransformer parameterAppender;

  private boolean preview;

  public WorkspaceIdAppendingLinkTransformer() {
    parameterAppender = new ParameterAppendingLinkTransformer();
    parameterAppender.setParameterName(AbstractCommerceContextInterceptor.QUERY_PARAMETER_WORKSPACE_ID);
  }

  @PostConstruct
  void initialize() throws Exception {
    parameterAppender.afterPropertiesSet();
  }

  @Override
  public String transform(String source, Object bean, String view, HttpServletRequest request,
                          HttpServletResponse response, boolean forRedirect) {
    if (preview) {
      String workspaceId = findWorkspaceId();
      if (!workspaceId.equals(NO_WS_MARKER)) {
        parameterAppender.setParameterValue(workspaceId);
        return parameterAppender.transform(source, bean, view, request, response, forRedirect);
      }
    }

    return source;
  }

  @Nonnull
  private static String findWorkspaceId() {
    return CurrentCommerceConnection.find()
            .map(CommerceConnection::getStoreContext)
            .map(StoreContext::getWorkspaceId)
            .orElse(NO_WS_MARKER);
  }

  public boolean isPreview() {
    return preview;
  }

  @Value("${cae.is.preview}")
  public void setPreview(boolean preview) {
    this.preview = preview;
  }
}
