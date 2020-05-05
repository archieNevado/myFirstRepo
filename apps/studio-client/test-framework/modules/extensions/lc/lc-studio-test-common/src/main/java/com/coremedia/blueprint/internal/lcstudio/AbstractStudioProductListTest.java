package com.coremedia.blueprint.internal.lcstudio;

import com.coremedia.blueprint.studio.uitest.core.StudioTestCase;
import com.coremedia.blueprint.uitesting.lc.TestSiteConfiguration;
import com.coremedia.blueprint.uitesting.lc.UapiConnectionUtils;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cms.integration.test.util.CleanupRegistries;
import com.coremedia.cms.integration.test.util.SiteBuilder;
import com.coremedia.testing.net.HttpClientUtil;
import com.coremedia.uitesting.cms.editor.AutoOpenStudio;
import com.coremedia.uitesting.cms.editor.components.LocalComboBox;
import com.coremedia.uitesting.cms.editor.components.desktop.WorkArea;
import com.coremedia.uitesting.cms.editor.components.premular.PreviewIFrame;
import com.coremedia.uitesting.cms.editor.sdk.sites.SitesService;
import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSBy;
import com.coremedia.uitesting.ext3.wrappers.form.NumberField;
import com.coremedia.uitesting.ext3.wrappers.form.field.ComboBoxField;
import com.coremedia.uitesting.uapi.helper.ContentUtils;
import com.coremedia.uitesting.webdriver.CoreMediaWebDriverProvider;
import com.coremedia.uitesting.webdriver.IdleIndicators;
import com.coremedia.uitesting.webdriver.WebAppProxy;
import com.coremedia.uitesting.webdriver.conditions.WebElementConditions;
import com.google.common.collect.Comparators;
import net.joala.bdd.reference.Reference;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static net.joala.bdd.reference.References.ref;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ProhibitedExceptionDeclared")
public class AbstractStudioProductListTest extends StudioTestCase {

  private final static String PREVIEW_CONTENT_XPATH = "//*[contains(@class, 'cm-preview-content')][descendant::*[contains(@class, 'content')]]";
  private static final int SEARCH_LENGTH = 3;
  private static final int SEARCH_OFFSET = 2;
  private static final String EMPTY_VALUE = "";
  private static final String ORDER_BY_PRICE_ASC = "ORDER_BY_TYPE_PRICE_ASC";
  private static final String PRICE_MATCH_REGEX = "[$]\\d*[.]\\d{2}";
  private static final String CMPRODUCTLIST = "CMProductList";

  @Inject
  @Rule
  public CleanupRegistries cleanupRegistries;

  public void scenario_test_product_list(String contentBasePath, TestSiteConfiguration testSite, WebAppProxy<? extends WebAppProxy> siteWrapper) throws Exception {
    Reference<Content> contentRef = ref("productListRef");
    Reference<Site> siteRef = ref("anySite");

    steps.given_I_am_logged_in_to_CoreMedia_Studio_as_an_editor(testSite);
    steps.given_I_have_a_site(siteRef, siteWrapper);

    steps.given_I_create_a_new_content(contentRef, contentBasePath);
    steps.given_I_open_the_content_in_tab(contentRef);

    steps.given_the_category_facets_fieldgroup_is_visible();

    steps.given_the_search_refinement_fieldgroup_is_visible();
    steps.given_the_search_length_fields_value_is_reflected_in_preview();
    steps.given_the_search_offset_fields_value_is_reflected_in_preview();
    steps.given_the_search_order_fields_value_is_reflected_in_preview();
  }

  @Inject
  private Steps steps;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Named
  @Singleton
  public static class Steps {

    @Inject
    private AutoOpenStudio studio;
    @Inject
    private WorkArea workArea;
    @Inject
    private ContentUtils contentUtils;
    @Inject
    private Provider<SiteBuilder> siteBuilderProvider;
    @Inject
    private SitesService sitesServiceProxy;
    @Inject
    private IdleIndicators idleIndicators;
    @Inject
    private UapiConnectionUtils uapiConnectionUtils;
    @Inject
    private CoreMediaWebDriverProvider driverProvider;
    @Inject
    private WebElementConditions webElementConditions;

    private WebAppProxy<? extends WebAppProxy> siteWrapper;

    void given_I_am_logged_in_to_CoreMedia_Studio_as_an_editor(TestSiteConfiguration testSiteConfiguration) {
      sitesServiceProxy.switchPreferredSiteInContent(uapiConnectionUtils.getSite(testSiteConfiguration).getId());
      studio.get();
      idleIndicators.idle().waitUntilTrue();
    }

    void given_I_create_a_new_content(Reference<Content> contentRef, String contentBasePath) {
      Content folder = contentUtils.getContentRepository().getChild(contentBasePath);
      Content testFolder = contentUtils.createFolder(folder, "test");
      Content content = contentUtils.createDocument(CMPRODUCTLIST, testFolder);
      content.checkOut();
      content.set("title", "product list test document");
      content.checkIn();
      contentRef.set(content);
    }

    void given_I_have_a_site(Reference<Site> siteRef, WebAppProxy<? extends WebAppProxy> siteWrapper) {
      Site newSite = siteBuilderProvider.get().build();
      siteRef.set(newSite);

      this.siteWrapper = siteWrapper;
    }

    void given_the_category_facets_fieldgroup_is_visible() {
      Panel facetsFieldGroup = assertComponentIsVisible("cmProductListFacetsFieldGroup");

      LocalComboBox filterByComboBox = facetsFieldGroup.find(LocalComboBox.class, ExtJSBy.itemId("facetCombo"));
      filterByComboBox.visible().assertTrue();

      LocalComboBox filterValueComboBox = facetsFieldGroup.find(LocalComboBox.class, ExtJSBy.itemId("facetValueCombo"));
      filterValueComboBox.visible().assertTrue();
    }

    void given_the_search_refinement_fieldgroup_is_visible() {
      assertComponentIsVisible("cmProductListSearchRefinementFieldGroup");
    }

    void given_the_search_length_fields_value_is_reflected_in_preview() {
      Panel searchFieldGroup = assertComponentIsVisible("cmProductListSearchRefinementFieldGroup");

      Container searchLengthSpinner = searchFieldGroup.find(Container.class, ExtJSBy.itemId("cmProductListSearchRefinementMaxLength"));
      searchLengthSpinner.visible().assertTrue();

      NumberField searchLengthField = searchLengthSpinner.getComponent("spinnerPropertyField", NumberField.class);
      searchLengthField.setValue(String.valueOf(SEARCH_LENGTH));
      List<String> products = getProductTexts();
      assertThat(products.size()).isEqualTo(SEARCH_LENGTH);

      searchLengthField.visible().await();
      searchLengthField.setValue(EMPTY_VALUE);
      searchLengthField.value().assertThat(Matchers.nullValue());
    }

    void given_the_search_offset_fields_value_is_reflected_in_preview() {
      Panel searchFieldGroup = assertComponentIsVisible("cmProductListSearchRefinementFieldGroup");

      Container searchOffsetSpinner = searchFieldGroup.find(Container.class, ExtJSBy.itemId("cmProductListSearchRefinementOffset"));
      searchOffsetSpinner.visible().assertTrue();

      NumberField searchOffsetField = searchOffsetSpinner.getComponent("spinnerPropertyField", NumberField.class);
      List<String> withoutOffset = getProductTexts();
      searchOffsetField.visible().await();
      searchOffsetField.setValue(String.valueOf(SEARCH_OFFSET));
      List<String> withOffset = getProductTexts();
      assertThat(withoutOffset.get(SEARCH_OFFSET - 1)).isEqualTo(withOffset.get(0));

      searchOffsetField.visible().await();
      searchOffsetField.setValue(EMPTY_VALUE);
      searchOffsetField.value().assertThat(Matchers.nullValue());
    }

    void given_the_search_order_fields_value_is_reflected_in_preview() {
      Panel searchFieldGroup = assertComponentIsVisible("cmProductListSearchRefinementFieldGroup");

      Container comboBoxStringPropertyField = searchFieldGroup.find(Container.class, ExtJSBy.itemId("cmProductListOrderByField"));
      comboBoxStringPropertyField.visible().assertTrue();

      ComboBoxField searchOrderComboBox = comboBoxStringPropertyField.getComponent("comboBoxStringPropertyField", ComboBoxField.class);
      searchOrderComboBox.containsValue(ORDER_BY_PRICE_ASC);
      searchOrderComboBox.select(ORDER_BY_PRICE_ASC);
      searchOrderComboBox.value().assertEquals(ORDER_BY_PRICE_ASC);

      Pattern pattern = Pattern.compile(PRICE_MATCH_REGEX);
      List<Double> prices = getProductTexts()
              .stream()
              .map(pattern::matcher)
              .filter(Matcher::find)
              .map(Matcher::group)
              .map(s -> Double.valueOf(s.substring(1)))
              .collect(Collectors.toList());
      assertThat(Comparators.isInOrder(prices, Comparator.naturalOrder())).isTrue();

      searchOrderComboBox.visible().await();
      searchOrderComboBox.setValue(EMPTY_VALUE);
      searchOrderComboBox.value().assertThat(Matchers.nullValue());
    }

    void given_I_open_the_content_in_tab(Reference<Content> contentRef) {
      workArea.openInTab(contentRef.get());
    }

    private Panel assertComponentIsVisible(String componentId) {
      workArea.getActivePremular().getDocumentForm().visibleToUser().assertTrue();
      Panel collapsibleViewTypeFormPanel = workArea.getActivePremular().getDocumentForm().getSubPanel(componentId);
      collapsibleViewTypeFormPanel.visible().assertTrue();
      collapsibleViewTypeFormPanel.expand();
      return collapsibleViewTypeFormPanel;
    }

    private List<String> getProductTexts() {
      final PreviewIFrame previewIFrame = workArea.getActivePremular().getPreviewPanel().getInnerPreviewPanel().getPreviewIFrame();
      previewIFrame.visible().assertTrue();
      loadPreviewInOwnWindow();
      WebElement previewContent = webElementConditions.element(driverProvider.get(), By.xpath(PREVIEW_CONTENT_XPATH))
              .withMessage(format("Can't find the content element in preview with xpath: %s", PREVIEW_CONTENT_XPATH))
              .await();

      List<WebElement> products = Stream
              .of("cm-landscape-banner", "cm-portrait-banner", "cm-square-banner")
              .map(className -> previewContent.findElements(By.className(className)))
              .flatMap(Collection::stream)
              .collect(Collectors.toList());
      List<String> productTexts = products
              .stream()
              .map(WebElement::getText)
              .collect(Collectors.toList());

      studio.reload();
      return productTexts;
    }

    private void loadPreviewInOwnWindow() {
      final PreviewIFrame previewIFrame = workArea.getActivePremular().getPreviewPanel().getInnerPreviewPanel().getPreviewIFrame();
      previewIFrame.visible().assertTrue();
      String previewUrl = previewIFrame.url().await();
      if (!previewUrl.startsWith("http:") && !previewUrl.startsWith("https:")) {
        previewUrl = "https:" + previewUrl;
      }

      String redirectUrl;
      try {
        redirectUrl = HttpClientUtil.getRedirectUrl(previewUrl, uapiConnectionUtils.getLatestContentEventSequenceNumber());
      } catch (URISyntaxException | IOException e) {
        throw new IllegalArgumentException(e);
      }

      siteWrapper.setTestApplicationUrl(redirectUrl);
      siteWrapper.loadWithoutDeletingCookies();
      siteWrapper.setPingUrl(redirectUrl);
      siteWrapper.get();
    }
  }
}
