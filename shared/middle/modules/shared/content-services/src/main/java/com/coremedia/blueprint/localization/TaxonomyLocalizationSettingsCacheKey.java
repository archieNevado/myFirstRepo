package com.coremedia.blueprint.localization;

import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.struct.Struct;
import com.google.common.base.Objects;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TaxonomyLocalizationSettingsCacheKey extends CacheKey<TaxonomyLocalizationSettings> {
  private static final Logger LOG = LoggerFactory.getLogger(TaxonomyLocalizationSettingsCacheKey.class);

  public static final String TRANSLATION_STRUCT = "translations";

  private static final String TAXONOMY_SETTINGS = "/Settings/Options/Settings/TaxonomySettings";
  private static final String DEFAULT_LOCALE = "defaultLocale";

  private final ContentRepository contentRepository;

  public TaxonomyLocalizationSettingsCacheKey(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }

  /**
   * Creates the supported locales list representations that is cached.
   *
   * @param cache the cache managing asked to return a value for this cache key
   * @return the source list representations that is cached
   * @throws Exception if the value cannot be computed. This will prevent the value from being cached.
   */
  @Override
  public TaxonomyLocalizationSettings evaluate(Cache cache) throws Exception {
    TaxonomyLocalizationSettings l10nSettings =new TaxonomyLocalizationSettings();
    Content settings = contentRepository.getChild(TAXONOMY_SETTINGS);
    if (settings == null) {
      return l10nSettings;
    }

    try {
      Struct dataStruct = settings.getStruct("settings");
      if (dataStruct == null
              || !dataStruct.getProperties().containsKey(TRANSLATION_STRUCT)
              || !dataStruct.getProperties().containsKey(DEFAULT_LOCALE)) {
        return l10nSettings;
      }

      List<String> languages = dataStruct.getStrings(TRANSLATION_STRUCT);
      for (String language : languages) {
        String formattedLocale = language.replaceAll("-", "_");
        Locale locale = LocaleUtils.toLocale(formattedLocale);
        l10nSettings.getSupportedLocales().add(locale);
      }

      String defaultLocale = dataStruct.getString(DEFAULT_LOCALE);
      if (StringUtils.isEmpty(defaultLocale)) {
        l10nSettings.setDefaultLocale(Locale.getDefault());
      }
      else {
        l10nSettings.setDefaultLocale(LocaleUtils.toLocale(defaultLocale.replaceAll("-", "_")));
      }
    } catch (Exception e) {
      LOG.error("Failed to read locale list from '{}': {}", settings.getPath(), e.getMessage(), e);
      throw e;
    }
    return l10nSettings;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    TaxonomyLocalizationSettingsCacheKey that = (TaxonomyLocalizationSettingsCacheKey) o;
    return Objects.equal(contentRepository, that.contentRepository);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(contentRepository);
  }
}
