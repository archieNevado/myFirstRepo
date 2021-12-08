package com.coremedia.blueprint.common.util;

import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CMObject;
import com.coremedia.objectserver.beans.ContentBeanIdScheme;

import java.util.List;
import java.util.stream.Collectors;

public final class ContentBeanSolrSearchFormatHelper {

  private ContentBeanSolrSearchFormatHelper() {
  }

  private static final String NAVIGATION_PATH_DELIMITER = "\\/";

  /**
   * Generate the <b>numeric</b> search engine representation of a Content Bean id.
   *
   * @param cmObject the Content Bean
   * @return a String representation of the ID of the Content Bean for the search engine
   */
  public static String cmObjectToId(CMObject cmObject) {
    return String.valueOf(cmObject.getContentId());
  }

  /**
   * Generate a search engine representation of a Content Bean id which will look like "contentbean:1234". The double
   * quotes are necessary because the coloin is a special Solr character.
   * <p/>
   * The id within the search engine must always include a prefix because the id could belong to a third party object.
   *
   * @param cmObject the Content Bean
   * @return a String representation of the ID of the Content Bean for the search engine
   */
  public static String getContentBeanId(CMObject cmObject) {
    return '"' + ContentBeanIdScheme.getContentBeanId(cmObject.getContent().getId()) + '"';
  }

  public static List<String> cmObjectsToIds(List<? extends CMObject> cmObjects) {
    return cmObjects.stream()
            .map(input -> {
              if (input == null) {
                throw new IllegalArgumentException("input must not be null");
              }
              return cmObjectToId(input);
            })
            .collect(Collectors.toList());
  }

  public static List<String> cmNavigationsToId(List<? extends CMNavigation> navigations) {
    return navigations.stream()
            .map(input -> {
              if (input==null) {
                throw new IllegalArgumentException("input must not be null");
              }
              return cmNavigationToId(input);
            })
            .collect(Collectors.toList());
  }

  public static String cmNavigationToId(CMNavigation navigation) {
    return getIdBasedNavigationPath(navigation);
  }

  private static String getIdBasedNavigationPath(CMNavigation navigation) {
    StringBuilder sb = new StringBuilder();
    String currentIdBasedNavigationPath = NAVIGATION_PATH_DELIMITER + navigation.getContentId();

    CMNavigation parent = (CMNavigation) navigation.getParentNavigation();
    if (parent == null) {
      return currentIdBasedNavigationPath;
    } else {
      sb.append(getIdBasedNavigationPath(parent)).append(currentIdBasedNavigationPath);
    }
    return sb.toString();
  }
}
