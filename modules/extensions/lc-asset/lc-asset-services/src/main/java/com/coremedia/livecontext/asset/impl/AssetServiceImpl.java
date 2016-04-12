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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;

public class AssetServiceImpl implements AssetService {

  private static final Logger LOG = LoggerFactory.getLogger(AssetServiceImpl.class);
  private SitesService sitesService;
  private SettingsService settingsService;
  private AssetChanges assetChanges;

  private AssetSearchService assetSearchService;
  private AssetValidationService assetValidationService;

  @Override
  public CatalogPicture getCatalogPicture(String url) {

    String id = getReferenceIdFromUrl(url);
    if (id == null) {
      // Not a CMS URL
      return new CatalogPicture(url, null);
    }

    // Make absolute and replace {cmsHost}
    String imageUrl = getAssetUrlProvider().getImageUrl(url);

    List<Content> cmPictures = findPictures(id);
    if (cmPictures.isEmpty()) {
      return new CatalogPicture(imageUrl, null);
    }

    return new CatalogPicture(imageUrl, cmPictures.get(0));
  }

  @Override
  @Nonnull
  public List<Content> findPictures(@Nonnull String id) {

    List<Content> references = Collections.emptyList();

    Site site = getSite();
    if (site != null) {
      references = findAssets("CMPicture", id, site);

      if (references.isEmpty()) {
        Content defaultPicture = getDefaultPicture(site);
        if (defaultPicture != null) {
          return Collections.singletonList(defaultPicture);
        }
      }
    }
    return references;
  }

  @Override
  @Nonnull
  public List<Content> findVisuals(@Nullable String id) {
    return findVisuals(id, true);
  }

  @Override
  @Nonnull
  public List<Content> findVisuals(@Nullable String id, boolean withDefault) {
    Site site = getSite();
    if (site != null && id != null) {
      List<Content> visuals = findAssets("CMVisual", id, site);
      if (withDefault && visuals.isEmpty()) {
        Content defaultPicture = getDefaultPicture(site);
        if (defaultPicture != null) {
          return Collections.singletonList(defaultPicture);
        }
      }
      return filterSpinners(visuals);
    }
    return Collections.emptyList();
  }

  private List<Content> filterSpinners(List<Content> allVisuals) {
    Set<Content> picturesInSpinners = extractPicturesInSpinners(allVisuals);
    return removePicturesInSpinners(allVisuals, picturesInSpinners);
  }

  private List<Content> removePicturesInSpinners(List<Content> allVisuals, Set<Content> picturesInSpinners) {
    List<Content> filteredVisuals = new ArrayList<>();
    for (Content oneVisual : allVisuals) {
      boolean isPartOfSpinner = picturesInSpinners.contains(oneVisual);
      if(!isPartOfSpinner) {
        filteredVisuals.add(oneVisual);
      }
    }
    return filteredVisuals;
  }

  @Nonnull
  private Set<Content> extractPicturesInSpinners(@Nonnull List<Content> allVisuals) {
    List<Content> spinners = findSpinners(allVisuals);
    Set<Content> allPictures = new HashSet<>();
    for (Content spinner : spinners) {
      List<Content> sequence = (List<Content>) spinner.getList("sequence");
      allPictures.addAll(sequence);
    }
    return allPictures;
  }

  @Nonnull
  private List<Content> findSpinners(List<Content> allVisuals) {
    List<Content> spinners = new ArrayList<>();
    for (Content visual : allVisuals) {
      if(visual.getType().isSubtypeOf("CMSpinner")) {
        spinners.add(visual);
      }
    }
    return spinners;
  }

  @Override
  @Nonnull
  public List<Content> findDownloads(@Nullable String id) {
    List<Content> references = Collections.emptyList();
    Site site = getSite();
    if (site != null && id != null) {
      references = findAssets("CMDownload", id, site);
    }
    return references;
  }

  /**
   * find assets of the given content type, reference id and site
   * @param contentType the given content type
   * @param id the reference id
   * @param site the given site
   * @return the found assets
   */
  @Nonnull
  List<Content> findAssets(@Nonnull String contentType, @Nonnull String id, @Nonnull Site site) {

    if (assetSearchService == null) {
      LOG.error("assetSearchService is not set, cannot find assets for {} in site {}", id, site.getName());
      return Collections.emptyList();
    }

    Collection<Content> changedAssets = assetChanges.get(id, site);
    String externalId = BaseCommerceIdHelper.getCurrentCommerceIdProvider().parseExternalIdFromId(id);
    List<Content> indexedAssets = assetSearchService.searchAssets(contentType, externalId, site);

    //merge indexed assets with changed assets
    List<Content> assets = new ArrayList<>(indexedAssets);
    if (changedAssets != null) {
      for (Content changedContent: changedAssets) {
        //filter the documents of the given contentType
        if (!changedContent.isDestroyed() && changedContent.getType().isSubtypeOf(contentType)) {
          if (!assets.contains(changedContent)) {
            assets.add(changedContent);
          }
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
    Collections.sort(assets, new Comparator<Content>() {
      @Override
      public int compare(Content content1, Content content2) {
        return content1.getName().compareToIgnoreCase(content2.getName());
      }
    });


    if (assets.isEmpty() && BaseCommerceIdHelper.isSkuId(id)) {
      //try to load assets from parent Product, if partNumber belongs to ProductVariant
      assets = findFallbackForProductVariant(id, site, contentType);
    }

    return assets;
  }


  /**
   * find fallback assets of the given content type, reference id of a sku and site
   * @param contentType the given content type
   * @param id the reference id of a sku
   * @param site the given site
   * @return the found assets
   */
  private List<Content> findFallbackForProductVariant(String id, Site site, String contentType) {
    List<Content> assets = Collections.emptyList();
    StoreContextProvider storeContextProvider = Commerce.getCurrentConnection().getStoreContextProvider();
    StoreContext storeContextForSite = storeContextProvider.findContextBySite(site);

    if (storeContextForSite != null) {

      storeContextProvider.setCurrentContext(storeContextForSite);

      Product product = getCatalogService().findProductById(id);

      if (product != null && product instanceof ProductVariant) {
        Product parentProduct = ((ProductVariant) product).getParent();
        if (parentProduct != null) {
          //noinspection unchecked
          assets = findAssets(contentType, parentProduct.getReference(), site);
        }
      }
    }
    return assets;
  }

  private Site getSite() {
    if (Commerce.getCurrentConnection() != null) {
      String siteId = Commerce.getCurrentConnection().getStoreContext().getSiteId();
      return sitesService.getSite(siteId);
    }
    return null;
  }

  @Override
  public Content getDefaultPicture(@Nonnull Site site) {
    return AssetHelper.getDefaultPicture(site, settingsService);
  }

  private String getReferenceIdFromUrl(String urlStr) {
    if (StringUtils.isBlank(urlStr)) {
      return null;
    }

    String partNumber = parsePartNumberFromUrl(urlStr);

    if (urlStr.contains(CATEGORY_URI_PREFIX)) {
      return BaseCommerceIdHelper.getCurrentCommerceIdProvider().formatCategoryId(partNumber);
    } else if (urlStr.contains(PRODUCT_URI_PREFIX)) {
      CommerceConnection connection = Commerce.getCurrentConnection();
      if (connection != null && connection.getCatalogService() != null) {
        Product productOrSkuByPartNumber = connection.getCatalogService().findProductById(
                BaseCommerceIdHelper.getCurrentCommerceIdProvider().formatProductId(partNumber));
        return productOrSkuByPartNumber != null ? productOrSkuByPartNumber.getReference() :
                BaseCommerceIdHelper.getCurrentCommerceIdProvider().formatProductId(partNumber);
      }
      else {
        return BaseCommerceIdHelper.getCurrentCommerceIdProvider().formatProductId(partNumber);
      }
    }
    return null;
  }

  private static String parsePartNumberFromUrl(String urlStr) {
    int index = urlStr.lastIndexOf(".");
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

  private CatalogService getCatalogService() {
    return Commerce.getCurrentConnection().getCatalogService();
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

  public AssetUrlProvider getAssetUrlProvider() {
    return Commerce.getCurrentConnection().getAssetUrlProvider();
  }
}
