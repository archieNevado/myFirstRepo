package com.coremedia.livecontext.ecommerce.hybris.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.catalog.Catalog;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.beans.CategoryImpl;
import com.coremedia.livecontext.ecommerce.hybris.cache.CatalogCacheKey;
import com.coremedia.livecontext.ecommerce.hybris.cache.CategoryCacheKey;
import com.coremedia.livecontext.ecommerce.hybris.cache.ProductCacheKey;
import com.coremedia.livecontext.ecommerce.hybris.common.AbstractHybrisService;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATALOG;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.SKU;
import static com.coremedia.livecontext.ecommerce.hybris.common.HybrisCommerceIdProvider.commerceId;
import static java.util.Collections.emptyList;

public class CatalogServiceImpl extends AbstractHybrisService implements CatalogService {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogServiceImpl.class);

  public static final String SEARCH_PARAM_PAGENUMBER = "currentPage";
  public static final String SEARCH_PARAM_CATEGORYID = "category";

  private CatalogResource catalogResource;

  private int maxProductSearchResults = 50;

  @Nullable
  @Override
  public Product findProductById(@Nonnull CommerceId id, @Nonnull StoreContext storeContext) {
    CommerceCache cache = getCommerceCache();

    ProductCacheKey cacheKey = new ProductCacheKey(id, storeContext, catalogResource, cache);

    return cache.find(cacheKey)
            .map(p -> createProductBean(p, storeContext))
            .orElse(null);
  }

  @Nullable
  private Product createProductBean(@Nonnull ProductDocument productDocument, @Nonnull StoreContext storeContext) {
    if (productDocument.getBaseProduct() == null) {
      return createBeanFor(productDocument, storeContext, PRODUCT, Product.class);
    } else {
      return createBeanFor(productDocument, storeContext, SKU, ProductVariant.class);
    }
  }

  @Nullable
  @Override
  public Product findProductBySeoSegment(@Nonnull String seoSegment, @Nonnull StoreContext storeContext) {
    throw new UnsupportedOperationException("Hybris webservice does not support to find products by seo segments.");
  }

  @Nullable
  @Override
  public ProductVariant findProductVariantById(@Nonnull CommerceId id, @Nonnull StoreContext storeContext) {
    CommerceCache cache = getCommerceCache();

    ProductCacheKey cacheKey = new ProductCacheKey(id, storeContext, catalogResource, cache);

    return cache.find(cacheKey)
            .map(productDocument -> createBeanFor(productDocument, storeContext, SKU, ProductVariant.class))
            .orElse(null);
  }

  @Nonnull
  @Override
  public List<Product> findProductsByCategory(@Nonnull Category category) {
    CategoryImpl categoryImpl = (CategoryImpl) category;
    CategoryDocument categoryDocument = categoryImpl.getDelegate();
    List<ProductRefDocument> productRefDocuments = filterProductRefs(categoryDocument.getProducts());
    return resolveProductRefs(productRefDocuments, category.getContext());
  }

  @Nonnull
  public List<Category> findTopCategories(@Nonnull CatalogAlias catalogAlias, @Nonnull StoreContext storeContext) {
    // to be implemented with CMS-9516 (multi catalog support for hybris)
    String catalogId = StoreContextHelper.getCatalogId(storeContext);
    CommerceCache cache = getCommerceCache();

    CommerceId catalogCommerceId = commerceId(CATALOG).withExternalId(catalogId).build();
    CatalogCacheKey cacheKey = new CatalogCacheKey(catalogCommerceId, storeContext, catalogResource, cache);
    Optional<CatalogDocument> catalogDocument = cache.find(cacheKey);

    List<CategoryRefDocument> rootCategories = catalogDocument
            .map(CatalogDocument::getRootCategories)
            .orElse(emptyList());

    List<Category> topCategories = new ArrayList<>();
    for (CategoryRefDocument categoryRefDocument : rootCategories) {
      String externalId = categoryRefDocument.getCode();
      if (externalId != null) {
        CommerceId commerceId = commerceId(CATEGORY).withExternalId(externalId).build();
        Category category = findCategoryById(commerceId, storeContext);
        if (category == null) {
          throw new NotFoundException("Top level category not found with id: " + externalId);
        }

        topCategories.add(category);
      }
    }

    return topCategories;
  }

  @Nonnull
  @Override
  public List<Category> findSubCategories(@Nonnull Category parentCategory) {
    if (parentCategory.isRoot()) {
      CatalogAlias catalogAlias = parentCategory.getId().getCatalogAlias();
      return findTopCategories(catalogAlias, parentCategory.getContext());
    }

    CategoryImpl parentCategoryImpl = (CategoryImpl) parentCategory;
    CategoryDocument categoryDocument = parentCategoryImpl.getDelegate();
    List<Category> subCategories = new ArrayList<>();
    List<CategoryRefDocument> subCategoryRefDocuments = categoryDocument.getSubCategories();
    if (subCategoryRefDocuments != null) {
      for (CategoryRefDocument subCategoryRefDeocument : subCategoryRefDocuments) {
        String code = subCategoryRefDeocument.getCode();
        CommerceId commerceId = commerceId(CATEGORY).withExternalId(code).build();
        Category subCategory = findCategoryById(commerceId, parentCategory.getContext());
        if (subCategory == null) {
          LOG.warn("Cannot find subcategory '{}'.", code);
        } else {
          subCategories.add(subCategory);
        }
      }
    }
    return subCategories;
  }

  @Nullable
  @Override
  public Category findCategoryById(@Nonnull CommerceId id, @Nonnull StoreContext storeContext) {
    if (CategoryImpl.isRootCategoryId(id)) {
      return findRootCategory(id.getCatalogAlias(), storeContext);
    }

    CommerceCache cache = getCommerceCache();

    CategoryCacheKey cacheKey = new CategoryCacheKey(id, storeContext, catalogResource, cache);

    return cache.find(cacheKey)
            .map(categoryDocument -> createBeanFor(categoryDocument, storeContext, CATEGORY, Category.class))
            .orElse(null);
  }

  @Nullable
  @Override
  public Category findCategoryBySeoSegment(@Nonnull String seoSegment, @Nonnull StoreContext storeContext) {
    return null;
  }

  @Nonnull
  @Override
  public SearchResult<Product> searchProducts(@Nonnull String searchTerm,
                                              @Nonnull Map<String, String> searchParams,
                                              @Nonnull StoreContext storeContext) {
    SearchResult<Product> result = new SearchResult<>();
    // catalogAlias processing to be implemented with CMS-9516 (multi catalog support for hybris)

    // in some cases of an id as search term we do not find a product, therefore we try a direct find call first...
    CommerceId commerceId = commerceId(PRODUCT).withExternalId(searchTerm).build();
    Product product = findProductById(commerceId, storeContext);
    if (product != null) {
      List<Product> products = convertToProducts(Collections.singletonList(product));
      if (!products.isEmpty()) {
        result.setSearchResult(products);
        result.setTotalCount(products.size());
        result.setPageSize(products.size());
        result.setPageNumber(1);
        return result;
      }
    }
    result = searchProductsPaginated(searchTerm, searchParams, storeContext, Product.class);
    return result;
  }

  @Nonnull
  @Override
  public SearchResult<ProductVariant> searchProductVariants(@Nonnull String searchTerm,
                                                            @Nonnull Map<String, String> searchParams,
                                                            @Nonnull StoreContext storeContext) {
    SearchResult<ProductVariant> result = new SearchResult<>();
    // catalogAlias processing to be implemented with CMS-9516 (multi catalog support for hybris)

    CommerceId commerceId = commerceId(SKU).withExternalId(searchTerm).build();
    Product product = findProductById(commerceId, storeContext);
    if (product != null) {
      if (product.isVariant()) {
        result.setSearchResult(Collections.singletonList((ProductVariant) product));
        result.setTotalCount(1);
        result.setPageSize(1);
        result.setPageNumber(1);
      } else {
        List<ProductVariant> skus = product.getVariants();
        result.setSearchResult(skus);
        result.setTotalCount(skus.size());
        result.setPageSize(skus.size());
        result.setPageNumber(1);
      }
    } else {
      result = searchProductsPaginated(searchTerm, searchParams, storeContext, ProductVariant.class);
    }
    return result;
  }

  @Nonnull
  @Override
  public List<Catalog> getCatalogs(@Nonnull StoreContext storeContext) {
    // to be implemented with CMS-9516 (multi catalog support for hybris)
    return Collections.emptyList();
  }

  @Nonnull
  @Override
  public Optional<Catalog> getCatalog(@Nonnull CatalogId catalogId, @Nonnull StoreContext storeContext) {
    // to be implemented with CMS-9516 (multi catalog support for hybris)
    return Optional.empty();
  }

  @Nonnull
  @Override
  public Optional<Catalog> getCatalog(@Nonnull CatalogAlias alias, @Nonnull StoreContext storeContext) {
    // to be implemented with CMS-9516 (multi catalog support for hybris)
    return Optional.empty();
  }

  @Nonnull
  @Override
  public Optional<Catalog> getDefaultCatalog(@Nonnull StoreContext storeContext) {
    // to be implemented with CMS-9516 (multi catalog support for hybris)
    return Optional.empty();
  }

  @SuppressWarnings("unchecked")
  private <T> SearchResult<T> searchProductsPaginated(@Nonnull String searchTerm,
                                                      @Nonnull Map<String, String> searchParams,
                                                      @Nonnull StoreContext storeContext,
                                                      @Nonnull Class<T> returnType) {
    Set<T> resultSet = new LinkedHashSet<>();
    Map<String, String> params = new HashMap<>(searchParams);

    int pageCount = params.get(CatalogService.SEARCH_PARAM_PAGENUMBER) != null ?
            Integer.parseInt(params.get(CatalogService.SEARCH_PARAM_PAGENUMBER)) : 0;
    int pageSize = params.get(CatalogService.SEARCH_PARAM_PAGESIZE) != null ?
            Integer.parseInt(params.get(CatalogService.SEARCH_PARAM_PAGESIZE)) : maxProductSearchResults;
    int pages = Integer.MAX_VALUE;

    int maxItems = params.get(CatalogService.SEARCH_PARAM_TOTAL) != null ?
            Integer.parseInt(params.get(CatalogService.SEARCH_PARAM_TOTAL)) : maxProductSearchResults;

    // the given offset in parameters is actually the startPosition (startPostion is one more than offset)
    int startPosition = params.get(CatalogService.SEARCH_PARAM_OFFSET) != null ?
            Integer.parseInt(params.get(CatalogService.SEARCH_PARAM_OFFSET)) : 1;
    int offset = startPosition > 1 ? startPosition - 1 : 0;


    ProductSearchDocument productSearchDocument = null;
    while (pageCount < pages && resultSet.size() < maxItems) {
      params.put(CatalogService.SEARCH_PARAM_PAGENUMBER, Integer.toString(pageCount));
      params.put(CatalogService.SEARCH_PARAM_PAGESIZE, Integer.toString(pageSize));

      productSearchDocument = catalogResource.searchProducts(
              searchTerm, params, storeContext);
      if (productSearchDocument == null) {
        break;
      }

      List<Product> productsFromPage = resolveProductRefs(productSearchDocument.getProducts(), storeContext);
      productsFromPage = returnType == ProductVariant.class ?
              filterProductVariants(productsFromPage) :
              convertToProducts(productsFromPage);

      for (Product p : productsFromPage) {
        if (resultSet.size() < maxItems + offset) {
          resultSet.add((T) p);
        } else {
          break;
        }
      }

      pageCount = productSearchDocument.getPagination().getCurrentPage() + 1;
      pages = productSearchDocument.getPagination().getTotalPages();
    }

    List<T> totalList = new ArrayList<>(resultSet);
    List<T> subList = totalList.subList(offset, totalList.size());

    SearchResult<T> result = new SearchResult<>();
    result.setSearchResult(subList);
    if (productSearchDocument != null) {
      result.setTotalCount(subList.size() <= maxItems ?
              subList.size() : productSearchDocument.getPagination().getTotalResults());
      result.setPageSize(productSearchDocument.getPagination().getPageSize());
      result.setPageNumber(productSearchDocument.getPagination().getCurrentPage());
      result.setFacets(productSearchDocument.getFacets());
    }
    return result;
  }

  /**
   * Resolve a list of products and variant refs and turn it into a list of products and variants.
   */
  private List<Product> resolveProductRefs(List<ProductRefDocument> productRefDocuments, @Nonnull StoreContext context) {
    List<Product> result = new ArrayList<>();
    if (productRefDocuments != null) {
      for (ProductRefDocument productRefDocument : productRefDocuments) {
        String externalId = productRefDocument.getCode();
        CommerceId commerceId = commerceId(PRODUCT).withExternalId(externalId).build();
        Product product = findProductById(commerceId, context); //this is expensive but necessary to filter out product variants
        if (product == null) {
          LOG.warn("Cannot find product '{}'.", externalId);
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
  private static List<ProductRefDocument> filterProductRefs(List<ProductRefDocument> productRefs) {
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
  private static List<Product> convertToProducts(List<Product> products) {
    Set<Product> result = new LinkedHashSet<>();
    for (Product product : products) {
      if (!product.isVariant()) {
        result.add(product);
      } else if (product instanceof ProductVariant) {
        Product parent = ((ProductVariant) product).getParent();
        while (parent instanceof ProductVariant && ((ProductVariant) parent).getParent() != null) {
          parent = ((ProductVariant) parent).getParent();
        }
        if (parent != null) {
          result.add(parent);
        }
      }
    }
    return new ArrayList<>(result);
  }

  /**
   * Filters variants from a list with variants and products.
   */
  private static List<Product> filterProductVariants(List<Product> products) {
    Set<ProductVariant> result = new LinkedHashSet<>();
    for (Product product : products) {
      if (product.isVariant()) {
        result.add((ProductVariant) product);
      }
    }
    return new ArrayList<>(result);
  }

  @Nonnull
  @Override
  public Category findRootCategory(@Nonnull CatalogAlias catalogAlias, @Nonnull StoreContext storeContext) {
    CommerceId commerceId = commerceId(CATEGORY).withExternalId(CategoryImpl.ROOT_CATEGORY_ROLE_ID).withCatalogAlias(catalogAlias).build();
    Category rootCategoryBean = (Category) getCommerceBeanFactory().createBeanFor(commerceId, storeContext);
    if (rootCategoryBean == null) {
      throw new NotFoundException("Cannot create root category for id " + commerceId);
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

  public void setMaxProductSearchResults(int maxProductSearchResults) {
    this.maxProductSearchResults = maxProductSearchResults;
  }
}
