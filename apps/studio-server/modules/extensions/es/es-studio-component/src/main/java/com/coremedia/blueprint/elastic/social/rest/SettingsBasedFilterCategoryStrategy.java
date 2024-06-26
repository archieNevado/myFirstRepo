package com.coremedia.blueprint.elastic.social.rest;

import com.coremedia.blueprint.base.elastic.tenant.TenantSiteMapping;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.content.Content;
import com.coremedia.elastic.core.api.tenant.TenantService;
import com.coremedia.elastic.social.rest.api.CategoryKeyAndDisplay;
import com.coremedia.elastic.social.rest.api.FilterCategoryStrategy;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A strategy to retrieve a list of categories for filtering the moderation list by a link list property
 * 'filterCategories' in the Elastic Social settings of the local settings.
 * A list of {@link CategoryResolver CategoryResolvers} is used to determine a {@link com.coremedia.elastic.social.rest.api.CategoryKeyAndDisplay}
 * for a linked {@link Content}.
 **/
@Component
public class SettingsBasedFilterCategoryStrategy implements FilterCategoryStrategy {

  private static final Logger LOG = LoggerFactory.getLogger(SettingsBasedFilterCategoryStrategy.class);

  static final String FILTER_CATEGORIES_PROPERTY = "filterCategories";
  public static final String ELASTIC_SOCIAL_STRUCT_NAME = "elasticSocial";

  private final SettingsService settingsService;
  private final TenantService tenantService;
  private final TenantSiteMapping tenantSiteMapping;
  private final List<CategoryResolver> categoryResolvers;

  public SettingsBasedFilterCategoryStrategy(SettingsService settingsService, TenantService tenantService, TenantSiteMapping tenantSiteMapping, List<CategoryResolver> categoryResolvers) {
    this.settingsService = settingsService;
    this.tenantService = tenantService;
    this.tenantSiteMapping = tenantSiteMapping;
    this.categoryResolvers = categoryResolvers;
  }

  @Override
  public List<CategoryKeyAndDisplay> getCategoryList() {
    List<CategoryKeyAndDisplay> categories = new ArrayList<>();

    String currentTenant = tenantService.getCurrent();
    Collection<Content> rootNavigations = tenantSiteMapping.getTenantSiteMap().get(currentTenant);
    if (rootNavigations != null) {
      for (Content rootNavigation : rootNavigations) {
        addCategoryListForRootChannel(categories, rootNavigation);
      }
    }

    return categories;
  }

  private void addCategoryListForRootChannel(List<CategoryKeyAndDisplay> categories, Content rootNavigation) {
    Map<String, Object> elasticSocialSettings = getElasticSocialSettings(rootNavigation, rootNavigation);
    //noinspection unchecked
    List<Content> categoryDefiningContents = (List<Content>) elasticSocialSettings.get(FILTER_CATEGORIES_PROPERTY);
    if (categoryDefiningContents != null) {
      for (Content content : categoryDefiningContents) {
        CategoryKeyAndDisplay category = resolveCategory(content);
        if (category != null) {
          addCategory(categories, category);
        } else {
          LOG.info("Could not resolve a category for " + content);
        }
      }
    }
  }

  private void addCategory(List<CategoryKeyAndDisplay> categories, @NonNull CategoryKeyAndDisplay category) {
    if (!categories.contains(category)) {
      categories.add(category);
    }
  }

  /**
   * Returns the {@link CategoryKeyAndDisplay} resolved by the first {@link CategoryResolver} which returns
   * a non null value for the given {@link Content}.
   * @param content the {@link Content} to convert
   * @return a {@link CategoryKeyAndDisplay} resolved by the first {@link CategoryResolver} which returns
   * a non null value
   */
  private CategoryKeyAndDisplay resolveCategory(Content content) {
    for (CategoryResolver categoryResolver : categoryResolvers) {
      CategoryKeyAndDisplay categoryKeyAndDisplay = categoryResolver.resolve(content);
      if (categoryKeyAndDisplay != null) {
        return categoryKeyAndDisplay;
      }
    }
    return null;
  }

  private Map<String,Object> getElasticSocialSettings(Content content, Content parent) {
    return settingsService.mergedSettingAsMap(ELASTIC_SOCIAL_STRUCT_NAME, String.class, Object.class, content, parent);
  }
}
