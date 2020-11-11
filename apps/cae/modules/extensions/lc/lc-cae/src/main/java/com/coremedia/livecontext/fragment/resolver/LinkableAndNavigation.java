package com.coremedia.livecontext.fragment.resolver;

import com.coremedia.cap.content.Content;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A simple container for a content and a navigation.
 */
public class LinkableAndNavigation {
  private Content linkable;
  @Nullable
  private Content navigation;

  public LinkableAndNavigation(Content linkable, @Nullable Content navigation) {
    this.linkable = linkable;
    this.navigation = navigation;
  }

  public Content getLinkable() {
    return linkable;
  }

  @Nullable
  public Content getNavigation() {
    return navigation;
  }
}
