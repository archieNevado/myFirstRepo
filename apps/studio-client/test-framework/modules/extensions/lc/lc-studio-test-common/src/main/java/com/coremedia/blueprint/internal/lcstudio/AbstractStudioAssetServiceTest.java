package com.coremedia.blueprint.internal.lcstudio;

import com.coremedia.blueprint.internal.lcstudio.wrapper.CommerceProductContentForm;
import com.coremedia.blueprint.internal.lcstudio.wrapper.CommerceProductWorkAreaTab;
import com.coremedia.blueprint.studio.uitest.core.StudioTestCase;
import com.coremedia.blueprint.uitesting.lc.CatalogUtils;
import com.coremedia.blueprint.uitesting.lc.TestSiteConfiguration;
import com.coremedia.blueprint.uitesting.lc.UapiConnectionUtils;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.testing.junit.SequenceErrorCollector;
import com.coremedia.uitesting.cms.editor.AutoLoginStudio;
import com.coremedia.uitesting.cms.editor.EditorContext;
import com.coremedia.uitesting.cms.editor.components.premular.InnerPreviewPanel;
import com.coremedia.uitesting.cms.editor.components.premular.PreviewIFrame;
import com.coremedia.uitesting.cms.editor.components.premular.PreviewPanel;
import com.coremedia.uitesting.ext3.wrappers.form.TextArea;
import com.coremedia.uitesting.ext3.wrappers.form.field.TextField;
import com.coremedia.uitesting.webdriver.IdleIndicators;
import edu.umd.cs.findbugs.annotations.Nullable;
import net.joala.bdd.reference.Reference;
import net.joala.condition.ConditionFactory;
import net.joala.expression.AbstractExpression;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.springframework.test.context.TestPropertySource;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static net.joala.bdd.reference.References.ref;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ProhibitedExceptionDeclared")
@TestPropertySource(properties = "livecontext.cache.invalidation.enabled:false")
public class AbstractStudioAssetServiceTest extends StudioTestCase {
  @Rule
  public SequenceErrorCollector sequenceExecutor = new SequenceErrorCollector();

  private void openProductForm(String testPictureNamePrefix, String productReference,
                               TestSiteConfiguration testSiteConfiguration, Reference<CommerceBean> productBeanRef) {
    steps.when_I_have_opened_the_product_in_studio(productBeanRef, productReference, testSiteConfiguration);
    steps.then_product_form_with_asset_list_is_displayed(productBeanRef);
    steps.when_I_added_a_new_product_picture(testPictureNamePrefix, productReference, testSiteConfiguration);
    steps.then_asset_list_contains_the_new_picture();
    steps.then_the_name_of_$0_is_displayed_in_preview(productBeanRef, testPictureNamePrefix);
  }

  public void open_commerce_bean_forms(String testPictureNamePrefix, String productReference, TestSiteConfiguration testSiteConfiguration) {
    final Reference<CommerceBean> productBeanRef = ref("product");
    open_commerce_bean_forms(testPictureNamePrefix, productReference, testSiteConfiguration, productBeanRef);
  }

  public void open_commerce_bean_forms(String testPictureNamePrefix, String productReference,
                                       TestSiteConfiguration testSiteConfiguration, Reference<CommerceBean> productBeanRef) {
    sequenceExecutor.perform(new SequenceErrorCollector.Sequence() {
      @Override
      public void execute() throws Exception {
        steps.given_I_am_logged_in_to_CoreMedia_Studio_as_an_editor(testSiteConfiguration);

        openProductForm(testPictureNamePrefix, productReference, testSiteConfiguration, productBeanRef);
      }
    });
  }

  @Inject
  private Steps steps;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Named
  @Singleton
  public static class Steps {
    @Inject
    private AutoLoginStudio studio;
    @Inject
    private EditorContext editorContext;
    @Inject
    private com.coremedia.uitesting.cms.editor.sdk.sites.SitesService sitesServiceProxy;
    @Inject
    private CommerceProductContentForm commerceProductContentForm;
    @Inject
    private CommerceProductWorkAreaTab commerceProductWorkAreaTab;
    @Inject
    private IdleIndicators idleIndicators;
    @Inject
    private CatalogUtils catalogUtils;
    @Inject
    private UapiConnectionUtils uapiConnectionUtils;
    @Inject
    private ConditionFactory conditionFactory;

    private long assetCount;

    public void given_I_am_logged_in_to_CoreMedia_Studio_as_an_editor(TestSiteConfiguration testSiteConfiguration) {
      sitesServiceProxy.switchPreferredSiteInContent(uapiConnectionUtils.getSite(testSiteConfiguration).getId());
      studio.get();
      idleIndicators.idle().waitUntilTrue();
    }

    //test product form
    public void when_I_have_opened_the_product_in_studio(final Reference<CommerceBean> productBeanRef, String productReference, TestSiteConfiguration testSiteConfiguration) {
      CommerceBean commerceBean = catalogUtils.getCommerceBean(productReference, testSiteConfiguration);
      //set the product reference only if it is not set already (e.g. with a mock)
      if (!productBeanRef.hasValue()) {
        productBeanRef.set(commerceBean);
      }
      editorContext.getWorkAreaTabManager().openTabForEntity(commerceBean);
      commerceProductContentForm.visible().waitUntilTrue();
      idleIndicators.idle().waitUntilTrue();
    }

    public void then_product_form_with_asset_list_is_displayed(final Reference<CommerceBean> refProduct) {
      CommerceBean commerceBean = refProduct.get();
      assertTrue(commerceBean instanceof Product);
      Product product = (Product) commerceBean;

      //Check text content
      TextField nameField = commerceProductContentForm.getName();
      nameField.value().assertEquals(product.getName());
      TextArea longDescription = commerceProductContentForm.getLongDescription();
      longDescription.value().assertThat(Matchers.not(Matchers.isEmptyOrNullString()));

      assetCount = commerceProductContentForm.getRichMediaGrid().getStore().dataLength().await();
    }

    public void when_I_added_a_new_product_picture(String testPictureNamePrefix, String productReference, TestSiteConfiguration testSiteConfiguration) {
      uapiConnectionUtils.createImageWithProductReference(
              uapiConnectionUtils.getSite(testSiteConfiguration).getSiteRootFolder(),
              testPictureNamePrefix,
              productReference);
    }


    public void then_asset_list_contains_the_new_picture() {
      //check if the new asset is displayed
      commerceProductContentForm.getRichMediaGrid().getStore().dataLength().assertEquals(assetCount++);
    }

    public void then_the_name_of_$0_is_displayed_in_preview(Reference<CommerceBean> reference, String testPictureNamePrefix) {
      CommerceBean commerceBean = reference.get();
      PreviewPanel previewPanel;
      if (commerceBean instanceof Product) {
        previewPanel = commerceProductWorkAreaTab.getPreviewPanel();
      } else {
        throw new IllegalStateException("cannot handle commerce bean " + commerceBean);
      }
      final InnerPreviewPanel innerPreviewPanel = previewPanel.getInnerPreviewPanel();
      final PreviewIFrame previewIFrame = innerPreviewPanel.getPreviewIFrame();

      previewPanel.visible().assertTrue();

      previewIFrame.visible().waitUntilTrue();

      // The following is pretty ugly but the most obvious
      // previewIFrame.text().assertThat(StringContains.containsString(testPictureNamePrefix));
      // is not working. Another workaround would be:
      // webElementConditions.text(driverProvider.get(), By.tagName("body")).assertThat(StringContains.containsString(testPictureNamePrefix));
      conditionFactory.booleanCondition(new PreviewContainsTextExpression(previewIFrame, testPictureNamePrefix)).withTimeoutFactor(5);
    }

  }

  private static class PreviewContainsTextExpression extends AbstractExpression<Boolean> {

    private PreviewIFrame previewIFrame;
    private String text;

    private PreviewContainsTextExpression(PreviewIFrame previewIFrame, String text) {
      super("preview contains text: " + text);
      this.previewIFrame = previewIFrame;
      this.text = text;
    }

    @Nullable
    @Override
    public Boolean get() {
      return previewIFrame.text().get().contains(text);
    }
  }

}
