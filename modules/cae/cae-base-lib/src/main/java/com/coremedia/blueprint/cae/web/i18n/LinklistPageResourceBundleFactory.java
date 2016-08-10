package com.coremedia.blueprint.cae.web.i18n;

import com.coremedia.blueprint.base.util.ObjectCacheKey;
import com.coremedia.blueprint.base.util.StructUtil;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.localization.LocalizationService;
import com.coremedia.blueprint.theme.ThemeService;
import com.coremedia.cache.Cache;
import com.coremedia.cap.common.CapStructHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.struct.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * An implementation of PageResourceBundleFactory which is backed by the
 * resource bundles of the page's navigation and its theme.
 */
public class LinklistPageResourceBundleFactory implements PageResourceBundleFactory {
  private static final Logger LOG = LoggerFactory.getLogger(LinklistPageResourceBundleFactory.class);

  private static final String LINKABLE_RESOURCEBUNDLES = "resourceBundles2";  // CMLinkable#resourceBundles
  private static final String THEME_RESOURCEBUNDLES = "resourceBundles";  // CMTheme#resourceBundles
  private static final String RESOURCEBUNDLE_LOCALIZATIONS = "localizations";  // CMResourceBundle#localizations

  private Cache cache = null;
  private boolean useLocalresources = false;

  private SitesService sitesService;
  private LocalizationService localizationService;
  private ThemeService themeService;


  // --- configure --------------------------------------------------

  /**
   * Usage of a cache is strongly recommended for production use.
   */
  public void setCache(Cache cache) {
    this.cache = cache;
  }

  /**
   * Only for frontend development.
   * <p>
   * MUST NOT be set in production instances, because it disables caching of
   * resource bundles.
   */
  public void setUseLocalresources(boolean useLocalresources) {
    this.useLocalresources = useLocalresources;
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Required
  public void setLocalizationService(LocalizationService localizationService) {
    this.localizationService = localizationService;
  }

  @Required
  public void setThemeService(ThemeService themeService) {
    this.themeService = themeService;
  }


  // --- PageResourceBundleFactory ----------------------------------

  @Override
  public ResourceBundle resourceBundle(Page page) {
    // For performance and cache size reasons this implementation supports
    // resource bundles only for the page's navigation.  If you really need
    // resource bundles at content level, you can include the page content's
    // resourceBundles here.
    return resourceBundle(page.getNavigation());
  }

  @Override
  public ResourceBundle resourceBundle(Navigation navigation) {
    if (useLocalresources || cache==null) {
      LOG.warn("Using " + getClass().getName() + " without cache.  Ok for testing, too expensive for production.");
      return fetchNavigationResourceBundle(navigation);
    } else {
      return cache.get(new NavigationCacheKey(navigation));
    }
  }


  // --- internal ---------------------------------------------------

  private ResourceBundle fetchNavigationResourceBundle(Navigation navigation) {
    Struct struct = hierarchicalMergedResourceBundles(navigation);
    return struct == null ? EmptyResourceBundle.emptyResourceBundle() : CapStructHelper.asResourceBundle(struct);
  }

  private Struct hierarchicalMergedResourceBundles(Navigation navigation) {
    Struct bundle = null;
    for (Navigation nav = navigation; nav!=null; nav = nav.getParentNavigation()) {
      if (nav instanceof CMNavigation) {
        Content navContent = ((CMNavigation)nav).getContent();
        bundle = StructUtil.mergeStructs(bundle, mergedResourceBundles(navContent));
        bundle = StructUtil.mergeStructs(bundle, mergedResourceBundlesFromTheme(navContent));
      }
    }
    return bundle;
  }

  private static Struct mergedResourceBundles(Content linkable) {
    List<Content> bundles = linkable.getLinks(LINKABLE_RESOURCEBUNDLES);
    List<Struct> localizations = new ArrayList<>();
    for (Content bundle : bundles) {
      localizations.add(bundle.getStruct(RESOURCEBUNDLE_LOCALIZATIONS));
    }
    return StructUtil.mergeStructList(localizations);
  }

  /**
   * Instead of doing a simple lookup of just the cmNavigation's locale, this method will merge resourcebundles by
   * 1. language, country and variant
   * 2. language and country
   * 3. or just a country.
   * <p>
   * Therefore an editor will only need to link one resource bundle if all resource bundles are linked via the master linklist.
   * In case of a translation there is no need to link one specific bundle anymore.
   *
   * @param cmNavigation the navigation containing the theme and the locale
   * @return a Struct containing the localizations.
   */
  private Struct mergedResourceBundlesFromTheme(Content cmNavigation) {
    List<Struct> structs = new ArrayList<>();
    Content theme = themeService.theme(cmNavigation);
    if (theme != null) {
      List<Content> bundles = theme.getLinks(THEME_RESOURCEBUNDLES);
      Locale locale = sitesService.getContentSiteAspect(cmNavigation).getLocale();
      structs.add(localizationService.resources(bundles, locale));
    }
    return StructUtil.mergeStructList(structs);
  }


  // --- caching ----------------------------------------------------

  private class NavigationCacheKey extends ObjectCacheKey<Navigation, ResourceBundle> {
    NavigationCacheKey(Navigation navigation) {
      super(navigation);
    }

    @Override
    public ResourceBundle evaluate(Cache cache) throws Exception {
      // Do not add anything here.  Behaviour must be transparent
      // compared to a direct invocation of fetchNavigationResourceBundle.
      return fetchNavigationResourceBundle(getObj());
    }
  }
}
