package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents.CustomObjectDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.resources.CustomObjectsResource;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ShopApiTestConfiguration.class)
public class CustomObjectsResourceIT extends ShopApiResourceTestBase {

  @Autowired
  private CustomObjectsResource resource;

  @Test
  @Ignore("custom objects not used at the moment")
  public void testSampleCustomObject() {
    CustomObjectDocument doc = resource.getCustomObject("sample_object_type", "someKey");
    assertEquals("sample_object_type", doc.getObjectType());
    assertEquals("key_attribute", doc.getKeyProperty());
    assertEquals("test", doc.get("c_foo"));
  }
}
