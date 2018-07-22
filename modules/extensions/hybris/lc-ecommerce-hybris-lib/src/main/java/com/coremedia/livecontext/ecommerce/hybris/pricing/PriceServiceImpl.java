package com.coremedia.livecontext.ecommerce.hybris.pricing;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.beans.ProductImpl;
import com.coremedia.livecontext.ecommerce.hybris.common.AbstractHybrisService;
import com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.PriceDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.PriceRefDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.resources.CatalogResource;
import com.coremedia.livecontext.ecommerce.pricing.PriceService;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class PriceServiceImpl extends AbstractHybrisService implements PriceService {

  private static final Logger LOG = LoggerFactory.getLogger(PriceServiceImpl.class);

  private CatalogResource catalogResource;

  @Inject
  @Named("hybrisCatalogService")
  private CatalogService catalogService;

  /**
   * Returns the list price value for the given product.
   *
   * @param product
   * @return The list price value or NULL if no such price exists.
   */
  @Nullable
  public BigDecimal findOfferPriceForProduct(@NonNull Product product) {
    // TODO Use cache keys
    // read prices via REST service
    List<PriceDocument> prices = getPriceDocuments(product);
    return findOfferPriceForPrices(prices).orElse(null);
  }

  @NonNull
  @VisibleForTesting
  Optional<BigDecimal> findOfferPriceForPrices(@NonNull List<PriceDocument> prices) {
    // filter currency
    StoreContext currentContext = CurrentCommerceConnection.get().getStoreContext();
    List<PriceDocument> filteredPriceDocuments = filterCurrency(prices, currentContext.getCurrency());

    // The offer price in Hybris is modeled as price.isGiveAwayPrice
    return filterGiveAwayPrice(filteredPriceDocuments, Boolean.TRUE)
            .map(PriceDocument::getPrice)
            .flatMap(PriceServiceImpl::convertStringToBigDecimal);
  }

  /**
   * Returns the list price value for the given product.
   *
   * @param product
   * @return The list price value or NULL if no such price exists.
   */
  @Nullable
  public BigDecimal findListPriceForProduct(@NonNull Product product) {
    // TODO Use cache keys
    // read prices via REST service
    List<PriceDocument> prices = getPriceDocuments(product);
    return findListPriceForPrices(prices).orElse(null);
  }

  @NonNull
  @VisibleForTesting
  Optional<BigDecimal> findListPriceForPrices(@NonNull List<PriceDocument> prices) {
    // filter currency
    StoreContext currentContext = CurrentCommerceConnection.get().getStoreContext();
    List<PriceDocument> filteredPriceDocuments = filterCurrency(prices, currentContext.getCurrency());

    // The list price in Hybris is modeled as !price.isGiveAwayPrice
    return filterGiveAwayPrice(filteredPriceDocuments, Boolean.FALSE)
            .map(PriceDocument::getPrice)
            .flatMap(PriceServiceImpl::convertStringToBigDecimal);
  }

  @NonNull
  private List<PriceDocument> getPriceDocuments(@NonNull Product product) {
    ProductDocument delegate = ((ProductImpl) product).getDelegate();
    List<PriceRefDocument> priceRefDocuments = delegate.getPriceRefDocuments();
    if (priceRefDocuments == null) {
      return emptyList();
    }

    List<PriceDocument> prices = new ArrayList<>();

    StoreContext storeContext = StoreContextHelper.getCurrentContextOrThrow();
    for (PriceRefDocument priceRefDocument : priceRefDocuments) {
      PriceDocument priceDocument = catalogResource.getPriceDocumentById(priceRefDocument.getKey(), storeContext);
      if (priceDocument == null) {
        LOG.warn("Cannot find price " + priceRefDocument.getKey() + " for product " + product.getExternalId());
      }
      prices.add(priceDocument);
    }

    return prices;
  }

  /**
   * Returns the price which matches the given boolean give away price value.
   *
   * @param priceDocuments The prices
   * @param giveAwayPrice
   * @return The first price which matches the boolean value giveAwayPrice. Returns nothing if no price matches.
   */
  @NonNull
  @VisibleForTesting
  Optional<PriceDocument> filterGiveAwayPrice(@NonNull List<PriceDocument> priceDocuments,
                                              @NonNull Boolean giveAwayPrice) {
    return priceDocuments.stream()
            .filter(priceDocument -> giveAwayPrice.equals(priceDocument.isGiveAwayPrice()))
            .findFirst();
  }

  /**
   * Filters prices of given currency.
   *
   * @param priceDocuments The prices.
   * @param currency       The required currency.
   * @return Only the prices which are of the given currency. Empty list if no price matches the given currency.
   */
  @NonNull
  @VisibleForTesting
  List<PriceDocument> filterCurrency(@NonNull List<PriceDocument> priceDocuments, Currency currency) {
    if (currency == null) {
      return emptyList();
    }

    String currencyCode = currency.getCurrencyCode();

    return priceDocuments.stream()
            .filter(Objects::nonNull)
            .filter(priceDocument -> currencyCode.equals(priceDocument.getCurrencyISOCode()))
            .collect(toList());
  }

  @Nullable
  @Override
  public BigDecimal findOfferPriceForProduct(@NonNull String productId) {
    CommerceId commerceId = CommerceIdParserHelper.parseCommerceIdOrThrow(productId);
    StoreContext storeContext = StoreContextHelper.getCurrentContextOrThrow();

    Product product = catalogService.findProductById(commerceId, storeContext);
    if (product == null) {
      throw new CommerceException("No product found for commerce ID '" + commerceId + "'.");
    }

    return product.getOfferPrice();
  }

  @Nullable
  @Override
  public BigDecimal findListPriceForProduct(@NonNull String productId) {
    CommerceId commerceId = CommerceIdParserHelper.parseCommerceIdOrThrow(productId);
    StoreContext storeContext = StoreContextHelper.getCurrentContextOrThrow();

    Product product = catalogService.findProductById(commerceId, storeContext);
    if (product == null) {
      throw new CommerceException("No product found for commerce ID '" + commerceId + "'.");
    }

    return product.getListPrice();
  }

  @NonNull
  private static Optional<BigDecimal> convertStringToBigDecimal(String value) {
    if (!NumberUtils.isNumber(value)) {
      return Optional.empty();
    }

    BigDecimal bigDecimal = NumberUtils.createBigDecimal(value);
    return Optional.ofNullable(bigDecimal);
  }

  public CatalogResource getCatalogResource() {
    return catalogResource;
  }

  @Required
  public void setCatalogResource(CatalogResource catalogResource) {
    this.catalogResource = catalogResource;
  }
}
