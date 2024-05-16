package com.coremedia.blueprint.caas.augmentation.connection;

import com.coremedia.livecontext.ecommerce.catalog.Catalog;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.search.SearchQuery;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static java.lang.invoke.MethodHandles.lookup;

@DefaultAnnotation(NonNull.class)
class CmsOnlyCatalogService implements CatalogService {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());
  static final CommerceBeanType BEAN_TYPE_CATALOG = CommerceBeanType.of("catalog");
  private static final String CATEGORY_ID_ROOT = "virtual-root";

  private final CommerceBeanFactory commerceBeanFactory;

  public CmsOnlyCatalogService(CommerceBeanFactory commerceBeanFactory) {
    this.commerceBeanFactory = commerceBeanFactory;
  }

  @Nullable
  @Override
  public Product findProductById(CommerceId commerceId, StoreContext storeContext) {
    throw new UnsupportedOperationException("findProductById");
  }

  @Nullable
  @Override
  public Product findProductBySeoSegment(String s, StoreContext storeContext) {
    throw new UnsupportedOperationException("findProductBySeoSegment");
  }

  @Nullable
  @Override
  public ProductVariant findProductVariantById(CommerceId commerceId, StoreContext storeContext) {
    throw new UnsupportedOperationException("findProductVariantById");
  }

  @Override
  public List<Product> findProductsByCategory(Category category) {
    throw new UnsupportedOperationException("findProductsByCategory");
  }

  @Override
  public List<Category> findTopCategories(CatalogAlias catalogAlias, StoreContext storeContext) {
    throw new UnsupportedOperationException("findTopCategories");
  }

  @Override
  public Category findRootCategory(CatalogAlias catalogAlias, StoreContext storeContext) {
    if (!catalogAlias.equals(storeContext.getCatalogAlias())) {
      // multi catalog setup not supported in CmsOnly mode
      throw new UnsupportedOperationException("CmsOnlyCatalogService does not support multi-catalogs.");
    }
    // only support virtual root category
    var commerceId = from(CATEGORY_ID_ROOT, CommerceBeanType.of("category"), storeContext);
    return (Category) commerceBeanFactory.createBeanFor(commerceId, storeContext);
  }

  @Override
  public List<Category> findSubCategories(Category category) {
    throw new UnsupportedOperationException("findSubCategories");
  }

  @Nullable
  @Override
  public Category findCategoryById(CommerceId commerceId, StoreContext storeContext) {
    throw new UnsupportedOperationException("findCategoryById");
  }

  @Nullable
  @Override
  public Category findCategoryBySeoSegment(String s, StoreContext storeContext) {
    throw new UnsupportedOperationException("findCategoryBySeoSegment");
  }

  @Override
  public <T extends CommerceBean> SearchResult<T> search(SearchQuery searchQuery, StoreContext storeContext) {
    LOG.warn("CmsOnlyCatalogService does not support search.");
    return SearchResult.emptySearchResult();
  }

  @Override
  public List<Catalog> getCatalogs(StoreContext storeContext) {
    LOG.warn("CmsOnlyCatalogService does not support catalogs.");
    return List.of();
  }

  @Override
  public Optional<Catalog> getCatalog(CatalogId catalogId, StoreContext storeContext) {
    var commerceId = from(catalogId.value(), BEAN_TYPE_CATALOG, storeContext);
    return Optional.of((Catalog) commerceBeanFactory.createBeanFor(commerceId, storeContext));
  }

  private static CommerceId from(String id, CommerceBeanType beanType, StoreContext storeContext) {
    var catalogAlias = storeContext.getCatalogAlias();
    var idProvider = storeContext.getConnection().getIdProvider();
    return idProvider.format(beanType, catalogAlias, id);
  }

  @Override
  public Optional<Catalog> getCatalog(CatalogAlias catalogAlias, StoreContext storeContext) {
    if (catalogAlias.equals(storeContext.getCatalogAlias())) {
      return getCatalog(storeContext);
    }
    // multi catalog setup not supported in CmsOnly mode
    throw new UnsupportedOperationException("CmsOnlyCatalogService does not support multi-catalogs.");
  }

  @Override
  public Optional<Catalog> getDefaultCatalog(StoreContext storeContext) {
    // default catalog support. Use the configured catalog, if any.
    return getCatalog(storeContext);
  }

  private Optional<Catalog> getCatalog(StoreContext storeContext) {
    return storeContext.getCatalogId()
            .map(id -> from(id.value(), BEAN_TYPE_CATALOG, storeContext))
            .map(commerceId -> (Catalog) commerceBeanFactory.createBeanFor(commerceId, storeContext));
  }

}
