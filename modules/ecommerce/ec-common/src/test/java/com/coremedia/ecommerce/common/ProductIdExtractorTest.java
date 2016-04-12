package com.coremedia.ecommerce.common;

import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ProductIdExtractorTest {

  @Test
  public void testExtractInventoryInfoWithXmp() throws Exception {
    Collection<String> externalIds = ProductIdExtractor.extractProductIds(
            getClass().getResourceAsStream("image-with-xmp-product-reference.jpg"));
    assertNotNull(externalIds);
    assertTrue(externalIds.size() == 2);
  }

  @Test
  public void testExtractInventoryInfoNoData() throws Exception {
    Collection<String> externalIds = ProductIdExtractor.extractProductIds(
            getClass().getResourceAsStream("image-no-xmp.jpg"));
    assertNotNull(externalIds);
    assertTrue(externalIds.size() == 0);
  }

  @Test
  public void testExtractInventoryInfoWrongFormat() throws Exception {
    Collection<String> externalIds = ProductIdExtractor.extractProductIds(
            getClass().getResourceAsStream("no-pic.jpg"));
    assertNotNull(externalIds);
    assertTrue(externalIds.size() == 0);
  }
}
