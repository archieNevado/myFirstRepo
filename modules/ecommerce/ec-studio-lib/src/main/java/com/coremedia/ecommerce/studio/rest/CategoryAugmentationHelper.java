package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.pagegrid.PageGridContentKeywords;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

/**
 * A REST service to augment a category.
 */
@Named
class CategoryAugmentationHelper extends AugmentationHelperBase<Category> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CategoryAugmentationHelper.class);

  private static final String CM_EXTERNAL_CHANNEL = "CMExternalChannel";

  static final String CATEGORY_PAGEGRID_STRUCT_PROPERTY = PageGridContentKeywords.PAGE_GRID_STRUCT_PROPERTY;
  public static final String CATEGORY_PRODUCT_PAGEGRID_STRUCT_PROPERTY = "pdpPagegrid";

  static final String TITLE = "title";
  static final String SEGMENT = "segment";

  @Override
  @Nullable
  Content augment(@Nonnull Category category) {
    // create folder hierarchy for category
    Content categoryFolder = contentRepository.createSubfolders(computeFolderName(category));

    if (categoryFolder == null) {
      return null;
    }

    Map<String, Object> properties = buildCategoryContentDocumentProperties(category);

    if (augmentationService != null) {
      initializeLayoutSettings(category, properties);
    }

    return createContent(categoryFolder, getEscapedDisplayName(category), properties);
  }

  private void initializeLayoutSettings(@Nonnull Category category, @Nonnull Map<String, Object> properties) {
    Category rootCategory = getRootCategory(category);
    Content rootCategoryContent = getCategoryContent(rootCategory);

    if (rootCategoryContent == null) {
      LOGGER.warn("Root category is not augmented (requested category is ' " + category.getId() +
              "') , cannot set default layouts.");
      return;
    }

    initializeCategoryLayout(rootCategoryContent, rootCategory, category, properties);
    initializeProductLayout(rootCategoryContent, rootCategory, category, properties);
  }

  private void initializeCategoryLayout(@Nonnull Content rootCategoryContent, @Nonnull Category rootCategory,
                                        @Nonnull CommerceBean commerceBean, @Nonnull Map<String, Object> properties) {
    Content defaultCategoryLayoutSettings = getLayoutSettings(rootCategoryContent, CATEGORY_PAGEGRID_STRUCT_PROPERTY);

    if (defaultCategoryLayoutSettings == null) {
      LOGGER.warn("No default category page layout found for root category '{}', "
                      + "cannot initialize category page layout for augmented category '{}'.",
              rootCategory.getId(), commerceBean.getId());
      return;
    }

    Struct structWithLayoutLink = createStructWithLayoutLink(defaultCategoryLayoutSettings);
    properties.put(CATEGORY_PAGEGRID_STRUCT_PROPERTY, structWithLayoutLink);
  }

  private void initializeProductLayout(@Nonnull Content rootCategoryContent, @Nonnull Category rootCategory,
                                       @Nonnull CommerceBean commerceBean, @Nonnull Map<String, Object> properties) {
    Content defaultProductLayoutSettings = getLayoutSettings(rootCategoryContent, CATEGORY_PRODUCT_PAGEGRID_STRUCT_PROPERTY);

    if (defaultProductLayoutSettings == null) {
      LOGGER.warn("No default product page layout found for root category '{}', "
                      + "cannot initialize product page layout for augmented category '{}'.",
              rootCategory.getId(), commerceBean.getId());
      return;
    }

    Struct structWithLayoutLink = createStructWithLayoutLink(defaultProductLayoutSettings);
    properties.put(CATEGORY_PRODUCT_PAGEGRID_STRUCT_PROPERTY, structWithLayoutLink);
  }

  @Override
  protected Content getCategoryContent(@Nonnull Category category) {
    return augmentationService.getContent(category);
  }

  /**
   * Builds properties for an <code>CMExternalChannel</code> document.
   */
  private Map<String, Object> buildCategoryContentDocumentProperties(@Nonnull Category category) {
    Map<String, Object> properties = new HashMap<>();

    properties.put(EXTERNAL_ID, category.getId());

    // Initialize title and segment with the display name instead of relying on
    // `ContentInitializer.initChannel` as the latter will initialize the title
    // with the escaped display name of the content.
    properties.put(TITLE, category.getDisplayName());
    properties.put(SEGMENT, category.getDisplayName());

    return properties;
  }

  @Autowired(required = false)
  @Qualifier("categoryAugmentationService")
  public void setAugmentationService(AugmentationService augmentationService) {
    this.augmentationService = augmentationService;
  }

  @Value("${livecontext.augmentation.category.type:" + CM_EXTERNAL_CHANNEL + "}")
  public void setAugmentedContentType(ContentType contentType) {
    this.contentType = contentType;
  }
}
