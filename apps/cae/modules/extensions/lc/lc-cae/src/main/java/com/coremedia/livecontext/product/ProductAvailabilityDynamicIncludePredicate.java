package com.coremedia.livecontext.product;

import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityInfo;
import com.coremedia.objectserver.view.RenderNode;
import com.coremedia.objectserver.view.dynamic.DynamicIncludePredicate;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Predicate to determine if a node to render is an instance of {@link AvailabilityInfo}.
 *
 * @deprecated availability will is no longer part of commerce hub. Any stock related rendering in the should be
 *  calculated corresponding commerce system.
 */
@DefaultAnnotation(NonNull.class)
@Deprecated(forRemoval = true, since = "2007.1")
public class ProductAvailabilityDynamicIncludePredicate implements DynamicIncludePredicate {

  public static final String VIEW_NAME_AVAILABILITY_FRAGMENT = "availabilityFragment";

  @Override
  public boolean test(RenderNode input) {
    return input.getBean() instanceof ProductInSite && VIEW_NAME_AVAILABILITY_FRAGMENT.equals(input.getView());
  }
}
