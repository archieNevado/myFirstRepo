package com.coremedia.livecontext.ecommerce.sfcc.cae.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.ForVendor;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.objectserver.web.links.LinkTransformer;
import com.coremedia.objectserver.web.links.ParameterAppendingLinkTransformer;
import edu.umd.cs.findbugs.annotations.NonNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@ForVendor("sfcc")
public class UserSegmentAppendingLinkTransformer implements LinkTransformer {

  private static final String REQUEST_PARAMETER_USER_SEGMENTS = "__customerGroup";

  private final ParameterAppendingLinkTransformer parameterAppender;

  public UserSegmentAppendingLinkTransformer() {
    parameterAppender = new ParameterAppendingLinkTransformer();
    parameterAppender.setParameterName(REQUEST_PARAMETER_USER_SEGMENTS);
  }

  @Override
  public String transform(String source, Object bean, String view, HttpServletRequest request,
                          HttpServletResponse response, boolean forRedirect) {
    findUserSegments()
      .map(UserSegmentFormatter::format)
      .ifPresent(userSegments -> request.setAttribute(REQUEST_PARAMETER_USER_SEGMENTS, userSegments));

    return parameterAppender.transform(source, bean, view, request, response, forRedirect);
  }

  @NonNull
  private static Optional<String> findUserSegments() {
    return CurrentCommerceConnection.find()
      .map(CommerceConnection::getStoreContext)
      .flatMap(StoreContext::getUserSegments);
  }

}
