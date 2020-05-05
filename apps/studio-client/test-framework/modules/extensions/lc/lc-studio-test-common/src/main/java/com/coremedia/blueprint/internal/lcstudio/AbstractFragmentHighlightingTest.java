package com.coremedia.blueprint.internal.lcstudio;

import com.coremedia.blueprint.studio.uitest.core.StudioTestCase;
import com.coremedia.blueprint.uitesting.lc.TestSiteConfiguration;
import com.coremedia.blueprint.uitesting.lc.UapiConnectionUtils;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructBuilder;
import com.coremedia.cap.struct.StructService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.uitesting.cms.editor.AutoLoginStudio;
import com.coremedia.uitesting.cms.editor.components.desktop.WorkArea;
import com.coremedia.uitesting.cms.editor.components.premular.PreviewIFrame;
import com.coremedia.uitesting.cms.editor.components.premular.PreviewIFrameToolbar;
import com.coremedia.uitesting.cms.editor.sdk.sites.SitesService;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSBy;
import com.coremedia.uitesting.uapi.helper.ContentUtils;
import com.coremedia.uitesting.uapi.helper.VersionCleanupService;
import com.coremedia.uitesting.ui.IconButton;
import com.coremedia.uitesting.webdriver.IdleIndicators;
import com.google.common.base.Preconditions;
import edu.umd.cs.findbugs.annotations.Nullable;
import net.joala.condition.Condition;
import net.joala.condition.ConditionFactory;
import net.joala.expression.AbstractExpression;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.test.context.TestPropertySource;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;

@TestPropertySource(properties = "livecontext.cache.invalidation.enabled:false")
public abstract class AbstractFragmentHighlightingTest extends StudioTestCase {

  @Inject
  private Steps steps;

  protected abstract String getCategoryExternalId();

  protected abstract String getSecondCategoryExternalId();

  protected abstract String getXpathPlacementFilled();

  protected abstract String getXpathElementInPlacement();

  protected abstract String getFilledFragmentCssClass();

  protected abstract String getXpathPlacementEmpty();

  protected abstract String getXpathEmptyText();

  protected abstract String getXpathEmptyOverlay();

  protected abstract String getEmptyFragmentCssClass();

  protected abstract String getXpathPlacementUnavailable();

  protected abstract String getXpathUnavailableText();

  protected abstract String getXpathUnavailableOverlay();

  protected abstract String getPlacementHighlightButtonXtype();

  protected abstract String getPagegridPlacement();

  protected abstract String getLinkedCollection();

  protected abstract String getPagegridLayout();

  public void fragmentHighlighting(TestSiteConfiguration testSite) {

    // ------------------ Setup --------------------------

    steps.given_I_am_logged_in_to_Studio_as_editor(testSite);

    steps.given_augmented_category_is_open_$0(
            getCategoryExternalId(), getPagegridPlacement(), getPagegridLayout(), getLinkedCollection(), testSite);

    // ------------------ assert that placements are highlighted ---------------------

    steps.when_I_use_the_fragment_highlight_button(getPlacementHighlightButtonXtype());

    steps.then_filled_placements_are_highlighted(getXpathPlacementFilled(),
            getXpathElementInPlacement(), getFilledFragmentCssClass());

    steps.then_empty_fragments_are_highlighted_and_have_additional_text(getXpathPlacementEmpty(),
            getXpathEmptyText(), getXpathEmptyOverlay(), getEmptyFragmentCssClass());

    steps.then_unresolveable_fragments_are_highlighted_and_have_additional_text(getXpathPlacementUnavailable(),
            getXpathUnavailableText(), getXpathUnavailableOverlay());

    // ------------------ Open Another Category  --------------------------

    steps.given_augmented_category_is_open_$0(
            getSecondCategoryExternalId(), getPagegridPlacement(), getPagegridLayout(), getLinkedCollection(), testSite);

    // ------------------ assert that placements are still  highlighted ---------------------

    steps.then_filled_placements_are_highlighted(getXpathPlacementFilled(),
            getXpathElementInPlacement(), getFilledFragmentCssClass());

    steps.then_empty_fragments_are_highlighted_and_have_additional_text(getXpathPlacementEmpty(),
            getXpathEmptyText(), getXpathEmptyOverlay(), getEmptyFragmentCssClass());

    steps.then_unresolveable_fragments_are_highlighted_and_have_additional_text(getXpathPlacementUnavailable(),
            getXpathUnavailableText(), getXpathUnavailableOverlay());

    // ------------------ Deselect highlighting ---------------------

    steps.when_I_use_the_fragment_highlight_button(getPlacementHighlightButtonXtype());

    // ------------------ assert that placements are not highlighted ---------------------

    steps.then_filled_placements_are_not_highlighted(getXpathPlacementFilled(),
            getXpathElementInPlacement(), getFilledFragmentCssClass());

    steps.then_empty_fragments_are_not_highlighted(
            getXpathPlacementEmpty(), getEmptyFragmentCssClass());

  }

  // -------------------------- Helper Class ------------------------------------

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Named
  @Singleton
  public static class Steps {

    @Inject
    private ConditionFactory conditionFactory;

    @Inject
    private VersionCleanupService versionCleanupService;

    @Inject
    private IdleIndicators idleIndicators;

    @Inject
    private AutoLoginStudio studio;

    @Inject
    private SitesService sitesServiceProxy;

    @Inject
    private WorkArea workArea;

    @Inject
    private UapiConnectionUtils uapiConnectionUtils;

    @Inject
    private CapConnection capConnection;

    @Inject
    private ContentUtils contentUtils;

    @Named("categoryAugmentationService")
    @Inject
    private AugmentationService augmentationService;

    void given_I_am_logged_in_to_Studio_as_editor(TestSiteConfiguration testSiteConfiguration) {
      sitesServiceProxy.switchPreferredSiteInContent(getTestSite(testSiteConfiguration).getId());
      studio.get();
      idleIndicators.idle().waitUntilTrue();
    }

    void given_augmented_category_is_open_$0(
            String externalId, String pagegridPlacement, String pagegridLayout,
            String linkedCollection, TestSiteConfiguration testSiteConfiguration) {

      Site testSite = getTestSite(testSiteConfiguration);
      Content contentByExternalId = augmentationService.getContentByExternalId(externalId, testSite);
      Preconditions.checkNotNull(contentByExternalId, "augmented content not found for externalId: " + externalId);

      versionCleanupService.register(contentByExternalId);

      workArea.openInTab(contentByExternalId);
      idleIndicators.idle().waitUntilTrue();

      // we change the layout to cover all possible fragment highlights: empty, full and unsupported.
      changeLayoutOfContent(contentByExternalId, pagegridPlacement, pagegridLayout, linkedCollection);

    }

    private void changeLayoutOfContent(Content content, String pagegridPlacement, String pagegridLayout, String linkedCollection) {
      Struct layoutStruct = buildLayoutStruct(linkedCollection, pagegridPlacement, pagegridLayout);
      contentUtils.setProperty(content, "placement", layoutStruct);
    }

    private Struct buildLayoutStruct(String linkedCollection, String pagegridPlacement, String pagegridLayout) {
      StructService structService = capConnection.getStructService();
      StructBuilder structBuilder = structService.createStructBuilder();
      StructBuilder placements2 = structService.createStructBuilder();
      StructBuilder placements = structService.createStructBuilder();
      StructBuilder placements998 = structService.createStructBuilder();

      placements998.declareBoolean("locked", false);
      Content placementToSet = capConnection.getContentRepository().getChild(pagegridPlacement);
      placements998.declareLink("section", toContentType("CMSymbol"), placementToSet);
      ArrayList<Content> links = new ArrayList<>();
      Content child = capConnection.getContentRepository().getChild(linkedCollection);
      links.add(child);
      placements998.declareLinks("items", toContentType("CMLinkable"), links);

      placements.declareStruct("998", placements998.build());
      placements2.declareLink("layout", toContentType("CMSettings"), capConnection
              .getContentRepository().getChild(pagegridLayout));
      placements2.declareStruct("placements", placements.build());
      structBuilder.declareStruct("placements_2", placements2.build());
      return structBuilder.build();
    }

    private ContentType toContentType(String contentType) {
      return capConnection.getContentRepository().getContentType(contentType);
    }

    void when_I_use_the_fragment_highlight_button(String placementHighlightButtonXtype) {
      PreviewIFrameToolbar previewIFrameToolbar = getPreviewFrameToolbar();
      IconButton button = previewIFrameToolbar.find(IconButton.class, ExtJSBy.xtype(placementHighlightButtonXtype));
      button.visible().assertTrue();
      button.click();
      idleIndicators.idle().waitUntilTrue();
    }

    void then_filled_placements_are_highlighted(
            String xpathPlacementFilled, String xpathElementInPlacement, String filledFragmentCssClass) {

      WebElement filledPlacement = getFilledPlacement(xpathPlacementFilled, xpathElementInPlacement);

      Condition<String> attributeCondition = getCondition(filledPlacement);
      attributeCondition.assertThat(containsString(filledFragmentCssClass));

    }

    void then_filled_placements_are_not_highlighted(
            String xpathPlacementFilled, String xpathElementInPlacement, String filledFragmentCssClass) {

      WebElement filledPlacement = getFilledPlacement(xpathPlacementFilled, xpathElementInPlacement);

      Condition<String> attributeCondition = getCondition(filledPlacement);
      attributeCondition.assertThat(not(containsString(filledFragmentCssClass)));

    }

    private WebElement getFilledPlacement(String xpathPlacementFilled, String xpathElementInPlacement) {
      final PreviewIFrame previewIFrame = getPreviewFrame();

      previewIFrame.visible().assertTrue();
      WebElement filledPlacement = previewIFrame.containedElement(By.xpath(xpathPlacementFilled)).await();

      assertThat(filledPlacement).isNotNull();

      Condition<WebElement> inPlacementElement = previewIFrame.containedElement(By.xpath(xpathElementInPlacement));
      assertThat(inPlacementElement.await())
              .as("Filled placement must have at least one element")
              .isNotNull();

      return filledPlacement;
    }

    //This condition is needed, due sometimes the expected css attribut needs some time to be provided.
    Condition<String> getCondition(WebElement filledPlacement) {
      return conditionFactory.condition(new AbstractExpression<String>() {

        @Nullable
        @Override
        public String get() {
          return filledPlacement.getAttribute("class");
        }
      });

    }

    void then_empty_fragments_are_highlighted_and_have_additional_text(
            String xpathPlacementEmpty, String xpathEmptyText, String xpathEmptyOverlay, String emptyFragmentCssClass) {
      final PreviewIFrame previewIFrame = getPreviewFrame();

      previewIFrame.visible().assertTrue();

      WebElement placementHeader = getEmptyPlacement(xpathPlacementEmpty, previewIFrame);
      assertThat(placementHeader).isNotNull();

      Condition<WebElement> textElement = previewIFrame.containedElement(By.xpath(xpathEmptyText));
      assertThat(textElement.await())
              .as("Header placement must contain Text Element (that describes that the placement is empty)")
              .isNotNull();

      Condition<WebElement> overlayElement = previewIFrame.containedElement(By.xpath(xpathEmptyOverlay));
      assertThat(overlayElement.await())
              .as("Header placement must contain Overlay Element (that contains the placement name)")
              .isNotNull();

      assertThat(placementHeader.getAttribute("class").contains(emptyFragmentCssClass))
              .as("Empty fragment doesn't contain CSS class for empty highlighted fragments")
              .isTrue();
    }

    void then_empty_fragments_are_not_highlighted(
            String xpathPlacementEmpty, String emptyFragmentCssClass) {

      final PreviewIFrame previewIFrame = getPreviewFrame();

      previewIFrame.visible().assertTrue();

      WebElement placementHeader = getEmptyPlacement(xpathPlacementEmpty, previewIFrame);
      assertThat(placementHeader).isNotNull();

      assertThat(placementHeader.getAttribute("class"))
              .as("Empty fragment should not contain CSS class for empty highlighted fragments")
              .doesNotContain(emptyFragmentCssClass);

    }

    private WebElement getEmptyPlacement(String xpathPlacementEmpty, PreviewIFrame previewIFrame) {
      return previewIFrame.containedElement(By.xpath(xpathPlacementEmpty)).await();
    }

    void then_unresolveable_fragments_are_highlighted_and_have_additional_text(
            String xpathPlacementUnavailable, String xpathUnavailableText, String xpathUnavailableOverlay) {
      final PreviewIFrame previewIFrame = getPreviewFrame();
      previewIFrame.visible().assertTrue();

      WebElement placementSidebarOverlay = getUnresolveablePlacement(xpathPlacementUnavailable, previewIFrame);
      assertThat(placementSidebarOverlay).isNotNull();

      Condition<WebElement> textElement = previewIFrame.containedElement(By.xpath(xpathUnavailableText));
      assertThat(textElement.await())
              .as("Sidebar placement must contain Text Element (describes that placement is not in layout)")
              .isNotNull();

      Condition<WebElement> overlayElement = previewIFrame.containedElement(By.xpath(xpathUnavailableOverlay));
      assertThat(overlayElement.await())
              .as("Sidebar placement must contain Overlay Element (that contains the placement name)")
              .isNotNull();
    }

    private WebElement getUnresolveablePlacement(String xpathPlacementUnavailable, PreviewIFrame previewIFrame) {
      return previewIFrame.containedElement(By.xpath(xpathPlacementUnavailable)).await();
    }

    private Site getTestSite(TestSiteConfiguration testSiteConfiguration) {
      return uapiConnectionUtils.getSite(testSiteConfiguration);
    }

    private PreviewIFrame getPreviewFrame() {
      return workArea
              .getActivePremular()
              .getPreviewPanel()
              .getInnerPreviewPanel()
              .getPreviewIFrame();
    }

    private PreviewIFrameToolbar getPreviewFrameToolbar() {
      return workArea
              .getActivePremular()
              .getPreviewPanel()
              .getInnerPreviewPanel()
              .getPreviewIFrameToolbar();
    }
  }
}
