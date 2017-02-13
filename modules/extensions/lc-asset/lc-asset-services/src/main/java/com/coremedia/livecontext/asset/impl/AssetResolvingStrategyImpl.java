package com.coremedia.livecontext.asset.impl;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.util.CommerceReferenceHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.asset.AssetSearchService;
import com.coremedia.livecontext.asset.AssetValidationService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;

public class AssetResolvingStrategyImpl implements AssetResolvingStrategy {
  private static final Logger LOG = LoggerFactory.getLogger(AssetResolvingStrategyImpl.class);

  private AssetChanges assetChanges;
  private AssetSearchService assetSearchService;
  private AssetValidationService assetValidationService;

  /**
   * find assets of the given content type, reference id and site
   *
   * @param contentType the given content type
   * @param id          the reference id
   * @param site        the given site
   * @return the found assets
   */
  @Nonnull
  public List<Content> findAssets(@Nonnull String contentType, @Nonnull String id, @Nonnull Site site) {
    if (assetSearchService == null) {
      LOG.error("assetSearchService is not set, cannot find assets for {} in site {}", id, site.getName());
      return emptyList();
    }

    Set<Content> assets = resolveCachedAndIndexedAssets(contentType, id, site);
    List<Content> upToDate = selectUpToDate(id, site, assets);
    List<Content> referencedInContent = selectReferencedContent(id, upToDate);
    List<Content> validAssets = filterWithAssetValidationService(referencedInContent);

    sortByContentName(validAssets);

    if (validAssets.isEmpty() && isSkuId(id)) {
      //try to load referencedAssets from parent Product, if partNumber belongs to ProductVariant
      validAssets = findFallbackForProductVariant(id, site, contentType);
    }

    return validAssets;
  }

  @Nonnull
  private Set<Content> resolveCachedAndIndexedAssets(@Nonnull String contentType, @Nonnull String id,
                                                     @Nonnull Site site) {
    Collection<Content> cachedAssets = assetChanges.get(id, site);
    String externalId = BaseCommerceIdHelper.getCurrentCommerceIdProvider().parseExternalIdFromId(id);
    List<Content> indexedAssets = assetSearchService.searchAssets(contentType, externalId, site);

    List<Content> filteredCachedAssets = filterCachedAssets(contentType, cachedAssets);

    Set<Content> assets = new HashSet<>(indexedAssets);
    assets.addAll(filteredCachedAssets);
    return assets;
  }

  @Nonnull
  private static List<Content> filterCachedAssets(@Nonnull String contentType, @Nonnull Collection<Content> cachedAssets) {
    List<Content> filteredCachedAssets = new ArrayList<>();
    for (Content cachedAsset : cachedAssets) {
      if (!cachedAsset.isDestroyed() && cachedAsset.getType().isSubtypeOf(contentType)) {
        filteredCachedAssets.add(cachedAsset);
      }
    }
    return filteredCachedAssets;
  }

  @Nonnull
  private List<Content> selectUpToDate(@Nonnull String id, @Nonnull Site site, @Nonnull Collection<Content> assets) {
    List<Content> upToDate = new ArrayList<>();
    for (Content asset : assets) {
      if (assetChanges.isUpToDate(asset, id, site)) {
        upToDate.add(asset);
      }
    }
    return upToDate;
  }

  @Nonnull
  private List<Content> selectReferencedContent(@Nonnull String id, @Nonnull List<Content> upToDate) {
    List<Content> referencedInContent = new ArrayList<>();
    for (Content asset : upToDate) {
      List<String> externalReferences = getExternalReferences(asset);
      if (externalReferences.contains(id)) {
        referencedInContent.add(asset);
      }
    }
    return referencedInContent;
  }

  @Nonnull
  private List<Content> filterWithAssetValidationService(@Nonnull List<Content> assets) {
    if (assetValidationService == null) {
      return assets;
    }
    return assetValidationService.filterAssets(assets);
  }

  private static void sortByContentName(@Nonnull List<Content> validAssets) {
    Collections.sort(validAssets, (content1, content2) -> content1.getName().compareToIgnoreCase(content2.getName()));
  }

  /**
   * find fallback assets of the given content type, reference id of a sku and site
   *
   * @param contentType the given content type
   * @param id          the reference id of a sku
   * @param site        the given site
   * @return the found assets
   */
  @Nonnull
  private List<Content> findFallbackForProductVariant(@Nonnull String id, @Nonnull Site site,
                                                      @Nonnull String contentType) {
    StoreContextProvider storeContextProvider = getCommerceConnection().getStoreContextProvider();
    StoreContext storeContextForSite = storeContextProvider.findContextBySite(site);
    if (storeContextForSite == null) {
      return emptyList();
    }

    storeContextProvider.setCurrentContext(storeContextForSite);

    Product product = getCatalogService().findProductById(id);
    if (!(product instanceof ProductVariant)) {
      return emptyList();
    }

    Product parentProduct = ((ProductVariant) product).getParent();
    if (parentProduct == null) {
      return emptyList();
    }

    //noinspection unchecked
    return findAssets(contentType, parentProduct.getReference(), site);
  }

  private static CatalogService getCatalogService() {
    return getCommerceConnection().getCatalogService();
  }

  private static CommerceConnection getCommerceConnection() {
    return Commerce.getCurrentConnection();
  }

  @VisibleForTesting
  boolean isSkuId(@Nonnull String id) {
    return BaseCommerceIdHelper.isSkuId(id);
  }

  @VisibleForTesting
  List<String> getExternalReferences(Content asset) {
    return CommerceReferenceHelper.getExternalReferences(asset);
  }

  @Autowired
  public void setAssetChanges(AssetChanges assetChanges) {
    this.assetChanges = assetChanges;
  }

  @Autowired(required = false)
  public void setAssetValidationService(AssetValidationService assetValidationService) {
    this.assetValidationService = assetValidationService;
  }

  @Autowired(required = false)
  public void setAssetSearchService(AssetSearchService assetSearchService) {
    this.assetSearchService = assetSearchService;
  }
}
