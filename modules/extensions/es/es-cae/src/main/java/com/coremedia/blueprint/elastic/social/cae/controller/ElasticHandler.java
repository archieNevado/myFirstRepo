package com.coremedia.blueprint.elastic.social.cae.controller;


import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.handlers.HandlerBase;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.cae.web.i18n.PageResourceBundleFactory;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.elastic.social.cae.ElasticSocialService;
import com.coremedia.blueprint.elastic.social.cae.user.ElasticSocialUserHelper;
import com.coremedia.blueprint.base.elastic.social.common.ContributionTargetHelper;
import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialPlugin;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import javax.inject.Inject;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

public abstract class ElasticHandler<T extends ContributionResult> extends HandlerBase {

  public static final String ROOT_SEGMENT = "segment";
  public static final String CONTEXT_ID = "contextId";
  public static final String ID = "id";

  public static final String SUCCESS_MESSAGE = "success";
  public static final String ERROR_MESSAGE = "error";

  private SitesService sitesService;
  private SettingsService settingsService;
  private ElasticSocialPlugin elasticSocialPlugin;
  private ElasticSocialService elasticSocialService;
  private ElasticSocialUserHelper elasticSocialUserHelper;
  private NavigationSegmentsUriHelper navigationSegmentsUriHelper;
  private ContributionTargetHelper contributionTargetHelper;
  private PageResourceBundleFactory resourceBundleFactory;

  @Inject
  public void setContributionTargetHelper(ContributionTargetHelper contributionTargetHelper) {
    this.contributionTargetHelper = contributionTargetHelper;
  }

  @Inject
  public void setNavigationSegmentsUriHelper(NavigationSegmentsUriHelper navigationSegmentsUriHelper) {
    this.navigationSegmentsUriHelper = navigationSegmentsUriHelper;
  }

  @Inject
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Inject
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Inject
  public void setPageResourceBundleFactory(PageResourceBundleFactory resourceBundleFactory) {
    this.resourceBundleFactory = resourceBundleFactory;
  }

  @Inject
  public void setElasticSocialUserHelper(ElasticSocialUserHelper elasticSocialUserHelper) {
    this.elasticSocialUserHelper = elasticSocialUserHelper;
  }

  public ElasticSocialUserHelper getElasticSocialUserHelper() {
    return elasticSocialUserHelper;
  }

  public SitesService getSitesService() {
    return sitesService;
  }

  @Inject
  public void setElasticSocialPlugin(ElasticSocialPlugin elasticSocialPlugin) {
    this.elasticSocialPlugin = elasticSocialPlugin;
  }

  public ElasticSocialService getElasticSocialService() {
    return elasticSocialService;
  }

  @Inject
  public void setElasticSocialService(ElasticSocialService elasticSocialService) {
    this.elasticSocialService = elasticSocialService;
  }

  protected UriComponentsBuilder getUriComponentsBuilder(UriTemplate uriTemplate, Navigation navigation, Object id) {
    final String segment = navigationSegmentsUriHelper.getPathList(navigation).get(0);
    final CMContext navigationContext = navigation.getContext();
    if (navigationContext == null) {
      throw new IllegalArgumentException("Cannot resolve 'navigation' parameter when building link for content " + id);
    }
    final int nearestNavigationId = navigationContext.getContentId();
    final URI uri = uriTemplate.expand(segment, nearestNavigationId, id);
    return UriComponentsBuilder.fromUri(uri);
  }

  protected UriComponents buildFragmentUri(Site site,
                                         T result,
                                         UriTemplate uriTemplate,
                                         Map<String, Object> linkParameters) {
    UriComponentsBuilder uriBuilder = getUriComponentsBuilder(site, result, uriTemplate);
    addLinkParametersAsQueryParameters(uriBuilder, linkParameters);
    return uriBuilder.build();
  }

  protected abstract UriComponentsBuilder getUriComponentsBuilder(Site site, T result, UriTemplate uriTemplate);

  /**
   * @deprecated Use #getMessage(Navigation, String)
   */
  @Deprecated
  protected String getMessage(String key, Object... beans) {
    for (Object bean : beans) {
      if (bean instanceof Navigation) {
        String result = doGetMessage((Navigation)bean, key, null);
        if (result!=null) {
          return result;
        }
      }
    }

    // backward compatible fallback
    LOG.debug("Running into the deprecated part of ElasticHandler#getMessage.  Fix your resource bundles!");
    return settingsService.settingWithDefault(key, String.class, key, beans);
  }

  protected String getMessage(Navigation navigation, String key) {
    return doGetMessage(navigation, key, key);
  }

  protected ElasticSocialConfiguration getElasticSocialConfiguration(Object... beans) {
    return elasticSocialPlugin.getElasticSocialConfiguration(beans);
  }

  protected List getBeansForSettings(Object target, Navigation navigation) {
    List<Object> beans = new ArrayList<>();
    Content content = contributionTargetHelper.getContentFromTarget(target);
    if (content != null) {
      beans.add(content);
    } else {
      beans.add(target);
    }
    beans.add(navigation);

    return beans;
  }

  protected void addErrorMessage(HandlerInfo handlerInfo, String path, Navigation navigation, String messageKey) {
    doAddErrorMessage(handlerInfo, path, getMessage(navigation, messageKey));
  }

  /**
   * @deprecated Use #addErrorMessage(HandlerInfo, String, Navigation, String)
   */
  @Deprecated
  protected void addErrorMessage(HandlerInfo handlerInfo, String path, String messageKey, Object... beans) {
    doAddErrorMessage(handlerInfo, path, getMessage(messageKey, beans));
  }

  private String doGetMessage(Navigation navigation, String key, String defaultValue) {
    try {
      return resourceBundleFactory.resourceBundle(navigation).getString(key);
    } catch (MissingResourceException e) {
      return defaultValue;
    }
  }

  private void doAddErrorMessage(HandlerInfo handlerInfo, String path, String message) {
    handlerInfo.addMessage(ERROR_MESSAGE, path, message);
    handlerInfo.setSuccess(false);
  }
}
