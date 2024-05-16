package com.coremedia.livecontext.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.link.LinkService;
import com.coremedia.objectserver.web.links.LinkTransformer;
import com.coremedia.objectserver.web.links.ParameterAppendingLinkTransformer;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * LinkTransformer implementation that adds preview request parameters to links.
 */
public class PreviewParametersAppendingLinkTransformer implements LinkTransformer {

  private DeliveryConfigurationProperties deliveryConfigurationProperties;

  @Autowired
  public void setDeliveryConfigurationProperties(DeliveryConfigurationProperties deliveryConfigurationProperties) {
    this.deliveryConfigurationProperties = deliveryConfigurationProperties;
  }

  @Override
  public String transform(String source, Object bean, String view, @NonNull HttpServletRequest request,
                          @NonNull HttpServletResponse response, boolean forRedirect) {
    if (!deliveryConfigurationProperties.isPreviewMode()) {
      return source;
    }

    var previewParameterNames = CurrentStoreContext.find(request)
            .map(StoreContext::getConnection)
            .flatMap(CommerceConnection::getLinkService)
            .map(LinkService::getPreviewParameterNames)
            .orElseGet(List::of);

    String transformed = source;
    for (String parameterName : previewParameterNames) {
      transformed = appendParameter(transformed, parameterName, bean, view, request, response, forRedirect);
    }

    return transformed;
  }

  private static String appendParameter(String source, String parameterName, Object bean, String view,
                                        @NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                        boolean forRedirect) {
    ParameterAppendingLinkTransformer parameterAppendingLinkTransformer = new ParameterAppendingLinkTransformer();
    parameterAppendingLinkTransformer.setParameterName(parameterName);
    return parameterAppendingLinkTransformer.transform(source, bean, view, request, response, forRedirect);
  }
}
