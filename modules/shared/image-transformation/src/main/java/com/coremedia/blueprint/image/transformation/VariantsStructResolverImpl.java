package com.coremedia.blueprint.image.transformation;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.transform.VariantsStructResolver;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;

/**
 * Responsible for reading the struct with the image variants from the root channel.
 */
@Named
public class VariantsStructResolverImpl implements VariantsStructResolver {
  private static final String VARIANTS_STRUCT_NAME = "responsiveImageSettings";
  private static final String SETTINGS_PROPERTY = "settings";
  private static final String SETTINGS_DOCTYPE = "CMSettings";

  private String globalVariantsSettings = "/Settings/Options/Settings/Responsive Image Settings";

  @Autowired
  private SettingsService settingsService;

  @Autowired
  private ContentRepository contentRepository;

  @Override
  public Struct getVariantsForSite(Site site) {
    Struct setting = settingsService.setting(VARIANTS_STRUCT_NAME, Struct.class, site);
    if(setting == null) {
      setting = getGlobalVariants();
    }
    return setting;
  }

  private Struct getGlobalVariants() {
    Content settings = contentRepository.getChild(globalVariantsSettings);
    if (settings != null) {
      return getStruct(settings);
    }
    return null;
  }

  private Struct getStruct(Content setting) {
    if(setting.getType().isSubtypeOf(SETTINGS_DOCTYPE)) {
      Struct subStruct = setting.getStruct(SETTINGS_PROPERTY);
      //find settings document that contains the struct with the configured name
      if (subStruct.toNestedMaps().containsKey(VARIANTS_STRUCT_NAME)) {
        return subStruct.getStruct(VARIANTS_STRUCT_NAME);
      }
    }
    return null;
  }
}
