package com.coremedia.livecontext.ecommerce.sfcc.push;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.cache.Cache;
import com.coremedia.id.IdProvider;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.push.AbstractPushService;
import com.coremedia.livecontext.ecommerce.push.PushState;
import com.coremedia.livecontext.ecommerce.push.SyncStatusStrategy;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.LibrariesResource;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.HttpClientFactory.createHttpClient;
import static com.coremedia.livecontext.ecommerce.push.PushState.State.IN_SYNC;
import static com.coremedia.livecontext.ecommerce.push.PushState.State.NOT_PUSHED;
import static com.coremedia.livecontext.ecommerce.push.PushState.State.PUSHED;
import static com.coremedia.livecontext.ecommerce.push.PushState.State.UNKNOWN;
import static com.coremedia.livecontext.ecommerce.sfcc.push.SfccContentHelper.getExplicitDependency;
import static com.coremedia.livecontext.ecommerce.sfcc.push.SfccPushJsonFactory.createJsonObjectsPerPage;
import static java.util.stream.Collectors.toList;

public class PushServiceImpl extends AbstractPushService {

  private static final Logger LOG = LoggerFactory.getLogger(PushServiceImpl.class);
  public static final String PUSH_MODE_PARAMETER = "pushMode";
  public static final String PUSH_MODE_PARAMETER_VALUE_STRICT = "strict";
  public static final String PUSH_MODE_PARAMETER_VALUE_RECORD = "record";
  public static final String PREVIEW_PARAMETER = "preview";

  private final FragmentParser fragmentParser = new FragmentParser();

  private final LibrariesResource resource;
  private final IdProvider idProvider;
  private final Cache cache;
  private final SfccContentHelper sfccContentHelper;
  private final FetchContentUrlHelper fetchContentUrlHelper;
  private RestTemplate restTemplate;

  public PushServiceImpl(LibrariesResource resource,
                         IdProvider idProvider,
                         SfccContentHelper sfccContentHelper,
                         FetchContentUrlHelper fetchContentUrlHelper,
                         SyncStatusStrategy syncStatusStrategy,
                         Cache cache) {
    super(syncStatusStrategy);
    this.resource = resource;
    this.idProvider = idProvider;
    this.sfccContentHelper = sfccContentHelper;
    this.fetchContentUrlHelper = fetchContentUrlHelper;
    this.cache = cache;
  }

  @PostConstruct
  void initialize() {
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(createHttpClient(true));
    restTemplate = new RestTemplate(requestFactory);
  }

  @Override
  public void push(@NonNull String commerceOrContentId, @NonNull StoreContext storeContext) {
    //expand list of contents to be pushed
    List<String> idList = expandIdsToBePushed(commerceOrContentId);
    //push all the pages
    idList.forEach(id -> pushSinglePage(id, storeContext));
  }

  private void pushSinglePage(@NonNull String commerceOrContentId, @NonNull StoreContext storeContext) {
    Map<String, JSONObject> fragmentsForId = fetchFragmentsForId(commerceOrContentId, storeContext);
    Object bean = idProvider.parseId(commerceOrContentId);
    String pageKey = sfccContentHelper.computePageKey(bean);
    JSONObject jsonForPage = fragmentsForId.get(pageKey);
    if (jsonForPage == null) {
      LOG.info("Nothing found to be pushed for {}", pageKey);
      return;
    }
    //push content and invalidate belonging cache entries
    pushContentAsset(pageKey, sfccContentHelper.computePageName(bean), sfccContentHelper.computePageTitle(bean),
            jsonForPage, storeContext);
    cache.invalidate(getExplicitDependency(commerceOrContentId, storeContext));
  }


  /**
   * fetch payload from sfcc system and prepare as json objects ready to be pushed
   * @param id commerce or content id
   * @param storeContext current context
   * @return map(pageKey, fragmentsPerPage)
   */
  private Map<String, JSONObject> fetchFragmentsForId(@NonNull String id, @NonNull StoreContext storeContext) {
    String previewUrl = fetchContentUrlHelper.computePreviewUrl(id, storeContext);

    ResponseEntity<String> response = restTemplate.getForEntity(previewUrl, String.class);
    HttpStatus statusCode = response.getStatusCode();

    String shopPayload;
    if (statusCode.is2xxSuccessful()) {
      String responseBody = response.getBody();
      shopPayload = responseBody == null ? "" : responseBody;
      Map<String, Map<String, String>> fragmentsPerPage = fragmentParser.parseFragments(shopPayload);
      return createJsonObjectsPerPage(fragmentsPerPage);
    }

    throw new CommerceException(String.format("Cannot read shop page from %s (%s)",
            previewUrl, statusCode.toString()));
  }

  /**
   * For certain bean types more than one document shall be pushed (e.g. Products and all its SKUs)
   * @return list of all IDs to be pushed
   */
  @NonNull
  private List<String> expandIdsToBePushed(@NonNull String contentOrCommerceId) {
    Object bean = idProvider.parseId(contentOrCommerceId);
    List<String> idList = new ArrayList<>();
    idList.add(contentOrCommerceId);
    if (bean instanceof Product && !(bean instanceof ProductVariant)) {
      List<String> idsForProduct = getIdsForProduct((Product) bean);
      idList.addAll(idsForProduct);
    }
    return idList;
  }

  /**
   * Get all the List of SKU IDs for a Product
   */
  private static List<String> getIdsForProduct(Product product) {
    return product.getVariants().stream()
            .map(CommerceBean::getId)
            .map(CommerceIdFormatterHelper::format)
            .collect(toList());
  }

  /**
   * Creates a single Content Asset Document on the SFCC System
   * @param pageKey id of the document
   * @param pageName name of the document as a better readable id
   * @param pageTitle html title of the document
   * @param jsonForPage payload for this document
   * @param storeContext current store context
   */
  @VisibleForTesting
  void pushContentAsset(@NonNull String pageKey, @NonNull String pageName, @NonNull String pageTitle,
                        @NonNull JSONObject jsonForPage, @NonNull StoreContext storeContext) {
    boolean documentExist = doesContentAssetDocumentExist(pageKey, storeContext);
    if (documentExist) {
      resource.patchContentById(pageKey, pageName, pageTitle, jsonForPage.toString(), storeContext);
    } else {
      resource.putContentById(pageKey, pageName, pageTitle, jsonForPage.toString(), storeContext);
      resource.assignContentToFolder(pageKey, storeContext);
    }
  }

  /**
   * Check if content asset with certain pageKey already exists on the sfcc system
   */
  private boolean doesContentAssetDocumentExist(String pageKey, StoreContext storeContext) {
    return resource.getContentById(pageKey, storeContext).isPresent();
  }

  /**
   * Return the state of the pushed asset document (see {@link PushState})
   * @param id: Can be a CoreMedia Content Id or Commerce Id
   * @param storeContext the current store context
   * @return push state
   */
  @Override
  public PushState getPushState(@NonNull String id, @NonNull StoreContext storeContext) {
    boolean pushed;
    boolean isInSync;
    try {
      pushed = isPushed(id, storeContext);
      isInSync = isInSync(id, storeContext);
    } catch(IllegalArgumentException ex){
      LOG.debug("Could not request Push State for {}", id, ex);
      return new PushState(UNKNOWN, null);
    }

    Optional<ZonedDateTime> modificationDate = sfccContentHelper.getModificationDate(id, storeContext);

    PushState.State state;
    if (!pushed) {
      state = NOT_PUSHED;
    } else {
      state = PUSHED;
      if (isInSync) {
        state = IN_SYNC;
      }
    }

    return new PushState(state, modificationDate.orElse(null));
  }

  /**
   * Checks if bean already has bean pushed.
   * @param id: Can be a CoreMedia Content Id or Commerce Id
   * @param storeContext the current store context
   * @return true if the commerce systems has any version of the page stored
   */
  private boolean isPushed(@NonNull String id, @NonNull StoreContext storeContext) {
    String storedJsonAsString = sfccContentHelper.getStoredJsonById(id, storeContext);
    return storedJsonAsString != null;
  }

  /**
   * Deletes the pushed content belonging to a bean id.
   */
  @Override
  public void delete(@NonNull String commerceOrContentId, @NonNull StoreContext storeContext) {
    //expand list of contents to be pushed
    List<String> idList = expandIdsToBePushed(commerceOrContentId);
    //push all the pages
    idList.forEach(id -> deleteSinglePage(id, storeContext));
  }

  private void deleteSinglePage(@NonNull String id, @NonNull StoreContext storeContext) {
    Object bean = idProvider.parseId(id);
    String pageKey = sfccContentHelper.computePageKey(bean);
    deleteByPageKey(pageKey, storeContext);
    cache.invalidate(getExplicitDependency(id, storeContext));
  }

  /**
   * Deletes content asset document with a certain pageKey.
   * @param pageKey id of the content asset document
   * @param storeContext current store context
   */
  @VisibleForTesting
  void deleteByPageKey(@NonNull String pageKey, @NonNull StoreContext storeContext) {
    resource.deleteContentById(pageKey, storeContext);
  }
}
