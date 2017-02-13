package com.coremedia.livecontext.asset.impl;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.asset.AssetUrlProvider;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class AssetServiceImpl implements AssetService {

  private static final String CONFIG_KEY_DEFAULT_PICTURE = "livecontext.assets.default.picture";

  private SitesService sitesService;
  private SettingsService settingsService;
  private AssetResolvingStrategy assetResolvingStrategy;

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

    List<Content> references = assetResolvingStrategy.findAssets("CMPicture", id, site);
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

    List<Content> visuals = assetResolvingStrategy.findAssets("CMVisual", id, site);

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

    return assetResolvingStrategy.findAssets("CMDownload", id, site);
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
    return settingsService.setting(CONFIG_KEY_DEFAULT_PICTURE, Content.class, site.getSiteRootDocument());
  }

  @Nullable
  private static String getReferenceIdFromUrl(@Nullable String url) {
    if (url == null || StringUtils.isBlank(url)) {
      return null;
    }

    CommerceIdProvider commerceIdProvider = BaseCommerceIdHelper.getCurrentCommerceIdProvider();
    String partNumber = parsePartNumberFromUrl(url);

    if (url.contains(CATEGORY_URI_PREFIX)) {
      return commerceIdProvider.formatCategoryId(partNumber);
    }

    if (!url.contains(PRODUCT_URI_PREFIX)) {
      return null;
    }

    String productId = commerceIdProvider.formatProductId(partNumber);

    CommerceConnection connection = getCommerceConnection();
    if (connection == null || connection.getCatalogService() == null) {
      return productId;
    }

    Product product = connection.getCatalogService().findProductById(productId);
    return product != null ? product.getReference() : productId;
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

  @Autowired
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Autowired
  public void setAssetResolvingStrategy(AssetResolvingStrategy assetResolvingStrategy) {
    this.assetResolvingStrategy = assetResolvingStrategy;
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
