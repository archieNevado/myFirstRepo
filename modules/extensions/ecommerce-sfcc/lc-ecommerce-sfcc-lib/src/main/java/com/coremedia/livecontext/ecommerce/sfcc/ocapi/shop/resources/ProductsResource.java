package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.resources;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents.ProductDocument;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.OCShopApiConnector.STORE_ID_PARAM;

@Service("ocapiShopProductsResource")
public class ProductsResource extends AbstractShopResource {

  private static final String PRODUCT_ID_PARAM = "productId";

  private static final String PRODUCTS_PATH = "/products";
  private static final String PRODUCTS_ID_PATH = PRODUCTS_PATH + "/{" + PRODUCT_ID_PARAM + "}";

  /**
   * Returns a product using the specified id.
   *
   * @param productId product id
   * @return
   */
  public Optional<ProductDocument> getProductById(@Nonnull String productId, @Nonnull String storeId) {
    return getProductById(productId, storeId, null, null);
  }

  /**
   * Returns a product using the specified id with the given locale and currency.
   *
   * @param productId product id
   * @param locale    locale to use for texts like name and description
   * @param currency  currency to use for prices
   * @return
   */
  public Optional<ProductDocument> getProductById(@Nonnull String productId, @Nonnull String storeId, Locale locale,
                                                  Currency currency) {
    Map<String, String> pathParameters = ImmutableMap.of(STORE_ID_PARAM, storeId, PRODUCT_ID_PARAM, productId);

    ListMultimap<String, String> queryParams = buildQueryParams(locale, currency);

    return getConnector().getResource(PRODUCTS_ID_PATH, pathParameters, queryParams, ProductDocument.class);
  }

  @Nonnull
  private static ListMultimap<String, String> buildQueryParams(@Nullable Locale locale, @Nullable Currency currency) {
    ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.builder();

    builder.put("expand",
            "availability,bundled_products,links,promotions,options,images,prices,variations,set_products");

    if (locale != null) {
      builder.put("locale", locale.toLanguageTag());
    }

    if (currency != null) {
      builder.put("currency", currency.getCurrencyCode());
    }

    return builder.build();
  }
}
