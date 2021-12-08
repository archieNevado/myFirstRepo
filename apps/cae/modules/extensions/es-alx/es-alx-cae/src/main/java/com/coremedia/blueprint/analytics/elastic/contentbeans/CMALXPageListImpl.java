package com.coremedia.blueprint.analytics.elastic.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The bean corresponding to the <code>CMALXPageList</code> document type. It selects its list of
 * {@link CMLinkable}s by firing an analytics query.
 */
public class CMALXPageListImpl extends CMALXPageListBase {

  /**
   * Get the list of {@link CMLinkable}s selected by an analytics query.
   *
   * @return the list of {@link CMLinkable}s selected by an analytics query
   */
  @Override
  public List<CMLinkable> getItemsUnfiltered() {
    int maxLength = getMaxLength();

    List<CMLinkable> trackedLinkables = filterLinkables(getTrackedItemsUnfiltered(), maxLength);


    // default content
    if (trackedLinkables.isEmpty()) {
      trackedLinkables = filterLinkables(getDefaultContent(), maxLength);
    }
    return trackedLinkables;
  }


  private List<CMLinkable> filterLinkables(List<? extends Object> contentBeans, int maxLength) {
    return contentBeans.stream()
            .filter(CMLinkable.class::isInstance)
            .map(CMLinkable.class::cast)
            .limit(maxLength)
            .collect(Collectors.toUnmodifiableList());
  }
}
