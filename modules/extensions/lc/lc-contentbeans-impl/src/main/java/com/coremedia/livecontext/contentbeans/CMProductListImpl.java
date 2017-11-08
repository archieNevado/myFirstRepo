package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.cae.contentbeans.CMDynamicListImpl;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cap.common.NoSuchPropertyDescriptorException;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import com.coremedia.livecontext.navigation.ProductInSiteImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper.parseCommerceId;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class CMProductListImpl extends CMDynamicListImpl implements CMProductList {

  private static final Logger LOG = LoggerFactory.getLogger(CMProductListImpl.class);

  public static final String EXTERNAL_ID = "externalId";

  public static final int MAX_LENGTH_DEFAULT = 10;
  public static final int OFFSET_DEFAULT = 0;

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link CMProductList} objects
   */
  @Override
  public CMProductList getMaster() {
    return (CMProductList) super.getMaster();
  }

  @Override
  public Map<Locale, ? extends CMProductList> getVariantsByLocale() {
    return getVariantsByLocale(CMProductList.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<? extends CMProductList> getLocalizations() {
    return (Collection<? extends CMProductList>) super.getLocalizations();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, ? extends Aspect<? extends CMProductList>> getAspectByName() {
    return (Map<String, ? extends Aspect<? extends CMProductList>>) super.getAspectByName();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends Aspect<? extends CMProductList>> getAspects() {
    return (List<? extends Aspect<? extends CMProductList>>) super.getAspects();
  }

  @Override
  public String getExternalId() {
    return getContent().getString(EXTERNAL_ID);
  }

  /**
   * @return the value of the document property "teaserTitle".
   * If it is empty then fallback to the document property "title".
   * If it is still empty then fallback to the name of the category.
   */
  @Override
  public String getTeaserTitle() {
    String teaserTitle = super.getTeaserTitle();
    if (isBlank(teaserTitle)) {
      Category category = getCategory();
      if (category != null && category.getName() != null) {
        teaserTitle = category.getName();
      }
    }
    return teaserTitle;
  }

  public Category getCategory() {
    Optional<CommerceId> categoryIdOptional = parseCommerceId(getExternalId());

    if (!categoryIdOptional.isPresent()) {
      return null;
    }

    try {
      CommerceConnection commerceConnection = CurrentCommerceConnection.get();
      StoreContext storeContext = commerceConnection.getStoreContextProvider().findContextByContent(getContent());
      CommerceId commerceId = categoryIdOptional.get();
      CatalogService catalogService = requireNonNull(CurrentCommerceConnection.get().getCatalogService(), "catalog service not available");
      return catalogService.withStoreContext(storeContext).findCategoryById(commerceId, storeContext);
    } catch (CommerceException e) {
      LOG.warn("Could not retrieve category for Product List {}.", this, e);
      return null;
    }
  }

  @Override
  public List getItems() {
    List<Object> result = new ArrayList<>();
    List contents = super.getItems();
    result.addAll(contents);
    List<ProductInSite> products = getProducts();
    result.addAll(products);
    return result;
  }

  public String getOrderBy() {
    Object value = getProductListSettings().get("orderBy");
    return value instanceof String ? value.toString() : null;
  }

  public int getOffset() {
    Object value = getProductListSettings().get("offset");
    return value instanceof String ? Integer.parseInt((String)value) : OFFSET_DEFAULT;
  }

  @Override
  public int getMaxLength() {
    int maxLength = super.getMaxLength();
    return maxLength >= 0 ? maxLength : MAX_LENGTH_DEFAULT;
  }

  public List<ProductInSite> getProducts() {
    Site site = getSitesService().getSiteAspect(getContent()).getSite();
    if (site == null) {
      LOG.debug("Site not found for content: " + getContent());
      return Collections.emptyList();
    }

    Category category = getCategory();
    CatalogAlias catalogAlias = category != null ? category.getReference().getCatalogAlias() : null;

    CommerceConnection commerceConnection = CurrentCommerceConnection.get();
    CatalogService catalogService = requireNonNull(commerceConnection.getCatalogService(), "catalog service not available");
    SearchResult<Product> searchResult = catalogService.searchProducts(getQuery(),
            getSearchParams(category, catalogAlias, getOrderBy(), getMaxLength(), getOffset()), commerceConnection.getStoreContext());

    return searchResult.getSearchResult().stream()
            .map(product -> new ProductInSiteImpl(product, site))
            .collect(toList());
  }

  @Override
  public String getFacet() {
    return null;
  }

  @Override
  public String getQuery() {
    return "*";
  }

  @Override
  public Map<String, Object> getProductListSettings() {
    Map<String, Object> result = new HashMap<>();
    try {
      if (getLocalSettings() != null) {
        Map<String, Object> structMap = getLocalSettings().getStruct("productList").getProperties();
        //copy struct because it may be cached and the cache MUST NEVER be modified.
        for(Map.Entry<String, Object> entry : structMap.entrySet()) {
          if(entry.getValue() != null) {
            result.put(entry.getKey(),entry.getValue().toString());
          }
        }
      }
    }
    catch (NoSuchPropertyDescriptorException e) {
      //no struct configured for current content, empty map will be returned.
    }
    return result;
  }

  @Nonnull
  private Map<String, String> getSearchParams(@Nullable Category category, @Nullable CatalogAlias catalogAlias, String orderBy, int limit, int offset) {
    Map<String, String> params = new HashMap<>();

    if (category != null && !category.isRoot()) {
      params.put(CatalogService.SEARCH_PARAM_CATEGORYID, category.getExternalTechId());
    }

    if (catalogAlias != null && !StringUtils.isEmpty(catalogAlias.value())) {
      params.put(CatalogService.SEARCH_PARAM_CATALOG_ALIAS, catalogAlias.value());
    }

    if (!StringUtils.isEmpty(orderBy)) {
      params.put(CatalogService.SEARCH_PARAM_ORDERBY, orderBy);
    }

    if (limit >= 0) {
      params.put(CatalogService.SEARCH_PARAM_TOTAL, String.valueOf(limit));
    }

    if (offset > 0) {
      params.put(CatalogService.SEARCH_PARAM_OFFSET, String.valueOf(offset));
    }

    return params;
  }

  public StoreContextProvider getStoreContextProvider() {
    return CurrentCommerceConnection.get().getStoreContextProvider();
  }
}
