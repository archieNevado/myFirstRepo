package com.coremedia.livecontext.product;

import com.coremedia.blueprint.base.links.ContentLinkBuilder;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultConnection;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriTemplate;

import static com.coremedia.blueprint.base.links.UriConstants.Segments.PREFIX_DYNAMIC;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENTS_FRAGMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link com.coremedia.livecontext.product.ProductAvailabilityHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductAvailabilityHandlerTest {

  private static final String ANY_VANITY_NAME = "any vanity name";
  private static final String PRODUCT_AVAILABILITY_PREFIX = '/' + PREFIX_DYNAMIC + '/' + SEGMENTS_FRAGMENT + "/productavailability/";

  private ProductAvailabilityHandler testling;

  @Mock
  private StoreContextProvider storeContextProvider;
  @Mock
  private StoreContext defaultContext;
  @Mock
  private CatalogService catalogService;
  @Mock
  private SitesService sitesService;
  @Mock
  private ContentLinkBuilder contentLinkBuilder;
  @Mock
  private NavigationSegmentsUriHelper navigationSegmentsUriHelper;
  @Mock
  private CommerceConnection connection;
  @Mock
  private CommerceIdProvider idProvider;


  @Before
  public void setUp() throws Exception {
    testling = new ProductAvailabilityHandler();
    testling.setSiteService(sitesService);
    testling.setContentLinkBuilder(contentLinkBuilder);
    testling.setNavigationSegmentsUriHelper(navigationSegmentsUriHelper);
    DefaultConnection.set(connection);
    when(connection.getStoreContextProvider()).thenReturn(storeContextProvider);
    when(connection.getCatalogService()).thenReturn(catalogService);
    when(connection.getIdProvider()).thenReturn(idProvider);
    when(storeContextProvider.findContextBySite(any(Site.class))).thenReturn(defaultContext);
    when(connection.getStoreContext()).thenReturn(defaultContext);
  }

  @After
  public void teardown() {
    DefaultConnection.clear();
  }

  @Test
  public void testBuildLinkForProduct() throws Exception {
    UriTemplate uriTemplate = mock(UriTemplate.class);
    when(uriTemplate.toString()).thenReturn(ProductAvailabilityHandler.URI_PATTERN);

    when(contentLinkBuilder.getVanityName(any(Content.class))).thenReturn(ANY_VANITY_NAME);

    Product product = mock(Product.class);
    when(product.getExternalId()).thenReturn("0815");
    ProductInSite productInSite = mock(ProductInSite.class);
    when(productInSite.getProduct()).thenReturn(product);
    when(productInSite.getSite()).thenReturn(mock(Site.class));

    Content siteRootDocument = mock(Content.class);
    Site anySite = mock(Site.class);
    when(anySite.getSiteRootDocument()).thenReturn(siteRootDocument);
    when(sitesService.getSite(anyString())).thenReturn(anySite);

    UriComponents uriComponents = testling.buildLinkFor(productInSite, uriTemplate, null);

    assertEquals("Expected link does not match built link.",
            PRODUCT_AVAILABILITY_PREFIX + ANY_VANITY_NAME+ "/product/0815", uriComponents.getPath());

  }

  @Test
  public void testBuildLinkForProductVariant() throws Exception {
    UriTemplate uriTemplate = mock(UriTemplate.class);
    when(uriTemplate.toString()).thenReturn(ProductAvailabilityHandler.URI_PATTERN);

    when(contentLinkBuilder.getVanityName(any(Content.class))).thenReturn(ANY_VANITY_NAME);

    ProductVariant product = mock(ProductVariant.class);
    when(product.getExternalId()).thenReturn("0815");
    ProductInSite productInSite = mock(ProductInSite.class);
    when(productInSite.getProduct()).thenReturn(product);
    when(productInSite.getSite()).thenReturn(mock(Site.class));

    Content siteRootDocument = mock(Content.class);
    Site anySite = mock(Site.class);
    when(anySite.getSiteRootDocument()).thenReturn(siteRootDocument);
    when(sitesService.getSite(anyString())).thenReturn(anySite);

    UriComponents uriComponents = testling.buildLinkFor(productInSite, uriTemplate, null);

    assertEquals("Expected link does not match built link.",
                 PRODUCT_AVAILABILITY_PREFIX + ANY_VANITY_NAME + "/variant/0815", uriComponents.getPath());

  }

  @Test
  public void testHandleDynamicRequestProduct() throws Exception {
    Product product = mock(Product.class);
    when(catalogService.findProductById(
            DefaultConnection.get().getIdProvider().formatProductId(eq("0815")))).thenReturn(product);
    ModelAndView modelAndView = testling.handleDynamicFragmentRequest("anyShopName", "product", "0815", "availabilityFragment");

    assertEquals("availabilityFragment", modelAndView.getViewName());
    assertTrue(modelAndView.getModel().get("self") instanceof Product);
  }

  @Test
  public void testHandleDynamicRequestProductVariant() throws Exception {
    ProductVariant product = mock(ProductVariant.class);
    when(catalogService.findProductVariantById(
            DefaultConnection.get().getIdProvider().formatProductVariantId("ibm://catalog/sku/0815_v"))).thenReturn(product);
    ModelAndView modelAndView = testling.handleDynamicFragmentRequest("anyShopName", "variant", "0815_v", "availabilityFragment");

    assertEquals("availabilityFragment", modelAndView.getViewName());
    assertTrue(modelAndView.getModel().get("self") instanceof ProductVariant);
  }
}
