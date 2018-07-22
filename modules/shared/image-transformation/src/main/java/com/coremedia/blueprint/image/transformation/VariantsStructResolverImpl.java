package com.coremedia.blueprint.image.transformation;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.transform.VariantsStructResolver;
import org.springframework.beans.factory.annotation.Autowired;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import javax.inject.Named;
import java.util.Optional;

import static java.util.Optional.empty;

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

  @Nullable
  @Override
  public Struct getVariantsForSite(@NonNull Site site) {
    Struct setting = settingsService.setting(VARIANTS_STRUCT_NAME, Struct.class, site);

    if (setting == null) {
      setting = getGlobalVariants().orElse(null);
    }

    return setting;
  }

  @NonNull
  private Optional<Struct> getGlobalVariants() {
    Content settings = contentRepository.getChild(globalVariantsSettings);

    if (settings == null) {
      return empty();
    }

    return getStruct(settings);
  }

  @NonNull
  private static Optional<Struct> getStruct(@NonNull Content setting) {
    if (setting.getType().isSubtypeOf(SETTINGS_DOCTYPE)) {
      Struct subStruct = setting.getStruct(SETTINGS_PROPERTY);

      // Find settings document that contains the struct with the configured name.
      if (subStruct.toNestedMaps().containsKey(VARIANTS_STRUCT_NAME)) {
        Struct variantsStruct = subStruct.getStruct(VARIANTS_STRUCT_NAME);
        return Optional.ofNullable(variantsStruct);
      }
    }

    return empty();
  }
}
