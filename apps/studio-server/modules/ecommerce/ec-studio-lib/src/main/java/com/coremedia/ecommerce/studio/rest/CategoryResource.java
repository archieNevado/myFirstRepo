package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.ecommerce.studio.rest.configuration.ECommerceStudioConfigurationProperties;
import com.coremedia.ecommerce.studio.rest.configuration.PreloadChildCategories;
import com.coremedia.ecommerce.studio.rest.model.ChildRepresentation;
import com.coremedia.ecommerce.studio.rest.model.SearchFacets;
import com.coremedia.ecommerce.studio.rest.model.Store;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.xml.Markup;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.util.CommerceBeanUtils.getCatalog;
import static com.coremedia.ecommerce.studio.rest.configuration.PreloadChildCategories.ALL;
import static com.coremedia.ecommerce.studio.rest.configuration.PreloadChildCategories.ALL_EXCEPT_TOP_LEVEL;
import static java.util.stream.Collectors.toList;

/**
 * A catalog {@link Category} object as a RESTful resource.
 */
@RestController
@RequestMapping(value = CategoryResource.URI_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoryResource extends CommerceBeanResource<Category> {

  static final String PATH_TYPE = "category";
  static final String URI_PATH
          = "livecontext/" + PATH_TYPE + "/{" + PATH_SITE_ID + "}/{" + PATH_CATALOG_ALIAS + "}/{id:.+}";

  private final PreloadChildCategories preloadChildCategories;

  @Autowired
  public CategoryResource(CatalogAliasTranslationService catalogAliasTranslationService, ECommerceStudioConfigurationProperties properties) {
    super(catalogAliasTranslationService);
    preloadChildCategories = properties.getPreloadChildCategories();
  }

  @Override
  protected CategoryRepresentation getRepresentation(@NonNull Map<String, String> params) {
    CategoryRepresentation categoryRepresentation = new CategoryRepresentation();
    fillRepresentation(params, categoryRepresentation);
    return categoryRepresentation;
  }

  protected void fillRepresentation(@NonNull Map<String, String> params, CategoryRepresentation representation) {
    Category entity = getEntity(params);
    if (entity == null) {
      String errorMessage = String.format("Could not load category with id '%s'.", params.get(PATH_ID));
      throw new CatalogBeanNotFoundRestException(errorMessage);
    }
    super.fillRepresentation(params, entity, representation);

    representation.setName(entity.getName());
    Markup shortDescription = entity.getShortDescription();
    if (shortDescription != null) {
      representation.setShortDescription(shortDescription.asXml());
    }
    Markup longDescription = entity.getLongDescription();
    if (longDescription != null) {
      representation.setLongDescription(longDescription.asXml());
    }
    representation.setThumbnailUrl(RepresentationHelper.modifyAssetImageUrl(entity.getThumbnailUrl(), getContentRepositoryResource().getContentRepository()));
    representation.setParent(entity.getParent());

    // All subcategories can be (pre)loaded so to be able to check whether they are virtual or not.
    // Root category is by definition non virtual (there is no parent to check)
    // In Commerce systems with no physical root category this also applies top level categories.
    // Preloading can be disabled via configuration
    boolean loadChildCategories = preloadChildCategories(entity);
    List<Category> subCategories = loadChildCategories ?
            entity.getChildren().stream()
                    .filter(this::ensureCategoryIsLoadable)
                    .collect(toList()) : entity.getChildren();

    representation.setSubCategories(subCategories);
    representation.setProducts(entity.getProducts());

    StoreContext storeContext = entity.getContext();
    representation.setStore(new Store(storeContext));
    getCatalog(entity).ifPresent(representation::setCatalog);
    representation.setDisplayName(entity.getDisplayName());

    List<CommerceBean> children = new ArrayList<>();
    children.addAll(representation.getSubCategories());
    children.addAll(representation.getProducts());
    representation.setChildren(children);
    representation.setPictures(entity.getPictures());
    representation.setDownloads(entity.getDownloads());

    List<ChildRepresentation> result = new ArrayList<>();
    String myExternalId = entity.getExternalId();
    for (CommerceBean child : children) {
      ChildRepresentation childRepresentation = new ChildRepresentation();
      childRepresentation.setChild(child);
      childRepresentation.setDisplayName(child.getExternalId());
      if (child instanceof Category && loadChildCategories) {
        // Note, getting the parent leads to the load of the whole child category.
        Category childParent = ((Category)child).getParent();
        // isVirtual is true if the child means to belong to another parent than me
        childRepresentation.setIsVirtual(childParent != null && !myExternalId.equals(childParent.getExternalId()));
      }
      result.add(childRepresentation);
    }
    representation.setChildrenData(result);

    representation.setContent(getContent(entity));
    representation.setSearchFacets(new SearchFacets(storeContext, myExternalId));
  }

  @Override
  protected Category doGetEntity(@NonNull Map<String, String> params) {
    Optional<StoreContext> storeContextOptional = getStoreContext(params);
    if (storeContextOptional.isEmpty()) {
      return null;
    }

    StoreContext storeContext = storeContextOptional.get();
    CommerceConnection commerceConnection = storeContext.getConnection();

    CatalogAlias catalogAlias = storeContext.getCatalogAlias();
    CommerceId commerceId = commerceConnection.getIdProvider().formatCategoryId(catalogAlias, params.get(PATH_ID));
    return commerceConnection.getCatalogService().findCategoryById(commerceId, storeContext);
  }

  @Override
  @Autowired(required = false)
  @Qualifier("categoryAugmentationService")
  public void setAugmentationService(AugmentationService augmentationService) {
    super.setAugmentationService(augmentationService);
  }

  private boolean ensureCategoryIsLoadable(Category category) {
    try {
      category.load();
    } catch (CommerceException e) {
      LOG.warn("Cannot load category with id '{}' ({})", category.getId(), e.getMessage());
      return false;
    }
    return true;
  }

  private boolean preloadChildCategories(Category category) {
    return preloadChildCategories == ALL || (preloadChildCategories == ALL_EXCEPT_TOP_LEVEL && !category.isRoot());
  }
}
