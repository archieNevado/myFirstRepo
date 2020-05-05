package com.coremedia.blueprint.internal.lcstudio;

import com.coremedia.blueprint.studio.uitest.base.wrappers.newcontent.NewContentDialog;
import com.coremedia.blueprint.studio.uitest.core.StudioTestCase;
import com.coremedia.blueprint.uitesting.lc.TestSiteConfiguration;
import com.coremedia.blueprint.uitesting.lc.UapiConnectionUtils;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cms.integration.test.util.ContentBuilder;
import com.coremedia.cms.integration.test.util.ContentCleanupRegistry;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.uitesting.cms.editor.AutoLoginStudio;
import com.coremedia.uitesting.cms.editor.EditorContext;
import com.coremedia.uitesting.cms.editor.components.contextmenu.PreviewContextMenu;
import com.coremedia.uitesting.cms.editor.components.desktop.WorkArea;
import com.coremedia.uitesting.cms.editor.components.premular.DocumentForm;
import com.coremedia.uitesting.cms.editor.components.premular.InnerPreviewPanel;
import com.coremedia.uitesting.cms.editor.components.premular.PreviewIFrame;
import com.coremedia.uitesting.cms.editor.components.premular.PreviewIFrameToolbar;
import com.coremedia.uitesting.cms.editor.components.premular.PreviewPanel;
import com.coremedia.uitesting.doctypes.CMExternalChannel;
import com.coremedia.uitesting.ext3.wrappers.Ext;
import com.coremedia.uitesting.ext3.wrappers.WindowManager;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import com.coremedia.uitesting.ext3.wrappers.form.field.TextField;
import com.coremedia.uitesting.ext3.wrappers.menu.Item;
import com.coremedia.uitesting.ext3.wrappers.slider.SingleSlider;
import com.coremedia.uitesting.uapi.helper.ContentUtils;
import com.coremedia.uitesting.uapi.helper.VersionCleanupService;
import com.coremedia.uitesting.webdriver.IdleIndicators;
import com.google.common.base.Preconditions;
import edu.umd.cs.findbugs.annotations.Nullable;
import net.joala.bdd.reference.Reference;
import net.joala.condition.Condition;
import net.joala.condition.ConditionFactory;
import net.joala.expression.AbstractExpression;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.springframework.test.context.TestPropertySource;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

import static net.joala.bdd.reference.References.ref;
import static org.hamcrest.Matchers.greaterThan;

@TestPropertySource(properties = "livecontext.cache.invalidation.enabled:false")
public class AbstractExternalPagePbeTest extends StudioTestCase {

  static final String EXTERNAL_ID = "externalId";
  private static final String AUGMENT_SHOP_PAGE_CONTEXT_MENU_ITEM_ID = "augmentShopPage";
  private static final String CMEXTERNALPAGE = "CMExternalPage";

  @Inject
  private Steps steps;

  public void augment_external_page(String shopRootPagePath, String externalPageId, String externalPageLinkText, String pageHeaderClass, TestSiteConfiguration testSite) {
    final Reference<Content> shopRootPage = ref("shopRootPage");
    final Reference<Content> content = ref("content");

    steps.given_shop_root_page(shopRootPage, shopRootPagePath);
    steps.given_content_$0_is_augmented(content, externalPageId, shopRootPage, shopRootPagePath, testSite);
    steps.given_content_$0_is_no_longer_augmented(content);

    steps.given_I_am_logged_in_to_CoreMedia_Studio_as_an_editor(testSite);

    steps.when_I_have_opened_content_$0(shopRootPage);
    steps.when_I_switch_to_desktop_mode();
    steps.when_I_navigate_to_page(externalPageId, externalPageLinkText);
    steps.when_I_right_click_page_header(pageHeaderClass);

    steps.then_the_augment_page_pde_action_is_enabled();

    steps.when_I_click_the_augment_page_action();
    steps.then_the_augment_page_dialog_opens_with_name(externalPageId);

    steps.when_I_confirm_the_dialog();
    steps.then_page_is_augmented_again(externalPageId);
  }

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Named
  @Singleton
  public static class Steps {

    @Inject
    @Named("externalPageAugmentationService")
    private AugmentationService augmentationService;
    @Inject
    private AutoLoginStudio studio;
    @Inject
    private EditorContext editorContext;
    @Inject
    private com.coremedia.uitesting.cms.editor.sdk.sites.SitesService sitesServiceProxy;
    @Inject
    private WorkArea workArea;
    @Inject
    private IdleIndicators idleIndicators;
    @Inject
    private UapiConnectionUtils uapiConnectionUtils;
    @Inject
    private VersionCleanupService versionCleanupService;
    @Inject
    private ContentUtils contentUtils;
    @Inject
    private ConditionFactory conditionFactory;
    @Inject
    private Ext ext;
    @Inject
    private WindowManager windowMgr;
    @Inject
    private ContentCleanupRegistry contentCleanupRegistry;
    @Inject
    private Provider<ContentBuilder> contentBuilderProvider;

    private NewContentDialog newContentDialog;
    private DocumentForm documentForm;
    private Site testSite;
    private Item augmentShopPageItem;

    public void given_I_am_logged_in_to_CoreMedia_Studio_as_an_editor(TestSiteConfiguration testSiteConfiguration) {
      sitesServiceProxy.switchPreferredSiteInContent(uapiConnectionUtils.getSite(testSiteConfiguration).getId());
      studio.get();
      idleIndicators.idle().waitUntilTrue();
    }

    public void when_I_have_opened_content_$0(final Reference<Content> refContent) {
      editorContext.getWorkAreaTabManager().openTabForEntity(refContent.get());
      documentForm = workArea.getActivePremular().getDocumentForm();
      documentForm.visible().waitUntilTrue();
      idleIndicators.idle().waitUntilTrue();
    }

    public void when_I_switch_to_desktop_mode() {
      PreviewIFrameToolbar previewIFrameToolbar = workArea.getActivePremular().getPreviewPanel().getInnerPreviewPanel().getPreviewIFrameToolbar();
      SingleSlider sliderComponent = previewIFrameToolbar.getDeviceTypeSlider().getSliderComponent();
      sliderComponent.maxValue().assertThat(greaterThan(0L));
      Long maxValue = sliderComponent.maxValue().get();
      Assert.assertNotNull(maxValue);
      sliderComponent.setValue(maxValue);
      previewIFrameToolbar.getResponsiveModeButton().pressed().assertTrue();
    }

    public void given_shop_root_page(Reference<Content> rootPageContent, String rootPagePath) {
      Content rootpage = contentUtils.getContentRepository().getChild(rootPagePath);
      Preconditions.checkNotNull(rootpage, "shop root page not found for path: " + rootPagePath);
      rootPageContent.set(rootpage);
    }

    public void given_content_$0_is_augmented(Reference<Content> augmentedContent, String externalId, Reference<Content> shopRootPage, String shopRootPagePath, TestSiteConfiguration testSiteConfiguration) {
      testSite = uapiConnectionUtils.getSite(testSiteConfiguration);
      Content contentByExternalId = augmentationService.getContentByExternalId(externalId, testSite);
      if (contentByExternalId == null) {
        String baseFolderPath = StringUtils.substringBeforeLast(shopRootPagePath, "/");
        contentByExternalId = createAugmentedPageFor(externalId, shopRootPage.get(), baseFolderPath);
      }
      augmentedContent.set(contentByExternalId);
    }

    private Content createAugmentedPageFor(String externalId, Content parentChannel, String baseFolder) {
      Content folder = contentUtils.getContentRepository().getChild(baseFolder);

      Content testFolder = contentBuilderProvider.get()
              .parent(folder)
              .folderType()
              .named("test")
              .build();

      Content augmentedPage = contentBuilderProvider.get()
              .contentType(CMEXTERNALPAGE)
              .named("augmentedPage")
              .parent(testFolder)
              .property(EXTERNAL_ID, externalId)
              .build();

      //link to parent channel
      versionCleanupService.register(parentChannel);

      List<Content> newChildren = new ArrayList<>(parentChannel.getLinks(CMExternalChannel.P_CHILDREN));
      newChildren.add(augmentedPage);
      contentUtils.setProperty(parentChannel, CMExternalChannel.P_CHILDREN, newChildren);
      return augmentedPage;
    }

    public void given_content_$0_is_no_longer_augmented(Reference<Content> ref) {
      final Content content = ref.get();
      versionCleanupService.register(content);
      //noinspection ConstantConditions
      final String externalId = content.getString(EXTERNAL_ID);
      contentUtils.setProperty(content, EXTERNAL_ID, null);
      conditionFactory.condition(new AugmentedContentExpression(externalId)).assertThat(org.hamcrest.Matchers.nullValue());
    }

    public void when_I_navigate_to_page(String externalId, String pageLinkText) {
      PreviewPanel previewPanel = workArea.getActivePremular().getPreviewPanel();
      final InnerPreviewPanel innerPreviewPanel = previewPanel.getInnerPreviewPanel();
      final PreviewIFrame previewIFrame = innerPreviewPanel.getPreviewIFrame();

      previewPanel.visible().assertTrue();
      previewIFrame.visible().waitUntilTrue();

      // only available in desktop preview!
      innerPreviewPanel.click(By.linkText(pageLinkText));

      previewPanel.stringCondition("self.getPreviewUrl()").await(StringContains.containsString(externalId));
    }

    public void when_I_right_click_page_header(String pageHeaderClass) {
      PreviewPanel previewPanel = workArea.getActivePremular().getPreviewPanel();
      final InnerPreviewPanel innerPreviewPanel = previewPanel.getInnerPreviewPanel();
      innerPreviewPanel.contextClick(By.className(pageHeaderClass)).await();
    }

    public void then_the_augment_page_pde_action_is_enabled() {
      PreviewPanel previewPanel = workArea.getActivePremular().getPreviewPanel();
      PreviewContextMenu contextMenu = previewPanel.getContextMenu();
      contextMenu.visible().waitUntilTrue();
      augmentShopPageItem = contextMenu.getItems().getItemBy(Item.class, "item.itemId == itemId", "itemId", AUGMENT_SHOP_PAGE_CONTEXT_MENU_ITEM_ID);
      augmentShopPageItem.visible().waitUntilTrue();
      augmentShopPageItem.disabled().waitUntilFalse();
    }

    public void when_I_click_the_augment_page_action() {
      augmentShopPageItem.click();
    }

    public void then_the_augment_page_dialog_opens_with_name(String name) {
      newContentDialog = ext.getCmp(NewContentDialog.class, windowMgr.getActive().getId());
      newContentDialog.visible().waitUntilTrue();
      final TextField contentNameField = newContentDialog.getCreateContentNameField();
      contentNameField.visible().waitUntilTrue();
      contentNameField.value().assertEquals(name);
    }

    public void when_I_confirm_the_dialog() {
      final Button createButton = newContentDialog.getCreateButton();
      createButton.enabled().waitUntilTrue();
      createButton.click();
    }

    public void then_page_is_augmented_again(String externalId) {
      Condition<Content> condition = conditionFactory.condition(new AugmentedContentExpression(externalId));
      condition.assertThat(org.hamcrest.Matchers.notNullValue());
      contentCleanupRegistry.register(condition.get());
    }

    private class AugmentedContentExpression extends AbstractExpression<Content> {

      private final String externalId;

      AugmentedContentExpression(String externalId) {
        this.externalId = externalId;
      }

      @Nullable
      @Override
      public Content get() {
        return augmentationService.getContentByExternalId(externalId, testSite);
      }
    }
  }

}
