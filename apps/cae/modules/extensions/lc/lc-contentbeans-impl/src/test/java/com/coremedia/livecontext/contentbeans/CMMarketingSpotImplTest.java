package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.Vendor;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpot;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CMMarketingSpotImplTest {

  private static final String MY_MARKETING_SPOT_NAME = "myMarketingSpotName";

  @Mock
  private Content content;

  @Mock
  private MarketingSpot marketingSpot;

  @Mock
  private MarketingSpotService marketingSpotService;

  @Mock
  private CommerceConnectionSupplier commerceConnectionSupplier;

  @Mock
  private SitesService sitesService;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private StoreContext storeContext;

  private CMMarketingSpotImpl testling;

  @BeforeEach
  void init() {
    CommerceConnection commerceConnection = mock(CommerceConnection.class);
    when(commerceConnection.getInitialStoreContext()).thenReturn(storeContext);
    when(commerceConnection.getMarketingSpotService()).thenReturn(Optional.of(marketingSpotService));

    when(commerceConnectionSupplier.findConnection(any(Content.class))).thenReturn(Optional.of(commerceConnection));

    CommerceId externalId = BaseCommerceIdProvider.commerceId(Vendor.of("moin"), BaseCommerceBeanType.MARKETING_SPOT)
            .withExternalId("myExternalId")
            .build();
    lenient().when(marketingSpotService.findMarketingSpotById(eq(externalId), any(StoreContext.class))).thenReturn(marketingSpot);
    lenient().when(marketingSpot.getName()).thenReturn(MY_MARKETING_SPOT_NAME);

    lenient().when(content.getString(CMMarketingSpotImpl.EXTERNAL_ID)).thenReturn(CommerceIdFormatterHelper.format(externalId));

    testling = new TestCMMarketingSpotImpl();
    testling.setSitesService(sitesService);
    testling.setCommerceConnectionSupplier(commerceConnectionSupplier);
  }

  @Test
  void testGetTeaserTitle() {
    //teaser title is set
    String myTeaserTitle = "myTeaserTitle";
    when(content.getString(CMTeasable.TEASER_TITLE)).thenReturn(myTeaserTitle);
    assertThat(testling.getTeaserTitle())
            .as("the teaser title of the CMMarketingSpot is the same as the teaserTitle string property of the content")
            .isEqualTo(myTeaserTitle);

    //teaser title is not set
    when(content.getString(CMTeasable.TEASER_TITLE)).thenReturn(null);
    assertThat(testling.getTeaserTitle())
            .as("the teaser title of the CMMarketingSpot is the same as the teaserTitle string property of the content")
            .isEqualTo(MY_MARKETING_SPOT_NAME);
  }

  @Test
  void testGetMarketingSpot() {
    MarketingSpot marketingSpot = testling.getMarketingSpot();
    assertThat(marketingSpot).isNotNull();
    assertThat(marketingSpot.getName()).isEqualTo(MY_MARKETING_SPOT_NAME);
  }

  @Test
  void testGetMarketingSpotIdNotValid() {
    when(content.getString(CMMarketingSpotImpl.EXTERNAL_ID)).thenReturn("test:///me/marketingspot/notFound");
    assertThat(testling.getMarketingSpot()).isNull();
  }

  private class TestCMMarketingSpotImpl extends CMMarketingSpotImpl {
    @Override
    public Content getContent() {
      return content;
    }
  }
}
