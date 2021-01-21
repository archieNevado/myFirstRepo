package com.coremedia.livecontext.asset.impl;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.events.ContentEvent;
import com.coremedia.cap.content.events.ContentRepositoryListenerBase;

/**
 * A {@link com.coremedia.cap.content.events.ContentRepositoryListener} that reacts on changes
 * of CMPicture documents and stores them in {@link AssetChanges}
 */
class AssetChangesRepositoryListener extends ContentRepositoryListenerBase {

  private static final String CMVISUAL_TYPE = "CMVisual";
  private static final String CMDOWNLOAD_TYPE = "CMDownload";

  private final ContentRepository repository;
  private final AssetChanges assetChanges;

  AssetChangesRepositoryListener(ContentRepository repository, AssetChanges assetChanges) {
    this.repository = repository;
    this.assetChanges = assetChanges;
  }

  @Override
  protected void handleContentEvent(ContentEvent event) {
    Content content = event.getContent();
    if (!content.isDestroyed() &&
            (content.getType().isSubtypeOf(CMVISUAL_TYPE) ||
                    content.getType().isSubtypeOf(CMDOWNLOAD_TYPE))) {
      assetChanges.update(content);
    }
  }

  public void start() {
    repository.addContentRepositoryListener(this);
  }

  public void stop() {
    repository.removeContentRepositoryListener(this);
  }

}
