package com.coremedia.livecontext.product;

import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.objectserver.view.RenderNode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link com.coremedia.livecontext.product.ProductAvailabilityDynamicIncludePredicate}.
 */
public class ProductAvailabilityDynamicIncludePredicateTest {

  ProductAvailabilityDynamicIncludePredicate testling;

  @Before
  public void setUp() {
    testling = new ProductAvailabilityDynamicIncludePredicate();
  }

  @Test
  public void testInputNotInstanceOfProductInSite() {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(new Object());
    assertFalse(testling.test(input));
  }

  @Test
  public void testInputProductInSiteNoView() {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(mock(ProductInSite.class));
    when(input.getView()).thenReturn(null);
    assertFalse(testling.test(input));
  }

  @Test
  public void testInputProductInSiteViewNotMatching() {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(mock(ProductInSite.class));
    when(input.getView()).thenReturn("i_do_not_match");
    assertFalse(testling.test(input));
  }

  @Test
  public void testInputProductInSiteViewMatches() {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(mock(ProductInSite.class));
    when(input.getView()).thenReturn(ProductAvailabilityDynamicIncludePredicate.VIEW_NAME_AVAILABILITY_FRAGMENT);
    assertTrue(testling.test(input));
  }

  @Test
  public void testInputNotProductInSiteViewMatching() {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(new Object());
    when(input.getView()).thenReturn(ProductAvailabilityDynamicIncludePredicate.VIEW_NAME_AVAILABILITY_FRAGMENT);
    assertFalse(testling.test(input));
  }
}
