package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.common.SfccStoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.common.SfccStoreContextBuilder;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.DataApiResourceTestBase;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.ContentAssetDocument;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Locale;
import java.util.Optional;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static java.util.Optional.empty;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class LibrariesResourceIT extends DataApiResourceTestBase {

  private static final String TEST_CONTENT_ID = "testContentId";

  @Inject
  private LibrariesResource resource;

  @Test
  public void putAndGetContentById() {
    if (useBetamaxTapes()) {
      return;
    }

    //create a test content with the current timestamp
    long timeMillis = System.currentTimeMillis();
    String testJson = "{currenttime: " + timeMillis + "}";
    String contentAssetId = getTestContentId();
    Optional<ContentAssetDocument> testContentId = resource.putContentById(contentAssetId, "", "", testJson, storeContext);
    assertThat(testContentId.isPresent()).isTrue();

    //request the same test content and check the timestamp
    Optional<ContentAssetDocument> testContent = resource.getContentById(contentAssetId, storeContext);
    assertThat(testContent.isPresent()).isTrue();
    assertThat(testContent.get().getBody().getValue(storeContext.getLocale()).getMarkup()).isEqualTo(testJson);
  }

  @Test
  public void testDeleteContentById(){
    if (useBetamaxTapes()) {
      return;
    }

    String contentAssetId = getTestContentId();
    Optional<ContentAssetDocument> testContentId = resource.putContentById(contentAssetId, "", "", "{}" , storeContext);
    assertThat(testContentId.isPresent()).isTrue();

    resource.deleteContentById(contentAssetId, storeContext);
    assertThat(resource.getContentById(contentAssetId, storeContext)).isEqualTo(empty());
  }

  @Test
  public void testPatchContentById(){
    if (useBetamaxTapes()) {
      return;
    }

    String contentAssetId = getTestContentId();
    StoreContext storeContextUK = storeContext;
    Optional<ContentAssetDocument> testContentId = resource.putContentById(contentAssetId, "", "", "good morning" , storeContextUK);
    assertThat(testContentId.isPresent()).isTrue();

    SfccStoreContextBuilder storeContextBuilder = SfccStoreContextBuilder.from((SfccStoreContext) storeContextUK);
    Locale localeFR = new Locale("fr", "FR");
    storeContextBuilder.withLocale(localeFR);
    SfccStoreContext storeContextFR = storeContextBuilder.build();

    Optional<ContentAssetDocument> contentAssetDocument = resource.patchContentById(contentAssetId, "", "", "bonjour", storeContextFR);

    String contentUK = contentAssetDocument.get().getBody().getValue(storeContextUK.getLocale()).getMarkup();
    assertThat(contentUK).isEqualTo("good morning");

    String contentFR = contentAssetDocument.get().getBody().getValue(storeContextFR.getLocale()).getMarkup();
    assertThat(contentFR).isEqualTo("bonjour");
  }

  private static String getTestContentId(){
    return TEST_CONTENT_ID + System.currentTimeMillis();
  }
}
