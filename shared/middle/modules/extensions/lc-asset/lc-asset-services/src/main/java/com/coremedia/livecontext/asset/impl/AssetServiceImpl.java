package com.coremedia.livecontext.asset.impl;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class AssetServiceImpl implements AssetService {

  private static final String CONFIG_KEY_DEFAULT_PICTURE = "livecontext.assets.default.picture";

  private SitesService sitesService;
  private SettingsService settingsService;
  private AssetResolvingStrategy assetResolvingStrategy;

  @NonNull
  @Override
  public List<Content> findPictures(@NonNull CommerceId commerceId, boolean withDefault, String siteId) {
    Site site = sitesService.getSite(siteId);
    if (site == null) {
      return emptyList();
    }

    List<Content> references = assetResolvingStrategy.findAssets("CMPicture", commerceId, site);
    if (!references.isEmpty()) {
      return references;
    }

    if (withDefault) {
      Content defaultPicture = getDefaultPicture(site);
      if (defaultPicture != null) {
        return singletonList(defaultPicture);
      }
    }

    return emptyList();
  }

  @NonNull
  @Override
  public List<Content> findVisuals(@NonNull CommerceId commerceId, boolean withDefault, String siteId) {
    Site site = sitesService.getSite(siteId);
    if (site == null) {
      return emptyList();
    }

    List<Content> visuals = assetResolvingStrategy.findAssets("CMVisual", commerceId, site);

    if (withDefault && visuals.isEmpty()) {
      Content defaultPicture = getDefaultPicture(site);
      if (defaultPicture != null) {
        return singletonList(defaultPicture);
      }
    }

    return filterSpinners(visuals);
  }

  @NonNull
  private static List<Content> filterSpinners(@NonNull List<Content> allVisuals) {
    Set<Content> picturesInSpinners = extractPicturesInSpinners(allVisuals);
    return removePicturesInSpinners(allVisuals, picturesInSpinners);
  }

  @NonNull
  private static List<Content> removePicturesInSpinners(@NonNull List<Content> allVisuals,
                                                        @NonNull Set<Content> picturesInSpinners) {
    return allVisuals.stream()
            .filter(visual -> !picturesInSpinners.contains(visual))
            .collect(toList());
  }

  @NonNull
  private static Set<Content> extractPicturesInSpinners(@NonNull List<Content> allVisuals) {
    return allVisuals.stream()
            .filter(visual -> visual.getType().isSubtypeOf("CMSpinner"))
            .flatMap(spinner -> ((List<Content>) spinner.getList("sequence")).stream())
            .collect(toSet());
  }

  @NonNull
  @Override
  public List<Content> findDownloads(@NonNull CommerceId commerceId, String siteId) {
    return sitesService.findSite(siteId)
            .map(site -> assetResolvingStrategy.findAssets("CMDownload", commerceId, site))
            .orElseGet(Collections::emptyList);
  }

  @Nullable
  @Override
  public Content getDefaultPicture(@NonNull Site site) {
    return settingsService.getSetting(CONFIG_KEY_DEFAULT_PICTURE, Content.class, site)
            .orElse(null);
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
}
