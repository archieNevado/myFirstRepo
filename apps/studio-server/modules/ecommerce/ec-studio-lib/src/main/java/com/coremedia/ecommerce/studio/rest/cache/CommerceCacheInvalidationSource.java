package com.coremedia.ecommerce.studio.rest.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCacheInvalidationEvent;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.rest.invalidations.SimpleInvalidationSource;
import com.coremedia.rest.linking.Linker;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

/**
 * Invalidates studio commerce remote beans based on incoming {@link CommerceCacheInvalidationEvent}s.
 */
public class CommerceCacheInvalidationSource extends SimpleInvalidationSource {

  private static final Logger LOG = LoggerFactory.getLogger(CommerceCacheInvalidationSource.class);

  private static final Set<String> GENERIC_INVALIDATION_URI_PATTERNS = Set.of(
          "livecontext/{type:.*}/{siteId:.*}/{catalogAlias:.*}/{id:.*}",
          "livecontext/{type:.*}/{siteId:.*}/{id:.*}",
          "livecontext/{type:.*}/{siteId:.*}");

  private static final long DELAY = 1000L;

  private final TaskScheduler taskScheduler;
  private final Linker linker;
  private final SettingsService settingsService;
  private final CommerceBeanClassResolver commerceBeanClassResolver;

  public CommerceCacheInvalidationSource(ObjectProvider<TaskScheduler> taskScheduler,
                                         Linker linker,
                                         SettingsService settingsService,
                                         CommerceBeanClassResolver commerceBeanClassResolver) {
    this.taskScheduler = taskScheduler.getIfAvailable(CommerceCacheInvalidationSource::getDefaultTaskScheduler);
    this.linker = linker;
    this.settingsService = settingsService;
    this.commerceBeanClassResolver = commerceBeanClassResolver;
  }

  private static TaskScheduler getDefaultTaskScheduler() {
    LOG.info("Creating single threaded task scheduler for delayed invalidations.");
    return new ConcurrentTaskScheduler();
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
    return toCommerceBeanUri(type, storeContext)
            .map(s -> externalId == null ? s : CommerceBeanDelegateProvider.forEncodedExternalId(s, externalId));
  }

  @NonNull
  private Optional<String> toCommerceBeanUri(@NonNull CommerceBeanType commerceBeanType,
                                             @Nullable StoreContext storeContext) {
    return commerceBeanClassResolver.findByType(commerceBeanType)
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
}
