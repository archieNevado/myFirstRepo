package com.coremedia.blueprint.analytics.elastic.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMCollection;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.cap.content.Content;
import com.coremedia.objectserver.beans.ContentBean;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 *  Implementation class for beans of document type "CMALXEventList".
 */
public class CMALXEventListImpl extends CMALXEventListBase {

  /**
   * @return The unmodified tracked events, which are custom Strings (depending on what you tracked)
   */
  @Override
  public List<CMPicture> getItemsUnfiltered() {
    int maxLength = getMaxLength();

    // although CMALXEventLists are designed to work on arbitrary events (producing arbitrary objects for rendering)
    // we only render pictures for now - just move filtering somewhere else if this is to be refactored

    List<CMPicture> filteredPictures = filterPictures(getTrackedItemsUnfiltered(), maxLength);

    // default content
    if (filteredPictures.isEmpty()) {
      List<Content> defaultContentLinks = getContent().getLinks(CMCollection.ITEMS);
      List<ContentBean> defaultContentBeans = createBeansFor(defaultContentLinks, ContentBean.class);
      filteredPictures = filterPictures(defaultContentBeans, maxLength);
    }
    return filteredPictures;
  }

  @NonNull
  private List<CMPicture> filterPictures(List<? extends Object> contentBeans, int maxLength) {
    return contentBeans.stream()
            .filter(CMPicture.class::isInstance)
            .map(CMPicture.class::cast)
            .limit(maxLength)
            .collect(Collectors.toUnmodifiableList());
  }
}
