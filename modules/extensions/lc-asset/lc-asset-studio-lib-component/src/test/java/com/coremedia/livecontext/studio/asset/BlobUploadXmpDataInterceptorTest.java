package com.coremedia.livecontext.studio.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.NoCommerceConnectionAvailable;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.ecommerce.common.ProductIdExtractor;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.asset.util.AssetHelper;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@PrepareForTest({ProductIdExtractor.class, Commerce.class})
@RunWith(PowerMockRunner.class)
public class BlobUploadXmpDataInterceptorTest {

  @Mock
  private ContentRepository contentRepository;

  @Mock
  private CommerceConnectionInitializer commerceConnectionInitializer;

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
  private AssetHelper assetHelper;

  @Mock
  private StoreContext defaultContext;

  private BlobUploadXmpDataInterceptor testling;

  @Before
  public void setup() {
    testling = new BlobUploadXmpDataInterceptor();
    testling.setCommerceConnectionInitializer(commerceConnectionInitializer);
    testling.setBlobProperty("data");
    testling.setAssetHelper(assetHelper);
    commerceConnection = MockCommerceEnvBuilder.create().setupEnv();
  }

  @Test
  public void testInterceptNoMatch() {
    when(contentWriteRequest.getProperties()).thenReturn(Collections.<String, Object>emptyMap());
    testling.intercept(contentWriteRequest);

    PowerMockito.verifyStatic(times(0));
    ProductIdExtractor.extractProductIds(Matchers.any(Blob.class));
  }

  @Test
  public void testInterceptNoCommerceConnection() {
    Map<String, Object> properties = new HashMap<>();
    properties.put("data", blob);
    when(contentWriteRequest.getProperties()).thenReturn(properties);
    when(contentWriteRequest.getParent()).thenReturn(parentFolder);
    doThrow(NoCommerceConnectionAvailable.class).when(commerceConnectionInitializer).init(parentFolder);

    testling.intercept(contentWriteRequest);

    PowerMockito.verifyStatic(times(0));
    ProductIdExtractor.extractProductIds(Matchers.any(Blob.class));
  }

  @Test
  public void testInterceptNoXmpData() {
    Map<String, Object> propertiesMock = mock(Map.class);
    when(propertiesMock.get("data")).thenReturn(blob);
    when(contentWriteRequest.getProperties()).thenReturn(propertiesMock);
    when(contentWriteRequest.getParent()).thenReturn(parentFolder);
    PowerMockito.mockStatic(ProductIdExtractor.class);
    when(ProductIdExtractor.extractProductIds(blob)).thenReturn(Collections.<String>emptyList());

    testling.intercept(contentWriteRequest);

    verify(propertiesMock, never()).put(Matchers.anyString(), Matchers.anyObject());
  }

  @Test
  public void testInterceptWithXmpData() {
    Map<String, Object> propertiesMock = mock(Map.class);
    when(propertiesMock.get("data")).thenReturn(blob);
    when(propertiesMock.containsKey("data")).thenReturn(true);

    when(contentWriteRequest.getProperties()).thenReturn(propertiesMock);
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
    verify(propertiesMock, times(1)).put(Matchers.anyString(), Matchers.anyObject());
  }

  @Test
  public void testRetrieveProductOrVariant(){
    String aProductExtId = "PC_EVENING_DRESS";
    String aSkuExtId = "PC_EVENING_DRESS-RED-M";
    String unknown = "unknown";
    when(commerceConnection.getCatalogService().findProductById("vendor:///catalog/product/" + aProductExtId)).thenReturn(mock(Product.class));
    when(commerceConnection.getCatalogService().findProductVariantById("vendor:///catalog/sku/" + aSkuExtId)).thenReturn(mock(ProductVariant.class));
    when(commerceConnection.getCatalogService().findProductById("vendor:///catalog/product/" + unknown)).thenReturn(null);
    when(commerceConnection.getCatalogService().findProductVariantById("vendor:///catalog/sku/" + unknown)).thenReturn(null);

    Assert.assertNotNull(testling.retrieveProductOrVariant(aProductExtId));
    Assert.assertNotNull(testling.retrieveProductOrVariant(aSkuExtId));
    Assert.assertNull(testling.retrieveProductOrVariant("unkown"));
  }
}