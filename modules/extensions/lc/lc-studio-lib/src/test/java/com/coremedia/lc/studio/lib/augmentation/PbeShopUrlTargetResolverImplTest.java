package com.coremedia.lc.studio.lib.augmentation;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PbeShopUrlTargetResolverImplTest {

  @InjectMocks
  private
  PbeShopUrlTargetResolverImpl testling;

  private BaseCommerceConnection commerceConnection;

  @Mock
  private SitesService sitesService;

  @Mock
  private AugmentationService externalPageAugmentationService;

  @Mock
  private Site site;

  @Before
  public void setup() {
    commerceConnection = MockCommerceEnvBuilder.create().setupEnv();

    DefaultConnection.get().getStoreContext().put(StoreContextImpl.SITE, "theSiteId");
    DefaultConnection.get().getStoreContext().put(StoreContextImpl.STORE_NAME, "storeName");
    when(sitesService.getSite("theSiteId")).thenReturn(site);

    when(commerceConnection.getCatalogService().findCategoryBySeoSegment(anyString())).thenReturn(null);
  }

  @Test
  public void resolveCategoryUrlTest() {
    String testUrl = "http://anyhost/pc-on-the-table/pc-glasses#facet:&productBeginIndex:0&orderBy:&pageView:grid&minPrice:&maxPrice:&pageSize:&";

    Category category = mock(Category.class);
    when(commerceConnection.getCatalogService().findCategoryBySeoSegment("pc-glasses")).thenReturn(category);

    Object resolvedCategory = testling.resolveUrl(testUrl, "theSiteId");

    assertThat(resolvedCategory).isEqualTo(category);
  }

  @Test
  public void resolveExternalPageSeoUrlTest() {
    String testUrl = "http://anyhost/contact-us";

    Object resolvedBean = testling.resolveUrl(testUrl, "theSiteId");

    assertThat(resolvedBean).isNull();
  }

  @Test
  public void resolveExternalPageSeoUrlNotAugmentedTest() {
    String testUrl = "http://anyhost/contact-us";

    Object resolvedBean = testling.resolveUrl(testUrl, "theSiteId");

    assertThat(resolvedBean).isNull();
  }

  @Test
  public void resolveSiteRootDocument() {
    String testUrl = "http://anyhost/storeName/";

    Content rootDocument = mock(Content.class);
    when(site.getSiteRootDocument()).thenReturn(rootDocument);

    Object resolvedBean = testling.resolveUrl(testUrl, "theSiteId");

    assertThat(resolvedBean).isEqualTo(rootDocument);
  }

  @Test
  public void resolveExternalPageSeoUrlAugmentedTest() {
    String testUrl = "http://anyhost/contact-us";

    Content externalPage = mock(Content.class);
    when(externalPageAugmentationService.getContentByExternalId("contact-us", site)).thenReturn(externalPage);

    Object resolvedBean = testling.resolveUrl(testUrl, "theSiteId");

    assertThat(resolvedBean).isEqualTo(externalPage);
  }

  @Test
  public void resolveExternalPageUnresolvableSeoUrl() {
    String testUrl = "http://anyhost/";

    Object resolvedBean = testling.resolveUrl(testUrl, "theSiteId");

    assertThat(resolvedBean).isNull();
  }

  @Test
  public void resolveExternalPageNonSeoUrlNotAugmentedTest() {
    String testUrl = "http://anyhost/AdvancedSearchDisplay?catalogId=10152&langId=-1&storeId=10301";

    Object resolvedBean = testling.resolveUrl(testUrl, "theSiteId");

    assertThat(resolvedBean).isNull();
  }

  @Test
  public void resolveExternalPageNonSeoUrlAugmentedTest() {
    String testUrl = "http://anyhost/AdvancedSearchDisplay?catalogId=10152&langId=-1&storeId=10301";

    Content externalPage = mock(Content.class);
    when(externalPageAugmentationService.getContentByExternalId("AdvancedSearchDisplay", site)).thenReturn(externalPage);

    Object resolvedBean = testling.resolveUrl(testUrl, "theSiteId");

    assertThat(resolvedBean).isEqualTo(externalPage);
  }
}
