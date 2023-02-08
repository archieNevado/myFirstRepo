package com.coremedia.blueprint.localization;

import com.coremedia.blueprint.base.taxonomies.TaxonomyLocalizationStrategy;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.struct.Struct;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.coremedia.blueprint.localization.TaxonomyLocalizationSettingsCacheKey.TRANSLATION_STRUCT;

public class TaxonomyLocalizationStrategyImpl implements TaxonomyLocalizationStrategy {
  private static final Logger LOG = LoggerFactory.getLogger(TaxonomyLocalizationStrategyImpl.class);


  private final ContentRepository contentRepository;
  private final SitesService sitesService;
  private final Cache cache;
  private final String structProperty;

  private static final String STUDIO_LOCALE_COOKIE_NAME = "com.coremedia.cms.editor.locale";

  public TaxonomyLocalizationStrategyImpl(ContentRepository contentRepository, SitesService sitesService, Cache cache, String structProperty) {
    this.contentRepository = contentRepository;
    this.sitesService = sitesService;
    this.cache = cache;
    this.structProperty = structProperty;
  }

  @NonNull
  @Override
  public String getDisplayName(@NonNull Content content, @Nullable Locale locale) {
    if (!getSupportedLocales().isEmpty()) {
      Locale targetLocale = resolveTargetLocale(content, locale);
      String translation = this.getTranslation(content, targetLocale);
      if (!StringUtils.isEmpty(translation)) {
        return translation;
      }
    }
    return getDefaultName(content);
  }

  /**
   * Returns the translation for the given locale from the localSettings struct.
   * Note that the default language must be set, otherwise the strategy is considered to be inactive.
   *
   * @param content the tag content to read the translation for
   * @param locale  the requested translation locale
   * @return the translated tag name or null if no value was found
   */
  @Nullable
  protected String getTranslation(@NonNull Content content, @NonNull Locale locale) {
    try {
      Locale defaultLocale = getDefaultLocale();
      String defaultValue = content.getString("value");
      if (locale.equals(defaultLocale) && !StringUtils.isEmpty(defaultValue)) {
        return defaultValue;
      }

      Struct localSettings = content.getStruct(structProperty);
      if (localSettings == null) {
        return null;
      }

      Map<String, Object> localSettingsMap = localSettings.toNestedMaps();
      if (localSettingsMap.containsKey(TRANSLATION_STRUCT)) {
        String localeString = locale.toString().replaceAll("_", "-");

        Map<String, Object> translationStructMap = (Map<String, Object>) localSettingsMap.get(TRANSLATION_STRUCT);
        if (translationStructMap != null) {
          String editorTranslation = null;

          if (translationStructMap.containsKey(localeString)) {
            editorTranslation = (String) translationStructMap.get(localeString);
          } else if (translationStructMap.containsKey(locale.getLanguage())) {
            editorTranslation = (String) translationStructMap.get(locale.getLanguage());
          }

          if (StringUtils.isEmpty(editorTranslation)) {
            String nearestLocale = findNearestTranslation(translationStructMap, locale);
            if (nearestLocale != null) {
              editorTranslation = (String) translationStructMap.get(nearestLocale);
            }
          }

          if (!StringUtils.isEmpty(editorTranslation)) {
            return editorTranslation;
          }
        }
      }
    } catch (Exception e) {
      LOG.error("Failed to read translation from '{}': {}", content.getPath(), e.getMessage(), e);
    }
    return null;
  }

  /**
   * If no matching locale string was found inside the TaxonomySettings,
   * we take the language prefix and return the first matching locale key which has this language.
   *
   * @param structMap the struct map which contains all available translation.
   * @param locale    the locale a translation is search for
   */
  @Nullable
  private String findNearestTranslation(@NonNull Map<String, Object> structMap, @NonNull Locale locale) {
    Set<Map.Entry<String, Object>> entries = structMap.entrySet();
    String language = locale.getLanguage();

    for (Map.Entry<String, Object> entry : entries) {
      if (entry.getKey().startsWith(language) && !StringUtils.isEmpty((String) entry.getValue())) {
        return entry.getKey();
      }
    }
    return null;
  }

  /**
   * Returns the default name if no struct based translation is configured or set.
   *
   * @param content the content to retrieve the translation for
   * @return the "value" property value or the content name
   */
  @NonNull
  protected String getDefaultName(@NonNull Content content) {
    String value = content.getString("value");
    if (!StringUtils.isEmpty(value)) {
      return value;
    }
    return content.getName();
  }

  /**
   * Determines the actual locale that should be used for translation.
   * If the given locale is <code>null</code>, the locale will be calculated on the site locale
   * or extracted from the request language.
   * <p>
   * In the Studio context, the language tag is extracted from the localization cookie.
   *
   * @param content the content to determine the target language for
   * @param locale  the locale to retrieve the translation for or null
   * @return the language tag that should be used for the translation lookup.
   */
  @NonNull
  protected Locale resolveTargetLocale(@NonNull Content content, @Nullable Locale locale) {
    if (locale != null) {
      return locale;
    }

    Site site = sitesService.getContentSiteAspect(content).getSite();
    if (site != null) {
      return site.getLocale();
    }
    return getRequestLocale();
  }

  @NonNull
  @Override
  public List<Locale> getSupportedLocales() {
    return cache.get(new TaxonomyLocalizationSettingsCacheKey(contentRepository)).getSupportedLocales();
  }

  @NonNull
  public Locale getDefaultLocale() {
    return cache.get(new TaxonomyLocalizationSettingsCacheKey(contentRepository)).getDefaultLocale();
  }

  /**
   * Calculates the request locale.
   * Studio request do contain a cookie with the user's preferred language.
   * This is the one which we want to display.
   *
   * @return The user's preferred locale or the system default if none is found.
   */
  @NonNull
  protected Locale getRequestLocale() {
    HttpServletRequest currentHttpRequest = getCurrentHttpRequest();
    if (currentHttpRequest != null) {
      Optional<Cookie> languageCookie = Arrays.stream(currentHttpRequest.getCookies()).filter(c -> c.getName().equals(STUDIO_LOCALE_COOKIE_NAME)).findFirst();
      if (languageCookie.isPresent()) {
        return LocaleUtils.toLocale(languageCookie.get().getValue());
      }
      return currentHttpRequest.getLocale();
    }
    return getDefaultLocale();
  }

  /**
   * @return Returns the current HTTP request the taxonomy node is requested for
   */
  @Nullable
  protected HttpServletRequest getCurrentHttpRequest() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes instanceof ServletRequestAttributes) {
      return ((ServletRequestAttributes) requestAttributes).getRequest();
    }
    return null;
  }
}
