package com.coremedia.livecontext.ecommerce.ibm.catalog;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OfferPriceFormattingHelperTest {

  @Test
  public void testFormatOfferPrices() {
    String currency = "USD";
    String formattedPrice = OfferPriceFormattingHelper.formatOfferPrice(currency,"({* 100} 100)");
    assertThat("<= 100,00 USD").isEqualTo(formattedPrice);
    formattedPrice = OfferPriceFormattingHelper.formatOfferPrice(currency,"({100 200} 200)");
    assertThat("100,01 - 200,00 USD").isEqualTo(formattedPrice);
    formattedPrice = OfferPriceFormattingHelper.formatOfferPrice(currency,"({500 *})");
    assertThat("> 500,00 USD").isEqualTo(formattedPrice);
  }
}
