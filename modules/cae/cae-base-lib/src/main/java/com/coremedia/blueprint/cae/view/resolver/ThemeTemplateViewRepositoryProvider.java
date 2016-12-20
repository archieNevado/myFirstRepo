package com.coremedia.blueprint.cae.view.resolver;

import com.coremedia.blueprint.base.util.ContentCacheKey;
import com.coremedia.blueprint.theme.ThemeService;
import com.coremedia.cache.Cache;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.util.JarBlobResourceLoader;
import com.coremedia.objectserver.view.ViewRepository;
import com.coremedia.objectserver.view.resolver.AbstractTemplateViewRepositoryProvider;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Collections2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ThemeTemplateViewRepositoryProvider extends AbstractTemplateViewRepositoryProvider {
  private static final Logger LOG = LoggerFactory.getLogger(ThemeTemplateViewRepositoryProvider.class);

  private static final String THEME_VIEW_REPOSITORY_NAME_PREFIX = "theme:";
  private static final String CM_TEMPLATESET_ARCHIVE = "archive";
  private static final String TEMPLATES_PATH_PREFIX = "META-INF/resources/WEB-INF/templates";

  private Cache cache;

  private ContentRepository contentRepository;
  private ThemeService themeService;
  private JarBlobResourceLoader jarBlobResourceLoader;
  private boolean useLocalResources = false;


  // --- construct and configure ------------------------------------

  public void setCache(Cache cache) {
    this.cache = cache;
  }

  @Required
  public void setThemeService(ThemeService themeService) {
    this.themeService = themeService;
  }

  @Required
  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }

  @Required
  public void setJarBlobResourceLoader(JarBlobResourceLoader jarBlobResourceLoader) {
    this.jarBlobResourceLoader = jarBlobResourceLoader;
  }

  public void setUseLocalResources(boolean useLocalResources) {
    this.useLocalResources = useLocalResources;
  }


  // --- ViewRepositoryProvider -------------------------------------

  /**
   * Get the view repository for the given name.
   * <p>
   * Implementation restriction: The view repository provides all templates
   * of the theme, which may include other view repository names.
   *
   * @return A view repository or null if this ViewRepositoryProvider is not responsible
   */
  @Override
  public ViewRepository getViewRepository(String name) {
    if (!isThemeViewRepository(name)) {
      // We encounter this case for alien repository names, but also
      // for our own #viewRepositoryNames(Content theme), if
      // * useLocalResources is set, or
      // * a theme uses the deprecated viewRepositoryName property rather than
      //   bringing its own templates.
      return null;
    }
    return createViewRepository(templateLocations(name));
  }


  // --- more features ----------------------------------------------

  /**
   * Returns the view repository names of the theme.
   */
  List<String> viewRepositoryNames(Content theme) {
    if (cache!=null) {
      return cache.get(new ViewRepositoryNamesCacheKey(theme));
    } else {
      LOG.warn("No cache. Ok for development/test, too slow for production.");
      return viewRepositoryNamesUncached(theme);
    }
  }


  // --- internal ---------------------------------------------------

  /**
   * Returns the template locations for the given view repository name.
   * <p>
   * The format is suitable for the {@link JarBlobResourceLoader}.
   *
   * @return the templates locations of the theme
   */
  @VisibleForTesting
  List<String> templateLocations(String themeViewRepositoryName) {
    if (!isThemeViewRepository(themeViewRepositoryName)) {
      throw new IllegalArgumentException(themeViewRepositoryName + " is not a theme backed view repository.");
    }
    ThemeViewRepositoryName tvrn = new ThemeViewRepositoryName(themeViewRepositoryName);
    Content theme = tvrn.theme();
    if (cache!=null) {
      return cache.get(new TemplateLocationsCacheKey(theme));
    } else {
      LOG.warn("No cache. Ok for development/test, too slow for production.");
      return templateLocationsUncached(theme);
    }
  }

  private List<String> viewRepositoryNamesUncached(Content theme) {
    List<String> locations = new ArrayList<>();
    for (Content templateSet : themeService.templateSets(theme)) {
      locations.addAll(viewRepositoryNamesFromJar(templateSet).stream().map(vrn -> viewRepositoryName(theme, vrn)).collect(toList()));
    }
    if (!locations.isEmpty()) {
      return locations;
    }

    // Legacy fallback:
    // The theme does not bring its own templates but assumes a certain
    // view repository to exist.  Leave the name as is, it won't be served by
    // #getViewRepository but hopefully by some other ViewRepositoryProvider.
    String legacyVRN = themeService.viewRepositoryName(theme);
    return legacyVRN==null ? Collections.emptyList() : Collections.singletonList(legacyVRN);
  }

  private String viewRepositoryName(Content theme, String vrn) {
    if (useLocalResources) {
      // suitable to match tomcat-contexts.xml#Resources#/=${project.basedir}/../../frontend/target/resources
      // e.g. "corporate"
      return vrn;
    } else {
      // suitable to match #templateLocations(String themeViewRepositoryName)
      // e.g. "theme:1234/corporate"
      int themeId = IdHelper.parseContentId(theme.getId());
      return THEME_VIEW_REPOSITORY_NAME_PREFIX + themeId + "/" + vrn;
    }
  }

  /**
   * Check whether the given viewRepositoryName denotes a theme backed
   * view repository.
   */
  private boolean isThemeViewRepository(String viewRepositoryName) {
    return viewRepositoryName.startsWith(THEME_VIEW_REPOSITORY_NAME_PREFIX);
  }

  /**
   * Figure out the view repository names of a template set jar.
   * <p>
   * A template set jar has entries like
   * META-INF/resources/WEB-INF/templates/corporate/com.coremedia.blueprint.common.layout/Container.asGap.ftl
   * or more abstract:
   * prefix/view-repository/package/template
   * In this example "corporate" would be the view repository name.
   */
  private Collection<String> viewRepositoryNamesFromJar(Content templateSet) {
    String location = jarBlobResourceLoader.toLocation(templateSet, CM_TEMPLATESET_ARCHIVE, TEMPLATES_PATH_PREFIX);
    return jarBlobResourceLoader.getChildren(location, true, false, true);
  }

  private List<String> templateLocationsUncached(Content theme) {
    List<String> locations = new ArrayList<>();
    for (Content templateSet : themeService.templateSets(theme)) {
      locations.addAll(templatesRoots(templateSet));
    }
    return locations;
  }

  private Collection<String> templatesRoots(Content templateSet) {
    String location = jarBlobResourceLoader.toLocation(templateSet, CM_TEMPLATESET_ARCHIVE, TEMPLATES_PATH_PREFIX);
    Collection<String> paths = jarBlobResourceLoader.getChildren(location, false, false, true);
    return Collections2.transform(paths, (String s) -> jarBlobResourceLoader.toLocation(templateSet, CM_TEMPLATESET_ARCHIVE, s));
  }


  // --- inner classes ----------------------------------------------

  private class ThemeViewRepositoryName {
    int themeId;
    String viewRepositoryName;

    ThemeViewRepositoryName(String themeViewRepositoryName) {
      try {
        int slash = themeViewRepositoryName.indexOf('/');
        String idSubstring = themeViewRepositoryName.substring(THEME_VIEW_REPOSITORY_NAME_PREFIX.length(), slash);
        themeId = Integer.parseInt(idSubstring);
        viewRepositoryName = themeViewRepositoryName.substring(slash+1);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Cannot parse theme view repository name " + viewRepositoryName, e);
      }
    }

    Content theme() {
      return contentRepository.getContent(IdHelper.formatContentId(themeId));
    }
  }

  private class TemplateLocationsCacheKey extends ContentCacheKey<List<String>> {
    TemplateLocationsCacheKey(Content content) {
      super(content);
    }

    @Override
    public List<String> evaluate(Cache cache) {
      return templateLocationsUncached(getContent());
    }
  }

  private class ViewRepositoryNamesCacheKey extends ContentCacheKey<List<String>> {
    ViewRepositoryNamesCacheKey(Content content) {
      super(content);
    }

    @Override
    public List<String> evaluate(Cache cache) {
      return viewRepositoryNamesUncached(getContent());
    }
  }
}
