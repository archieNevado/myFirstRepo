package com.coremedia.livecontext.ecommerce.hybris.pricing;

import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.beans.ProductImpl;
import com.coremedia.livecontext.ecommerce.hybris.common.AbstractHybrisService;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.PriceDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.PriceRefDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.resources.CatalogResource;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

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

public class PriceServiceImpl extends AbstractHybrisService {

  private static final Logger LOG = LoggerFactory.getLogger(PriceServiceImpl.class);

  private CatalogResource catalogResource;

  @Inject
  @Named("hybrisCatalogService")
  private CatalogService catalogService;

  /**
   * Returns the offer price value for the given product.
   *
   * @param product the product to obtain the offer price for
   * @return The list price value, or nothing if no such price exists.
   */
  @NonNull
  public Optional<BigDecimal> findOfferPriceForProduct(@NonNull Product product) {
    StoreContext storeContext = product.getContext();

    // TODO Use cache keys
    // read prices via REST service
    List<PriceDocument> prices = getPriceDocuments(product, storeContext);
    return findOfferPriceForPrices(prices, storeContext);
  }

  @NonNull
  @VisibleForTesting
  Optional<BigDecimal> findOfferPriceForPrices(@NonNull List<PriceDocument> prices,
                                               @NonNull StoreContext storeContext) {
    List<PriceDocument> filteredPriceDocuments = filterCurrency(prices, storeContext.getCurrency());

    // The offer price in Hybris is modeled as price.isGiveAwayPrice
    return filterGiveAwayPrice(filteredPriceDocuments, Boolean.TRUE)
            .map(PriceDocument::getPrice)
            .flatMap(PriceServiceImpl::convertStringToBigDecimal);
  }

  /**
   * Returns the list price value for the given product.
   *
   * @param product the product to obtain the list price for
   * @return The list price value, or nothing if no such price exists.
   */
  @NonNull
  public Optional<BigDecimal> findListPriceForProduct(@NonNull Product product) {
    StoreContext storeContext = product.getContext();

    // TODO Use cache keys
    // read prices via REST service
    List<PriceDocument> prices = getPriceDocuments(product, storeContext);
    return findListPriceForPrices(prices, storeContext);
  }

  @NonNull
  @VisibleForTesting
  Optional<BigDecimal> findListPriceForPrices(@NonNull List<PriceDocument> prices, @NonNull StoreContext storeContext) {
    List<PriceDocument> filteredPriceDocuments = filterCurrency(prices, storeContext.getCurrency());

    // The list price in Hybris is modeled as !price.isGiveAwayPrice
    return filterGiveAwayPrice(filteredPriceDocuments, Boolean.FALSE)
            .map(PriceDocument::getPrice)
            .flatMap(PriceServiceImpl::convertStringToBigDecimal);
  }

  @NonNull
  private List<PriceDocument> getPriceDocuments(@NonNull Product product, @NonNull StoreContext storeContext) {
    ProductDocument delegate = ((ProductImpl) product).getDelegate();
    List<PriceRefDocument> priceRefDocuments = delegate.getPriceRefDocuments();
    if (priceRefDocuments == null) {
      return emptyList();
    }

    List<PriceDocument> prices = new ArrayList<>();

    for (PriceRefDocument priceRefDocument : priceRefDocuments) {
      PriceDocument priceDocument = catalogResource.getPriceDocumentById(priceRefDocument.getKey(), storeContext);
      if (priceDocument == null) {
        LOG.warn("Cannot find price {} for product {}", priceRefDocument.getKey(), product.getExternalId());
      }
      prices.add(priceDocument);
    }

    return prices;
  }

  /**
   * Returns the price which matches the given boolean give away price value.
   *
   * @param priceDocuments the prices
   * @param giveAwayPrice  true if the price is a give away price
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
