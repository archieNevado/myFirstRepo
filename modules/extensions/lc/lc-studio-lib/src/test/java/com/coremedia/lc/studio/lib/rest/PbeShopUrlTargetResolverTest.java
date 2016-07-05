package com.coremedia.lc.studio.lib.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PbeShopUrlTargetResolverTest {
  @InjectMocks
  PbeShopUrlTargetResolver testling;

  BaseCommerceConnection commerceConnection;

  @Mock
  private CommerceConnectionInitializer commerceConnectionInitializer;
  @Mock
  private SitesService sitesService;
  @Mock
  private AugmentationService externalPageAugmentationService;
  @Mock
  private Site site;

  @Before
  public void setup() {
    commerceConnection = MockCommerceEnvBuilder.create().setupEnv();
    Commerce.getCurrentConnection().getStoreContext().put(StoreContextBuilder.SITE, "theSiteId");
    when(sitesService.getSite("theSiteId")).thenReturn(site);

    when(commerceConnection.getCatalogService().findCategoryBySeoSegment(anyString())).thenReturn(null);
  }

  @Test
  public void resolveCategoryUrlTest() {
    String testUrl = "http://shop-helios.toko-test-05.coremedia.vm/webapp/wcs/stores/servlet/en/auroraesite/pc-on-the-table/pc-glasses#facet:&productBeginIndex:0&orderBy:&pageView:grid&minPrice:&maxPrice:&pageSize:&";
    Category category = mock(Category.class);
    when(commerceConnection.getCatalogService().findCategoryBySeoSegment("pc-glasses")).thenReturn(category);

    Object resolvedCategory = testling.resolveUrl(testUrl, "theSiteId");
    assertNotNull(resolvedCategory);
    assertEquals(category, resolvedCategory);
  }

  @Test
  public void resolveExternalPageSeoUrlTest() {
    String testUrl = "http://shop-helios.toko-test-05.coremedia.vm/webapp/wcs/stores/servlet/en/auroraesite/contact-us";

    Object resolvedBean = testling.resolveUrl(testUrl, "theSiteId");
    assertNull(resolvedBean);
  }

  @Test
  public void resolveExternalPageSeoUrlNotAugmentedTest() {
    String testUrl = "http://shop-helios.toko-test-05.coremedia.vm/webapp/wcs/stores/servlet/en/auroraesite/contact-us";

    Object resolvedBean = testling.resolveUrl(testUrl, "theSiteId");
    assertNull(resolvedBean);
  }

  @Test
  public void resolveSiteRootDocument() {
    String testUrl = "http://shop-helios.toko-test-05.coremedia.vm/webapp/wcs/stores/servlet/en/aurora/";
    Content rootDocument = mock(Content.class);
    when(site.getSiteRootDocument()).thenReturn(rootDocument);

    Object resolvedBean = testling.resolveUrl(testUrl, "theSiteId");
    assertEquals(rootDocument, resolvedBean);
  }

  @Test
  public void resolveExternalPageSeoUrlAugmentedTest() {
    String testUrl = "http://shop-helios.toko-test-05.coremedia.vm/webapp/wcs/stores/servlet/en/auroraesite/contact-us";
    Content externalPage = mock(Content.class);
    when(externalPageAugmentationService.getContentByExternalId("contact-us", site)).thenReturn(externalPage);

    Object resolvedBean = testling.resolveUrl(testUrl, "theSiteId");
    assertNotNull(resolvedBean);
    assertEquals(externalPage, resolvedBean);
  }

  @Test
  public void resolveExternalPageUnresolvableSeoUrl() {
    String testUrl = "http://shop-helios.toko-test-05.coremedia.vm/webapp/wcs/stores/servlet/en/auroraesite//";
    Object resolvedBean = testling.resolveUrl(testUrl, "theSiteId");
    assertNull(resolvedBean);
  }

  @Test
  public void resolveExternalPageNonSeoUrlNotAugmentedTest() {
    String testUrl = "http://shop-helios.toko-test-05.coremedia.vm/webapp/wcs/stores/servlet/AdvancedSearchDisplay?catalogId=10152&langId=-1&storeId=10301";

    Object resolvedBean = testling.resolveUrl(testUrl, "theSiteId");
    assertNull(resolvedBean);
  }

  @Test
  public void resolveExternalPageNonSeoUrlAugmentedTest() {
    String testUrl = "http://shop-helios.toko-test-05.coremedia.vm/webapp/wcs/stores/servlet/AdvancedSearchDisplay?catalogId=10152&langId=-1&storeId=10301";
    Content externalPage = mock(Content.class);
    when(externalPageAugmentationService.getContentByExternalId("AdvancedSearchDisplay", site)).thenReturn(externalPage);

    Object resolvedBean = testling.resolveUrl(testUrl, "theSiteId");
    assertNotNull(resolvedBean);
    assertEquals(externalPage, resolvedBean);
  }
}