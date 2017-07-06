package com.coremedia.livecontext.ecommerce.hybris.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.NoStoreContextAvailable;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.beans.CategoryImpl;
import com.coremedia.livecontext.ecommerce.hybris.cache.CatalogCacheKey;
import com.coremedia.livecontext.ecommerce.hybris.cache.CategoryCacheKey;
import com.coremedia.livecontext.ecommerce.hybris.cache.ProductCacheKey;
import com.coremedia.livecontext.ecommerce.hybris.common.AbstractCommerceService;
import com.coremedia.livecontext.ecommerce.hybris.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CatalogDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CategoryDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CategoryRefDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductRefDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductSearchDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.resources.CatalogResource;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;

public class CatalogServiceImpl extends AbstractCommerceService implements CatalogService {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogServiceImpl.class);

  private CatalogResource catalogResource;
  private CommerceBeanFactory commerceBeanFactory;

  public static String SEARCH_PARAM_PAGENUMBER = "currentPage";
  public static String SEARCH_PARAM_CATEGORYID = "category";

  private int maxProductSearchResults = 50;

  @Nullable
  @Override
  public Product findProductById(@Nonnull String id) throws CommerceException {
    String internalId = CommerceIdHelper.isInternalId(id) ? id : CommerceIdHelper.formatProductId(id);
    ProductCacheKey variantCacheKey = new ProductCacheKey(internalId, StoreContextHelper.getCurrentContext(), catalogResource, getCommerceCache());
    ProductDocument delegate = (ProductDocument) getCommerceCache().get(variantCacheKey);
    if (delegate != null) {
      if (delegate.getBaseProduct() == null) {
        return getCommerceBeanHelper().createBeanFor(delegate, Product.class);
      }
      else {
        return getCommerceBeanHelper().createBeanFor(delegate, ProductVariant.class);
      }
    }
    return null;
  }

  @Nullable
  @Override
  public Product findProductBySeoSegment(@Nonnull String seoSegment) throws CommerceException {
    throw new UnsupportedOperationException("Hybris webservice does not support to find products by seo segments.");
  }

  @Nullable
  @Override
  public ProductVariant findProductVariantById(@Nonnull String id) throws CommerceException {
    String internalId = CommerceIdHelper.isInternalId(id) ? id : CommerceIdHelper.formatProductVariantId(id);
    ProductCacheKey cacheKey = new ProductCacheKey(internalId, StoreContextHelper.getCurrentContext(), catalogResource, getCommerceCache());
    ProductDocument delegate = (ProductDocument) getCommerceCache().get(cacheKey);
    return delegate != null ? getCommerceBeanHelper().createBeanFor(delegate, ProductVariant.class) : null;
  }

  @Nonnull
  @Override
  public List<Product> findProductsByCategory(@Nonnull Category category) throws CommerceException {
    CategoryImpl categoryImpl = (CategoryImpl) category;
    CategoryDocument categoryDocument = categoryImpl.getDelegate();
    List<ProductRefDocument> productRefDocuments = filterProductRefs(categoryDocument.getProducts());
    return resolveProductRefs(productRefDocuments);
  }

  @Nonnull
  @Override
  public List<Category> findTopCategories(Site site) throws CommerceException {
    String catalogId = StoreContextHelper.getCatalogId();
    CatalogCacheKey cacheKey = new CatalogCacheKey("catalog:" + catalogId, StoreContextHelper.getCurrentContext(), catalogResource, getCommerceCache());
    CatalogDocument delegate = (CatalogDocument) getCommerceCache().get(cacheKey);
    List<Category> topCategories = new ArrayList<>();
    if (delegate != null) {
      List<CategoryRefDocument> rootCategories = delegate.getRootCategories();
      if (rootCategories != null) {
        for (CategoryRefDocument categoryRefDocument : rootCategories) {
          String externalId = categoryRefDocument.getCode();
          if (externalId != null) {
            Category category = findCategoryById(externalId);
            if (category == null) {
              throw new NotFoundException("Top level category not found with id: " + externalId);
            }
            topCategories.add(category);
          }
        }
      }
    }
    return topCategories;
  }

  @Nonnull
  @Override
  public List<Category> findSubCategories(@Nonnull Category parentCategory) throws CommerceException {
    if (parentCategory.isRoot()) {
      return findTopCategories(null);
    }
    CategoryImpl parentCategoryImpl = (CategoryImpl) parentCategory;
    CategoryDocument categoryDocument = parentCategoryImpl.getDelegate();
    List<Category> subCategories = new ArrayList<>();
    List<CategoryRefDocument> subCategoryRefDocuments = categoryDocument.getSubCategories();
    if (subCategoryRefDocuments != null) {
      for (CategoryRefDocument subCategoryRefDeocument : subCategoryRefDocuments) {
        String code = subCategoryRefDeocument.getCode();
        Category subCategory = findCategoryById(code);
        if (subCategory == null) {
          LOG.warn("Cannot find subcategory " + code);
        } else {
          subCategories.add(subCategory);
        }
      }
    }
    return subCategories;
  }

  @Nullable
  @Override
  public Category findCategoryById(@Nonnull String id) throws CommerceException {
    if (CommerceIdHelper.isRootCategoryId(id)) {
      return findRootCategory();
    }
    CategoryCacheKey cacheKey = new CategoryCacheKey(CommerceIdHelper.formatCategoryId(CommerceIdHelper.convertToExternalId(id)),
            StoreContextHelper.getCurrentContext(), catalogResource, getCommerceCache());
    CategoryDocument delegate = (CategoryDocument) getCommerceCache().get(cacheKey);
    return getCommerceBeanHelper().createBeanFor(delegate, Category.class);
  }

  @Nullable
  @Override
  public Category findCategoryBySeoSegment(@Nonnull String seoSegment) throws CommerceException {
    return null;
  }

  @Nonnull
  @Override
  public SearchResult<Product> searchProducts(@Nonnull String searchTerm, @Nullable Map<String, String> searchParams) throws CommerceException {

    SearchResult<Product> result = new SearchResult<>();

    // in some cases of an id as search term we do not find a product, therefore we try a direct find call first...
    Product product = findProductById(searchTerm);
    if (product != null) {
      List<Product> products = convertToProducts(Collections.singletonList(product), maxProductSearchResults);
      if (!products.isEmpty()) {
        result.setSearchResult(products);
        result.setTotalCount(products.size());
        result.setPageSize(products.size());
        result.setPageNumber(1);
        return result;
      }
    }
    result = searchProductsPaginated(searchTerm, searchParams, Product.class);
    return result;
  }

  @Nonnull
  @Override
  public SearchResult<ProductVariant> searchProductVariants(@Nonnull String searchTerm, @Nullable Map<String, String> searchParams) throws CommerceException {

    SearchResult<ProductVariant> result = new SearchResult<>();

    Product product = findProductById(searchTerm);
    if (product != null) {
      if (product.isVariant()) {
        result.setSearchResult(Collections.singletonList((ProductVariant)product));
        result.setTotalCount(1);
        result.setPageSize(1);
        result.setPageNumber(1);
      }
      else {
        List<ProductVariant> skus = product.getVariants();
        result.setSearchResult(skus);
        result.setTotalCount(skus.size());
        result.setPageSize(skus.size());
        result.setPageNumber(1);
      }
    }
    else {
      result = searchProductsPaginated(searchTerm, searchParams, ProductVariant.class);
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private <T> SearchResult<T> searchProductsPaginated(
          @Nonnull String searchTerm, @Nullable Map<String, String> searchParams, Class<T> returnType) {

    Set<T> resultSet = new HashSet<>();
    Map<String, String> params = searchParams != null ? new HashMap<>(searchParams) : new HashMap<>();

    int pageCount = params.get(CatalogService.SEARCH_PARAM_PAGENUMBER) != null ?
            Integer.parseInt(params.get(CatalogService.SEARCH_PARAM_PAGENUMBER)) : 0;
    int pageSize = params.get(CatalogService.SEARCH_PARAM_PAGESIZE) != null ?
            Integer.parseInt(params.get(CatalogService.SEARCH_PARAM_PAGESIZE)) : maxProductSearchResults;
    int pages = Integer.MAX_VALUE;

    ProductSearchDocument productSearchDocument = null;
    while(pageCount < pages && resultSet.size() < maxProductSearchResults) {
      params.put(CatalogService.SEARCH_PARAM_PAGENUMBER, pageCount+"");
      params.put(CatalogService.SEARCH_PARAM_PAGESIZE, pageSize+"");
      productSearchDocument = catalogResource.searchProducts(
              searchTerm, params, StoreContextHelper.getCurrentContext());
      if (productSearchDocument == null) {
        break;
      }
      List<Product> productsFromPage = resolveProductRefs(productSearchDocument.getProducts());
      productsFromPage = returnType == ProductVariant.class ?
              filterProductVariants(productsFromPage, maxProductSearchResults) :
              convertToProducts(productsFromPage, maxProductSearchResults);
      for (Product p : productsFromPage) {
        resultSet.add((T) p);
        if (resultSet.size() >= maxProductSearchResults) {
          break;
        }
      }
      pageCount = productSearchDocument.getPagination().getCurrentPage() + 1;
      pages = productSearchDocument.getPagination().getTotalPages();
    }

    List<T> resultList = new ArrayList<>(resultSet);
    SearchResult<T> result = new SearchResult<>();
    result.setSearchResult(resultList);
    if (productSearchDocument != null) {
      result.setTotalCount(resultList.size() <= maxProductSearchResults ?
              resultList.size() : productSearchDocument.getPagination().getTotalResults());
      result.setPageSize(productSearchDocument.getPagination().getPageSize());
      result.setPageNumber(productSearchDocument.getPagination().getCurrentPage());
      result.setFacets(productSearchDocument.getFacets());
    }
    return result;
  }

  /**
   * Resolve a list of products and variant refs and turn it into a list of products and variants.
   */
  private List<Product> resolveProductRefs(List<ProductRefDocument> productRefDocuments) {
    List<Product> result = new ArrayList<>();
    if (productRefDocuments != null) {
      for (ProductRefDocument productRefDocument : productRefDocuments) {
        Product product = findProductById(productRefDocument.getCode()); //this is expensive but necessary to filter out product variants
        if (product == null) {
          LOG.warn("Cannot find product " + productRefDocument.getCode());
        } else {
          result.add(product);
        }
      }
    }
    return result;
  }

  /**
   * Filters product refs in a list of product and variant refs.
   */
  private List<ProductRefDocument> filterProductRefs(List<ProductRefDocument> productRefs) {
    Set<ProductRefDocument> result = new LinkedHashSet<>();
    if (productRefs != null) {
      for (ProductRefDocument productRef : productRefs) {
        if (productRef.getType() == null) {
          result.add(productRef);
        }
      }
    }
    return new ArrayList<>(result);
  }

  /**
   * Filters products from a list with variants and products.
   * Variants will be additionally converted to products.
   */
  private List<Product> convertToProducts(List<Product> products, int max) {
    Set<Product> result = new LinkedHashSet<>();
    for (Product product : products) {
      if (!product.isVariant()) {
        result.add(product);
      }
      else if (product instanceof ProductVariant) {
        Product parent = ((ProductVariant) product).getParent();
        while (parent instanceof ProductVariant && ((ProductVariant) parent).getParent() != null) {
          parent = ((ProductVariant) parent).getParent();
        }
        if (parent != null) {
          result.add(parent);
        }
      }
      if (result.size() >= max) {
        break;
      }
    }
    return new ArrayList<>(result);
  }

  /**
   * Filters variants from a list with variants and products.
   */
  private List<Product> filterProductVariants(List<Product> products, int max) {
    Set<ProductVariant> result = new LinkedHashSet<>();
    for (Product product : products) {
      if (product.isVariant()) {
        result.add((ProductVariant) product);
        if (result.size() >= max) {
          break;
        }
      }
    }
    return new ArrayList<>(result);
  }

  @Nonnull
  @Override
  public Category findRootCategory() throws CommerceException {
    String rootCategoryId = CommerceIdHelper.formatCategoryId(CommerceIdHelper.ROOT_CATEGORY_ID);
    StoreContext storeContext = StoreContextHelper.getCurrentContext();
    if (storeContext == null) {
      throw new NoStoreContextAvailable("cannot find root category");
    }
    Category rootCategoryBean = (Category) commerceBeanFactory.createBeanFor(rootCategoryId, storeContext);
    if (rootCategoryBean == null) {
      throw new InvalidIdException("cannot create root category " + rootCategoryId);
    }
    return rootCategoryBean;
  }

  @Nonnull
  @Override
  public CatalogService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, CatalogService.class);
  }

  @Required
  public void setCatalogResource(CatalogResource catalogResource) {
    this.catalogResource = catalogResource;
  }

  @Required
  public void setCommerceBeanFactory(CommerceBeanFactory commerceBeanFactory) {
    this.commerceBeanFactory = commerceBeanFactory;
  }

  public void setMaxProductSearchResults(int maxProductSearchResults) {
    this.maxProductSearchResults = maxProductSearchResults;
  }
}
