package com.coremedia.livecontext.ecommerce.sfcc.beans;

import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.sfcc.catalog.CategoryCacheKey;
import com.coremedia.livecontext.ecommerce.sfcc.common.CommerceBeanUtils;
import com.coremedia.livecontext.ecommerce.sfcc.configuration.SfccConfigurationProperties;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CategoryDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.CategoriesResource;
import com.coremedia.xml.Markup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import javax.inject.Named;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Named("sfccCommerceBeanFactory:category")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CategoryImpl extends AbstractSfccCommerceBean implements Category {

  private CategoriesResource resource;

  public CategoryImpl(@NonNull SfccConfigurationProperties sfccConfigurationProperties) {
    super(sfccConfigurationProperties);
  }

  @Override
  public CategoryDocument getDelegate() {
    return (CategoryDocument) super.getDelegate();
  }

  @Override
  public void load() {
    CategoryCacheKey categoryCacheKey = new CategoryCacheKey(getId(), getContext(), resource, getCommerceCache());
    CategoryDocument delegate = getCommerceCache().get(categoryCacheKey);
    if (delegate == null) {
      throw new NotFoundException("Commerce object not found with id " + getId());
    }

    setDelegate(delegate);
  }

  @Override
  public String getName() {
    return getLocalizedValue(getDelegate().getName());
  }

  @Override
  public Markup getShortDescription() {
    return buildRichtextMarkup(getLocalizedValue(getDelegate().getDescription()));
  }

  @Override
  public Markup getLongDescription() {
    return buildRichtextMarkup(getLocalizedValue(getDelegate().getPageDescription()));
  }

  /**
   * Returns the absolute url of the thumbnail image.
   * Uses the default image url as fallback, when not explicit thumbnail is set.
   *
   * @return
   */
  @Override
  public String getThumbnailUrl() {
    if (this.isLightweight()){
      load();
    }
    return getDelegate().getThumbnail();
  }

  @Override
  public String getDefaultImageUrl() {
    if (this.isLightweight()){
      load();
    }
    return getDelegate().getImage();
  }

  @NonNull
  @Override
  public List<Category> getChildren() {
    if (this.isLightweight()){
      load();
    }
    return CommerceBeanUtils.createLightweightBeansFor(getCommerceBeanFactory(), getDelegate().getCategories(), getContext(), BaseCommerceBeanType.CATEGORY, Category.class);
  }

  @NonNull
  @Override
  public List<Product> getProducts() {
    return getCatalogService().findProductsByCategory(this);
  }

  @Nullable
  @Override
  public Category getParent() {
    String parentCategoryId = getDelegate().getParentCategoryId();
    if (StringUtils.isNotBlank(parentCategoryId)) {
      return getCatalogService().findCategoryById(
              getCommerceIdProvider().formatCategoryId(getCatalogAlias(), parentCategoryId),
              getContext()
      );
    }
    return null;
  }

  @Override
  public boolean isRoot() {
    return getId().getExternalId()
            .filter("root"::equalsIgnoreCase)
            .isPresent();
  }

  @NonNull
  @Override
  public List<Category> getBreadcrumb() {
    List<Category> result = new ArrayList<>();

    Category parent = getParent();
    if (parent != null) {
      result.addAll(parent.getBreadcrumb());
    }

    if (!isRoot()) {
      result.add(this);
    }

    return result;

  }

  @NonNull
  @Override
  public String getSeoSegment() {
    try {
      return URLEncoder.encode(getDelegate().getId(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return getExternalId();
    }
  }

  @Override
  public String getMetaDescription() {
    return getLocalizedValue(getDelegate().getPageDescription());
  }

  @Override
  public String getMetaKeywords() {
    return getLocalizedValue(getDelegate().getPageKeywords());
  }

  @Override
  public String getTitle() {
    return getLocalizedValue(getDelegate().getPageTitle());
  }

  @NonNull
  @Override
  public String getDisplayName() {
    String name = getName();
    return name != null ? name : getId().getExternalId().orElse("no name");
  }

  @Override
  public CatalogPicture getCatalogPicture() {
    return findAssetService()
            .map(assetService -> assetService.getCatalogPicture(getDefaultImageUrl(), getId()))
            .orElseGet(() -> new CatalogPicture("#", null));
  }

  @Override
  public Content getPicture() {
    List<Content> pictures = getPictures();
    return pictures != null && !pictures.isEmpty() ? pictures.get(0) : null;
  }

  @Override
  public List<Content> getPictures() {
    return findAssetService()
            .map(assetService -> assetService.findPictures(getReference()))
            .orElseGet(Collections::emptyList);
  }

  @Override
  public List<Content> getVisuals() {
    return findAssetService()
            .map(assetService -> assetService.findVisuals(getReference()))
            .orElseGet(Collections::emptyList);
  }

  @Override
  public List<Content> getDownloads() {
    return findAssetService()
            .map(assetService -> assetService.findDownloads(getReference()))
            .orElseGet(Collections::emptyList);
  }

  @Autowired
  public void setResource(CategoriesResource ocapiCategoriesResource) {
    this.resource = ocapiCategoriesResource;
  }
}
