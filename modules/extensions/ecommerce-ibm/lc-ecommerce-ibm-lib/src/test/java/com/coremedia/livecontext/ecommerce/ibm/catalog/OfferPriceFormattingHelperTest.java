package com.coremedia.livecontext.ecommerce.ibm.catalog;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OfferPriceFormattingHelperTest {

  @Test
  public void testFormatOfferPrices() {
    String currency = "USD";
    String formattedPrice = OfferPriceFormattingHelper.formatOfferPrice(currency,"({* 100} 100)");
    assertEquals("<= 100,00 USD", formattedPrice);
    formattedPrice = OfferPriceFormattingHelper.formatOfferPrice(currency,"({100 200} 200)");
    assertEquals("100,01 - 200,00 USD", formattedPrice);
    formattedPrice = OfferPriceFormattingHelper.formatOfferPrice(currency,"({500 *})");
    assertEquals("> 500,00 USD", formattedPrice);
  }
}
