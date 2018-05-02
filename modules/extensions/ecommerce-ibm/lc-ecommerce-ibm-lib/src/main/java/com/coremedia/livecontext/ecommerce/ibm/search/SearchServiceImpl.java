package com.coremedia.livecontext.ecommerce.ibm.search;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.search.SearchService;
import com.coremedia.livecontext.ecommerce.search.SuggestionResult;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * IBM Commerce Service implementation.
 */
public class SearchServiceImpl implements SearchService {
  private WcSearchWrapperService searchWrapperService;

  @Override
  public List<SuggestionResult> getAutocompleteSuggestions(String term, @Nonnull StoreContext currentContext) {
    List<WcSuggestion> wcSuggestions = searchWrapperService.getKeywordSuggestionsByTerm(term, currentContext);

    if (wcSuggestions == null || wcSuggestions.isEmpty()) {
      return emptyList();
    }

    return wcSuggestions.stream()
            .map(wcSuggestion -> new SuggestionResult(wcSuggestion.getTerm(), term, wcSuggestion.getFrequency()))
            .collect(toList());
  }

  public WcSearchWrapperService getSearchWrapperService() {
    return searchWrapperService;
  }

  @Required
  public void setSearchWrapperService(WcSearchWrapperService searchWrapperService) {
    this.searchWrapperService = searchWrapperService;
  }

}
