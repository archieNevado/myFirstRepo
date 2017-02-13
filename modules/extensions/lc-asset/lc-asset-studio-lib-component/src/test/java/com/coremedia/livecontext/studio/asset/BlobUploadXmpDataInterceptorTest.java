package com.coremedia.livecontext.studio.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.ecommerce.common.ProductIdExtractor;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.asset.util.AssetHelper;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.activation.MimeType;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@PrepareForTest({ProductIdExtractor.class, Commerce.class})
@RunWith(PowerMockRunner.class)
public class BlobUploadXmpDataInterceptorTest {

  @Mock
  private ContentRepository contentRepository;

  @Mock
  private CommerceConnectionSupplier commerceConnectionSupplier;

  @Mock
  private CommerceConnection commerceConnection;

  @Mock
  private ContentWriteRequest contentWriteRequest;

  @Mock
  private Content parentFolder;

  @Mock
  private Content content;

  @Mock
  private Blob blob;

  @Mock
  private InputStream blobInputStream;

  @Mock
  private MimeType blobMimeType;

  @Mock
  private AssetHelper assetHelper;

  @Mock
  private StoreContext defaultContext;

  private Map<String, Object> properties;

  private BlobUploadXmpDataInterceptor testling;

  @Before
  public void setup() {
    properties = new HashMap<>();

    testling = new BlobUploadXmpDataInterceptor(commerceConnectionSupplier);
    testling.setBlobProperty("data");
    testling.setAssetHelper(assetHelper);

    when(blobMimeType.getPrimaryType()).thenReturn("image/jpeg");
    when(blob.getContentType()).thenReturn(blobMimeType);
    when(blob.getInputStream()).thenReturn(blobInputStream);

    commerceConnection = MockCommerceEnvBuilder.create().setupEnv();

    when(commerceConnectionSupplier.findConnectionForContent(any(Content.class)))
            .thenReturn(Optional.of(commerceConnection));
  }

  @Test
  public void testInterceptNoMatch() {
    testling.intercept(contentWriteRequest);

    PowerMockito.verifyStatic(times(0));
    ProductIdExtractor.extractProductIds(blob);

    assertThat(properties).doesNotContainKey("localSettings");
  }

  @Test
  public void testInterceptNoXmpData() {
    properties.put("data", blob);

    when(contentWriteRequest.getProperties()).thenReturn(properties);
    when(contentWriteRequest.getParent()).thenReturn(parentFolder);
    PowerMockito.mockStatic(ProductIdExtractor.class);
    when(ProductIdExtractor.extractProductIds(blob)).thenReturn(Collections.<String>emptyList());

    testling.intercept(contentWriteRequest);

    assertThat(properties).containsEntry("localSettings", null);
  }

  @Test
  public void testInterceptWithXmpData() {
    properties.put("data", blob);

    when(contentWriteRequest.getProperties()).thenReturn(properties);
    when(contentWriteRequest.getParent()).thenReturn(parentFolder);
    when(contentWriteRequest.getEntity()).thenReturn(content);
    List<String> xmpData = Arrays.asList("PC_EVENING_DRESS", "PC_EVENING_DRESS-RED-M");
    PowerMockito.mockStatic(ProductIdExtractor.class);
    when(ProductIdExtractor.extractProductIds(blob)).thenReturn(xmpData);
    when(assetHelper.updateCMPictureForExternalIds(content, xmpData)).thenReturn(null);

    Product productMock = mock(Product.class);
    when(productMock.getId()).thenReturn("vendor:///catalog/product/PC_EVENING_DRESS");
    when(productMock.isVariant()).thenReturn(false);
    when(commerceConnection.getCatalogService().findProductById("vendor:///catalog/product/PC_EVENING_DRESS")).thenReturn(productMock);

    testling.intercept(contentWriteRequest);

    assertThat(properties).containsKey("localSettings");
  }

  @Test
  public void testRetrieveProductOrVariant() {
    String aProductExtId = "PC_EVENING_DRESS";
    String aSkuExtId = "PC_EVENING_DRESS-RED-M";
    String unknown = "unknown";
    when(commerceConnection.getCatalogService().findProductById("vendor:///catalog/product/" + aProductExtId)).thenReturn(mock(Product.class));
    when(commerceConnection.getCatalogService().findProductVariantById("vendor:///catalog/sku/" + aSkuExtId)).thenReturn(mock(ProductVariant.class));
    when(commerceConnection.getCatalogService().findProductById("vendor:///catalog/product/" + unknown)).thenReturn(null);
    when(commerceConnection.getCatalogService().findProductVariantById("vendor:///catalog/sku/" + unknown)).thenReturn(null);

    CatalogService catalogService = commerceConnection.getCatalogService();
    CommerceIdProvider idProvider = commerceConnection.getIdProvider();

    assertThat(testling.retrieveProductOrVariant(aProductExtId, idProvider, catalogService)).isNotNull();
    assertThat(testling.retrieveProductOrVariant(aSkuExtId, idProvider, catalogService)).isNotNull();
    assertThat(testling.retrieveProductOrVariant("unkown", idProvider, catalogService)).isNull();
  }
}