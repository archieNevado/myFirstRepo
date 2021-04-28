package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMQueryList;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.Category;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * A product list combines commerce products retrieved from the commerce system with arbitrarily compiled content items.
 * The commerce products can belong to a commerce category.
 * </p>
 * <p>
 * This content bean represents documents of that type within the CAE.
 * </p>
 */
public interface CMProductList extends CMQueryList {
  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMProductList'.
   */
  String NAME = "CMProductList";

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a {@link CMProductList} object
   */
  @Override
  CMProductList getMaster();

  /**
   * Returns the variants of this {@link CMProductList} indexed by their {@link Locale}
   *
   * @return the variants of this {@link CMProductList} indexed by their {@link Locale}
   */
  @Override
  Map<Locale, ? extends CMProductList> getVariantsByLocale();

  /**
   * Returns the {@link Locale} specific variants of this {@link CMProductList}
   *
   * @return the {@link Locale} specific variants of this {@link CMProductList}
   */
  @Override
  Collection<? extends CMProductList> getLocalizations();

  /**
   * Returns a {@code Map} from aspectIDs to Aspects. AspectIDs consists of an aspect name with a
   * prefix which identifies the plugin provider.
   *
   * @return a {@code Map} from aspectIDs to {@code Aspect}s
   * @deprecated since 1907.1; Implement optional features as extensions.
   */
  @Deprecated
  @Override
  Map<String, ? extends Aspect<? extends CMProductList>> getAspectByName();

  /**
   * Returns a list of all  {@code Aspect}s from all availiable
   * PlugIns that are registered to this content bean.
   *
   * @return a list of {@link Aspect}
   * @deprecated since 1907.1; Implement optional features as extensions.
   */
  @Deprecated
  @Override
  List<? extends Aspect<? extends CMProductList>> getAspects();

  /**
   * Returns an external id representing the category.
   *
   * @return an external id
   * @deprecated use {@link #getProducts()} instead
   */
  @Deprecated(since = "2104.1", forRemoval = true)
  String getExternalId();

  /**
   * Returns the category.
   *
   * @return the category
   * @deprecated use {@link #getProducts()} instead
   */
  @Deprecated(since = "2104.1", forRemoval = true)
  Category getCategory();

  /**
   * @return list of products
   */
  List<ProductInSite> getProducts();

  /**
   * @return string that determines the sorting of the list
   * @deprecated use {@link #getProducts()} instead
   */
  @Deprecated(since = "2104.1", forRemoval = true)
  String getOrderBy();

  /**
   * @return string that determines a facet search param
   * @deprecated use {@link #getProducts()} instead
   */
  @Deprecated(since = "2104.1", forRemoval = true)
  String getFacet();

  /**
   * @return string that determines a search query string
   * @deprecated use {@link #getProducts()} instead
   */
  @Deprecated(since = "2104.1", forRemoval = true)
  String getQuery();

  /**
   * @return the offset in the original product list where the rendering should start
   * @deprecated use {@link #getProducts()} instead
   */
  @Deprecated(since = "2104.1", forRemoval = true)
  int getOffset();

  /**
   * @return the sub struct "productList" of localSettings converted to a map
   * @deprecated use {@link #getProducts()} instead
   */
  @Deprecated(since = "2104.1", forRemoval = true)
  Map<String, Object> getProductListSettings();
}
