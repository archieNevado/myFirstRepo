package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.teaserOverlay.TeaserOverlaySettings;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.coremedia.livecontext.contentbeans.ProductTeasableHelperTest.createMarkup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CMProductTeaserImplTest {

  static final String CONTENT_TEASER_TITLE_PROPERTY = "teaserTitle";
  static final String CONTENT_TEASER_TITLE = "content teaser title";
  static final String CATALOG_TEASER_TITLE = "catalog teaser title";

  static final String CONTENT_TEASER_TEXT_PROPERTY = "teaserText";
  static final String CONTENT_TEASER_TEXT = "content teaser text";
  static final String CATALOG_TEASER_TEXT = "catalog teaser text";
  static final Markup CONTENT_TEASER_TEXT_MARKUP = createMarkup(CONTENT_TEASER_TEXT);
  static final Markup CATALOG_TEASER_TEXT_MARKUP = createMarkup(CATALOG_TEASER_TEXT);
  static final Markup EMPTY_MARKUP = createMarkup("");

  static final String CONTENT_TITLE_PROPERTY = "title";
  static final String CONTENT_TITLE = "content title";

  static final String CONTENT_DETAIL_TEXT_PROPERTY = "detailText";
  static final String CONTENT_DETAIL_TEXT = "content detail text";

  @Mock
  private Content content;

  @Mock
  private SettingsService settingsService;

  @Mock
  private TeaserOverlaySettings teaserOverlaySettings;

  private CMProductTeaserImpl testling;

  @Mock
  private ProductTeasableHelper productTeasableHelper;

  @BeforeEach
  public void setUp() {
    testling = new TestCMProductTeaserImpl(content, mock(Product.class));
    testling.setProductTeasableHelper(productTeasableHelper);
    testling.setSettingsService(settingsService);
  }

  @Test
  void testTeasableFallbackPropertiesWithNull() {
    when(content.getString(CONTENT_TEASER_TITLE_PROPERTY)).thenReturn(null);
    when(content.getMarkup(CONTENT_TEASER_TEXT_PROPERTY)).thenReturn(null);
    when(content.getString(CONTENT_TITLE_PROPERTY)).thenReturn(CONTENT_TITLE);
    when(content.getMarkup(CONTENT_DETAIL_TEXT_PROPERTY)).thenReturn(createMarkup(CONTENT_DETAIL_TEXT));

    when(productTeasableHelper.getTeaserTextInternal(testling, null)).thenReturn(null);
    when(productTeasableHelper.getTeaserTitleInternal(testling, null)).thenReturn(null);

    assertThat(testling.getTeaserTitle()).isEqualTo(CONTENT_TITLE);
    assertThat(MarkupUtil.asPlainText(testling.getTeaserText()).trim()).isEqualTo(CONTENT_DETAIL_TEXT);
  }

  @Test
  void testTeasableFallbackPropertiesWithEmptyValues() {
    when(content.getString(CONTENT_TEASER_TITLE_PROPERTY)).thenReturn("");
    when(content.getMarkup(CONTENT_TEASER_TEXT_PROPERTY)).thenReturn(EMPTY_MARKUP);
    when(content.getString(CONTENT_TITLE_PROPERTY)).thenReturn(CONTENT_TITLE);
    when(content.getMarkup(CONTENT_DETAIL_TEXT_PROPERTY)).thenReturn(createMarkup(CONTENT_DETAIL_TEXT));

    when(productTeasableHelper.getTeaserTitleInternal(testling, "")).thenReturn("");

    assertThat(testling.getTeaserTitle()).isEqualTo(CONTENT_TITLE);
    assertThat(MarkupUtil.asPlainText(testling.getTeaserText()).trim()).isEqualTo(CONTENT_DETAIL_TEXT);
  }

  private static class TestCMProductTeaserImpl extends CMProductTeaserImpl {
    private Content content;
    private Product product;

    TestCMProductTeaserImpl(Content content, Product product) {
      this.content = content;
      this.product = product;
    }

    @Override
    public Content getContent() {
      return content;
    }

    @Override
    public Product getProduct() {
      return product == null ? super.getProduct() : product;
    }
  }
}
