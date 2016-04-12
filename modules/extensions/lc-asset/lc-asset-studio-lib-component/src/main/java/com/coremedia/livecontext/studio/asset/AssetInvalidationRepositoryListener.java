package com.coremedia.livecontext.studio.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdHelper;
import com.coremedia.blueprint.base.livecontext.studio.cache.CommerceCacheInvalidationSource;
import com.coremedia.blueprint.base.livecontext.util.CommerceReferenceHelper;
import com.coremedia.blueprint.common.contentbeans.CMDownload;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.blueprint.common.contentbeans.CMVideo;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.events.ContentEvent;
import com.coremedia.cap.content.events.ContentRepositoryListenerBase;
import com.coremedia.cap.content.events.PropertiesChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A {@link com.coremedia.cap.content.events.ContentRepositoryListener}
 * which invalidates commerce remote beans in the studio associated with assets in the repository.
 * The invalidation is triggered by atomic content events like creation, deletion etc.
 * Invalidation triggered by property change events are handled by
 * {@link AssetInvalidationWriteInterceptor} and
 * {@link AssetInvalidationWritePostProcessor}
 */
class AssetInvalidationRepositoryListener extends ContentRepositoryListenerBase implements SmartLifecycle {

  static final HashSet<String> INVALIDATION_ALL = new HashSet<>(Arrays.asList(
          CommerceCacheInvalidationSource.INVALIDATE_CATEGORIES_URI_PATTERN,
          CommerceCacheInvalidationSource.INVALIDATE_PRODUCTS_URI_PATTERN,
          CommerceCacheInvalidationSource.INVALIDATE_PRODUCTVARIANTS_URI_PATTERN));
  static List<String> EVENT_WHITELIST = Arrays.asList(
          ContentEvent.CONTENT_CREATED,
          ContentEvent.CONTENT_DELETED,
          ContentEvent.CONTENT_MOVED,
          ContentEvent.CONTENT_RENAMED,
          ContentEvent.CONTENT_REVERTED,
          ContentEvent.CONTENT_UNDELETED);

  private final AtomicBoolean running = new AtomicBoolean(false);

  private CommerceCacheInvalidationSource commerceCacheInvalidationSource;
  private ContentRepository repository;

  @Override
  protected void handleContentEvent(ContentEvent event) {

    if (EVENT_WHITELIST.contains(event.getType())) {
      Content content = event.getContent();
      if (content != null && isRelevantType(content)){
        HashSet<String> invalidations = getInvalidations(event);
        if(!invalidations.isEmpty()) {
          commerceCacheInvalidationSource.triggerDelayedInvalidation(
                  invalidations);
        }
      }
    }
  }

  @Override
  public void propertiesChanged(PropertiesChangedEvent event) {
    super.propertiesChanged(event);
  }

  @Override
  public boolean isAutoStartup() {
    return true;
  }

  @Override
  public void stop(Runnable callback) {
    stop();
    callback.run();
  }

  @Override
  public void start() {
    if(!running.getAndSet(true)) {
      repository.addContentRepositoryListener(this);
    }
  }

  @Override
  public void stop() {
    if(running.getAndSet(false)) {
      repository.removeContentRepositoryListener(this);
    }
  }

  @Override
  public boolean isRunning() {
    return running.get();
  }

  @Override
  public int getPhase() {
    return 0;
  }

  @Autowired
  public void setCommerceCacheInvalidationSource(CommerceCacheInvalidationSource commerceCacheInvalidationSource) {
    this.commerceCacheInvalidationSource = commerceCacheInvalidationSource;
  }

  @Autowired
  public void setRepository(ContentRepository repository) {
    this.repository = repository;
  }

  private HashSet<String> getInvalidations(ContentEvent event) {
    if (event.getType().equals(ContentEvent.CONTENT_REVERTED)) {
      //when a content ist reverted we don't know the old external references.
      // So we have to invalidate all relevant catalog types
      return INVALIDATION_ALL;
    }

    HashSet<String> invalidations = new HashSet<>();
    List<String> externalReferences = CommerceReferenceHelper.getExternalReferences(event.getContent());
    for (String externalReference : externalReferences) {
      if (BaseCommerceIdHelper.isCategoryId(externalReference)) {
        invalidations.add(CommerceCacheInvalidationSource.INVALIDATE_CATEGORIES_URI_PATTERN);
      } else if (BaseCommerceIdHelper.isProductId(externalReference)) {
        invalidations.add(CommerceCacheInvalidationSource.INVALIDATE_PRODUCTS_URI_PATTERN);
        //product variants inherit pictures from master product when they don't have assigend pictures.
        invalidations.add(CommerceCacheInvalidationSource.INVALIDATE_PRODUCTVARIANTS_URI_PATTERN);
      } else if (BaseCommerceIdHelper.isSkuId(externalReference)) {
        invalidations.add(CommerceCacheInvalidationSource.INVALIDATE_PRODUCTVARIANTS_URI_PATTERN);
      }
    }
    return invalidations;
  }

  /**
   *
   * @param content
   * @return true if the content is a picture, video or a download or one of their subtypes.
   */
  private boolean isRelevantType(Content content) {
    return content.getType().isSubtypeOf(CMPicture.NAME) ||
           content.getType().isSubtypeOf(CMVideo.NAME) ||
           content.getType().isSubtypeOf(CMDownload.NAME);
  }
}
