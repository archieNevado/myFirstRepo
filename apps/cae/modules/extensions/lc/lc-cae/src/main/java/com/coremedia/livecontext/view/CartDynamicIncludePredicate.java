package com.coremedia.livecontext.view;

import com.coremedia.livecontext.ecommerce.order.Cart;
import com.coremedia.objectserver.view.RenderNode;
import com.coremedia.objectserver.view.dynamic.DynamicIncludePredicate;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Dynamically include {@link Cart} beans if they are displayed as Header items.
 */
@DefaultAnnotation(NonNull.class)
public class CartDynamicIncludePredicate implements DynamicIncludePredicate {

  @Override
  public boolean test(RenderNode input) {
    return input.getBean() instanceof Cart;
  }

}
