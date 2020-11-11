package com.coremedia.livecontext.ecommerce.ibm.search;

import java.util.List;

/**
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@Deprecated
public class WcSuggestionView {
  private List<WcSuggestion> entry;
  private String identifier;

  public List<WcSuggestion> getEntry() {
    return entry;
  }

  public void setEntry(List<WcSuggestion> entry) {
    this.entry = entry;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }
}
