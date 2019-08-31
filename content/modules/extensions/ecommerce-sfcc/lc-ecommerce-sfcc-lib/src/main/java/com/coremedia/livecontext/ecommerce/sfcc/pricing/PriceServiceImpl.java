package com.coremedia.livecontext.ecommerce.sfcc.pricing;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.resources.ProductsResource;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Optional;

/**
 * Price service for Salesforce Commerce Cloud.
 */
@DefaultAnnotation(NonNull.class)
public class PriceServiceImpl {

  private static final String LIST_PRICES_SUFFIX = "-list-prices";
  private static final String OFFER_PRICES_SUFFIX = "-sale-prices";

  private final ProductsResource productsResource;

  public PriceServiceImpl(ProductsResource productsResource) {
    this.productsResource = productsResource;
  }

  public Optional<BigDecimal> findListPriceForProduct(String productId, String storeId, Currency currency,
                                                      StoreContext storeContext) {
    return getPriceInternal(productId, storeId, currency, LIST_PRICES_SUFFIX, storeContext);
  }

  public Optional<BigDecimal> findOfferPriceForProduct(String productId, String storeId, Currency currency,
                                                       StoreContext storeContext) {
    return getPriceInternal(productId, storeId, currency, OFFER_PRICES_SUFFIX, storeContext);
  }

  private Optional<BigDecimal> getPriceInternal(String productId, String storeId, Currency currency,
                                                String listSuffix, StoreContext storeContext) {
    Optional<ProductDocument> doc = productsResource.getProductById(productId, storeId, null, currency, storeContext);

    return doc
            .map(ProductDocument::getPrice)
            .map(BigDecimal::valueOf)
            .map(price -> price.setScale(2, RoundingMode.HALF_UP));
  }
}
