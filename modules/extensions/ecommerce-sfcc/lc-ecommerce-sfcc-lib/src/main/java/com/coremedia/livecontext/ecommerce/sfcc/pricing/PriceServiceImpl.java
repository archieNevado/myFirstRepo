package com.coremedia.livecontext.ecommerce.sfcc.pricing;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.resources.ProductsResource;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Optional;

/**
 * Price service for Salesforce Commerce Cloud.
 */
public class PriceServiceImpl {

  private static final String LIST_PRICES_SUFFIX = "-list-prices";
  private static final String OFFER_PRICES_SUFFIX = "-sale-prices";

  private final ProductsResource productsResource;

  public PriceServiceImpl(@NonNull ProductsResource productsResource) {
    this.productsResource = productsResource;
  }

  @NonNull
  public Optional<BigDecimal> findListPriceForProduct(@NonNull String productId, @NonNull String storeId,
                                                      @NonNull Currency currency) {
    return getPriceInternal(productId, storeId, currency, LIST_PRICES_SUFFIX);
  }

  @NonNull
  public Optional<BigDecimal> findOfferPriceForProduct(@NonNull String productId, @NonNull String storeId,
                                                       @NonNull Currency currency) {
    return getPriceInternal(productId, storeId, currency, OFFER_PRICES_SUFFIX);
  }

  @NonNull
  private Optional<BigDecimal> getPriceInternal(@NonNull String productId, @NonNull String storeId,
                                                @NonNull Currency currency, @NonNull String listSuffix) {
    Optional<ProductDocument> doc = productsResource.getProductById(productId, storeId, null, currency);

    return doc
            .map(ProductDocument::getPrice)
            .map(BigDecimal::valueOf)
            .map(price -> price.setScale(2, RoundingMode.HALF_UP));
  }
}
