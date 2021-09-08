package com.coremedia.ecommerce.studio.rest.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCacheInvalidationEvent;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.ecommerce.studio.rest.model.Marketing;
import com.coremedia.ecommerce.studio.rest.model.Segments;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.event.InvalidationEvent;
import com.coremedia.livecontext.ecommerce.event.InvalidationPropagator;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpot;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.rest.invalidations.SimpleInvalidationSource;
import com.coremedia.rest.linking.Linker;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.coremedia.ecommerce.studio.rest.cache.CommerceBeanDelegateProvider.createStoreContext;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;

/**
 * Invalidates studio commerce remote beans based on incoming {@link CommerceCacheInvalidationEvent}s.
 */
@SuppressWarnings("java:S1874" /* "@Deprecated" code should not be used */)
public class CommerceCacheInvalidationSource extends SimpleInvalidationSource implements InvalidationPropagator {

  private static final Logger LOG = LoggerFactory.getLogger(CommerceCacheInvalidationSource.class);

  private static final Set<String> GENERIC_INVALIDATION_URI_PATTERNS = Set.of(
          "livecontext/facets/{siteId:.*}/{catalogAlias:.*}/{workspaceId:.*}/{id:.*}",
          "livecontext/searchfacets/{siteId:.*}/{catalogAlias:.*}/{workspaceId:.*}/{id:.*}",
          "livecontext/{type:.*}/{siteId:.*}/{id:.*}",
          "livecontext/{type:.*}/{siteId:.*}/{workspaceId:.*}",
          "livecontext/{type:.*}/{siteId:.*}/{workspaceId:.*}/{id:.*}",
          "livecontext/workspaces/{siteId:.*}");

  private static final String INVALIDATE_ALL_URI_PATTERN = "livecontext/{suffix:.*}";

  private static final long DELAY = 1000L;

  private TaskScheduler taskScheduler;
  private Linker linker;
  private SettingsService settingsService;

  @Override
  public void afterPropertiesSet() {
    super.afterPropertiesSet();

    if (taskScheduler == null) {
      LOG.info("creating single threaded task scheduler for delayed invalidations");
      taskScheduler = new ConcurrentTaskScheduler();
    }
  }

  @EventListener(CommerceCacheInvalidationEvent.class)
  @Order(1)
  public void invalidate(@NonNull CommerceCacheInvalidationEvent event) {
    Set<String> changes = getCommerceCacheInvalidationUris(event);
    addInvalidations(changes);
  }

  private Set<String> getCommerceCacheInvalidationUris(CommerceCacheInvalidationEvent event) {
    StoreContext storeContext = event.getStoreContext();
    var commerceBeanType = event.getCommerceBeanType().orElse(null);

    if (commerceBeanType == null) {
      // we got no bean type ==> invalidate all
      return GENERIC_INVALIDATION_URI_PATTERNS.stream()
              .map(link -> CommerceBeanDelegateProvider.postProcess(link, storeContext))
              .collect(toSet());
    }

    Set<String> invalidationUris = new HashSet<>();
    var externalId = event.getExternalId().orElse(null);
    invalidationUris.add(toCommerceBeanUri(commerceBeanType, externalId, storeContext).orElseThrow());

    if (commerceBeanType.equals(BaseCommerceBeanType.PRODUCT)) {
      invalidationUris.add(toCommerceBeanUri(BaseCommerceBeanType.SKU, externalId, storeContext).orElseThrow());
    }

    return invalidationUris;
  }

  @Override
  public void invalidate(@NonNull List<InvalidationEvent> invalidations) {
    Optional<InvalidationEvent> clearAll = invalidations.stream()
            .filter(e -> InvalidationEvent.CLEAR_ALL_EVENT.equals(e.getContentType()))
            .findFirst();

    if (clearAll.isPresent()) {
      // Individual remote beans that need to be invalidated cannot be identified.
      // Invalidate all commerce remote beans according to the given pattern.
      addInvalidations(Collections.singleton(INVALIDATE_ALL_URI_PATTERN));
      return;
    }

    // no clear all found: let's create resource URIs for all invalidation events
    Set<String> toBeInvalidated = invalidations.stream()
            .flatMap(this::addUriToInvalidate)
            .collect(toSet());

    addInvalidations(toBeInvalidated);
  }

  @NonNull
  private Stream<String> addUriToInvalidate(@NonNull InvalidationEvent invalidation) {
    Optional<String> contentType = Optional.ofNullable(invalidation.getContentType());
    return contentType.flatMap(InvalidationEventTypeMapping::get)
            .map(this::link)
            .orElseGet(Stream::empty);
  }

  @NonNull
  private Stream<String> link(@NonNull Class<? extends CommerceBean> aClass) {
    StoreContext storeContext = createStoreContext();
    String entityLink = createLink(aClass, storeContext);

    if (MarketingSpot.class.equals(aClass)) {
      Marketing resourceObject = new Marketing(storeContext);
      return of(entityLink, createLink(resourceObject));
    } else if (Segment.class.equals(aClass)) {
      Segments resourceObject = new Segments(storeContext);
      return of(entityLink, createLink(resourceObject));
    }
    return of(entityLink);
  }

  @NonNull
  private String createLink(@NonNull Class<? extends CommerceBean> aClass, @Nullable StoreContext storeContext) {
    Object commerceBeanProxy = settingsService.createProxy(aClass, CommerceBeanDelegateProvider.get());
    return CommerceBeanDelegateProvider.postProcess(createLink(commerceBeanProxy), storeContext);
  }

  @NonNull
  private String createLink(@NonNull Object resourceObject) {
    return urlDecode(linker.link(resourceObject));
  }

  @NonNull
  private static String urlDecode(@NonNull URI commerceBeanUri) {
    try {
      return URLDecoder.decode(commerceBeanUri.toString(), StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException("Unable to decode '" + commerceBeanUri + "'", e);
    }
  }

  public void invalidateReferences(@NonNull Set<String> references, @Nullable StoreContext storeContext) {
    Set<String> changes = references.stream()
            .map(CommerceIdParserHelper::parseCommerceId)
            .flatMap(Optional::stream)
            .map(commerceId -> toCommerceBeanUri(commerceId.getCommerceBeanType(), commerceId.getExternalId().orElse(null), storeContext))
            .flatMap(Optional::stream)
            .collect(Collectors.toSet());

    triggerDelayedInvalidation(changes);
  }

  /**
   * Creates the entity resource URI with matching ID if commerce id contains a part number (external id). Otherwise,
   * all entities of the commerce id's bean type are invalidated.
   */
  @NonNull
  public Optional<String> toCommerceBeanUri(@NonNull CommerceBeanType type, @Nullable String externalId, @Nullable StoreContext storeContext) {
    return toCommerceBeanUri(type.type(), storeContext)
            .map(s -> externalId == null ? s : CommerceBeanDelegateProvider.forEncodedExternalId(s, externalId));
  }

  @NonNull
  private Optional<String> toCommerceBeanUri(@NonNull String commerceBeanTypeString,
                                             @Nullable StoreContext storeContext) {
    return InvalidationEventTypeMapping.get(commerceBeanTypeString)
            .map(aClass -> createLink(aClass, storeContext));
  }

  /**
   * Trigger studio resource invalidation with 1s delay due to possible race conditions.
   * <p>
   * Example: The computation of the catalog picture by the cae might not has been finished, when the invalidation is
   * triggered.
   * <p>
   * Studio might show an outdated product picture.
   */
  private void triggerDelayedInvalidation(@NonNull Set<String> invalidations) {
    if (invalidations.isEmpty()) {
      // Do not schedule a task if there is nothing to invalidate.
      return;
    }

    Runnable addInvalidations = () -> addInvalidations(invalidations);

    Date startTime = new Date(System.currentTimeMillis() + DELAY);

    taskScheduler.schedule(addInvalidations, startTime);
  }

  @Autowired(required = false)
  void setTaskScheduler(TaskScheduler taskScheduler) {
    this.taskScheduler = taskScheduler;
  }

  @Autowired
  void setLinker(Linker linker) {
    this.linker = linker;
  }

  @Autowired
  void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }
}
