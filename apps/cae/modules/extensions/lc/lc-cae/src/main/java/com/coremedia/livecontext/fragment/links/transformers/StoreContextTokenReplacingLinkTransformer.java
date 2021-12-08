package com.coremedia.livecontext.fragment.links.transformers;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.objectserver.web.links.LinkTransformer;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Linktransformer to replace tokens in URLs, with values coming from the StoreContext.
 * Tokens must be enclosed with simple curly braces.
 */
@DefaultAnnotation(NonNull.class)
public class StoreContextTokenReplacingLinkTransformer implements LinkTransformer {

  @Override
  public String transform(String source, final Object bean, String view, @NonNull HttpServletRequest request,
                          @NonNull HttpServletResponse response, boolean forRedirect) {

    StoreContext storeContext = CurrentStoreContext.find(request).orElse(null);
    if (storeContext == null) {
      return source;
    }

    return TokenResolverUtils.replaceTokens(source, storeContext.getReplacements(), false, true);
  }
}
