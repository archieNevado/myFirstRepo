package com.coremedia.blueprint.cae.search.solr;

import com.coremedia.blueprint.cae.search.SearchQueryBean;

/**
 * Interface to pre-process a search query bean.
 */
public interface SearchPreprocessor {

  /**
   * Process the given search query bean. May be used to rewrite the {@link SearchQueryBean#query}, for example.
   *
   * @param searchQueryBean a search query bean
   */
  void preProcess(SearchQueryBean searchQueryBean);
}
