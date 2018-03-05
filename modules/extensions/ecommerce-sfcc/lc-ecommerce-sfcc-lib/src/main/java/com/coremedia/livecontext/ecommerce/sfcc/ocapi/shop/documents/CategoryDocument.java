package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Document representing a category.
 */
public class CategoryDocument extends AbstractOCDocument {

  /**
   * Array of subcategories.
   * Can be empty.
   */
  @JsonProperty("categories")
  private List<CategoryDocument> categories;

  /**
   * The localized description of the category.
   */
  @JsonProperty("description")
  private String description;

  /**
   * The URL to the category image.
   */
  @JsonProperty("image")
  private String imageUrl;

  /**
   * The localized name of the category.
   */
  @JsonProperty("name")
  private String name;

  /**
   * The localized page description of the category.
   */
  @JsonProperty("page_description")
  private String pageDescription;

  /**
   * The localized page keywords of the category.
   */
  @JsonProperty("page_keywords")
  private String pageKeywords;

  /**
   * The localized page title of the category.
   */
  @JsonProperty("page_title")
  private String pageTitle;

  /**
   * The id of the parent category.
   */
  @JsonProperty("parent_category_id")
  private String parentCategoryId;

  /**
   * The URL to the category thumbnail.
   */
  @JsonProperty("thumbnail")
  private String thumbnailUrl;

  public List<CategoryDocument> getCategories() {
    return categories;
  }

  public void setCategories(List<CategoryDocument> categories) {
    this.categories = categories;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPageDescription() {
    return pageDescription;
  }

  public void setPageDescription(String pageDescription) {
    this.pageDescription = pageDescription;
  }

  public String getPageKeywords() {
    return pageKeywords;
  }

  public void setPageKeywords(String pageKeywords) {
    this.pageKeywords = pageKeywords;
  }

  public String getPageTitle() {
    return pageTitle;
  }

  public void setPageTitle(String pageTitle) {
    this.pageTitle = pageTitle;
  }

  public String getParentCategoryId() {
    return parentCategoryId;
  }

  public void setParentCategoryId(String parentCategoryId) {
    this.parentCategoryId = parentCategoryId;
  }

  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  public void setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
  }
}
