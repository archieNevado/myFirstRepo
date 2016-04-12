package com.coremedia.lc.studio.lib.augmentation;

import com.coremedia.cap.common.DuplicateNameException;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import com.coremedia.rest.cap.intercept.InterceptService;
import com.coremedia.rest.linking.LinkResolver;
import com.coremedia.rest.linking.LocationHeaderResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * A REST service to augment a category.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/category/augment")
@Named
public class CategoryAugmentationResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(CategoryAugmentationResource.class);

  static final String CM_EXTERNAL_CHANNEL = "CMExternalChannel";
  private static final String EXTERNAL_ID = "externalId";
  private static final String TITLE = "title";
  private static final String SEGMENT = "segment";
  static final String CATEGORY_URI_PARAM = "categoryUri";
  static final String DEFAULT_BASE_FOLDER_NAME = "Augmentation";

  private String baseFolderName;
  private InterceptService interceptService;
  private LinkResolver linkResolver;
  private SitesService sitesService;
  private ContentType contentType;
  private ContentRepository contentRepository;

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @ResourceFilters(value = {LocationHeaderResourceFilter.class})
  public Content handlePost(final Map<String, Object> rawJson) {
    String categoryUri = (String) rawJson.get(CATEGORY_URI_PARAM);
    Category category = (Category) linkResolver.resolveLink(categoryUri);
    if (category == null) {
      return null;
    }

    //create folder hierarchy for category
    Content categoryFolder = contentRepository.createSubfolders(computeFolderName(category));

    //create CMExternalChannel document
    if (categoryFolder != null) {
      Map<String, Object> properties = new HashMap<>();
      properties.put(EXTERNAL_ID, category.getId());
      //we initialize the title and segment with the display name and don't rely on ContentInitializer.initChannel
      //as the latter will initialize the title with the escaped display name of the content
      properties.put(TITLE, category.getDisplayName());
      properties.put(SEGMENT, category.getDisplayName());
      return createContent(categoryFolder, getEscapedDisplayName(category), properties);
    }
    return null;
  }

  private Content createContent(Content parent, String name, Map<String, Object> properties) {
    // Create content (taking possible interceptors into consideration)
    ContentWriteRequest writeRequest = interceptService.interceptCreate(parent, name, contentType, properties);
    interceptService.handleErrorIssues(writeRequest);
    Content content = parent.getChild(name);
    if (content == null) {
      try {
        content = contentType.create(parent, name, writeRequest.getProperties());
      } catch (DuplicateNameException e) {
        LOGGER.debug("ignored concurrent (redundant) augmentation request", e);
        content = parent.getChild(name);
      }
      // will most likely be non-null but maybe we're facing a concurrent content deletion
      if(null != content) {
        interceptService.postProcess(content, null);
      }
    }
    return content;
  }

  @Nullable
  private String computeFolderName(@Nonnull Category category) {
    Site site = sitesService.getSite(category.getContext().getSiteId());
    if (site != null) {
      StringBuilder stringBuilder = new StringBuilder().append(site.getSiteRootFolder().getPath()).append('/').append(baseFolderName);
      for (Category category1 : category.getBreadcrumb()) {
        stringBuilder.append('/').append(getEscapedDisplayName(category1));
      }
      return stringBuilder.toString();
    }
    return null;
  }

  private String getEscapedDisplayName(Category category1) {
    String displayName = category1.getDisplayName();
    //External ids of category can contain '/'. See CMS-5075
    displayName = displayName.replace('/', '_');
    return displayName;
  }

  @Autowired
  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }

  @Autowired
  public void setInterceptService(InterceptService interceptService) {
    this.interceptService = interceptService;
  }

  @Autowired
  public void setLinkResolver(LinkResolver linkResolver) {
    this.linkResolver = linkResolver;
  }

  @Autowired
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
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
