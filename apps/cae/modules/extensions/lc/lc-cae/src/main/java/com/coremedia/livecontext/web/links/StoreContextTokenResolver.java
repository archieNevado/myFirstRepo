package com.coremedia.livecontext.web.links;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.objectserver.web.links.TokenResolver;
import org.springframework.core.annotation.Order;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 * @deprecated use {@link org.springframework.web.util.UriComponentsBuilder} instead to resolve link variables
 */
@Named
@Order(50)
@Deprecated(forRemoval = true, since = "2104.1")
public class StoreContextTokenResolver implements TokenResolver {

  @Override
  public String resolveToken(String token, Object target, HttpServletRequest request) {
    return CurrentStoreContext.find()
            .map(StoreContext::getReplacements)
            .map(replacements -> replacements.get(token))
            .orElse(null);
  }
}
