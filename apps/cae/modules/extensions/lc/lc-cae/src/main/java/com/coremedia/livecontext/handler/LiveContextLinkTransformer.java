package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.objectserver.web.links.LinkTransformer;
import edu.umd.cs.findbugs.annotations.NonNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LiveContextLinkTransformer implements LinkTransformer {

  @Override
  public String transform(String source, Object bean, String view, @NonNull HttpServletRequest request,
                          @NonNull HttpServletResponse response, boolean forRedirect) {
    return CurrentCommerceConnection.find()
            .flatMap(connection -> connection.getServiceForVendor(LinkTransformer.class))
            .map(linkTransformer -> linkTransformer.transform(source, bean, view, request, response, forRedirect))
            .orElse(source);
  }
}
