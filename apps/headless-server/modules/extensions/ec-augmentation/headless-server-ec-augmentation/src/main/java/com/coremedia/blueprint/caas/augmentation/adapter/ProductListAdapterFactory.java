package com.coremedia.blueprint.caas.augmentation.adapter;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.caas.augmentation.CommerceConnectionHelper;
import com.coremedia.caas.model.adapter.ExtendedLinkListAdapterFactory;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.SitesService;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Objects;

@DefaultAnnotation(NonNull.class)
public class ProductListAdapterFactory {
  private final SettingsService settingsService;
  private final SitesService sitesService;
  private final ExtendedLinkListAdapterFactory extendedLinkListAdapterFactory;
  private final CommerceConnectionHelper commerceConnectionHelper;
  private final CommerceSearchFacade commerceSearchFacade;

  public ProductListAdapterFactory(SettingsService settingsService,
                                   SitesService sitesService,
                                   ExtendedLinkListAdapterFactory extendedLinkListAdapterFactory,
                                   CommerceConnectionHelper commerceConnectionHelper,
                                   CommerceSearchFacade commerceSearchFacade) {
    this.settingsService = settingsService;
    this.sitesService = sitesService;
    this.extendedLinkListAdapterFactory = extendedLinkListAdapterFactory;
    this.commerceConnectionHelper = commerceConnectionHelper;
    this.commerceSearchFacade = commerceSearchFacade;
  }

  public ProductListAdapter to(Content productList) {
    return to(productList, ProductListAdapter.OFFSET_DEFAULT);
  }

  public ProductListAdapter to(Content productList, Integer offset) {
    return new ProductListAdapter(extendedLinkListAdapterFactory, productList, settingsService, sitesService, commerceConnectionHelper,
            commerceSearchFacade, Objects.requireNonNull(sitesService.getContentSiteAspect(productList).getSite()), offset);
  }
}
