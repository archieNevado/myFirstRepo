package com.coremedia.blueprint.image.transformation;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.transform.SiteVariantsStructResolver;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;

/**
 * Responsible for reading the struct with the image variants from the rot channel.
 */
@Named
public class SiteVariantsStructResolverImpl implements SiteVariantsStructResolver {
  private static final String VARIANTS_STRUCT_NAME = "responsiveImageSettings";

  @Autowired
  private SettingsService settingsService;

  @Override
  public Struct getVariantsForSite(Site site) {
    return settingsService.setting(VARIANTS_STRUCT_NAME, Struct.class, site);
  }
}
