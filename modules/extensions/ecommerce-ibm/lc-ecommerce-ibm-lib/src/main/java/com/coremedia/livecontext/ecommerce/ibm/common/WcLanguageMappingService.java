package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.cache.EvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

class WcLanguageMappingService {

  private static final Logger LOG = LoggerFactory.getLogger(WcLanguageMappingService.class);

  //default is english
  private static final String DEFAULT_LANGUAGE_ID = "-1";

  private static final WcRestServiceMethod<Map, Void>
          GET_LANGUAGE_MAPPING = WcRestConnector.createServiceMethod(HttpMethod.GET, "coremedia/languagemap", false, false, false, Map.class);

  private WcRestConnector restConnector;
  private Cache cache;

  private final CacheKey<Map<String, String>> languageMappingCacheKey = new LanguageMappingCacheKey(this);

  Map<String, String> getLanguageMapping() {
    try {
      return cache.get(languageMappingCacheKey);
    } catch (EvaluationException e) {
      LOG.info("ignoring exception while retrieving language mapping: {}", e.getCause().getMessage());
      return emptyMap();
    }
  }

  Map<String, String> getLanguageMappingUncached() {
    //noinspection unchecked
    return restConnector.callServiceInternal(GET_LANGUAGE_MAPPING, emptyList(), emptyMap(), null, null, null);
  }

  /**
   * Gets IBM specific language Id for a given locale String.
   * If a certain mapping does not exist or locale String is invalid, the default "-1" for "en" is returned.
   *
   * @param locale e.g. "en_US" "en" "de"
   */
  @Nonnull
  String getLanguageId(@Nullable Locale locale) {
    return findLanguageId(locale).orElse(DEFAULT_LANGUAGE_ID);
  }

  @Nonnull
  private Optional<String> findLanguageId(@Nullable Locale locale) {
    if (locale == null) {
      return Optional.empty();
    }

    Map mapping = getLanguageMapping();
    if (mapping == null) {
      return Optional.empty();
    }

    String localeStr = locale.toString();
    String key = mapping.containsKey(localeStr) ? localeStr : locale.getLanguage();

    String langId = (String) mapping.get(key);
    return Optional.ofNullable(langId);
  }

  @Autowired
  public void setCache(Cache cache) {
    this.cache = cache;
  }

  @Autowired
  public void setRestConnector(WcRestConnector connector) {
    this.restConnector = connector;
  }
}
