package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.objectserver.web.links.LinkTransformer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LiveContextLinkTransformer implements LinkTransformer{

  @Override
  public String transform(String source, Object bean, String view, HttpServletRequest request, HttpServletResponse response, boolean forRedirect) {
    return CurrentCommerceConnection.find()
      .flatMap(c -> c.getServiceForVendor(LinkTransformer.class))
      .map(l -> l.transform(source, bean, view, request, response, forRedirect))
      .orElse(source);
  }
}
