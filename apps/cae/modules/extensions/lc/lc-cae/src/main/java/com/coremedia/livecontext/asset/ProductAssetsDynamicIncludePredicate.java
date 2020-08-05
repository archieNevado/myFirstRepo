package com.coremedia.livecontext.asset;

import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.objectserver.view.RenderNode;
import com.coremedia.objectserver.view.dynamic.DynamicIncludePredicate;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Predicate to determine if a node to render is dynamic include of product assets.
 */
@DefaultAnnotation(NonNull.class)
public class ProductAssetsDynamicIncludePredicate implements DynamicIncludePredicate {

  private static final String VIEW_NAME = "asDynaAssets";

  @Override
  public boolean test(RenderNode input) {
    return input.getBean() instanceof Product && VIEW_NAME.equals(input.getView());
  }
}
