package com.coremedia.blueprint.caas.augmentation.adapter;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.caas.augmentation.CommerceEntityHelper;
import com.coremedia.blueprint.caas.augmentation.model.CommerceRef;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static java.util.Locale.US;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommerceRefAdapterTest {

  public static final String STORE_ID = "myStoreId";
  public static final String SITE_ID = "mySiteId";
  private static final CatalogAlias CATALOG_ALIAS = CatalogAlias.of("catalog");

  public static final String EXTERNAL_PRODUCT_ID = "myExternalProductId";
  private static final String PRODUCT_ID = "acme:///catalog/product/" + EXTERNAL_PRODUCT_ID;

  public static final String PROPERTY_NAME = "propertyName";
  public static final CatalogId CATALOG = CatalogId.of("catalog");

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private SitesService sitesService;

  @Mock
  private Site aSite;

  @Mock
  private CommerceEntityHelper commerceEntityHelper;

  @Mock
  private CommerceConnection aConnection;

  @Mock
  private StoreContext aStoreContext;

  @Mock
  private CatalogAliasTranslationService catalogAliasTranslationService;

  @InjectMocks
  private CommerceRefAdapter testling;

  private void initSite() {
    when(sitesService.getSite(SITE_ID)).thenReturn(aSite);
    when(aSite.getId()).thenReturn(SITE_ID);
  }

  @Test
  void getCommerceRef() {
    //init site
    initSite();
    when(aSite.getLocale()).thenReturn(US);

    //init commerce connection
    when(commerceEntityHelper.getCommerceConnection(SITE_ID)).thenReturn(aConnection);
    when(aConnection.getInitialStoreContext()).thenReturn(aStoreContext);
    when(aStoreContext.getStoreId()).thenReturn(STORE_ID);

    //init catalogAliasTranslation service
    when(catalogAliasTranslationService.getCatalogIdForAlias(any(CatalogAlias.class), any(StoreContext.class)))
            .thenReturn(Optional.of(CATALOG));

    String propertyName = "propertyName";
    Content content = mock(Content.class);
    when(content.getString(propertyName)).thenReturn(PRODUCT_ID);
    when(sitesService.getContentSiteAspect(content).getSite()).thenReturn(aSite);

    CommerceRef commerceRef = testling.getCommerceRef(content, propertyName);

    assertThat(commerceRef)
            .returns(BaseCommerceBeanType.PRODUCT, CommerceRef::getType)
            .returns(US.toLanguageTag(), CommerceRef::getLocale)
            .returns("catalog", CommerceRef::getCatalogId)
            .returns(aSite.getId(), CommerceRef::getSiteId)
            .returns(EXTERNAL_PRODUCT_ID, CommerceRef::getExternalId)
            .returns(STORE_ID, CommerceRef::getStoreId)
            .returns(SITE_ID + ":" + EXTERNAL_PRODUCT_ID, CommerceRef::getId);
  }

  @Test
  void getCommerceRefForContentWithNoSite(){
    Content content = mock(Content.class);
    when(content.getString(PROPERTY_NAME)).thenReturn(PRODUCT_ID);
    when(sitesService.getContentSiteAspect(content).getSite()).thenReturn(null);

    CommerceRef commerceRef = testling.getCommerceRef(content, PROPERTY_NAME);

    assertThat(commerceRef).isNull();
  }

  @Test
  void getCommerceRefForContentWithNoCommerceConnection(){
    initSite();
    Content content = mock(Content.class);
    when(content.getString(PROPERTY_NAME)).thenReturn(PRODUCT_ID);
    when(sitesService.getContentSiteAspect(content).getSite()).thenReturn(aSite);
    when(commerceEntityHelper.getCommerceConnection(SITE_ID)).thenReturn(null);

    CommerceRef commerceRef = testling.getCommerceRef(content, PROPERTY_NAME);

    assertThat(commerceRef).isNull();
  }

  @Test
  void getCommerceRefForContentWithNoExternalReference(){
    Content content = mock(Content.class);
    when(content.getString(PROPERTY_NAME)).thenReturn(null);

    CommerceRef commerceRef = testling.getCommerceRef(content, PROPERTY_NAME);

    assertThat(commerceRef).isNull();
  }

  @Test
  void getCommerceRefForContentWithInvalidExternalReference(){
    Content content = mock(Content.class);
    when(content.getString(PROPERTY_NAME)).thenReturn("an://invalid/commerce/id");
    when(sitesService.getContentSiteAspect(content).getSite()).thenReturn(aSite);

    CommerceRef commerceRef = testling.getCommerceRef(content, PROPERTY_NAME);

    assertThat(commerceRef).isNull();
  }
}
