package com.coremedia.livecontext.ecommerce.sfcc.push;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.id.IdProvider;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.ContentAssetDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.MarkupTextDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.LibrariesResource;
import com.google.gson.Gson;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.empty;

/**
 * Helper to access content asset documents on the sfcc system.
 */
public class SfccContentHelper {

  static final String EXTERNAL_PAGE_TYPE = "CMExternalPage";
  static final String AUGEMENTED_CATEGEORY_TYPE = "CMExternalChannel";
  static final String AUGEMENTED_PRODUCT_TYPE = "CMExternalProduct";

  private static final String LINKABLE_TYPE = "CMLinkable";
  private static final String EXTERNAL_ID_PROPERTY = "externalId";

  private LibrariesResource resource;
  private IdProvider idProvider;
  private Cache cache;
  private FetchContentUrlHelper fetchContentUrlHelper;

  @SuppressWarnings("unused")
  private SfccContentHelper() {
  }

  public SfccContentHelper(LibrariesResource resource,
                           IdProvider idProvider,
                           FetchContentUrlHelper fetchContentUrlHelper,
                           Cache cache) {
    this.resource = resource;
    this.idProvider = idProvider;
    this.fetchContentUrlHelper = fetchContentUrlHelper;
    this.cache = cache;
  }

  /**
   * Reads the last modification date of the stored locale specific body property of the content asset document
   * belonging to the id.
   *
   * @param id           ContentId or CommerceBeanId
   * @param storeContext current store context containing locale of interest
   * @return date
   */
  Optional<ZonedDateTime> getModificationDate(@NonNull String id, @NonNull StoreContext storeContext) {
    String storedJsonAsString = getStoredJsonById(id, storeContext);

    Gson g = new Gson();
    Map storedJson = g.fromJson(storedJsonAsString, Map.class);
    if (storedJson == null) {
      return empty();
    }
    String modificationDateStr = (String) storedJson.get(SfccPushJsonFactory.MODIFICATION_DATE_PROPERTY);
    if (modificationDateStr == null) {
      return empty();
    }

    return Optional.of(ZonedDateTime.parse(modificationDateStr, SfccPushJsonFactory.FORMATTER));
  }

  /**
   * Concatenates custom dependency to invalidate certain cache entries after content has been pushed.
   *
   * @param commerceOrContentId contentId or commerceBeanId
   * @param storeContext        current store context
   * @return String, which is used as custom dependency
   */
  static String getExplicitDependency(String commerceOrContentId, StoreContext storeContext) {
    return "ContentPush" + ";" + storeContext.getSiteId() + ";" + commerceOrContentId;
  }

  /**
   * Read stored JSON for content or commerce bean from sfcc system.
   *
   * @param commerceOrContentId contentId or commerceBeanId
   * @param storeContext        current store context
   * @return stored String in JSON format
   */
  String getStoredJsonById(@NonNull String commerceOrContentId, @NonNull StoreContext storeContext) {
    Object bean = idProvider.parseId(commerceOrContentId);
    String pageKey = computePageKey(bean);
    String explicitDependency = getExplicitDependency(commerceOrContentId, storeContext);
    return cache.get(new GetStoredJsonCacheKey(pageKey, storeContext, this, explicitDependency));
  }

  /**
   * Read stored JSON for a pageKey from the commerce system.
   *
   * @param pageKey      is used as ID of the content asset document within the sfcc system
   * @param storeContext current store context
   * @return locale specific fragment payload in JSON fromat
   */
  @Nullable
  String getStoredJsonByPageKey(@NonNull String pageKey, @NonNull StoreContext storeContext) {
    Optional<ContentAssetDocument> contentAssetDocument = resource.getContentById(pageKey, storeContext);
    String languageTag = storeContext.getLocale().toLanguageTag();
    return contentAssetDocument
            .map(ContentAssetDocument::getBody)
            .map(body -> body.getValue(languageTag))
            .map(MarkupTextDocument::getMarkup)
            .orElse(null);
  }

  /**
   * Compute pageKey for a Content or CommerceBean. The pageKey serves as id for the content asset documents stored on the
   * sfcc system. The pageKey is also used by the coremedia cartridge on the sfcc system to lookup fragment payload for
   * fragment includes on the sfcc system.
   *
   * @param bean content or commerce bean
   * @return String (e.g. "externalRef=;categoryId=root;productId=;pageId=" representing root category)
   */
  @NonNull
  String computePageKey(@NonNull Object bean) {
    if (bean instanceof Category) {
      return computePageKey(null, ((Category) bean).getId().getExternalId().orElse(null), null, null);
    } else if (bean instanceof Product) {
      return computePageKey(null, null, ((Product) bean).getId().getExternalId().orElse(null), null);

    } else if (bean instanceof Content) {
      Content content = (Content) bean;
      ContentType contentType = content.getType();
      if (contentType.isSubtypeOf(EXTERNAL_PAGE_TYPE)) {
        String externalId = content.getString(EXTERNAL_ID_PROPERTY);
        return computePageKey(null, null, null, externalId);
      } else if (contentType.isSubtypeOf(AUGEMENTED_CATEGEORY_TYPE)) {
        String externalId = content.getString(EXTERNAL_ID_PROPERTY);
        return computePageKey(null, extractExternalIdFromCommerceId(externalId), null, null);
      } else if (contentType.isSubtypeOf(AUGEMENTED_PRODUCT_TYPE)) {
        String externalId = content.getString(EXTERNAL_ID_PROPERTY);
        return computePageKey(null, null, extractExternalIdFromCommerceId(externalId), null);
      } else {
        String seoPath = computeSeoPath(content);
        return computePageKey("cm-seosegment:" + seoPath, null, null, null);
      }
    }
    throw new IllegalArgumentException("Cannot compute pageKey from bean " + bean);
  }

  /**
   * Compute page name for a Content or CommerceBean.
   * The page name serves as name for the content asset document name field stored on the sfcc system.
   *
   * @param bean content or commerce bean
   * @return page name
   */
  @NonNull
  String computePageName(@NonNull Object bean) {

    if (bean instanceof Category) {
      String externalId = ((Category) bean).getId().getExternalId().orElse(null);
      return "/category/" + externalId;

    } else if (bean instanceof ProductVariant) {
      String externalId = ((ProductVariant) bean).getId().getExternalId().orElse(null);
      return "/sku/" + externalId;

    } else if (bean instanceof Product) {
      String externalId = ((Product) bean).getId().getExternalId().orElse(null);
      return "/product/" + externalId;

    } else if (bean instanceof Content) {
      return computeName((Content) bean);
    }
    throw new IllegalArgumentException("Cannot compute page name from bean " + bean);
  }

  /**
   * Compute page title for a Content or CommerceBean.
   * The page title serves as description for the content asset documents stored on the sfcc system.
   *
   * @param bean content or commerce bean
   * @return page title
   */
  @NonNull
  String computePageTitle(@NonNull Object bean) {

    if (bean instanceof Category) {
      Category category = (Category) bean;
      String title = category.getTitle();
      return !StringUtils.isEmpty(title) ? title : "";

    } else if (bean instanceof Product) {
      return computeDescription((Product) bean);

    } else if (bean instanceof Content) {
      return computeDescription((Content) bean);
    }

    throw new IllegalArgumentException("Cannot compute page title from bean " + bean);
  }

  @NonNull
  private String computeSeoPath(@NonNull Content content) {
    return fetchContentUrlHelper.getSeoSegment(content)
            .orElseThrow(() -> new IllegalArgumentException(String.format("Cannot get seo path for %s", content)));
  }

  @NonNull
  private String computeName(@NonNull Content content) {
    ContentType contentType = content.getType();
    String name;

    if (contentType.isSubtypeOf(EXTERNAL_PAGE_TYPE)) {
      String externalId = content.getString(EXTERNAL_ID_PROPERTY);
      name = StringUtils.isEmpty(externalId) ? "/" : "/" + externalId;
    } else if (contentType.isSubtypeOf(AUGEMENTED_CATEGEORY_TYPE)) {
      String externalId = content.getString(EXTERNAL_ID_PROPERTY);
      CommerceId commerceId = CommerceIdParserHelper.parseCommerceIdOrThrow(externalId);
      name = "/category/" + commerceId.getExternalId().orElse(null);
    } else if (contentType.isSubtypeOf(AUGEMENTED_PRODUCT_TYPE)) {
      String externalId = content.getString(EXTERNAL_ID_PROPERTY);
      CommerceId commerceId = CommerceIdParserHelper.parseCommerceIdOrThrow(externalId);
      name = "/product/" + commerceId.getExternalId().orElse(null);
    } else {
      String seoPath = computeSeoPath(content);
      name = "/cm/" + seoPath;
    }

    return !StringUtils.isEmpty(name) ? name : "";
  }

  @NonNull
  private static String computeDescription(@NonNull Content content) {
    ContentType contentType = content.getType();
    String description = null;

    if (contentType.isSubtypeOf(EXTERNAL_PAGE_TYPE)) {
      description = content.getString("title");

    } else if (contentType.isSubtypeOf(AUGEMENTED_CATEGEORY_TYPE)) {
      CommerceId commerceId = CommerceIdParserHelper.parseCommerceIdOrThrow(content.getString(EXTERNAL_ID_PROPERTY));
      StoreContext currentContext = getCurrentContext();
      Category category = (Category) getCommerceBeanFactory().createBeanFor(commerceId, currentContext);
      description = category.getTitle();

    } else if (contentType.isSubtypeOf(AUGEMENTED_PRODUCT_TYPE)) {
      CommerceId commerceId = CommerceIdParserHelper.parseCommerceIdOrThrow(content.getString(EXTERNAL_ID_PROPERTY));
      StoreContext currentContext = getCurrentContext();
      Product product = (Product) getCommerceBeanFactory().createBeanFor(commerceId, currentContext);
      description = computeDescription(product);

    } else if (contentType.isSubtypeOf(LINKABLE_TYPE)) {
      description = content.getString("title");
    }

    return !StringUtils.isEmpty(description) ? description : "";
  }

  @NonNull
  private static String computeDescription(@NonNull Product product) {
    String title = product.getTitle();
    StringBuilder strBuilder = new StringBuilder();
    if (!StringUtils.isEmpty(title)) {
      strBuilder.append(title);
    }
    if (product instanceof ProductVariant) {
      ProductVariant variant = (ProductVariant) product;
      String axisValues = variant.getVariantAxisNames()
              .stream()
              .map(axis -> Objects.requireNonNull(variant.getAttributeValue(axis)).toString())
              .collect(Collectors.joining(","));
      if (!StringUtils.isEmpty(axisValues)) {
        strBuilder.append(" (").append(axisValues).append(")");
      }
    }
    return strBuilder.toString();
  }

  private static String extractExternalIdFromCommerceId(String commerceIdStr) {
    return CommerceIdParserHelper.parseCommerceId(commerceIdStr)
            .flatMap(CommerceId::getExternalId)
            .orElseThrow(() -> new IllegalArgumentException("Cannot extract external id from " + commerceIdStr));
  }

  private static StoreContext getCurrentContext() {
    return CurrentCommerceConnection.get().getStoreContext();
  }

  private static CommerceBeanFactory getCommerceBeanFactory() {
    return CurrentCommerceConnection.get().getCommerceBeanFactory();
  }

  @NonNull
  private static String computePageKey(@Nullable String externalRef,
                                       @Nullable String categoryId,
                                       @Nullable String productId,
                                       @Nullable String pageId) {
    // because of a sonar violation (max 3 conditional ops per expression) this code is ugly, sorry.
    final String externalRefTemp = externalRef != null ? externalRef : "";
    final String categoryIdTemp = categoryId != null ? categoryId : "";
    final String productIdTemp = productId != null ? productId : "";
    final String pageIdTemp = pageId != null ? pageId : "";
    return String.format("externalRef=%s;categoryId=%s;productId=%s;pageId=%s",
            externalRefTemp, categoryIdTemp, productIdTemp, pageIdTemp);
  }
}
