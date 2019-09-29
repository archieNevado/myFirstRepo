package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.DataApiResourceTestBase;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.ContentAssetDocument;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LibrariesResourceTest extends DataApiResourceTestBase {

  private static final String TEST_CONTENT_ID = "testContentId";
  @Autowired
  private LibrariesResource resource;

  @Test
  public void putAndGetContentById() {
    if (useBetamaxTapes()) {
      return;
    }

    //create a test content with the current timestamp
    String testJson = "{currenttime: " + System.currentTimeMillis() + "}";
    Optional<ContentAssetDocument> testContentId = resource.putContentById(TEST_CONTENT_ID, "", "", testJson, storeContext);
    assertTrue(testContentId.isPresent());

    //request the same test content and check the timestamp
    Optional<ContentAssetDocument> testContent = resource.getContentById(TEST_CONTENT_ID, storeContext);
    assertTrue(testContent.isPresent());
    assertEquals(testJson, testContent.get().getBody().getDefaultValue().getMarkup());
  }
}
