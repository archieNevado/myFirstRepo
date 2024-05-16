package com.coremedia.blueprint.personalization.search;

import com.coremedia.blueprint.cae.search.SearchQueryBean;
import com.coremedia.blueprint.cae.search.solr.SearchPreprocessor;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.search.SearchFunctionPreprocessor;
import com.coremedia.personalization.search.SearchFunctionUnknownException;
import org.owasp.esapi.ESAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

public class SolrContextualSearchPreprocessor implements SearchPreprocessor {

  private static final Logger LOG = LoggerFactory.getLogger(SolrContextualSearchPreprocessor.class);

  private SearchFunctionPreprocessor searchPreprocessor;
  private ContextCollection contextCollection;

  /**
   * Sets the {@link com.coremedia.personalization.search.SearchFunctionPreprocessor} to be used. This
   * preprocessor is responsible for the actual evaluation of search functions.
   *
   * @param searchPreprocessor the preprocessor to be used
   */
  public void setSearchPreprocessor(final SearchFunctionPreprocessor searchPreprocessor) {
    if (searchPreprocessor == null) {
      throw new IllegalArgumentException("supplied searchPreprocessor must not be null");
    }
    this.searchPreprocessor = searchPreprocessor;
  }

  /**
   * Sets the {@link com.coremedia.personalization.context.ContextCollection} to be used to retrieve the context objects
   * associated with the current HTTP request.
   *
   * @param contextCollection the ContextCollection to be used to retrieve context objects
   */
  public void setContextCollection(final ContextCollection contextCollection) {
    if (contextCollection == null) {
      throw new IllegalArgumentException("supplied contextCollection must not be null");
    }
    this.contextCollection = contextCollection;
  }

  @PostConstruct
  protected void initialize() {
    if (contextCollection == null) {
      throw new IllegalStateException("Required property not set: contextCollection");
    }
    if (searchPreprocessor == null) {
      throw new IllegalStateException("Required property not set: searchPreprocessor");
    }
  }

  @Override
  public void preProcess(SearchQueryBean sqb) {
    String query = sqb.getQuery();
    try {
      query = searchPreprocessor.process(sqb.getQuery(), contextCollection);
    } catch (SearchFunctionUnknownException ex) {
      if (LOG.isWarnEnabled()) {
        LOG.warn("Cannot evaluate searchfunction in query: {}", ESAPI.encoder().encodeForHTML(query), ex);
      }
    }
    sqb.setQuery(query);
  }
}
