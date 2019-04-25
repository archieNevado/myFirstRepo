package com.coremedia.livecontext.web.links;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.objectserver.web.links.TokenResolver;
import org.springframework.core.annotation.Order;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

@Named
@Order(50)
public class StoreContextTokenResolver implements TokenResolver {

  @Override
  public String resolveToken(String token, Object target, HttpServletRequest request) {
    return CurrentCommerceConnection.find()
            .map(CommerceConnection::getStoreContext)
            .map(StoreContext::getReplacements)
            .map(replacements -> replacements.get(token))
            .orElse(null);
  }
}
