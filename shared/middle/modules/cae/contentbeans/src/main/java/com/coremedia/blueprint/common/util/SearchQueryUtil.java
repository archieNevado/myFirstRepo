package com.coremedia.blueprint.common.util;

import com.coremedia.blueprint.cae.search.Condition;
import com.coremedia.blueprint.cae.search.SearchConstants;
import com.coremedia.blueprint.cae.search.SearchQueryBean;
import com.coremedia.blueprint.cae.search.Value;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.google.common.base.Splitter;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * Utility methods for search queries
 */
@DefaultAnnotation(NonNull.class)
public final class SearchQueryUtil {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private SearchQueryUtil() {
    // utility class
  }

  /**
   * Adds a filter for document types to the given search query as specified by the given comma-separated
   * string of document types.
   *
   * @param searchQuery search query
   * @param docTypes comma-separated string of content type names, possibly null or empty
   * @param repository the content repository
   */
  public static void addDocumentTypeFilter(SearchQueryBean searchQuery,
                                           @Nullable String docTypes,
                                           ContentRepository repository) {
    createDocumentTypeFilter(docTypes, repository).ifPresent(searchQuery::addFilter);
  }

  /**
   * Converts the given comma-separated string of document types into a search filter {@link Condition}.
   *
   * @param docTypes comma-separated string of content type names, possibly null or empty
   * @param repository the content repository
   * @return optional condition for use as document type filter, empty value for no restriction
   */
  public static Optional<Condition> createDocumentTypeFilter(@Nullable String docTypes, ContentRepository repository) {
    if (docTypes == null) {
      return Optional.empty();
    }

    // use a sorted set to have a deterministic order of types, so that Solr's filter cache can be used efficiently
    Set<String> types = new TreeSet<>();

    for (String contentTypeName: Splitter.on(',').trimResults().omitEmptyStrings().split(docTypes)) {
      ContentType contentType = repository.getContentType(contentTypeName);
      if (contentType != null) {
        contentType.getSubtypes().stream()
                .filter(ContentType::isConcrete)
                .map(ContentType::getName)
                .forEachOrdered(types::add);
      } else {
        // The content type does not exist. This will lead to an empty result, except for the rare case that
        // content hasn't been re-indexed after a type was renamed. So this is probably an error made by the
        // caller - let's log a warning, but still include it in the condition for functional correctness.
        LOG.warn("Constructing search query with filter for non-existing content type '{}'.", contentTypeName);
        types.add(contentTypeName);
      }
    }

    if (types.isEmpty()) {
      return Optional.empty();
    }

    Condition condition = Condition.is(SearchConstants.FIELDS.DOCUMENTTYPE, Value.anyOf(types));
    return Optional.of(condition);
  }

}
