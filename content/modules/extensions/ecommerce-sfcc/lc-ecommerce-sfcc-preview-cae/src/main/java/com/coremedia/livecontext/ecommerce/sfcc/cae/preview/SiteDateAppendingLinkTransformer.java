package com.coremedia.livecontext.ecommerce.sfcc.cae.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.objectserver.web.links.LinkTransformer;
import com.coremedia.objectserver.web.links.ParameterAppendingLinkTransformer;
import edu.umd.cs.findbugs.annotations.NonNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;
import java.util.Optional;

import static com.coremedia.livecontext.ecommerce.sfcc.cae.preview.SiteDateFilter.REQUEST_PARAMETER_SITE_DATE;

public class SiteDateAppendingLinkTransformer implements LinkTransformer {

  private final ParameterAppendingLinkTransformer parameterAppender;

  public SiteDateAppendingLinkTransformer() {
    parameterAppender = new ParameterAppendingLinkTransformer();
    parameterAppender.setParameterName(REQUEST_PARAMETER_SITE_DATE);
  }

  @Override
  public String transform(String source, Object bean, String view, @NonNull HttpServletRequest request,
                          @NonNull HttpServletResponse response, boolean forRedirect) {
    findPreviewDate()
            .map(SiteDateFormatter::format)
            .ifPresent(siteDateText -> request.setAttribute(REQUEST_PARAMETER_SITE_DATE, siteDateText));

    return parameterAppender.transform(source, bean, view, request, response, forRedirect);
  }

  @NonNull
  private static Optional<ZonedDateTime> findPreviewDate() {
    return CurrentCommerceConnection.find()
            .map(CommerceConnection::getStoreContext)
            .flatMap(StoreContext::getPreviewDate);
  }
}
