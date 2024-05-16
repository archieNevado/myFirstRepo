package com.coremedia.blueprint.cae.sitemap;

import com.coremedia.cap.content.Content;

import java.util.function.Predicate;

public class ExcludeFromSearchSitemapPredicate implements Predicate {

  private final String doctypeName;
  private final String notSearchablePropertyName;

  public ExcludeFromSearchSitemapPredicate(String doctypeName, String notSearchablePropertyName) {
    this.doctypeName = doctypeName;
    this.notSearchablePropertyName = notSearchablePropertyName;
  }

  @Override
  public boolean test(Object o) {
    return o instanceof Content && checkIsSearchable((Content) o);
  }

  /**
   * If its not the content type or the searchable flag is not set, return true, false otherwise
   *
   * @param o any content that must be checked
   * @return true if the content is not from given doctype or the searchable flag is not set.
   */
  private boolean checkIsSearchable(Content o) {
    return !o.getType().isSubtypeOf(doctypeName) || !o.getBoolean(notSearchablePropertyName);
  }

}
