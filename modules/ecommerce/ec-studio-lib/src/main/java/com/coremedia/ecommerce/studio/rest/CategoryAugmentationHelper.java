package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.pagegrid.PageGridContentKeywords;
import com.coremedia.cap.common.CapStructHelper;
import com.coremedia.cap.common.DuplicateNameException;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructBuilder;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import com.coremedia.rest.cap.intercept.InterceptService;
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

import static com.coremedia.cap.struct.StructBuilderMode.LOOSE;

/**
 * A REST service to augment a category.
 */
@Named
class CategoryAugmentationHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(CategoryAugmentationHelper.class);

  private static final String CM_EXTERNAL_CHANNEL = "CMExternalChannel";
  static final String EXTERNAL_ID = "externalId";
  static final String TITLE = "title";
  static final String SEGMENT = "segment";
  static final String DEFAULT_BASE_FOLDER_NAME = "Augmentation";

  static final String CATEGORY_PAGEGRID_STRUCT_PROPERTY = PageGridContentKeywords.PAGE_GRID_STRUCT_PROPERTY;
  static final String PRODUCT_PAGEGRID_STRUCT_PROPERTY = "pdpPagegrid";

  private String baseFolderName;
  private InterceptService interceptService;
  private SitesService sitesService;
  private ContentType contentType;
  private ContentRepository contentRepository;
  private AugmentationService augmentationService;

  @Nullable
  Content augment(@Nonnull Category category) {
    // create folder hierarchy for category
    Content categoryFolder = contentRepository.createSubfolders(computeFolderName(category));

    if (categoryFolder == null) {
      return null;
    }

    // create CMExternalChannel document
    Map<String, Object> properties = new HashMap<>();
    properties.put(EXTERNAL_ID, category.getId());
    // we initialize the title and segment with the display name and don't rely on ContentInitializer.initChannel
    // as the latter will initialize the title with the escaped display name of the content
    properties.put(TITLE, category.getDisplayName());
    properties.put(SEGMENT, category.getDisplayName());

    if (augmentationService != null) {
      initializeLayoutSettings(category, properties);
    }
    return createContent(categoryFolder, getEscapedDisplayName(category), properties);
  }

  private void initializeLayoutSettings(@Nonnull Category category, Map<String, Object> properties) {
    Category rootCategory = getRootCategory(category);
    if (rootCategory == null) {
      LOGGER.warn("Root category not found for category ' " + category.getId() + "' , cannot set default layouts.");
    } else {
      Content rootCategoryContent = augmentationService.getContent(rootCategory);
      if (rootCategoryContent == null) {
        LOGGER.warn("Root category is not augmented (requested category is ' " + category.getId() +
                "') , cannot set default layouts.");
      } else {
        Content defaultCategoryLayoutSettings = getLayoutSettings(rootCategoryContent, CATEGORY_PAGEGRID_STRUCT_PROPERTY);
        if (defaultCategoryLayoutSettings == null) {
          LOGGER.warn("No default category page layout found for root category ' " + rootCategory.getId() +
                  "' , cannot init category page layout for augmented category '" + category.getId() + "'.");
        } else {
          Struct structWithLayoutLink = createStructWithLayoutLink(defaultCategoryLayoutSettings);
          properties.put(CATEGORY_PAGEGRID_STRUCT_PROPERTY, structWithLayoutLink);
        }

        Content defaultProductLayoutSettings = getLayoutSettings(rootCategoryContent, PRODUCT_PAGEGRID_STRUCT_PROPERTY);
        if (defaultProductLayoutSettings == null) {
          LOGGER.warn("No default product page layout found for root category ' " + rootCategory.getId() +
                  "' , cannot init product page layout for augmented category '" + category.getId() + "'.");
        } else {
          Struct structWithLayoutLink = createStructWithLayoutLink(defaultProductLayoutSettings);
          properties.put(PRODUCT_PAGEGRID_STRUCT_PROPERTY, structWithLayoutLink);
        }
      }
    }
  }

  private Content createContent(Content parent, String name, Map<String, Object> properties) {
    // Create content (taking possible interceptors into consideration)
    ContentWriteRequest writeRequest = null;
    if (interceptService != null) {
      writeRequest = interceptService.interceptCreate(parent, name, contentType, properties);
      interceptService.handleErrorIssues(writeRequest);
    }

    Content content = parent.getChild(name);
    if (content != null) {
      return content;
    }

    try {
      Map<String, Object> myProperties = writeRequest != null ? writeRequest.getProperties() : properties;
      content = contentType.create(parent, name, myProperties);
    } catch (DuplicateNameException e) {
      LOGGER.debug("ignored concurrent (redundant) augmentation request", e);
      content = parent.getChild(name);
    }

    // will most likely be non-null but maybe we're facing a concurrent content deletion
    if (content == null) {
      return null;
    }

    if (interceptService != null) {
      interceptService.postProcess(content, null);
    }
    return content;
  }

  @Nullable
  private String computeFolderName(@Nonnull Category category) {
    Site site = sitesService.getSite(category.getContext().getSiteId());

    if (site == null) {
      return null;
    }

    StringBuilder stringBuilder = new StringBuilder().append(site.getSiteRootFolder().getPath()).append('/').append(baseFolderName);
    for (Category category1 : category.getBreadcrumb()) {
      stringBuilder.append('/').append(getEscapedDisplayName(category1));
    }
    return stringBuilder.toString();
  }

  private String getEscapedDisplayName(Category category1) {
    String displayName = category1.getDisplayName();
    //External ids of category can contain '/'. See CMS-5075
    displayName = displayName.replace('/', '_');
    return displayName;
  }

  private Category getRootCategory(@Nonnull Category category) {
    Category result = category;
    while (!result.isRoot()) {
      Category parent = result.getParent();
      if (parent == null) {
        LOGGER.warn("Root category is not properly recognized: " + result.getId());
        return result;
      }
      result = parent;
    }
    return result;
  }

  @Nullable
  private Content getLayoutSettings(@Nonnull Content content, @Nonnull String pageGridName) {
    Struct placementStruct = CapStructHelper.getStruct(content, pageGridName);
    if (placementStruct != null) {
      Struct placements2Struct = CapStructHelper.getStruct(placementStruct, PageGridContentKeywords.PLACEMENTS_PROPERTY_NAME);
      if (placements2Struct != null) {
        Content layoutSettings = (Content) placements2Struct.get(PageGridContentKeywords.LAYOUT_PROPERTY_NAME);
        if (layoutSettings != null) {
          return layoutSettings;
        }
      }
    }
    return null;
  }

  private Struct createEmptyStruct() {
    return contentRepository.getConnection().getStructService().createStructBuilder().build();
  }

  private Struct createStructWithLayoutLink(Content layoutSettings) {
    Struct pageGridStruct = createEmptyStruct();
    StructBuilder builder = pageGridStruct.builder().mode(LOOSE);
    builder.set(PageGridContentKeywords.PLACEMENTS_PROPERTY_NAME, createEmptyStruct());
    builder.enter(PageGridContentKeywords.PLACEMENTS_PROPERTY_NAME);
    builder.declareLink(PageGridContentKeywords.LAYOUT_PROPERTY_NAME,
            contentRepository.getContentType("Document_"), layoutSettings);
    builder.add(PageGridContentKeywords.PLACEMENTS_PLACEMENTS_PROPERTY_NAME, createEmptyStruct());
    return builder.build();
  }

  @Autowired
  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }

  @Autowired(required = false)
  public void setInterceptService(InterceptService interceptService) {
    this.interceptService = interceptService;
  }

  @Autowired
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Autowired(required = false)
  @Qualifier("categoryAugmentationService")
  public void setAugmentationService(AugmentationService augmentationService) {
    this.augmentationService = augmentationService;
  }

  @Value("${livecontext.augmentation.path:" + DEFAULT_BASE_FOLDER_NAME + "}")
  public void setBaseFolderName(String baseFolderName) {
    this.baseFolderName = baseFolderName;
  }

  @Value("${livecontext.augmentation.category.type:" + CM_EXTERNAL_CHANNEL + "}")
  public void setAugmentedCategoryType(ContentType augmentedCategoryType) {
    this.contentType = augmentedCategoryType;
  }
}
