package com.coremedia.livecontext.ecommerce.hybris.beans;

import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.hybris.cache.CategoryCacheKey;
import com.coremedia.livecontext.ecommerce.hybris.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CategoryDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CategoryRefDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.MediaDocument;
import com.coremedia.xml.Markup;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CategoryImpl extends AbstractHybrisCommerceBean implements Category {

  private List<Category> children;
  private List<Product> products;

  @Override
  public void load() throws CommerceException {
    if (isRoot()) {
      CategoryDocument delegate = new CategoryDocument();
      delegate.setCode(CommerceIdHelper.ROOT_CATEGORY_ID);
      setDelegate(delegate);
    } else {
      CategoryCacheKey cacheKey = new CategoryCacheKey(getId(), getContext(), getCatalogResource(), getCommerceCache());
      loadCached(cacheKey);
    }
  }

  @Override
  public CategoryDocument getDelegate() {
    return (CategoryDocument) super.getDelegate();
  }

  @Override
  public String getName() {
    String name = getDelegate().getName();
    return (name != null) ? name : getExternalId();
  }

  @Override
  public Markup getShortDescription() {
    return getLongDescription(); // Hybris Categories only have one description field, use the same value for both
  }

  @Override
  public Markup getLongDescription() {
    String description = getDelegate().getDescription();
    return buildRichtextMarkup(description);
  }

  @Override
  public String getThumbnailUrl() {
    List<Content> pictures = getAssetService().findPictures(getId());

    if (!pictures.isEmpty()) {
      return getAssetUrlProvider().getImageUrl("/catalogimage/category/" +
              StoreContextHelper.getStoreId() + "/" +
              StoreContextHelper.getLocale() + "/thumbnail/" + getExternalId() + ".jpg");
    }

    MediaDocument doc = getDelegate().getThumbnail();
    return doc != null ? getAssetUrlProvider().getImageUrl(doc.getDownloadUrl()) : getDefaultImageUrl();
  }

  @Override
  public String getDefaultImageUrl() {
    List<Content> pictures = getAssetService().findPictures(getId());

    if (!pictures.isEmpty()) {
      return getAssetUrlProvider().getImageUrl("/catalogimage/category/" +
              StoreContextHelper.getStoreId() + "/" +
              StoreContextHelper.getLocale() + "/full/" + getExternalId() + ".jpg");
    }

    MediaDocument doc = getDelegate().getPicture();
    return doc != null ? getAssetUrlProvider().getImageUrl(doc.getDownloadUrl()) : null;
  }

  @Nonnull
  @Override
  public List<Category> getChildren() throws CommerceException {
    if (children == null) {
      if (isRoot()) {
        return getCatalogService().findTopCategories(null);
      }

      List<Category> childrenNew = new ArrayList<>();

      List<CategoryRefDocument> refDocuments = getDelegate().getSubCategories();
      if (refDocuments != null) {
        for (CategoryRefDocument refDocument : refDocuments) {
          // lazy loading of subcategories
          childrenNew.add(getCommerceBeanHelper().createBeanFor(refDocument.getCode(), Category.class));
        }
      }

      children = childrenNew;
    }
    return children;
  }

  @Nonnull
  @Override
  public List<Product> getProducts() throws CommerceException {
    if (products == null) {
      products = getCatalogService().findProductsByCategory(this);
    }

    return products;
  }

  @Nullable
  @Override
  public Category getParent() throws CommerceException {
    if (isRoot()) {
      return null;
    }

    String parentId = getDelegate().getParentId();
    CatalogService catalogService = getCatalogService();

    if (StringUtils.isBlank(parentId)) {
      return catalogService.findRootCategory();
    }

    return catalogService.findCategoryById(parentId);
  }

  @Nonnull
  @Override
  public List<Category> getBreadcrumb() throws CommerceException {
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

  @Nullable
  @Override
  public String getSeoSegment() {
    return null;
  }

  @Override
  public String getMetaDescription() {
    return null;
  }

  @Override
  public String getMetaKeywords() {
    return null;
  }

  @Override
  public String getTitle() {
    return getName();
  }

  @Nonnull
  @Override
  public String getDisplayName() {
    return getName();
  }

  @Override
  public CatalogPicture getCatalogPicture() {
    return null;
  }

  @Override
  public Content getPicture() {
    return null;
  }

  @Override
  public List<Content> getPictures() {
    return null;
  }

  @Override
  public List<Content> getVisuals() {
    return null;
  }

  @Override
  public List<Content> getDownloads() {
    return null;
  }

  @Override
  public boolean isRoot() {
    return CommerceIdHelper.isRootCategoryId(getId());
  }

  @Override
  public String getReference() {
    return CommerceIdHelper.formatCategoryId(getExternalId());
  }
}
