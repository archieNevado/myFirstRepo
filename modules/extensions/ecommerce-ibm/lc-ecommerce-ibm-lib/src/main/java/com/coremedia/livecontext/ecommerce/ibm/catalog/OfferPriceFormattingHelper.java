package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.livecontext.ecommerce.search.SearchFacet;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * This class encapsulates a method to transform offer price facets
 * delivered by IBM to a more readable format to display in studio.
 */
class OfferPriceFormattingHelper {

  private static String REGEX_OFFERPRICE_START = "^\\(+\\{+\\*+\\s+[0-9]*+\\}+\\s+[0-9]*+\\)";
  private static String REGEX_OFFERPRICE_MIDDLE = "^\\(+\\{+[0-9]*+\\s+[0-9]*+\\}+\\s+[0-9]*+\\)";
  private static String REGEX_OFFERPRICE_END = "^\\(+\\{+[0-9]*+\\s+\\*+\\}+\\)";

  private OfferPriceFormattingHelper() {
  }

  @Nonnull
  static List<SearchFacet> formatOfferPrices(@Nonnull List<SearchFacet> searchFacets, @Nonnull String currency) {
    return searchFacets.stream().map(
            searchFacet -> {
              String value = searchFacet.getLabel();
              return new LocalizedSearchFacet(searchFacet, formatOfferPrice(currency, value), searchFacet.getChildFacets());
            }
    ).collect(toList());
  }

  @VisibleForTesting
  @Nonnull
  static String formatOfferPrice(@Nonnull String currency, @Nonnull String value) {
    if (value.matches(REGEX_OFFERPRICE_MIDDLE)) {
      value = value.substring(value.indexOf("{") + 1, value.indexOf("}")).replace(" ", ",01 - ") + ",00" + " " + currency;
    } else if (value.matches(REGEX_OFFERPRICE_START)) {
      value = "<= " + value.substring(value.indexOf("*") + 2, value.indexOf("}")) + ",00" + " " + currency;
    } else if (value.matches(REGEX_OFFERPRICE_END)) {
      value = "> " + value.substring(value.indexOf("{") + 1, value.indexOf("*") - 1) + ",00" + " " + currency;
    }
    return value;
  }

  @Nonnull
  static SearchFacet tryFormatOfferPrice(@Nonnull SearchFacet searchFacet) {
    String label = searchFacet.getLabel();
    String currency = localizeCurrency(label);
    if (!isOfferPriceFacet(label)) {
      return searchFacet;
    }
    List<SearchFacet> childFacets = OfferPriceFormattingHelper.formatOfferPrices(searchFacet.getChildFacets(), currency);
    return new LocalizedSearchFacet(searchFacet, currency, childFacets);
  }

  @Nonnull
  private static String localizeCurrency(@Nonnull String label) {
    return label.substring(label.indexOf("_") + 1);
  }

  private static boolean isOfferPriceFacet(@Nonnull String label) {
    return label.contains("OfferPrice_");
  }
}
