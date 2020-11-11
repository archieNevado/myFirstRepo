package com.coremedia.livecontext.ecommerce.ibm.search;

import java.util.List;

/**
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@Deprecated
public class WcSuggestionViews {
  private List<WcSuggestionView> suggestionView;

  public List<WcSuggestionView> getSuggestionView() {
    return suggestionView;
  }

  public void setSuggestionView(List<WcSuggestionView> suggestionView) {
    this.suggestionView = suggestionView;
  }
}
