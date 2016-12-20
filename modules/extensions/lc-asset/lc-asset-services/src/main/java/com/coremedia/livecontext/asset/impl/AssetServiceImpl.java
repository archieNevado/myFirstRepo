package com.coremedia.livecontext.asset.impl;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.util.CommerceReferenceHelper;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.asset.AssetSearchService;
import com.coremedia.livecontext.asset.AssetValidationService;
import com.coremedia.livecontext.asset.util.AssetHelper;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.asset.AssetUrlProvider;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class AssetServiceImpl implements AssetService {

  private static final Logger LOG = LoggerFactory.getLogger(AssetServiceImpl.class);

  private SitesService sitesService;
  private SettingsService settingsService;
  private AssetChanges assetChanges;

  private AssetSearchService assetSearchService;
  private AssetValidationService assetValidationService;

  @Nonnull
  @Override
  public CatalogPicture getCatalogPicture(String url) {
    String id = getReferenceIdFromUrl(url);
    if (id == null) {
      // Not a CMS URL
      return new CatalogPicture(url, null);
    }

    // Make absolute and replace {cmsHost}
    String imageUrl = getAssetUrlProvider().getImageUrl(url);

    List<Content> pictures = findPictures(id);
    Content picture = pictures.stream().findFirst().orElse(null);
    return new CatalogPicture(imageUrl, picture);
  }

  @Override
  @Nonnull
  public List<Content> findPictures(@Nonnull String id) {
    Site site = getSite();
    if (site == null) {
      return emptyList();
    }

    List<Content> references = findAssets("CMPicture", id, site);
    if (!references.isEmpty()) {
      return references;
    }

    Content defaultPicture = getDefaultPicture(site);
    if (defaultPicture == null) {
      return emptyList();
    }

    return singletonList(defaultPicture);
  }

  @Override
  @Nonnull
  public List<Content> findVisuals(@Nullable String id) {
    return findVisuals(id, true);
  }

  @Override
  @Nonnull
  public List<Content> findVisuals(@Nullable String id, boolean withDefault) {
    if (id == null) {
      return emptyList();
    }

    Site site = getSite();
    if (site == null) {
      return emptyList();
    }

    List<Content> visuals = findAssets("CMVisual", id, site);

    if (withDefault && visuals.isEmpty()) {
      Content defaultPicture = getDefaultPicture(site);
      if (defaultPicture != null) {
        return singletonList(defaultPicture);
      }
    }

    return filterSpinners(visuals);
  }

  @Nonnull
  private static List<Content> filterSpinners(@Nonnull List<Content> allVisuals) {
    Set<Content> picturesInSpinners = extractPicturesInSpinners(allVisuals);
    return removePicturesInSpinners(allVisuals, picturesInSpinners);
  }

  @Nonnull
  private static List<Content> removePicturesInSpinners(@Nonnull List<Content> allVisuals,
                                                        @Nonnull Set<Content> picturesInSpinners) {
    return allVisuals.stream()
            .filter(visual -> !picturesInSpinners.contains(visual))
            .collect(toList());
  }

  @Nonnull
  private static Set<Content> extractPicturesInSpinners(@Nonnull List<Content> allVisuals) {
    Set<Content> allPictures = new HashSet<>();

    List<Content> spinners = findSpinners(allVisuals);
    for (Content spinner : spinners) {
      List<Content> sequence = (List<Content>) spinner.getList("sequence");
      allPictures.addAll(sequence);
    }

    return allPictures;
  }

  @Nonnull
  private static List<Content> findSpinners(@Nonnull List<Content> allVisuals) {
    return allVisuals.stream()
            .filter(visual -> visual.getType().isSubtypeOf("CMSpinner"))
            .collect(toList());
  }

  @Override
  @Nonnull
  public List<Content> findDownloads(@Nullable String id) {
    if (id == null) {
      return emptyList();
    }

    Site site = getSite();
    if (site == null) {
      return emptyList();
    }

    return findAssets("CMDownload", id, site);
  }

  /**
   * find assets of the given content type, reference id and site
   *
   * @param contentType the given content type
   * @param id          the reference id
   * @param site        the given site
   * @return the found assets
   */
  @Nonnull
  List<Content> findAssets(@Nonnull String contentType, @Nonnull String id, @Nonnull Site site) {
    if (assetSearchService == null) {
      LOG.error("assetSearchService is not set, cannot find assets for {} in site {}", id, site.getName());
      return emptyList();
    }

    Collection<Content> changedAssets = assetChanges.get(id, site);
    String externalId = BaseCommerceIdHelper.getCurrentCommerceIdProvider().parseExternalIdFromId(id);
    List<Content> indexedAssets = assetSearchService.searchAssets(contentType, externalId, site);

    //merge indexed assets with changed assets
    List<Content> assets = new ArrayList<>(indexedAssets);
    if (changedAssets != null) {
      for (Content changedContent : changedAssets) {
        //filter the documents of the given contentType
        if (!changedContent.isDestroyed()
                && changedContent.getType().isSubtypeOf(contentType)
                && !assets.contains(changedContent)) {
          assets.add(changedContent);
        }
      }
    }

    for (int i = assets.size() - 1; i >= 0; i--) {
      Content asset = assets.get(i);
      //check now if the assets are up-to-date
      if (!assetChanges.isUpToDate(asset, id, site)) {
        assets.remove(asset);
      } else {
        //double-check if the assets contains the complete id in the struct
        List<String> externalReferences = CommerceReferenceHelper.getExternalReferences(asset);
        if (!externalReferences.contains(id)) {
          assets.remove(asset);
        }
      }
    }

    if (assetValidationService != null) {
      //filter validity
      assets = assetValidationService.filterAssets(assets);
    }

    //sort by the name of the content
    Collections.sort(assets, (content1, content2) -> content1.getName().compareToIgnoreCase(content2.getName()));

    if (assets.isEmpty() && BaseCommerceIdHelper.isSkuId(id)) {
      //try to load assets from parent Product, if partNumber belongs to ProductVariant
      assets = findFallbackForProductVariant(id, site, contentType);
    }

    return assets;
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

  @Nullable
  private Site getSite() {
    CommerceConnection connection = getCommerceConnection();

    if (connection == null) {
      return null;
    }

    String siteId = connection.getStoreContext().getSiteId();
    return sitesService.getSite(siteId);
  }

  @Nullable
  @Override
  public Content getDefaultPicture(@Nonnull Site site) {
    return AssetHelper.getDefaultPicture(site, settingsService);
  }

  @Nullable
  private static String getReferenceIdFromUrl(@Nullable String urlStr) {
    if (urlStr == null || StringUtils.isBlank(urlStr)) {
      return null;
    }

    CommerceIdProvider commerceIdProvider = BaseCommerceIdHelper.getCurrentCommerceIdProvider();
    String partNumber = parsePartNumberFromUrl(urlStr);

    if (urlStr.contains(CATEGORY_URI_PREFIX)) {
      return commerceIdProvider.formatCategoryId(partNumber);
    } else if (urlStr.contains(PRODUCT_URI_PREFIX)) {
      CommerceConnection connection = getCommerceConnection();
      if (connection != null && connection.getCatalogService() != null) {
        Product productOrSkuByPartNumber = connection.getCatalogService().findProductById(
                commerceIdProvider.formatProductId(partNumber));
        return productOrSkuByPartNumber != null ? productOrSkuByPartNumber.getReference() :
                commerceIdProvider.formatProductId(partNumber);
      } else {
        return commerceIdProvider.formatProductId(partNumber);
      }
    }

    return null;
  }

  @Nullable
  private static String parsePartNumberFromUrl(@Nonnull String urlStr) {
    int index = urlStr.lastIndexOf('.');
    if (index >= 0) {
      String fileName = urlStr.substring(0, index);
      index = fileName.lastIndexOf('/');
      if (index >= 0) {
        return fileName.substring(index + 1);
      }
    }

    return null;
  }

  @Autowired
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  private static CatalogService getCatalogService() {
    return getCommerceConnection().getCatalogService();
  }

  @Autowired
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
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

  @Nonnull
  @Override
  public AssetService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, AssetService.class);
  }

  public static AssetUrlProvider getAssetUrlProvider() {
    return getCommerceConnection().getAssetUrlProvider();
  }

  private static CommerceConnection getCommerceConnection() {
    return Commerce.getCurrentConnection();
  }
}
