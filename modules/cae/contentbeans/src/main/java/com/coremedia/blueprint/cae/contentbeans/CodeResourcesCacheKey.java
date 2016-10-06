package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CodeResources;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.cap.content.Content;
import com.coremedia.objectserver.dataviews.DataViewHelper;
import com.google.common.annotations.VisibleForTesting;

/**
 * Cache Key for caching the {@link CodeResources} for a {@link CMNavigation}.
 * <p>
 * Equality and evaluation fall back to the nearest ancestor of the given
 * navigation that actually contains code.  So there will be only a few cache
 * entries for those code-carrying channels, not for the (many) subchannels
 * which all inherit the same code resources.
 * <p>
 * Moreover, fewer different CodeResources instances lead to fewer different
 * links, and browser caching additionally relieves the CAE.
 */
public class CodeResourcesCacheKey extends CacheKey<CodeResources> {

  private final boolean developerMode;
  private final String codePropertyName;

  /**
   * The CMNavigation which actually carries the code.
   * <p>
   * Will usually differ from the constructor arg, since most channels
   * inherit their code from some parent channel.
   */
  private final CMNavigation navigation;

  public CodeResourcesCacheKey(CMNavigation navigation, String codePropertyName, boolean developerMode) {
    this.developerMode = developerMode;
    this.codePropertyName = codePropertyName;
    // Since we start with a CMNavigation (not any other Navigation), we can
    // be sure that the result of findRelevantNavigation is not null.
    this.navigation = DataViewHelper.getOriginal(findRelevantNavigation(navigation, codePropertyName));
  }

  @Override
  public CodeResources evaluate(Cache cache) {
    return new CodeResourcesImpl(navigation, codePropertyName, developerMode);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CodeResourcesCacheKey that = (CodeResourcesCacheKey) o;
    return developerMode==that.developerMode &&
           codePropertyName.equals(that.codePropertyName) &&
           navigation.equals(that.navigation);
  }

  @Override
  public int hashCode() {
    int result = developerMode ? 1 : 0;
    result = 31 * result + navigation.hashCode();
    result = 31 * result + codePropertyName.hashCode();
    return result;
  }


  // --- internal ---------------------------------------------------

  // Impl note: if you are about to make this non-static, be aware that it is
  // invoked already by the constructor.
  /**
   * Find the nearest code-carrying ancestor in the navigation hierarchy.
   * <p>
   * If there there is no ancestor with code, return the topmost CMNavigation
   * ancestor (aka root channel).
   */
  @VisibleForTesting
  static CMNavigation findRelevantNavigation(Navigation navigation, String codePropertyName) {
    if (navigation==null) {
      return null;
    }
    CMNavigation cmNavigation = navigation.getContext();
    if (cmNavigation!=null && hasCode(cmNavigation, codePropertyName)) {
      return cmNavigation;
    }
    // No code for navigation itself, fallback to parent.
    // Shortcut: Once we have arrived in the CMNavigation world, stay in there.
    Navigation parent = cmNavigation!=null ? cmNavigation.getParentNavigation() : navigation.getParentNavigation();
    CMNavigation parentResult = findRelevantNavigation(parent, codePropertyName);
    // If there is no parent result either, return the topmost non-code channel.
    return parentResult!=null? parentResult : cmNavigation;
  }

  private static boolean hasCode(CMNavigation cmNavigation, String codePropertyName) {
    Content content = cmNavigation.getContent();
    return content.getLink(CMNavigation.THEME)!=null || !content.getLinks(codePropertyName).isEmpty();
  }
}
