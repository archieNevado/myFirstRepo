package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMQueryList;
import com.coremedia.livecontext.commercebeans.ProductInSite;

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
   * @return list of products
   */
  List<ProductInSite> getProducts();
}
