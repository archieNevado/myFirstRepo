package com.coremedia.blueprint.cae.sitemap;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;

import java.util.List;
import java.util.function.Predicate;

/**
 * Returns true iff the object in question is a Content instance of a resource
 * type which is relevant for sitemaps.
 */
public class TestUrlsDoctypePredicate implements Predicate {
  private final List<String> includedDoctypes;

  public TestUrlsDoctypePredicate(List<String> includedDoctypes) {
    this.includedDoctypes = includedDoctypes;
  }


  // --- Predicate --------------------------------------------------

  @Override
  public boolean test(Object o) {
    if (!(o instanceof Content)) {
      return false;
    }
    Content content = (Content) o;
    return checkType(content);
  }


  // --- internal ---------------------------------------------------

  private boolean checkType(Content content) {
    // Include only teasable content, exclude non-textual content (media) and dynamic content.
    ContentType type = content.getType();
    return isTypeIncluded(type);
  }

  /**
   * Doctypes which are included by this predicate.
   * @param type The type to check.
   * @return True if type is included, else false.
   */
  private boolean isTypeIncluded(ContentType type) {
    for (String includedDoctype : includedDoctypes) {
      if (type.getName().equals(includedDoctype)) {
        return true;
      }
    }

    return false;
  }
}
