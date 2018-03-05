package com.coremedia.livecontext.ecommerce.sfcc.pricing;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.resources.ProductsResource;

import javax.annotation.Nonnull;
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

  public PriceServiceImpl(@Nonnull ProductsResource productsResource) {
    this.productsResource = productsResource;
  }

  @Nonnull
  public Optional<BigDecimal> findListPriceForProduct(@Nonnull String productId, @Nonnull String storeId,
                                                      @Nonnull Currency currency) {
    return getPriceInternal(productId, storeId, currency, LIST_PRICES_SUFFIX);
  }

  @Nonnull
  public Optional<BigDecimal> findOfferPriceForProduct(@Nonnull String productId, @Nonnull String storeId,
                                                       @Nonnull Currency currency) {
    return getPriceInternal(productId, storeId, currency, OFFER_PRICES_SUFFIX);
  }

  @Nonnull
  private Optional<BigDecimal> getPriceInternal(@Nonnull String productId, @Nonnull String storeId,
                                                @Nonnull Currency currency, @Nonnull String listSuffix) {
    Optional<ProductDocument> doc = productsResource.getProductById(productId, storeId, null, currency);

    return doc
            .map(ProductDocument::getPrice)
            .map(BigDecimal::valueOf)
            .map(price -> price.setScale(2, RoundingMode.HALF_UP));
  }
}
