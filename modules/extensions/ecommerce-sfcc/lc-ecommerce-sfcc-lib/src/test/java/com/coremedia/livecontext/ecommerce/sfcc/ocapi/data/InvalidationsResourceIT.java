package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.InvalidationsResource;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

@TestPropertySource(properties = "livecontext.sfcc.vendorVersion=v99_9")
public class InvalidationsResourceIT extends DataApiResourceTestBase {

  @Autowired
  private InvalidationsResource resource;

  @Test
  @Ignore("Current OCAPI version does not support invalidations.")
  public void testGetInvalidations() {
    Map<String, Object> invalidations = resource.getInvalidations(123L);
    assertNotNull(invalidations);
  }
}
