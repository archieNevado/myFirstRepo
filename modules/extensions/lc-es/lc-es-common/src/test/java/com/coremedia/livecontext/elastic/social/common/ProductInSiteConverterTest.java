package com.coremedia.livecontext.elastic.social.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.elastic.core.api.models.UnresolvableReferenceException;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.livecontext.elastic.social.common.ProductInSiteConverter.ID;
import static com.coremedia.livecontext.elastic.social.common.ProductInSiteConverter.SITE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductInSiteConverterTest {

  private String productId = "1234";
  private String productReferenceId = "vendor:///catalog/product/" + productId;
  private String siteId = "5678";

  @Mock
  private Product product;

  @Mock
  private ProductInSite productInSite;

  @Mock
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Mock
  private Site site;

  @Mock
  private SitesService sitesService;

  private BaseCommerceConnection commerceConnection;

  private ProductInSiteConverter converter;

  @Before
  public void setup() {
    commerceConnection = MockCommerceEnvBuilder.create().setupEnv();

    when(commerceConnectionInitializer.findConnectionForSite(site)).thenReturn(Optional.of(commerceConnection));

    commerceConnection.getStoreContext().put("site", siteId);
    when(commerceConnection.getCatalogService().findProductById(anyString())).thenReturn(product);

    when(product.getExternalId()).thenReturn(productId);
    when(product.getContext()).thenReturn(commerceConnection.getStoreContext());
    when(commerceConnection.getStoreContextProvider().createContext(site)).thenReturn(commerceConnection.getStoreContext());

    when(productInSite.getProduct()).thenReturn(product);
    when(productInSite.getSite()).thenReturn(site);

    when(sitesService.getSite(siteId)).thenReturn(site);

    when(site.getId()).thenReturn(siteId);

    converter = new ProductInSiteConverter(sitesService, commerceConnectionInitializer);
  }

  @Test
  public void getType() {
    assertThat(converter.getType()).isEqualTo(ProductInSite.class);
  }

  @Test
  public void serializeWithProductReferenceId() {
    Map<String, Object> serializedObject = new HashMap<>();

    converter.serialize(productInSite, serializedObject);

    assertThat(serializedObject.entrySet()).hasSize(2);
    assertThat(serializedObject.get(ID)).isEqualTo(productReferenceId);
    assertThat(serializedObject.get(SITE_ID)).isEqualTo(siteId);

    verify(product).getExternalId();
  }

  @Test
  public void deserialize() {
    Map<String, Object> serializedObject = new HashMap<>();
    serializedObject.put("id", productReferenceId);
    serializedObject.put("site", siteId);

    ProductInSite result = converter.deserialize(serializedObject);

    assertThat(result.getProduct()).isSameAs(product);
    assertThat(result.getSite()).isSameAs(site);
  }

  @Test(expected = UnresolvableReferenceException.class)
  public void deserializeUnresolvable() {
    Map<String, Object> serializedObject = new HashMap<>();
    serializedObject.put("id", "id");
    serializedObject.put("site", "site");

    when(commerceConnection.getCatalogService().findProductById(anyString())).thenReturn(null);

    converter.deserialize(serializedObject);
  }
}
