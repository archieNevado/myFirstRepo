package com.coremedia.livecontext.p13n.studio {
import com.coremedia.cap.undoc.content.Content;
import com.coremedia.livecontext.p13n.studio.config.commerceCatalogObjectsSelectFormTestView;
import com.coremedia.ui.data.dependencies.DependencyTracker;

import ext.Viewport;

public class CommerceCatalogObjectsSelectFormTestViewBase extends Viewport{
  public native function get content():Content;

  public function CommerceCatalogObjectsSelectFormTestViewBase(config:commerceCatalogObjectsSelectFormTestView = null) {
    super(config);
  }

  [ProvideToExtChildren]
  public function getContent():Content {
    DependencyTracker.dependOnObservable(this, "content");
    return this.content;
  }
}
}