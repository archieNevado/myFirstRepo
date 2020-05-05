package com.coremedia.blueprint.internal.lcstudio;

import com.coremedia.blueprint.base.pagegrid.PageGridContentKeywords;
import com.coremedia.blueprint.internal.lcstudio.util.JsCommerceBean;
import com.coremedia.blueprint.internal.lcstudio.wrapper.CatalogLinkPropertyField;
import com.coremedia.blueprint.internal.lcstudio.wrapper.CatalogRepositoryContextMenu;
import com.coremedia.blueprint.internal.lcstudio.wrapper.CatalogRepositoryList;
import com.coremedia.blueprint.internal.lcstudio.wrapper.CatalogSearchContextMenu;
import com.coremedia.blueprint.internal.lcstudio.wrapper.CatalogSearchList;
import com.coremedia.blueprint.internal.lcstudio.wrapper.CollectionViewWithLiveContext;
import com.coremedia.blueprint.internal.lcstudio.wrapper.CommerceBeanModel;
import com.coremedia.blueprint.internal.lcstudio.wrapper.CommerceCategoryContentForm;
import com.coremedia.blueprint.internal.lcstudio.wrapper.CommerceCategoryStructureForm;
import com.coremedia.blueprint.internal.lcstudio.wrapper.CommerceCategoryWorkAreaTab;
import com.coremedia.blueprint.internal.lcstudio.wrapper.CommerceProductContentForm;
import com.coremedia.blueprint.internal.lcstudio.wrapper.CommerceProductStructureForm;
import com.coremedia.blueprint.internal.lcstudio.wrapper.CommerceProductWorkAreaTab;
import com.coremedia.blueprint.internal.lcstudio.wrapper.CommerceSystemForm;
import com.coremedia.blueprint.studio.uitest.base.wrappers.pagegrid.PageGridLayoutSelector;
import com.coremedia.blueprint.studio.uitest.base.wrappers.pagegrid.PageGridPropertyField;
import com.coremedia.blueprint.studio.uitest.base.wrappers.pagegrid.PlacementField;
import com.coremedia.blueprint.studio.uitest.core.StudioTestCase;
import com.coremedia.blueprint.uitesting.lc.CatalogUtils;
import com.coremedia.blueprint.uitesting.lc.FragmentTestHelper;
import com.coremedia.blueprint.uitesting.lc.TestSiteConfiguration;
import com.coremedia.blueprint.uitesting.lc.UapiConnectionUtils;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructBuilder;
import com.coremedia.cap.struct.StructBuilderMode;
import com.coremedia.cap.undoc.common.CapConnection;
import com.coremedia.cms.integration.test.util.CleanupRegistries;
import com.coremedia.cms.integration.test.util.ContentBuilder;
import com.coremedia.cms.integration.test.util.ContentCleanup;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.uitesting.cms.editor.AutoLoginStudio;
import com.coremedia.uitesting.cms.editor.EditorContext;
import com.coremedia.uitesting.cms.editor.components.collectionview.CollectionView;
import com.coremedia.uitesting.cms.editor.components.collectionview.TreeViewContextMenu;
import com.coremedia.uitesting.cms.editor.components.collectionview.breadcrumb.Breadcrumb;
import com.coremedia.uitesting.cms.editor.components.collectionview.tree.LibraryTree;
import com.coremedia.uitesting.cms.editor.components.desktop.ActionsToolbar;
import com.coremedia.uitesting.cms.editor.components.desktop.WorkArea;
import com.coremedia.uitesting.cms.editor.components.premular.DocumentForm;
import com.coremedia.uitesting.cms.editor.components.premular.InnerPreviewPanel;
import com.coremedia.uitesting.cms.editor.components.premular.Premular;
import com.coremedia.uitesting.cms.editor.components.premular.PreviewIFrame;
import com.coremedia.uitesting.cms.editor.components.premular.PreviewPanel;
import com.coremedia.uitesting.cms.editor.components.premular.fields.LinkListPropertyFieldGridPanel;
import com.coremedia.uitesting.cms.editor.sdk.ContentTreeRelation;
import com.coremedia.uitesting.cms.editor.sdk.desktop.sidepanel.SidePanelManager;
import com.coremedia.uitesting.cms.editor.sdk.sites.SitesService;
import com.coremedia.uitesting.doctypes.CMExternalChannel;
import com.coremedia.uitesting.ext3.wrappers.Component;
import com.coremedia.uitesting.ext3.wrappers.MessageBox;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSBy;
import com.coremedia.uitesting.ext3.wrappers.data.TreeModel;
import com.coremedia.uitesting.ext3.wrappers.form.TextArea;
import com.coremedia.uitesting.ext3.wrappers.form.field.StringDisplayField;
import com.coremedia.uitesting.ext3.wrappers.form.field.TextField;
import com.coremedia.uitesting.ext3.wrappers.menu.Item;
import com.coremedia.uitesting.ext3.wrappers.slider.SingleSlider;
import com.coremedia.uitesting.ext3.wrappers.tab.Tab;
import com.coremedia.uitesting.ext3.wrappers.view.TableView;
import com.coremedia.uitesting.joo.LocaleSupport;
import com.coremedia.uitesting.uapi.helper.conditions.SearchConditions;
import edu.umd.cs.findbugs.annotations.NonNull;
import net.joala.bdd.reference.Reference;
import net.joala.condition.Condition;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.TestPropertySource;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.coremedia.blueprint.base.pagegrid.PageGridContentKeywords.PAGE_GRID_STRUCT_PROPERTY;
import static com.coremedia.blueprint.uitesting.lc.TestSiteConfiguration.DEFAULT_CATALOG_ALIAS;
import static com.coremedia.blueprint.uitesting.lc.TestSiteConfiguration.MASTER_CATALOG_ALIAS;
import static com.coremedia.cap.common.CapStructHelper.getStruct;
import static com.coremedia.cms.integration.test.util.CapConsumers.checkIn;
import static com.coremedia.cms.integration.test.util.CapConsumers.ensureCheckOut;
import static com.coremedia.configuration.cms.integration.test.util.TestUtilConfiguration.CMS_INTEGRATION_TEST_DEFAULT_QUALIFIER;
import static com.coremedia.hamcrest.matcher.webelement.WebElementInnerHtmlContains.innerHtmlContains;
import static com.coremedia.hamcrest.matcher.webelement.WebElementTextContains.textContainsString;
import static com.coremedia.uitesting.ext3.wrappers.data.NodeInterface.By.text;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static net.joala.bdd.reference.References.ref;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

@SuppressWarnings({"ProhibitedExceptionDeclared", "squid:S3306", "squid:S1213", "squid:S00100", "squid:S1192", "squid:CommentedOutCodeLine"})
@TestPropertySource(properties = "livecontext.cache.invalidation.enabled:false")
public abstract class AbstractStudioCommerceFormAndAugmentTestCase extends StudioTestCase {

  @Inject
  @Rule
  public CleanupRegistries cleanupRegistries;

  public void openNotAugmentedCategory(String categoryPath, Map<String, String> categoryPlacementTestData, Map<String, String> productPlacementTestData, Map<String, String> layoutTestData) {

    String[] tokens = categoryPath.split("/");
    String[] categoryPathCrumbs = Arrays.copyOfRange(tokens, 1, tokens.length);
    String[] categoryParentPathCrumbs = Arrays.copyOfRange(tokens, 1, tokens.length - 1);
    String categoryName = tokens[tokens.length - 1];
    String categoryReference = getReferencePrefix() + "category/" + categoryName;

    steps.given_I_am_logged_in_to_CoreMedia_Studio_as_an_editor(getTestSiteConfiguration());
    steps.given_a_non_augmented_category(categoryParentPathCrumbs, categoryName, categoryReference, getTestSiteConfiguration());
    steps.then_the_category_has_catalog_context_menu_in_repository_list(categoryName);

    steps.given_I_have_opened_the_category_with_library(categoryPathCrumbs, getTestSiteConfiguration());
    steps.then_category_form_with_data_is_displayed(categoryName, true, getTestSiteConfiguration());
    steps.then_the_action_toolbar_has_the_augment_category_button();

    if (TestSiteConfiguration.AURORA.equals(getTestSiteConfiguration())) { // Todo: preview shows errors, why is not clear.
      steps.then_the_name_category_is_displayed_in_preview(categoryReference, getTestSiteConfiguration());
    }

    steps.then_the_category_is_not_augmented_in_catalog_tree();
    steps.then_the_category_has_catalog_context_menu_in_catalog_tree();

    steps.then_the_category_pagegrid_is_inherited_from_original_root_category_layout(false, layoutTestData);
    steps.then_the_category_pagegrid_is_inherited_from_original_root_category_placements(false, categoryPlacementTestData);

    if (TestSiteConfiguration.AURORA.equals(getTestSiteConfiguration())) { // dirty but the hybris preview does not work
      steps.then_the_category_page_shows_inherited_placements_from_original_root_category_layout();
    }

    steps.then_the_product_pagegrid_of_category_is_inherited_from_root_category(false, productPlacementTestData);
  }

  public void openNotAugmentedProduct(String categoryPath, String productId, Map<String, String> productPlacementTestData) {
    Reference<CommerceBean> product = ref("product");
    openNotAugmentedProduct(categoryPath, productId, productPlacementTestData, product);
  }

  public void openNotAugmentedProduct(String categoryPath, String productId, Map<String, String> productPlacementTestData,
                                      Reference<CommerceBean> productRef) {

    String[] tokens = categoryPath.split("/");
    String[] categoryPathCrumbs = Arrays.copyOfRange(tokens, 1, tokens.length);
    String productBeanId = getReferencePrefix() + "product/" + productId;

    //we test here that a not-augmented product inherits the placements from the (grand)parent categories
    steps.given_I_am_logged_in_to_CoreMedia_Studio_as_an_editor(getTestSiteConfiguration());
    steps.given_a_non_augmented_product(categoryPathCrumbs, productId, productBeanId, getTestSiteConfiguration());

    steps.when_I_have_opened_the_product_in_studio(productRef, productBeanId, getTestSiteConfiguration());
    steps.then_product_form_with_data_is_displayed(productRef);
    steps.then_the_action_toolbar_has_the_augment_product_button();
/*
Todo: this works only in aurora (Preview is broken)
    steps.then_the_name_of_$0_is_displayed_in_preview(product);
*/
    steps.then_the_product_has_catalog_context_menu_in_repository_list(productId);

    steps.then_the_product_pagegrid_is_inherited_from_root_category(false, productPlacementTestData);
  }

  public void augmentProduct(String categoryPath, String productId, Map<String, String> productPlacementTestData) {
    String[] tokens = categoryPath.split("/");
    String[] categoryPathCrumbs = Arrays.copyOfRange(tokens, 1, tokens.length);
    String productReference = getReferencePrefix() + "product/" + productId;

    Reference<Content> augmentingContent = ref();

    steps.given_I_am_logged_in_to_CoreMedia_Studio_as_an_editor(getTestSiteConfiguration());
    steps.given_a_non_augmented_product(categoryPathCrumbs, productId, productReference, getTestSiteConfiguration());

    steps.when_I_augment_the_product(augmentingContent, productId);
    steps.then_the_ui_reflect_that_this_product_is_augmented(augmentingContent, productId);
    steps.then_the_product_pagegrid_is_inherited_from_root_category(true, productPlacementTestData);
  }

  public void augmentCategory(String categoryPath, String externalId, Map<String, String> categoryPlacementTestData,
                              Map<String, String> productPlacementTestData, Map<String, String> layoutTestData) {
    String[] tokens = categoryPath.split("/");
    String[] categoryParentPath = Arrays.copyOfRange(tokens, 1, tokens.length - 1);
    String categoryName = tokens[tokens.length - 1];
    String categoryReference = getReferencePrefix() + "category/" + externalId;

    Reference<Content> augmentedContent = ref();

    steps.given_I_am_logged_in_to_CoreMedia_Studio_as_an_editor(getTestSiteConfiguration());
    steps.given_a_non_augmented_category(categoryParentPath, categoryName, categoryReference, getTestSiteConfiguration());

    steps.when_I_augment_the_category(augmentedContent, externalId);
    steps.then_the_ui_reflect_that_this_category_is_augmented(augmentedContent, categoryName, getTestSiteConfiguration());
    steps.then_the_category_pagegrid_is_inherited_from_original_root_category_layout(true, layoutTestData);
    steps.then_the_category_pagegrid_is_inherited_from_original_root_category_placements(true, categoryPlacementTestData);

    if (TestSiteConfiguration.AURORA.equals(getTestSiteConfiguration())) { // dirty but the hybris preview does not work
      steps.then_the_category_page_shows_inherited_placements_from_original_root_category_layout();
    }

    steps.then_the_product_pagegrid_of_category_is_inherited_from_root_category(true, productPlacementTestData);
    steps.when_the_root_category_layout_is_changed(getRootCategoryPath(), getNewLayoutPath());
    steps.then_the_category_pagegrid_has_still_the_old_layout(augmentedContent, layoutTestData);
    //TODO (See CMS-8932: Placements of augmented categories get lost when the layout of the root category is changed.)
    //steps.then_the_category_pagegrid_is_inherited_from_original_root_category_placements(true);
  }

  public void augmentCategoryAsAdmin(String categoryPath, String externalId) {

    String[] categoryPathCrumbs = computePathCrumbs(categoryPath);
    String categoryName = computeName(categoryPath);
    String categoryReference = getReferencePrefix() + "category/" + externalId;

    steps.given_I_am_logged_in_to_CoreMedia_Studio_as_an_editor(getTestSiteConfiguration());
    steps.given_I_have_opened_the_category_with_library(categoryPathCrumbs, getTestSiteConfiguration());

    steps.when_admin_has_augmented_category(categoryName, categoryReference, getTestSiteConfiguration());
    steps.then_augmentation_message_for_category_pops_up(categoryName);
  }

  public void openAugmentedCategoryInTabFromRepositoryTree(String categoryPath, String categoryContentPath) {

    String[] categoryPathCrumbs = computePathCrumbs(categoryPath);

    Reference<Content> contentReference = ref("augmented category");
    steps.given_I_am_logged_in_to_CoreMedia_Studio_as_an_editor(getTestSiteConfiguration());
    steps.when_I_open_an_augmented_category_in_tab_from_the_tree(contentReference, categoryContentPath, categoryPathCrumbs, getTestSiteConfiguration());
    steps.then_the_augmenting_content_is_active_in_work_area(contentReference);
  }

  public void openAugmentedCategoryInRepositoryList(String categoryPath, String categoryContentPath) {

    String[] tokens = categoryPath.split("/");
    String[] categoryParentPathCrumbs = Arrays.copyOfRange(tokens, 1, tokens.length - 1);
    String categoryName = tokens[tokens.length - 1];

    Reference<Content> contentReference = ref("augmented category");
    steps.given_I_am_logged_in_to_CoreMedia_Studio_as_an_editor(getTestSiteConfiguration());
    steps.when_I_open_an_augmented_category_in_tab_from_repository_list(contentReference, categoryContentPath, categoryParentPathCrumbs, categoryName, getTestSiteConfiguration());
    steps.then_the_augmenting_content_is_active_in_work_area(contentReference);
  }

  private static String[] computePathCrumbs(String path) {
    String[] tokens = path.split("/");
    return Arrays.copyOfRange(tokens, 1, tokens.length);
  }

  private static String computeName(String path) {
    String[] tokens = path.split("/");
    return tokens[tokens.length - 1];
  }

  @Inject
  protected Steps steps;

  @SuppressWarnings({"SpringJavaAutowiringInspection", "SpringJavaAutowiredFieldsWarningInspection", "squid:S3306", "SameParameterValue", "squid:S1075"})
  @Named
  @Singleton
  public static class Steps {

    private static final String AURORA_ROOT_PATH = "/Sites/Aurora Augmentation";
    private static final String AURORA_B2B_ROOT_PATH = "/Sites/Aurora B2B Augmentation";
    private static final String US_ENGLISH = "/United States/English";

    private static final String NOT_AUGMENTED_ICON_CSS_CLASS = "cm-core-icons--type-category";
    private static final String AUGMENTED_ICON_CSS_CLASS = "cm-core-icons--type-augmented-category";

    private static final String AUGMENTATION_MESSAGE_TITLE_EN = "Category Augmented";
    private static final String AUGMENTATION_MESSAGE_TITLE_DE = "Kategorie augmentiert";

    private static final String SUB_CATEGORIES_ITEM_ID = "readOnlyCatalogLink";
    private static final String READ_ONLY_CATALOG_LINK_EMPTY_DISPLAYFIELD_ITEM_ID = "readOnlyCatalogLinkEmptyDisplayField";

    private static final String SECTION_HEADER = "header";
    private static final String SECTION_MAIN = "main";
    private static final String SECTION_FOOTER = "footer";

    private static final String XPATH_SIDEBAR_TEASER_TEXT = "//*[contains(@class, 'cm-placement--sidebar')][descendant::*[contains(@class,'__headline')]]";
    private static final String AURORA_MASTER_CATALOG_ROOT_DOCUMENT_PATH = AURORA_ROOT_PATH + US_ENGLISH + "/Augmentation/_other_catalogs/master/Extended Sites Catalog Asset Store";
    private static final String AURORA_B2B_CATALOG_ROOT_DOCUMENT_PATH = AURORA_B2B_ROOT_PATH + US_ENGLISH + "/Augmentation/Aurora B2B Catalog Root";
    private static final String AURORA_B2B_CATALOG_ROOT_DOCUMENT_DE_PATH = AURORA_B2B_ROOT_PATH + "/Germany/German/Augmentation/Aurora B2B Catalog Root";

    private static final String PICTURE_DOCUMENT_PATH = AURORA_ROOT_PATH + US_ENGLISH + "/Pictures/Product Pictures/Men/Suits/Albini Black Wool Suit";
    private static final String PICTURE_CAPTION = "Albini Black Wool Suit";

    @Inject
    private AutoLoginStudio studio;
    @Inject
    private EditorContext editorContext;
    @Inject
    private LocaleSupport localeSupport;
    @Inject
    private WorkArea workArea;
    @Inject
    private CollectionView collectionView;
    @Inject
    private CollectionViewWithLiveContext collectionViewWithLiveContext;
    @Inject
    private ActionsToolbar actionsToolbar;
    @Inject
    private MessageBox messageBox;
    @Inject
    private SitesService sitesServiceProxy;
    @Inject
    private CatalogUtils catalogUtils;
    @Inject
    private UapiConnectionUtils uapiConnectionUtils;
    @Inject
    private ContentRepository contentRepository;
    @Inject
    private CapConnection capConnection;
    @Inject
    private FragmentTestHelper fragmentTestHelper;
    @Inject
    private SidePanelManager sidePanelManager;
    @Inject
    private ContentCleanup contentCleanup;
    @Inject
    @Qualifier(CMS_INTEGRATION_TEST_DEFAULT_QUALIFIER)
    private Provider<ContentBuilder> contentBuilderProvider;
    @Inject
    private SearchConditions searchConditions;

    // GIVEN
    public void given_I_am_logged_in_to_CoreMedia_Studio_as_an_editor(TestSiteConfiguration testSiteConfiguration) {
      sitesServiceProxy.switchPreferredSiteInContent(uapiConnectionUtils.getSite(testSiteConfiguration).getId());
      studio.get();
    }

    void given_a_non_augmented_category(String[] categoryPath, String categoryName, String categoryId, TestSiteConfiguration testSiteConfiguration) {
      cleanupAugmentation(categoryId, testSiteConfiguration);

      openLibrary();
      openProductCatalog(testSiteConfiguration);
      openCategoryInLibrary(categoryPath, testSiteConfiguration);
      isCategoryNotAugmentedInRepositoryList(categoryName);
    }

    void given_a_non_augmented_product(String[] categoryPath, String productId, String productBeanId, TestSiteConfiguration testSiteConfiguration) {
      cleanupAugmentation(productBeanId, testSiteConfiguration);

      openLibrary();
      openProductCatalog(testSiteConfiguration);
      openCategoryInLibrary(categoryPath, testSiteConfiguration);
      isProductNotAugmentedInRepositoryList(productId);
    }

    void given_I_have_opened_the_category_with_library(String[] categoryPathCrumbs, TestSiteConfiguration testSiteConfiguration) {
      openLibrary();
      openProductCatalog(testSiteConfiguration);
      openCategoryInLibrary(categoryPathCrumbs, testSiteConfiguration);
      openSelectedCategoryInTabFromTree();
      nonAugmentedCategoryIsOpen();
    }

    //WHEN
    public void when_I_open_the_library() {
      openLibrary();
    }

    public void when_I_reload_the_studio() {
      studio.reload();
    }

    public void when_I_select_the_default_catalog(TestSiteConfiguration testSiteConfiguration) {
      openCategoryInLibrary(null, testSiteConfiguration);
    }

    public void when_I_open_the_master_catalog_root_document_of_aurora() {
      openDocument(AURORA_MASTER_CATALOG_ROOT_DOCUMENT_PATH);
    }

    public void when_I_open_a_picture_document_with_link_to_catalog_objects() {
      openDocument(PICTURE_DOCUMENT_PATH);
    }

    public void when_I_open_the_catalog_root_document_of_a_single_catalog_site() {
      openDocument(AURORA_B2B_CATALOG_ROOT_DOCUMENT_PATH);
    }

    public void when_I_open_the_catalog_root_document_of_a_single_catalog_site_de() {
      openDocument(AURORA_B2B_CATALOG_ROOT_DOCUMENT_DE_PATH);
    }

    private void openDocument(String path) {
      Content document = contentRepository.getChild(path);
      searchConditions.indexed(document).waitUntilTrue();
      workArea.openInTab(document);
    }

    public void when_I_select_the_master_catalog(TestSiteConfiguration testSiteConfiguration) {
      openCategoryInLibrary(null, testSiteConfiguration, "master");
    }

    public void when_I_search_for_b2b_products() {
      collectionView.getSearchArea().search("b2b_*");
    }

    public void when_I_search_for_not_existing_products() {
      collectionView.getSearchArea().search("adfadsfasdfsadf");
    }

    void when_I_augment_the_category(Reference<? super Content> augmentingContent, String categoryId) {
      CatalogRepositoryList catalogRepositoryList = collectionViewWithLiveContext.getCatalogRepositoryList();
      Content content = catalogRepositoryList.augmentCategory(categoryId);
      augmentingContent.set(content);
    }

    void when_I_augment_the_product(Reference<? super Content> augmentingContent, String productId) {
      CatalogRepositoryList catalogRepositoryList = collectionViewWithLiveContext.getCatalogRepositoryList();
      Content content = catalogRepositoryList.augmentProduct(productId);
      augmentingContent.set(content);
    }

    void when_I_have_opened_the_product_in_studio(
            Reference<? super CommerceBean> refProduct, String productBeanId, TestSiteConfiguration testSiteConfiguration) {
      CommerceBean commerceBean = catalogUtils.getCommerceBean(productBeanId, testSiteConfiguration);
      //set the product reference only if it is not set already (e.g. with a mock)
      //TODO: Use mock for IBM and Hybris as well. So we don't have to communicate directly to the commerce system.
      //We know the expected values of the product anyway.
      if (!refProduct.hasValue()) {
        refProduct.set(commerceBean);
      }
      editorContext.getWorkAreaTabManager().openTabForEntity(commerceBean);
      getActiveCommerceProductContentForm().visible().waitUntilTrue();
    }

    void when_I_open_an_augmented_category_in_tab_from_repository_list(Reference<? super Content> contentReference, String categoryContentPath, String[] categoryParentPath, String categoryName, TestSiteConfiguration testSiteConfiguration) {
      findExistingAugmentedCategory(contentReference, categoryContentPath);
      openLibrary();
      openProductCatalog(testSiteConfiguration);
      openCategoryInLibrary(categoryParentPath, testSiteConfiguration);
      openInTabFromCatalogRepositoryList(categoryName);
    }

    void when_I_open_an_augmented_category_in_tab_from_the_tree(Reference<? super Content> contentReference, String categoryContentPath, String[] categoryBreadcrumbPath, TestSiteConfiguration testSiteConfiguration) {
      findExistingAugmentedCategory(contentReference, categoryContentPath);
      openLibrary();
      openProductCatalog(testSiteConfiguration);
      openCategoryInLibrary(categoryBreadcrumbPath, testSiteConfiguration);
      openSelectedCategoryInTabFromTree();
    }

    void when_admin_has_augmented_category(String categoryName, String categoryReference, TestSiteConfiguration testSiteConfiguration) {
      // augment the category by creating an external channel (augmented category) for the given external id as admin
      Content siteRootFolder = catalogUtils.getSite(testSiteConfiguration).getSiteRootFolder();

      contentBuilderProvider.get()
              .asUser("admin")
              .contentType(CMExternalChannel.NAME)
              .named(categoryName).parent(siteRootFolder)
              .property(CMExternalChannel.P_EXTERNAL_ID, categoryReference)
              .build();

    }

    void when_the_root_category_layout_is_changed(String rootCategoryPath, String newLayoutPath) {
      Content rootCategoryContent = contentRepository.getChild(rootCategoryPath);
      changeLayout(rootCategoryContent, newLayoutPath);
    }

    public void when_I_augment_product_content_for_the_category_of_master_catalog(String categoryId,
                                                                                  String categoryPath,
                                                                                  String pictureDocumentPath,
                                                                                  TestSiteConfiguration testSiteConfiguration) {
      Content layout = requireChild("/Sites/Aurora Augmentation/United States/English/Options/Settings/Pagegrid/Layouts/Fragment PDP");
      Content headerPlacement = requireChild("/Sites/Aurora Augmentation/United States/English/Options/Settings/Pagegrid/Placements/header");
      Content contentToBeLinked = requireChild(pictureDocumentPath);

      //first we have to augment the category
      Content siteRootFolder = catalogUtils.getSite(testSiteConfiguration).getSiteRootFolder();

      Content masterFolder = contentBuilderProvider.get()
              .folderType()
              .parent(siteRootFolder)
              .named("master")
              .build();

      Content categoryContent = contentBuilderProvider.get()
              .contentType(CMExternalChannel.NAME)
              .named(computeName(categoryPath))
              .parent(masterFolder)
              .property(CMExternalChannel.P_EXTERNAL_ID, categoryId)
              .property("pdpPagegrid", fragmentTestHelper.createPlacementsStruct(layout, headerPlacement, contentToBeLinked))
              .postProcess(checkIn(), Integer.MAX_VALUE)
              .build();

      searchConditions.indexed(categoryContent).waitUntilTrue();
    }

    private Content requireChild(String path) {
      return requireNonNull(capConnection.getContentRepository().getChild(path), () -> "Required content at " + path + " does not exist.");
    }

    public void when_I_open_a_product_of_default_catalog_via_library(String productPath, TestSiteConfiguration testSiteConfiguration) {
      openProductInLibrary(productPath, testSiteConfiguration);
    }

    public void when_I_open_a_product_of_master_catalog_via_library(String productPath, TestSiteConfiguration testSiteConfiguration) {
      openProductInLibrary(productPath, testSiteConfiguration, MASTER_CATALOG_ALIAS);
    }

    public void when_I_close_the_active_tab() {
      workArea.closeActiveTab();
    }

    public void when_I_close_all_tabs() {
      workArea.closeAllTabs(0);
    }

    public void when_I_open_the_first_item_in_the_search_list() {
      openInTabFromCatalogSearchList(0);
    }

    public void when_I_switch_to_first_tab() {
      workArea.getWorkAreaTabProxies().setActiveTab(0);
    }

    public void when_I_switch_to_second_tab() {
      workArea.getWorkAreaTabProxies().setActiveTab(1);
    }

    private void changeLayout(Content categoryContent, String newLayoutPath) {
      Struct placementStruct = requireNonNull(getStruct(categoryContent, PAGE_GRID_STRUCT_PROPERTY), "Struct must not be null.");
      StructBuilder builder = placementStruct.builder().mode(StructBuilderMode.LOOSE);
      builder.enter(PageGridContentKeywords.PLACEMENTS_PROPERTY_NAME);
      builder.remove("layout");
      builder.declareLink("layout", toContentType("CMSettings"), requireChild(newLayoutPath));
      contentBuilderProvider.get()
              .content(categoryContent)
              .postProcess(ensureCheckOut())
              .property(PAGE_GRID_STRUCT_PROPERTY, builder.build())
              .postProcess(checkIn())
              .build();
    }
    // THEN

    void then_the_ui_reflect_that_this_category_is_augmented(Reference<? extends Content> augmentingContent, String categoryName, TestSiteConfiguration testSiteConfiguration) {
      then_the_augmenting_content_is_the_active_tab_in_workarea(augmentingContent);
      then_the_action_toolbar_has_no_augmentation_buttons();
      isCategoryAugmentedInRepositoryList(categoryName, testSiteConfiguration);
    }

    void then_the_ui_reflect_that_this_product_is_augmented(Reference<? extends Content> augmentingContent, String productId) {
      then_the_augmenting_content_is_the_active_tab_in_workarea(augmentingContent);
      then_the_action_toolbar_has_no_augmentation_buttons();
      isProductAugmentedInRepositoryList(productId);
    }

    void then_the_category_pagegrid_is_inherited_from_original_root_category_layout(boolean augmented, Map<String, String> layoutData) {
      PageGridPropertyField categoryPageGridPropertyField = getCategoryPageGridPropertyField(augmented);
      PageGridLayoutSelector layoutSelector = categoryPageGridPropertyField.getLayoutSelector();
      layoutSelector.effectiveLayoutName().assertThat(containsString(layoutData.get("name")));
      layoutSelector.effectiveLayoutDescription().assertThat(containsString(layoutData.get("description")));
    }

    void then_the_category_pagegrid_is_inherited_from_original_root_category_placements(boolean augmented, Map<String, String> categoryPlacementTestData) {
      PageGridPropertyField categoryPageGridPropertyField = getCategoryPageGridPropertyField(augmented);
      //test now the bugfix of CMS-8730
      categoryPageGridPropertyField.getPlacementsContainer().numberOfVisibleItems().assertEquals(4L);
      categoryPageGridPropertyField.getPlacementField(SECTION_HEADER).visible().assertTrue();
      categoryPageGridPropertyField.getPlacementField(SECTION_MAIN).visible().assertTrue();

      for (Map.Entry<String, String> testDataEntry : categoryPlacementTestData.entrySet()) {
        validatePlacementData(categoryPageGridPropertyField, testDataEntry.getKey(), testDataEntry.getValue(), augmented);
      }
      categoryPageGridPropertyField.getPlacementField(SECTION_FOOTER).visible().assertTrue();
    }

    void then_the_category_page_shows_inherited_placements_from_original_root_category_layout() {
      PreviewPanel previewPanel = workArea.getActivePremular().getPreviewPanel();
      previewPanel.reload();
      InnerPreviewPanel innerPreviewPanel = previewPanel.getInnerPreviewPanel();
      PreviewIFrame previewIFrame = innerPreviewPanel.getPreviewIFrame();
      previewIFrame.visible().assertTrue();
      SingleSlider sliderComponent = innerPreviewPanel.getPreviewIFrameToolbar().getDeviceTypeSlider().getSliderComponent();
      long maxValue = sliderComponent.maxValue().await(notNullValue());
      sliderComponent.setValue(maxValue);

      previewIFrame.containedElement(By.xpath(XPATH_SIDEBAR_TEASER_TEXT)).assertThat(textContainsString("Albini European Cut Chintz Wool Suit"));
      previewIFrame.containedElement(By.xpath(XPATH_SIDEBAR_TEASER_TEXT)).assertThat(textContainsString("Editorial Blog"));
    }

    void then_the_product_pagegrid_of_category_is_inherited_from_root_category(boolean augmented, Map<String, String> productTestData) {
      PageGridPropertyField productPageGridPropertyField = getProductPageGridPropertyFieldOfTheCategory(augmented);
      checkProductPageGrid(augmented, productPageGridPropertyField, productTestData);
    }

    void then_the_product_pagegrid_is_inherited_from_root_category(boolean augmented, Map<String, String> productTestData) {
      PageGridPropertyField productPageGridPropertyField = getProductPageGridPropertyField(augmented);
      checkProductPageGrid(augmented, productPageGridPropertyField, productTestData);
    }

    private static void checkProductPageGrid(boolean augmented, PageGridPropertyField productPageGridPropertyField, Map<String, String> productTestData) {
      PageGridLayoutSelector layoutSelector = productPageGridPropertyField.getLayoutSelector();
      layoutSelector.effectiveLayoutName().assertEquals("Fragment PDP");
      productPageGridPropertyField.getPlacementsContainer().numberOfVisibleItems().assertThat(greaterThan(0L));
      for (Map.Entry<String, String> testDataEntry : productTestData.entrySet()) {
        validatePlacementData(productPageGridPropertyField, testDataEntry.getKey(), testDataEntry.getValue(), augmented);
      }
    }

    void then_product_form_with_data_is_displayed(Reference<? extends CommerceBean> refProduct) {
      CommerceBean commerceBean = refProduct.get();

      Assert.assertThat(commerceBean, Matchers.instanceOf(Product.class));
      Product product = (Product) commerceBean;

      assert product != null;

      //Check text content
      TextField nameField = getActiveCommerceProductContentForm().getName();
      nameField.value().assertEquals(product.getName());

/*
Todo: can only works with aurora
      TextArea longDescription = getActiveCommerceProductContentForm().getLongDescription();
      longDescription.value().assertThat(Matchers.not(Matchers.isEmptyOrNullString()));
*/

      //check price only if the price is visible (e.g. for sfcc no price is displayed in studio)
      StringDisplayField listPrice = getActiveCommerceProductContentForm().getListPrice();
      if (listPrice.visible().await()) {
        listPrice.value().assertThat(containsString(requireNonNull(product.getListPrice()).intValue() + ""));
      }

/*
      getActiveCommerceProductContentForm().getOfferPrice().value().assertThat(Matchers.containsString(product.getOfferPrice().intValue()+""));
*/

      //check assets
      //not yet implemented

      //change to the sub tab 'Catalog Structure'
      CommerceProductWorkAreaTab activeTab = getActiveCommerceProductWorkAreaTab();
      activeTab.setActiveTab(2);
      //check if the parent category component is displayed
      getActiveCommerceProductStructureForm().getCategoryContainer().getActiveItemId().assertEquals(SUB_CATEGORIES_ITEM_ID);
      //TODO: check the name of the parent category
      //TODO: check the product variants
    }

    public void then_the_product_form_with_defalut_catalog_data_is_displayed(TestSiteConfiguration testSiteConfiguration) {
      then_the_product_form_with_catalog_data_is_displayed(testSiteConfiguration, DEFAULT_CATALOG_ALIAS);
    }

    public void then_the_product_form_with_master_catalog_data_is_displayed(TestSiteConfiguration testSiteConfiguration) {
      then_the_product_form_with_catalog_data_is_displayed(testSiteConfiguration, MASTER_CATALOG_ALIAS);
    }

    private void then_the_product_form_with_catalog_data_is_displayed(TestSiteConfiguration testSiteConfiguration, String catalogAlias) {
      CommerceProductWorkAreaTab productTab = getActiveCommerceProductWorkAreaTab();
      productTab.setActiveTab(3);
      CommerceSystemForm systemForm = productTab.getSystemForm();
      systemForm.getCatalogField().value().assertEquals(testSiteConfiguration.getCatalogName(catalogAlias));
    }

    public void then_the_metadata_tab_shows_the_catalog_link_with_no_catalog(TestSiteConfiguration testSiteConfiguration) {
      DocumentForm metadataTab = workArea.getActivePremular().setAndGetActiveTab(1);
      CatalogLinkPropertyField catalogLinkPropertyField = metadataTab.find(CatalogLinkPropertyField.class, ExtJSBy.xtype(CatalogLinkPropertyField.XTYPE));
      catalogLinkPropertyField.getView().cellElement(0, 1).assertThat(textContainsString(PICTURE_CAPTION));
      catalogLinkPropertyField.getView().cellElement(0, 1).assertThat(not(textContainsString(testSiteConfiguration.getCatalogName(DEFAULT_CATALOG_ALIAS))));
    }

    public void then_the_system_tab_shows_the_catalog_link_with_master_catalog(TestSiteConfiguration testSiteConfiguration) {
      workArea.getActivePremular().setActiveTab(4); //workaround
      DocumentForm systemTab = workArea.getActivePremular().setAndGetActiveTab(5);
      CatalogLinkPropertyField catalogLink = systemTab.find(CatalogLinkPropertyField.class, ExtJSBy.xtype(CatalogLinkPropertyField.XTYPE));
      catalogLink.getView().cellElement(0, 1).assertThat(textContainsString("ROOT"));
      catalogLink.getView().cellElement(0, 1).assertThat(textContainsString(testSiteConfiguration.getCatalogName(MASTER_CATALOG_ALIAS)));
    }

    public void then_the_system_tab_shows_the_catalog_link_with_no_catalog(TestSiteConfiguration testSiteConfiguration) {
      //Workaround
      workArea.getActivePremular().setActiveTab(4);
      DocumentForm systemTab = workArea.getActivePremular().setAndGetActiveTab(5);
      CatalogLinkPropertyField catalogLink = systemTab.find(CatalogLinkPropertyField.class, ExtJSBy.xtype(CatalogLinkPropertyField.XTYPE));
      catalogLink.getView().cellElement(0, 1).assertThat(textContainsString("ROOT"));
      catalogLink.getView().cellElement(0, 1).assertThat(not(textContainsString(testSiteConfiguration.getCatalogName(DEFAULT_CATALOG_ALIAS))));
    }

    void then_category_form_with_data_is_displayed(String categoryName, boolean isLeaf, TestSiteConfiguration testSiteConfiguration) {
      CommerceCategoryContentForm commerceCategoryContentForm = getActiveCommerceCategoryContentForm();
      //Check text content
      TextField nameField = commerceCategoryContentForm.getName();
      // Todo: its dirty, but the field contains only a substring of the id (e.g. only 'shoes' as part of 'girls shoes')
      nameField.value().assertThat(containsString(categoryName.substring(categoryName.length() - 4)));

      if (TestSiteConfiguration.AURORA.equals(testSiteConfiguration)) { // its dirty but the hybris cateories seems not have any descriptions
        TextArea shortDescription = commerceCategoryContentForm.getShortDescription();
        //we have to expand the collaped panel of short description before we are able to read the content
        commerceCategoryContentForm.getShortDescriptionCollapsiblePanel().expand(true);
        shortDescription.value().assertThat(not(Matchers.isEmptyOrNullString()));
      }

      //check thumbnail
      //not yet implemented

      //change to the sub tab 'Catalog Structure'
      CommerceCategoryWorkAreaTab activeTab = workArea.getActiveTab(CommerceCategoryWorkAreaTab.class);
      activeTab.setActiveTab(1);

      //check if categories and products are displayed
      CommerceCategoryStructureForm commerceCategoryStructureForm = getActiveCommerceCategoryStructureForm();
      commerceCategoryStructureForm.getParentCategoryContainer().getActiveItemId().assertEquals(SUB_CATEGORIES_ITEM_ID);

      commerceCategoryStructureForm.getSubCategoriesContainer()
              .getActiveItemId()
              .assertEquals(isLeaf ? READ_ONLY_CATALOG_LINK_EMPTY_DISPLAYFIELD_ITEM_ID : SUB_CATEGORIES_ITEM_ID);
      commerceCategoryStructureForm.getProductsContainer()
              .getActiveItemId()
              .assertEquals(isLeaf ? SUB_CATEGORIES_ITEM_ID : READ_ONLY_CATALOG_LINK_EMPTY_DISPLAYFIELD_ITEM_ID);
    }

    void then_the_action_toolbar_has_the_augment_category_button() {
      actionsToolbar.getAugmentCategoryButton().visible().assertTrue();
      actionsToolbar.getAugmentProductButton().visible().assertFalse();
    }

    void then_the_action_toolbar_has_the_augment_product_button() {
      actionsToolbar.getAugmentProductButton().visible().assertTrue();
      actionsToolbar.getAugmentCategoryButton().visible().assertFalse();
    }

    void then_the_action_toolbar_has_no_augmentation_buttons() {
      actionsToolbar.getAugmentCategoryButton().visible().assertFalse();
      actionsToolbar.getAugmentProductButton().visible().assertFalse();
    }

    void then_the_name_category_is_displayed_in_preview(String categoryReference, TestSiteConfiguration testSiteConfiguration) {
      Reference<CommerceBean> commerceBeanRef = ref("category");
      commerceBeanRef.set(catalogUtils.getCommerceBean(categoryReference, testSiteConfiguration));
      then_the_name_of_$0_is_displayed_in_preview(commerceBeanRef);
    }

    void then_the_name_of_$0_is_displayed_in_preview(Reference<? extends CommerceBean> reference) {
      CommerceBean commerceBean = reference.get();
      String name = catalogUtils.getCommerceBeanName(commerceBean);
      PreviewIFrame previewIFrame = getPreviewIFrame(commerceBean);
      previewIFrame.text().await(containsString(name));
    }

    void then_the_augmenting_content_is_the_active_tab_in_workarea(Reference<? extends Content> content) {
      workArea.getActivePremular().content().assertEquals(content.get());
    }

    void then_augmentation_message_for_category_pops_up(String categoryName) {

      messageBox.getDialog().title().assertThat(anyOf(equalToIgnoringCase(AUGMENTATION_MESSAGE_TITLE_EN),
              equalToIgnoringCase(AUGMENTATION_MESSAGE_TITLE_DE)));

      messageBox.getDialog().message().assertThat(containsString(categoryName));
      messageBox.getBottomToolbar().getOkButton().click();
    }

    private void isCategoryNotAugmentedInRepositoryList(String text) {
      CatalogRepositoryList catalogRepositoryList = collectionViewWithLiveContext.getCatalogRepositoryList();
      catalogRepositoryList.containsNonAugmentedCategory(text);
    }

    private void isProductNotAugmentedInRepositoryList(String text) {
      CatalogRepositoryList catalogRepositoryList = collectionViewWithLiveContext.getCatalogRepositoryList();
      catalogRepositoryList.containsNonAugmentedProduct(text);
    }

    void then_the_category_has_catalog_context_menu_in_repository_list(String categoryNameOrId) {
      CatalogRepositoryList catalogRepositoryList = collectionViewWithLiveContext.getCatalogRepositoryList();
      catalogRepositoryList.contextClickCommerceBeanByExternalIdOrName(categoryNameOrId);

      CatalogRepositoryContextMenu contextMenu = catalogRepositoryList.getContextMenu();

      contextMenu.getOpenMenuItem().visible().assertTrue();
      contextMenu.getOpenMenuItem().enabled().assertTrue();

      contextMenu.getOpenInTabMenuItem().visible().assertTrue();
      contextMenu.getOpenInTabMenuItem().enabled().assertTrue();

      contextMenu.getAugmentCategoryMenuItem().visible().assertTrue();
      contextMenu.getAugmentCategoryMenuItem().enabled().assertTrue();

      contextMenu.getAugmentProductMenuItem().visible().assertFalse();

    }

    void then_the_product_has_catalog_context_menu_in_repository_list(String productId) {
      CatalogRepositoryList catalogRepositoryList = collectionViewWithLiveContext.getCatalogRepositoryList();
      catalogRepositoryList.contextClickCommerceBeanByExternalId(productId);

      CatalogRepositoryContextMenu contextMenu = catalogRepositoryList.getContextMenu();

      contextMenu.getOpenInTabMenuItem().visible().assertTrue();
      contextMenu.getOpenInTabMenuItem().enabled().assertTrue();

      contextMenu.getAugmentProductMenuItem().visible().assertTrue();
      contextMenu.getAugmentProductMenuItem().enabled().assertTrue();

      contextMenu.getAugmentCategoryMenuItem().visible().assertFalse();
    }

    void then_the_category_has_catalog_context_menu_in_catalog_tree() {

      TreeModel selectedNode = collectionView.getLibraryTree().getSelectedNode();
      selectedNode.exists().waitUntilTrue();

      TreeViewContextMenu contextMenu = contextClickNode(selectedNode);

      contextMenu.getOpenInTabMenuItem().visible().assertTrue();
      contextMenu.getOpenInTabMenuItem().enabled().assertTrue();

      contextMenu.numberOfVisibleItems().assertThat(Matchers.greaterThanOrEqualTo(3L)); // 1 of them is a separator
      contextMenu.getAugmentCategoryMenuItem().visible().assertTrue();
      contextMenu.getAugmentCategoryMenuItem().enabled().assertTrue();
    }

    void then_the_category_is_not_augmented_in_catalog_tree() {
      TreeModel selectedNode = collectionView.getLibraryTree().getSelectedNode();
      Condition<WebElement> categoryInTreeCondition = selectedNode.element();
      categoryInTreeCondition.assertThat(
              allOf(
                      innerHtmlContains(NOT_AUGMENTED_ICON_CSS_CLASS),
                      not(innerHtmlContains(AUGMENTED_ICON_CSS_CLASS))
              )
      );
    }

    void then_the_category_is_augmented_in_catalog_tree(String externalIdOrName) {
      CommerceBeanModel commerceBeanModel = collectionViewWithLiveContext.getCatalogRepositoryList().getStore().queryByExternalIdOrName(externalIdOrName);
      JsCommerceBean jsCommerceBean = commerceBeanModel.getBean();
      Content augmentedCategoryContent = jsCommerceBean.getContent().bean().await(notNullValue());
      String uriPath = jsCommerceBean.uriPath().await(notNullValue());

      // Ensure that tree is expanded. Sometimes the tree is collapsed in this state.
      openLibraryForContent(augmentedCategoryContent);

      LibraryTree libraryTree = collectionView.getLibraryTree();
      // To be exact, we have a FolderTreeNode here.
      TreeModel selectedNode = libraryTree.getSelectedNode();
      selectedNode.dataString("id").assertEquals(uriPath);

      selectedNode
              .element()
              .assertThat(innerHtmlContains(AUGMENTED_ICON_CSS_CLASS));
    }

    void then_the_category_has_content_context_menu_in_catalog_tree(String categoryName, TestSiteConfiguration testSiteConfiguration) {
      TreeViewContextMenu contextMenu = contextClickNode(getCatalogTreeNode(categoryName, testSiteConfiguration));

      contextMenu.getOpenInTabMenuItem().visible().assertTrue();
      contextMenu.getOpenInTabMenuItem().enabled().assertTrue();

      contextMenu.getBookmarkMenuItem().visible().assertTrue();
      contextMenu.getBookmarkMenuItem().enabled().assertTrue();

      contextMenu.getCopyMenuItem().visible().assertTrue();
      contextMenu.getCopyMenuItem().enabled().assertTrue();

      //explicitly removed
      contextMenu.getPasteMenuItem().visible().assertFalse();
      contextMenu.getCutMenuItem().visible().assertFalse();
      contextMenu.getRenameMenuItem().visible().assertFalse();

      //Withdraw menu item should be disabled (as the augmented content is created and not published)
      // and have modified label.
      Item withdrawMenuItem = contextMenu.getWithdrawMenuItem();
      withdrawMenuItem.visible().assertTrue();
      withdrawMenuItem.disabled().assertTrue();
      withdrawMenuItem.text().assertEquals("Withdraw Augmentation");

      //Delete menu item should be enabled and have modified label.
      Item deleteMenuItem = contextMenu.getDeleteMenuItem();
      deleteMenuItem.visible().assertTrue();
      deleteMenuItem.enabled().assertTrue();
      deleteMenuItem.text().assertEquals("Delete Augmentation");

      //augment menu item should be hidden as the category is augmented.
      contextMenu.getAugmentCategoryMenuItem().visible().assertFalse();
    }

    void then_the_augmenting_content_is_active_in_work_area(Reference<? extends Content> contentReference) {
      workArea.getActivePremular().content().assertEquals(contentReference.get());
    }

    void then_the_category_pagegrid_has_still_the_old_layout(Reference<? extends Content> augmentedContent, Map<String, String> layoutData) {
      workArea.openInTab(augmentedContent.get());
      workArea.getActivePremular().content().assertEquals(augmentedContent.get());
      then_the_category_pagegrid_is_inherited_from_original_root_category_layout(true, layoutData);
    }

    public void then_there_are_multi_catalogs_in_the_library(TestSiteConfiguration testSiteConfiguration) {
      TreeModel storeNode = getStoreNode(testSiteConfiguration);
      //marketing spots + catalogs
      long storeChildrenCount = 1L + testSiteConfiguration.getCatalogMapping().size();
      storeNode.childCount().assertEquals(storeChildrenCount);

      Map<String, String> catalogMapping = testSiteConfiguration.getCatalogMapping();
      Iterator<String> iterator = catalogMapping.values().iterator();
      //the first child node is marketing spots
      for (int i = 1; i < storeChildrenCount; i++) {
        String catalogName = iterator.next();
        //the first node is default and should have the (Default) suffix
        if (i == 1) {
          catalogName = testSiteConfiguration.getDefaultCatalogNameInLibrary();
        }
        storeNode.getChild(i).text().assertEquals(catalogName);
      }
    }

    public void then_the_workspace_has_two_opened_tabs() {
      workArea.numberOfTabs().assertEquals(2L);
    }

    public void then_the_product_page_shows_the_caption(String caption) {
      getPreviewPanel(BaseCommerceBeanType.PRODUCT).reload();
      PreviewIFrame previewIFrame = getPreviewIFrame(BaseCommerceBeanType.PRODUCT);
      previewIFrame.text().await(containsString(caption));
    }

    public void then_the_product_page_shows_not_the_caption(String caption) {
      getPreviewPanel(BaseCommerceBeanType.PRODUCT).reload();
      PreviewIFrame previewIFrame = getPreviewIFrame(BaseCommerceBeanType.PRODUCT);
      previewIFrame.text().await(not(containsString(caption)));
    }

    public void then_there_will_be_no_search_results() {
      collectionViewWithLiveContext.getCatalogSearchList().getView().count().assertEquals(0L);
    }

    public void then_there_will_be_search_results() {
      collectionViewWithLiveContext.getCatalogSearchList().getView().count().assumeThat(greaterThan(0L));
    }

    public void then_the_tab_has_a_tooltip_with_name_and_no_catalog_info(String name, int tabIndex) {

      Tab tab = workArea.getWorkAreaTabProxies().getTabBar().getItems().getItemAs(Tab.class, tabIndex);
      String tooltip = "<div>" + name + "</div>";
      tab.tooltip().assertThat(equalToIgnoringCase(tooltip));
    }

    public void then_the_tab_has_a_tooltip_with_name_and_catalog_info(String name, int tabIndex, String catalogAlias,
                                                                      TestSiteConfiguration testSiteConfiguration) {

      Tab tab = workArea.getWorkAreaTabProxies().getTabBar().getItems().getItemAs(Tab.class, tabIndex);
      String tooltip = "<div>" + name + "</div><div style=\"height:6px\"></div><div>Catalog: " + testSiteConfiguration.getCatalogName(catalogAlias) + "</div>";
      tab.tooltip().assertThat(equalToIgnoringCase(tooltip));
    }

    public void then_the_tab_has_a_tooltip_with_name_store_and_locale_info(String name, int tabIndex, TestSiteConfiguration testSiteConfiguration) {
      Locale studioLocale = localeSupport.locale().await(notNullValue());
      Tab tab = workArea.getWorkAreaTabProxies().getTabBar().getItems().getItemAs(Tab.class, tabIndex);
      tab.tooltip().assertThat(allOf(
              containsString(name),
              containsString(testSiteConfiguration.getName()),
              containsString(testSiteConfiguration.getLocale().getDisplayName(studioLocale))
      ));
    }

    //INTERNAL

    private void openProductCatalog(TestSiteConfiguration testSiteConfiguration) {
      TreeModel productCatalogNode = getProductCatalogNode(testSiteConfiguration);
      productCatalogNode.select();
    }

    private void openCategoryInLibrary(String[] categoryPath, TestSiteConfiguration testSiteConfiguration) {
      openCategoryInLibrary(categoryPath, testSiteConfiguration, DEFAULT_CATALOG_ALIAS);
    }

    private void openCategoryInLibrary(String[] categoryPath, TestSiteConfiguration testSiteConfiguration, String catalogAlias) {
      Breadcrumb breadcrumb = collectionView.getSearchArea().getBreadcrumb();
      List<String> path = new ArrayList<>();
      path.add(testSiteConfiguration.getStoreName());
      String catalogName = testSiteConfiguration.getCatalogName(catalogAlias);
      if (catalogAlias.equals(DEFAULT_CATALOG_ALIAS)) {
        catalogName = testSiteConfiguration.getDefaultCatalogNameInLibrary();
      }

      path.add(catalogName);
      if (categoryPath != null) {
        path.addAll(Arrays.asList(categoryPath));
      }
      breadcrumb.select(path, true);
    }

    private void openProductInLibrary(String productPath, TestSiteConfiguration testSiteConfiguration) {
      openProductInLibrary(productPath, testSiteConfiguration, DEFAULT_CATALOG_ALIAS);
    }

    private void openProductInLibrary(String productPath, TestSiteConfiguration testSiteConfiguration, String catalogAlias) {
      String[] tokens = productPath.split("/");
      String[] categoryPathCrumbs = Arrays.copyOfRange(tokens, 0, tokens.length - 1);
      String productName = tokens[tokens.length - 1];
      openCategoryInLibrary(categoryPathCrumbs, testSiteConfiguration, catalogAlias);
      openInTabFromCatalogRepositoryList(productName);
    }

    private void openSelectedCategoryInTabFromTree() {
      TreeModel selectedNode = collectionView.getLibraryTree().getSelectedNode();
      selectedNode.exists().waitUntilTrue();
      TreeViewContextMenu contextMenu = contextClickNode(selectedNode);
      Component openInTabMenuItem = contextMenu.getOpenInTabMenuItem();
      contextMenu.visible().waitUntilTrue();
      openInTabMenuItem.enabled().waitUntilTrue();
      openInTabMenuItem.click();
    }

    private void openInTabFromCatalogRepositoryList(String externalIdOrName) {
      CatalogRepositoryList catalogRepositoryList = collectionViewWithLiveContext.getCatalogRepositoryList();
      catalogRepositoryList.contextClickCommerceBeanByExternalIdOrName(externalIdOrName);
      CatalogRepositoryContextMenu contextMenu = catalogRepositoryList.getContextMenu();
      contextMenu.visible().waitUntilTrue();
      contextMenu.getOpenInTabMenuItem().click();
    }

    private void openInTabFromCatalogSearchList(int index) {
      CatalogSearchList catalogSearchList = collectionViewWithLiveContext.getCatalogSearchList();
      catalogSearchList.contextClick(index);
      CatalogSearchContextMenu contextMenu = catalogSearchList.getContextMenu();
      contextMenu.visible().waitUntilTrue();
      contextMenu.getOpenInTabMenuItem().enabled().waitUntilTrue();
      contextMenu.getOpenInTabMenuItem().click();
    }

    /**
     * If the given commerce bean is already augmented, remove the augmentation.
     *
     * @param externalId            id of the commerce bean
     * @param testSiteConfiguration configuration
     */
    private void cleanupAugmentation(String externalId, TestSiteConfiguration testSiteConfiguration) {
      CommerceBean commerceBean = catalogUtils.getCommerceBean(externalId, testSiteConfiguration);
      Condition<Content> augmentingContentCondition = catalogUtils.getAugmentingContentCondition(commerceBean);
      Content augmentingContent = augmentingContentCondition.await();
      if (augmentingContent != null) {
        contentCleanup.cleanup(augmentingContent);
        augmentingContentCondition.waitUntil(nullValue());
      }
    }

    private void findExistingAugmentedCategory(Reference<? super Content> contentReference, String categoryPath) {
      Content content = contentRepository.getChild(categoryPath);
      if (content == null) {
        throw new IllegalStateException(format("Expected content does not exist at: '%s'", categoryPath));
      }
      searchConditions.indexed(content).waitUntilTrue();
      contentReference.set(content);
    }

    private void openLibrary() {
      editorContext.getCollectionViewManager().openRepository(true);
    }

    private void openLibraryForContent(@NonNull Content content) {
      // See also: ShowInRepositoryActionBase
      editorContext
              .getCollectionViewExtender()
              .getContentTreeRelation(content.getType())
              .showInTree(null, ContentTreeRelation.TREE_MODEL_ID_REPOSITORY, content);
    }

    private CommerceCategoryWorkAreaTab getActiveCommerceCategoryWorkAreaTab() {
      return workArea.getActiveTab(CommerceCategoryWorkAreaTab.class);
    }

    private CommerceCategoryContentForm getActiveCommerceCategoryContentForm() {
      return getActiveCommerceCategoryWorkAreaTab().getContentForm();
    }

    private CommerceCategoryStructureForm getActiveCommerceCategoryStructureForm() {
      return getActiveCommerceCategoryWorkAreaTab().getStructureForm();
    }

    private CommerceProductWorkAreaTab getActiveCommerceProductWorkAreaTab() {
      return workArea.getActiveTab(CommerceProductWorkAreaTab.class);
    }

    private CommerceProductContentForm getActiveCommerceProductContentForm() {
      return getActiveCommerceProductWorkAreaTab().getContentForm();
    }

    private CommerceProductStructureForm getActiveCommerceProductStructureForm() {
      return getActiveCommerceProductWorkAreaTab().getStructureForm();
    }

    private TreeModel getProductCatalogNode(TestSiteConfiguration testSiteConfiguration) {
      TreeModel storeNode = getStoreNode(testSiteConfiguration);

      String productCatalogName = testSiteConfiguration.getDefaultCatalogNameInLibrary();
      TreeModel productCatalogNode = storeNode.getChild(text(productCatalogName, false));
      productCatalogNode.exists()
              .withMessage(format("Product catalog with name '%s' should exist in library tree.", productCatalogName))
              .waitUntilTrue();
      return productCatalogNode;
    }

    private TreeModel getStoreNode(TestSiteConfiguration testSiteConfiguration) {
      LibraryTree libraryTree = collectionView.getLibraryTree();
      TreeModel rootNode = libraryTree.getRootNode();

      String storeName = testSiteConfiguration.getStoreName();
      TreeModel storeNode = rootNode.getChild(text(storeName, false));
      storeNode.exists()
              .withMessage(format("Store with name '%s' should exist in library tree.", storeName))
              .waitUntilTrue();
      storeNode.expand();
      return storeNode;
    }

    private TreeModel getCatalogTreeNode(String nodeName, TestSiteConfiguration testSiteConfiguration) {
      TreeModel productCatalogNode = getProductCatalogNode(testSiteConfiguration);
      productCatalogNode.expand();
      return productCatalogNode.getChild(text(nodeName, true));
    }

    private void nonAugmentedCategoryIsOpen() {
      getActiveCommerceCategoryContentForm().visible().waitUntilTrue();
    }

    private void isCategoryAugmentedInRepositoryList(String categoryName, TestSiteConfiguration testSiteConfiguration) {
      collectionViewWithLiveContext.getCatalogRepositoryList().containsAugmentedCategory(categoryName);
      collectionViewWithLiveContext.getCatalogRepositoryList().hasContextMenuForAugmentedCategory(categoryName);

      then_the_category_is_augmented_in_catalog_tree(categoryName);
      then_the_category_has_content_context_menu_in_catalog_tree(categoryName, testSiteConfiguration);
    }

    private void isProductAugmentedInRepositoryList(String externalId) {
      collectionViewWithLiveContext.getCatalogRepositoryList().containsAugmentedProduct(externalId);
      collectionViewWithLiveContext.getCatalogRepositoryList().hasContextMenuForAugmentedProduct(externalId);
    }

    private PreviewIFrame getPreviewIFrame(CommerceBeanType commerceBeanType) {
      PreviewPanel previewPanel = getPreviewPanel(commerceBeanType);
      InnerPreviewPanel innerPreviewPanel = previewPanel.getInnerPreviewPanel();
      PreviewIFrame previewIFrame = innerPreviewPanel.getPreviewIFrame();

      previewPanel.visible().waitUntilTrue();

      previewIFrame.visible().waitUntilTrue();
      return previewIFrame;
    }

    private PreviewIFrame getPreviewIFrame(CommerceBean commerceBean) {
      return getPreviewIFrame(commerceBean.getId().getCommerceBeanType());
    }

    private PreviewPanel getPreviewPanel(CommerceBeanType commerceBeanType) {
      PreviewPanel previewPanel;
      if (commerceBeanType == BaseCommerceBeanType.PRODUCT) {
        previewPanel = getActiveCommerceProductWorkAreaTab().getPreviewPanel();
      } else if (commerceBeanType == BaseCommerceBeanType.CATEGORY) {
        previewPanel = getActiveCommerceCategoryWorkAreaTab().getPreviewPanel();
      } else {
        throw new IllegalStateException("cannot handle commerce bean type" + commerceBeanType);
      }
      return previewPanel;
    }

    @NonNull
    private TreeViewContextMenu contextClickNode(@NonNull TreeModel node) {
      node.select();
      node.contextClick();
      TreeViewContextMenu contextMenu = collectionView.getLibraryTree().getContextMenu();
      contextMenu.visible().withMessage("Context menu should become visible for: " + node.text().await()).assertTrue();
      return contextMenu;
    }

    private PageGridPropertyField getCategoryPageGridPropertyField(boolean augmented) {
      if (augmented) {
        Premular premular = workArea.getActivePremular();
        return premular.focusDocumentProperty(PageGridPropertyField.class, PAGE_GRID_STRUCT_PROPERTY);
      } else {
        CommerceCategoryWorkAreaTab categoryTab = getActiveCommerceCategoryWorkAreaTab();
        categoryTab.setActiveTab(0);
        return categoryTab.getContentForm().getCategoryPageGridPropertyField();
      }
    }

    private PageGridPropertyField getProductPageGridPropertyFieldOfTheCategory(boolean augmented) {
      if (augmented) {
        Premular premular = workArea.getActivePremular();
        return premular.focusDocumentProperty(PageGridPropertyField.class, "pdpPagegrid");
      } else {
        CommerceCategoryWorkAreaTab categoryTab = getActiveCommerceCategoryWorkAreaTab();
        categoryTab.setActiveTab(2);
        return categoryTab.getProductContentForm().getProductPageGridPropertyField();
      }
    }

    private PageGridPropertyField getProductPageGridPropertyField(boolean augmented) {
      if (augmented) {
        Premular premular = workArea.getActivePremular();
        return premular.focusDocumentProperty(PageGridPropertyField.class, "pdpPagegrid");
      } else {
        CommerceProductWorkAreaTab productTab = getActiveCommerceProductWorkAreaTab();
        productTab.setActiveTab(0);
        return productTab.getContentForm().getProductPageGridPropertyField();
      }
    }

    private ContentType toContentType(String contentType) {
      return requireNonNull(capConnection.getContentRepository().getContentType(contentType), format("Required ContentType does not exist: '%s'.", contentType));
    }

    private static void validatePlacementData(PageGridPropertyField pageGridPropertyField, String placement, String testStr, boolean augmented) {
      PlacementField placementField = pageGridPropertyField.getPlacementField(placement);
      placementField.expand();
      placementField.inheritanceStateInherited().assertTrue();

      if (augmented) {
        placementField.getInheritButton().pressed().assertTrue();
      } else {
        placementField.getInheritButton().disabled().withMessage("When not augmented the whole toolbar is disabled.").assertTrue();
      }

      LinkListPropertyFieldGridPanel grid = placementField.getGrid();
      grid.readonly().assertTrue();
      grid.getStore().query("name", testStr, true).count()
              .withMessage("test data not correct, placement: " + placement + ", test data: " + testStr)
              .assertEquals(1L);
    }

    public void when_I_open_a_category_of_default_catalog_via_library(String categoryPath, TestSiteConfiguration testSiteConfiguration) {
      sidePanelManager.dockCollectionView();
      String[] tokens = categoryPath.split("/");
      openCategoryInLibrary(tokens, testSiteConfiguration);
      openSelectedCategoryInTabFromTree();
    }

    public void when_I_open_a_category_of_master_catalog_via_library(String categoryPath, TestSiteConfiguration testSiteConfiguration) {
      String[] tokens = categoryPath.split("/");
      openCategoryInLibrary(tokens, testSiteConfiguration, MASTER_CATALOG_ALIAS);
      openSelectedCategoryInTabFromTree();
    }

    public void then_the_category_form_with_default_catalog_data_is_displayed(TestSiteConfiguration testSiteConfiguration) {
      then_the_category_form_with_catalog_data_is_displayed(testSiteConfiguration, DEFAULT_CATALOG_ALIAS);
    }

    public void then_the_category_form_with_master_catalog_data_is_displayed(TestSiteConfiguration testSiteConfiguration) {
      then_the_category_form_with_catalog_data_is_displayed(testSiteConfiguration, MASTER_CATALOG_ALIAS);
    }

    private void then_the_category_form_with_catalog_data_is_displayed(TestSiteConfiguration testSiteConfiguration, String catalogAlias) {
      CommerceCategoryWorkAreaTab categoryTab = getActiveCommerceCategoryWorkAreaTab();
      categoryTab.setActiveTab(3);
      CommerceSystemForm systemForm = categoryTab.getSystemForm();
      systemForm.getCatalogField().value().assertEquals(testSiteConfiguration.getCatalogName(catalogAlias));
    }

    public void then_the_studio_shows_single_catalog_empty_text() {
      TableView view = collectionViewWithLiveContext.getCatalogSearchList().getView();
      view.element().assertThat(allOf(
              innerHtmlContains("No search result."),
              not(innerHtmlContains("Try searching in a different catalog."))
      ));
    }

    public void then_the_studio_shows_multi_catalog_empty_text() {
      TableView view = collectionViewWithLiveContext.getCatalogSearchList().getView();
      view.element().assertThat(allOf(
              innerHtmlContains("No search result, possibly because the search is limited to the catalog 'Extended Sites Catalog Asset Store Consumer Direct'."),
              innerHtmlContains("Try searching in a different catalog.")
      ));
    }

  }

  protected abstract TestSiteConfiguration getTestSiteConfiguration();

  protected abstract String getReferencePrefix();

  protected abstract String getRootCategoryPath();

  protected abstract String getNewLayoutPath();
}
