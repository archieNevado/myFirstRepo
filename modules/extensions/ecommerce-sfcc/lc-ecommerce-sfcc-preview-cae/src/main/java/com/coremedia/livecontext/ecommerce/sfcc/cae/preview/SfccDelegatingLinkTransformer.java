package com.coremedia.livecontext.ecommerce.sfcc.cae.preview;

import com.coremedia.livecontext.ecommerce.common.ForVendor;
import com.coremedia.objectserver.web.links.LinkTransformer;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Delegating link transformer for sfcc preview cae link transformers.
 */
@ForVendor("sfcc")
@DefaultAnnotation(NonNull.class)
public class SfccDelegatingLinkTransformer implements LinkTransformer {

  private List<LinkTransformer> delegates;

  public SfccDelegatingLinkTransformer(List<LinkTransformer> delegates) {
    this.delegates = delegates;
  }

  @Nullable
  @Override
  public String transform(@Nullable String source, @Nullable Object bean, @Nullable String view,
                          @NonNull HttpServletRequest request, @NonNull HttpServletResponse response, boolean forRedirect) {

    String transformedLink = source;
    for (LinkTransformer delegate : delegates) {
      transformedLink = delegate.transform(transformedLink, bean, view, request, response, forRedirect);
    }

    return transformedLink;
  }
}
