package com.coremedia.blueprint.cae.sitemap;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;

import java.util.List;
import java.util.function.Predicate;

public class SitemapDoctypePredicate implements Predicate {

  private final List<String> includes;
  private final List<String> excludes;

  public SitemapDoctypePredicate(List<String> includes, List<String> excludes) {
    this.includes = includes;
    this.excludes = excludes;
  }

  @Override
  public boolean test(Object o) {
    return o instanceof Content && checkType((Content)o);
  }

  private boolean checkType(Content content) {
    ContentType type = content.getType();

    boolean includeIt = includes == null; //if no list exists, include all, otherwise only list members are included
    if (includes!=null) {
      for (String include : includes) {
        includeIt = includeIt || type.isSubtypeOf(include);
      }
    }

    boolean excludeIt = false;
    if (includeIt && excludes!=null) {
      for (String exclude : excludes) {
        excludeIt = excludeIt || type.isSubtypeOf(exclude);
      }
    }

    return includeIt && !excludeIt;
  }
}
