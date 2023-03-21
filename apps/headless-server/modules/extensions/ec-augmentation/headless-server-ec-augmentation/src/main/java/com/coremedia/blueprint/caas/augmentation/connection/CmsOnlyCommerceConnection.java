package com.coremedia.blueprint.caas.augmentation.connection;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilderImpl;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.blueprint.caas.augmentation.CommerceSettingsHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.common.Vendor;
import com.coremedia.livecontext.ecommerce.link.LinkService;
import com.coremedia.livecontext.ecommerce.order.CartService;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpotService;
import com.coremedia.livecontext.ecommerce.p13n.SegmentService;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Optional;

@DefaultAnnotation(NonNull.class)
class CmsOnlyCommerceConnection implements CommerceConnection, StoreContextProvider {

  private final CommerceIdProvider idProvider;
  private final CommerceBeanFactory commerceBeanFactory = new CmsOnlyCommerceBeanFactory();
  private final CatalogService catalogService = new CmsOnlyCatalogService(commerceBeanFactory);
  private final Vendor vendor;
  private final StoreContext storeContext;

  CmsOnlyCommerceConnection(Vendor vendor, Site site, CommerceSettingsHelper settingsHelper) {
    this.vendor = vendor;
    idProvider = new BaseCommerceIdProvider(vendor);
    var storeContextBuilder = StoreContextBuilderImpl.from(this, site.getId());
    settingsHelper.findCatalogId(site).ifPresent(storeContextBuilder::withCatalogId);
    settingsHelper.findCatalogAlias(site).ifPresent(storeContextBuilder::withCatalogAlias);
    settingsHelper.findStoreId(site).ifPresent(storeContextBuilder::withStoreId);
    storeContext = storeContextBuilder.withLocale(settingsHelper.getLocale(site)).build();
  }

  @NonNull
  @Override
  public StoreContextProvider getStoreContextProvider() {
    return this;
  }

  @NonNull
  @Override
  public UserContextProvider getUserContextProvider() {
    throw new UnsupportedOperationException("getUserContextProvider");
  }

  @NonNull
  @Override
  public CatalogService getCatalogService() {
    return catalogService;
  }

  @NonNull
  @Override
  public Optional<MarketingSpotService> getMarketingSpotService() {
    return Optional.empty();
  }

  @NonNull
  @Override
  public Optional<SegmentService> getSegmentService() {
    return Optional.empty();
  }

  @NonNull
  @Override
  public Optional<CartService> getCartService() {
    return Optional.empty();
  }

  @NonNull
  @Override
  public Optional<AssetService> getAssetService() {
    return Optional.empty();
  }

  @NonNull
  @Override
  public Optional<LinkService> getLinkService() {
    return Optional.empty();
  }

  @NonNull
  @Override
  public CommerceIdProvider getIdProvider() {
    return idProvider;
  }

  @NonNull
  @Override
  public CommerceBeanFactory getCommerceBeanFactory() {
    return commerceBeanFactory;
  }

  @Override
  public StoreContext getInitialStoreContext() {
    return storeContext;
  }

  @Override
  public void setInitialStoreContext(StoreContext storeContext) {
    throw new UnsupportedOperationException("setInitialStoreContext");
  }

  @Override
  public String getVendorName() {
    return vendor.value();
  }

  @Override
  public Vendor getVendor() {
    return vendor;
  }

  @Override
  public Optional<StoreContext> findContextBySiteId(String siteId) {
    throw new UnsupportedOperationException("findContextBySiteId");
  }

  @Override
  public Optional<StoreContext> findContextBySite(Site site) {
    throw new UnsupportedOperationException("findContextBySite");
  }

  @Override
  public Optional<StoreContext> findContextByContent(Content content) {
    throw new UnsupportedOperationException("findContextByContent");
  }

  @Override
  public Optional<StoreContext> createContext(Site site) {
    throw new UnsupportedOperationException("createContext");
  }

  @Override
  public StoreContextBuilder buildContext(StoreContext storeContext) {
    if (storeContext instanceof StoreContextImpl) {
      return StoreContextBuilderImpl.from((StoreContextImpl) storeContext);
    }
    return StoreContextBuilderImpl.from(storeContext.getConnection(), storeContext.getSiteId());
  }
}
