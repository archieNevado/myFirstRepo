package com.coremedia.blueprint.uitesting.lc;


import com.coremedia.cap.common.CapObject;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructBuilder;
import com.coremedia.cap.struct.StructService;
import com.coremedia.cap.undoc.common.CapConnection;
import com.coremedia.cms.integration.test.util.ContentBuilder;
import com.coremedia.uitesting.doctypes.CMExternalChannel;
import com.coremedia.uitesting.doctypes.CMExternalProduct;
import com.coremedia.uitesting.uapi.helper.ContentPropertyUtils;
import com.google.common.base.Preconditions;
import net.joala.bdd.reference.Reference;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Named;
import javax.inject.Provider;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.cms.integration.test.util.CapConsumers.checkIn;
import static com.coremedia.cms.integration.test.util.CapConsumers.ensureCheckOut;
import static com.coremedia.configuration.cms.integration.test.util.TestUtilConfiguration.CMS_INTEGRATION_TEST_DEFAULT_QUALIFIER;

@Named
@SuppressWarnings("squid:S1160")
public class FragmentTestHelper {

  private final CapConnection capConnection;

  private static final String CMEXTERNALPAGE_NAME = "CMExternalPage";
  private static final String CMEXTERNALPAGE_EXTERNAL_ID = "externalId";

  /**
   * Required as we don't want to apply XSS changes to existing content.
   */
  private final Provider<? extends ContentBuilder> defaultContentBuilderProvider;
  private final Provider<? extends ContentBuilder> contentBuilderProvider;

  private final ContentRepository contentRepository;

  private final ContentPropertyUtils contentPropertyUtils;

  private final StructService structService;

  @Value("${apache.preview.helios.main.url}/preview/servlet/preview")
  private String previewServletUrl;

  public FragmentTestHelper(CapConnection capConnection,
                            @Qualifier(CMS_INTEGRATION_TEST_DEFAULT_QUALIFIER) Provider<? extends ContentBuilder> defaultContentBuilderProvider,
                            Provider<? extends ContentBuilder> contentBuilderProvider,
                            ContentRepository contentRepository,
                            ContentPropertyUtils contentPropertyUtils,
                            StructService structService) {
    this.capConnection = capConnection;
    this.defaultContentBuilderProvider = defaultContentBuilderProvider;
    this.contentBuilderProvider = contentBuilderProvider;
    this.contentRepository = contentRepository;
    this.contentPropertyUtils = contentPropertyUtils;
    this.structService = structService;
  }

  public void loadNewAugmentedCategory(Reference<? super Content> augmentedCategory, String categoryId, String copyFrom, String copyToFolder)
          throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    //take an existing augmented page and ...
    Content sourceExternalPage = contentRepository.getChild(copyFrom);
    //... copy it to the test folder
    Content auroraFolder = contentRepository.getChild(copyToFolder);
    Content testFolder = contentBuilderProvider.get()
            .folderType()
            .parent(auroraFolder)
            .build();

    // Attention: After the change to non-linked external channels its a bit tricky. The
    // hierarchy is now only determined by the external hierarchy plus the results from the
    // augmentation service that retrieves a corresponding external page document).
    // Copying a relatively "high located" external channel (with a smaller id like apparel)
    // and reset the externalId to another existing category (grocery) seems sufficient to trick
    // the augmentation service, so it returns the new copied instance instead of the other.

    Content externalPage = sourceExternalPage.copyTo(testFolder);
    augmentedCategory.set(externalPage);

    // if sourceExternalPage id checked out, the copy is checked out too
    if (!externalPage.isCheckedOut()) {
      externalPage.checkOut();
    }
    //... set the category to grocery.
    externalPage.set(CMExternalChannel.P_EXTERNAL_ID, categoryId);

    externalPage.checkIn();

    waitForPreviewCaeToSynchronize(externalPage);
  }

  public void loadNewAugmentedProduct(Reference<? super Content> augmentedProductReference, String productId, String baseFolder)
          throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    Content auroraFolder = contentRepository.getChild(baseFolder);
    Content testFolder = contentBuilderProvider.get()
            .folderType()
            .parent(auroraFolder)
            .build();

    Map<String, String> contentProperties = new HashMap<>();
    contentProperties.put(CMExternalProduct.P_EXTERNAL_ID, productId);
    // augment the product by creating an external product (augmented product) for the given external id as admin
    Content augmentedProduct = contentBuilderProvider.get()
            .contentType(toContentType(CMExternalProduct.NAME))
            .parent(testFolder)
            .named("augmentedProduct")
            .properties(contentProperties)
            .postProcess(checkIn())
            .build();
    augmentedProductReference.set(augmentedProduct);
    waitForPreviewCaeToSynchronize(augmentedProduct);
  }

  public void loadNewAugmentedPage(Reference<? super Content> augmentedPageReference, String externalId, Content parentChannel, String baseFolder)
          throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    Content auroraFolder = contentRepository.getChild(baseFolder);
    Content testFolder = contentBuilderProvider.get()
            .folderType()
            .parent(auroraFolder)
            .build();

    Map<String, String> contentProperties = new HashMap<>();
    contentProperties.put(CMEXTERNALPAGE_EXTERNAL_ID, externalId);
    // augment the product by creating an external product (augmented product) for the given external id as admin
    Content augmentedPage = contentBuilderProvider.get()
            .contentType(toContentType(CMEXTERNALPAGE_NAME))
            .parent(testFolder)
            .named("augmentedPage")
            .properties(contentProperties)
            .postProcess(checkIn())
            .build();

    //link to parent channel
    //important: do not delete old children when adding the new augmentedPage, otherwise parallel tests might fail
    List<Content> children = new ArrayList<>(parentChannel.getLinks(CMExternalChannel.P_CHILDREN));
    children.add(augmentedPage);
    defaultContentBuilderProvider.get()
            .content(parentChannel)
            .postProcess(ensureCheckOut())
            .property(CMExternalChannel.P_CHILDREN, children)
            .postProcess(checkIn())
            .build();

    augmentedPageReference.set(augmentedPage);
    waitForPreviewCaeToSynchronize(augmentedPage);
  }

  public void deleteAugmentation(Reference<? extends Content> externalContentReference) throws NoSuchAlgorithmException,
          KeyStoreException, KeyManagementException {
    // remove the augmented page from the children list of the fragment root page
    Content augmentingContent = externalContentReference.get();
    if (augmentingContent.isCheckedOut()) {
      augmentingContent.checkIn();
    }
    augmentingContent.delete();

    waitForPreviewCaeToSynchronize(augmentingContent);
  }

  /**
   * Perform blocking request so that CAE must synchronize with our CAP timestamp
   */
  private void waitForPreviewCaeToSynchronize(CapObject externalPage) throws KeyStoreException, NoSuchAlgorithmException,
          KeyManagementException {
    long currentSeqNo = ((CapConnection) contentRepository.getConnection()).getLatestContentEventSequenceNumber();
    String currentPreviewUrl = previewServletUrl + "?id=" + externalPage.getId() + "&contentTimestamp=" + currentSeqNo;
    SSLConnectionSocketFactory factory = getSslConnectionSocketFactory();
    try (CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(factory).build(); CloseableHttpResponse response = execute(httpClient, currentPreviewUrl)) {
      StatusLine statusLine = response.getStatusLine();
      // we don't care what the preview actually tells us - all it has to do is wait!
      Preconditions.checkArgument(null != statusLine);
    } catch (URISyntaxException | IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public static SSLConnectionSocketFactory getSslConnectionSocketFactory() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    SSLContextBuilder builder = new SSLContextBuilder();
    builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
    return new SSLConnectionSocketFactory(builder.build(), new NoopHostnameVerifier());
  }

  @SuppressWarnings("squid:S00117")
  public void linkContentToPageGrid(Content contentWithPageGrid, String propertyName, Content layout, Content headerPlacement, Content teasableToBeLinked) {
    contentPropertyUtils.setProperty(contentWithPageGrid,propertyName, createPlacementsStruct(layout, headerPlacement, teasableToBeLinked));
  }

  public Struct createPlacementsStruct(Content layout, Content headerPlacement, Content teasableToBeLinked) {
    StructBuilder structBuilder = structService.createStructBuilder();
    StructBuilder placements2 = structService.createStructBuilder();
    StructBuilder placements = structService.createStructBuilder();
    StructBuilder placements_998 = structService.createStructBuilder();

    placements_998.declareBoolean("locked", false);
    placements_998.declareLink("section", toContentType("CMSymbol"), headerPlacement);
    List<Content> links = new ArrayList<>();
    links.add(teasableToBeLinked);
    placements_998.declareLinks("items", toContentType("CMLinkable"), links);

    placements.declareStruct("998", placements_998.build());
    if (layout != null) {
      placements2.declareLink("layout", toContentType("CMSettings"), layout);
    }
    placements2.declareStruct("placements", placements.build());
    structBuilder.declareStruct("placements_2", placements2.build());
    return structBuilder.build();
  }

  private static CloseableHttpResponse execute(CloseableHttpClient httpClient, String url) throws IOException, URISyntaxException {
    HttpGet request = new HttpGet();
    request.setURI(new URI(url));
    return httpClient.execute(request);
  }

  private ContentType toContentType(String contentType) {
    return capConnection.getContentRepository().getContentType(contentType);
  }


}
