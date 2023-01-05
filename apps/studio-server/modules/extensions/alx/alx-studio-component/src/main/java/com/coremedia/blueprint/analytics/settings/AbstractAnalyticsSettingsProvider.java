package com.coremedia.blueprint.analytics.settings;

import com.coremedia.blueprint.base.links.ContentLinkBuilder;
import com.coremedia.blueprint.base.navigation.context.ContextStrategy;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.common.CapStructHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.struct.Struct;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Abstract implementation of an analytics settings provider.
 */
public abstract class AbstractAnalyticsSettingsProvider implements AnalyticsSettingsProvider {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractAnalyticsSettingsProvider.class);

  protected static final String KEY_REPORT_URL_PREFIX = "reportUrlPrefix";
  protected static final String UTF_8 = "UTF-8";

  private static final String INTERNAL_SETTINGS_PATH = "Options/Settings/Internal/InternalAnalyticsSettings";

  private static final String DEFAULT_HIDDEN_TEXT = "XXXXXXXX";
  private static final String PASSWORD_KEY = "password"; // NOSONAR false positive: Credentials should not be hard-coded
  private final List<String> keysWithSensitiveData = List.of(PASSWORD_KEY);

  @Inject
  private ContentLinkBuilder pageHandler;
  @Inject
  private LiveCAEUriComponentsBuilderCustomizer liveCaeSettings;
  @Inject
  private SettingsService settingsService;
  @Inject
  private SitesService sitesService;
  @Inject
  @Qualifier(value = "contentContextStrategy")
  private ContextStrategy<Content, Content> contextStrategy;

  @Override
  public String getReportUrlFor(Content content) {
    if(content.getType().isSubtypeOf("CMLinkable")) {
      final Content navigation = contextStrategy.findAndSelectContextFor(content, null);
      if(null != navigation) {
        UriComponentsBuilder uriComponentsBuilder = pageHandler.buildLinkForPage(content, navigation);

        if (null != uriComponentsBuilder) {
          if (absolute()) {
            liveCaeSettings.fillIn(uriComponentsBuilder);
          }

          final String linkToSelf = uriComponentsBuilder.build().toUriString();
          final String reportURL = buildReportUrl(content, navigation, linkToSelf);

          LOG.info("report URL for content {} and provider {} is: {}", content, getServiceKey(), reportURL);
          return reportURL;
        } else {
          LOG.info("cannot generate report URL for content {} with navigation {}", content, navigation);
        }
      } else {
        LOG.info("cannot generate report URL for content {}: unable to find navigation context", content);
      }
    } else {
      LOG.debug("cannot generate report URL for non-linkable content {} of type {}", content, content.getType().getName());
    }
    return null;
  }

  protected abstract boolean absolute();

  protected abstract String buildReportUrl(Map<String, Object> settings, String linkToSelf);

  protected static String getFromMap(Map<String, Object> settings, String key, String defaultResult) {
    final Object value = settings.get(key);
    if(value instanceof String){
      final String s = (String) value;
      if(!s.isEmpty()){
        return s;
      }
    }
    return defaultResult;
  }

  private String buildReportUrl(Content content, Content navigation, String linkToSelf) {
    final String serviceKey = getServiceKey();
    Map<String, Object> settings = getInternalSiteSpecificSettings(serviceKey, navigation, sitesService);
    settings.putAll(settingsService.mergedSettingAsMap(serviceKey, String.class, Object.class, content, navigation));

    if (!settings.isEmpty()) {
      String reportURL = buildReportUrl(settings, linkToSelf);
      if(LOG.isInfoEnabled()) {
        LOG.info("generated report URL {} for content {} with settings {}", reportURL, content, hideSensitiveData(settings));
      }
      return reportURL;
    } else {
      LOG.debug("source content {} has no settings for analytics provider {}", content, serviceKey);
    }
    return null;
  }

  private Map<String, Object> hideSensitiveData(Map<String, Object> settings) {
    return settings.entrySet().stream()
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey,
                    entry -> keysWithSensitiveData.contains(entry.getKey()) ? DEFAULT_HIDDEN_TEXT : entry.getValue()));
  }

  private static Map<String, Object> getInternalSiteSpecificSettings(
          String serviceKey,
          Content content,
          SitesService sitesService) {
    Site site = null;
    if (sitesService != null) {
      site = sitesService.getContentSiteAspect(content).getSite();
    }

    if (site != null) {
      Content siteRoot = site.getSiteRootFolder();

      final Content internalSiteSpecificSettings = siteRoot.getChild(INTERNAL_SETTINGS_PATH);
      Struct providerSettingsStruct = getStructForServiceKey(serviceKey, internalSiteSpecificSettings);
      if (providerSettingsStruct != null) {
        return providerSettingsStruct.toNestedMaps();
      }
    }
    return Collections.emptyMap();
  }

  private static Struct getStructForServiceKey(String serviceKey, Content settings) {
    if (settings != null && settings.isInProduction()) {
      Struct settingsStruct = CapStructHelper.getStruct(settings, "settings");
      if (settingsStruct != null) {
        return CapStructHelper.getStruct(settingsStruct, serviceKey);
      }
    }
    return null;
  }

}
