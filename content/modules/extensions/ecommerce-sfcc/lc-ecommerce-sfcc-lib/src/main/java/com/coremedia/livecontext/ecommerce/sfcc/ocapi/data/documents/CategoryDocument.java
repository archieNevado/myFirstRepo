package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.LocalizedStringDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Category document.
 */
public class CategoryDocument extends AbstractOCDocument {

  /**
   * The id of the catalog that contains it.
   */
  @JsonProperty("catalog_id")
  private String catalogId;

  /**
   * The list of sub categories for the category.
   */
  @JsonProperty("categories")
  private List<CategoryDocument> categories;

  /**
   * The localized description of the category.
   */
  @JsonProperty("description")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty<String> description;

  /**
   * The date when the category is created.
   * This is a computed attribute and cannot be modified.
   */
  @JsonProperty("creation_date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATA_API_DATE_PATTERN)
  private Date creationDate;

  /**
   * The name of the category image.
   * The URL to the image is computed.
   */
  @JsonProperty("image")
  private String image;

  /**
   * The URL to get the category.
   * This is a computed attribute and cannot be modified.
   */
  @JsonProperty("link")
  private String link;

  /**
   * The localized name of the category.
   */
  @JsonProperty("name")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty<String> name;

  /**
   * The online status of the category determines if it is visible in the storefront.
   * Defaults to <code>false</code> if not specified on create.
   */
  @JsonProperty("online")
  private boolean online;

  /**
   * The localized page description of the category.
   */
  @JsonProperty("page_description")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty<String> pageDescription;

  /**
   * The localized page keywords for the category.
   */
  @JsonProperty("page_keywords")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty<String> pageKeywords;

  /**
   * The localized page title of the category.
   */
  @JsonProperty("page_title")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty<String> pageTitle;

  /**
   * The id of the parent category.
   * Defaults to <code>root</code> if not specified on create.
   */
  @JsonProperty("parent_category_id")
  private String parentCategoryId;

  /**
   * The position of the category determines the display order in the storefront.
   */
  @JsonProperty("position")
  private double position;

  /**
   * The name of the category thumbnail.
   * The URL to the thumbnail is computed.
   */
  @JsonProperty("thumbnail")
  private String thumbnail;


  public String getCatalogId() {
    return catalogId;
  }

  public void setCatalogId(String catalogId) {
    this.catalogId = catalogId;
  }

  public List<CategoryDocument> getCategories() {
    return categories == null ? Collections.emptyList() : categories;
  }

  public void setCategories(List<CategoryDocument> categories) {
    this.categories = categories;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public LocalizedProperty<String> getName() {
    return name;
  }

  public void setName(LocalizedProperty<String> name) {
    this.name = name;
  }

  public boolean isOnline() {
    return online;
  }

  public void setOnline(boolean online) {
    this.online = online;
  }

  public LocalizedProperty<String> getDescription() {
    return description;
  }

  public void setDescription(LocalizedProperty<String> description) {
    this.description = description;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public LocalizedProperty<String> getPageDescription() {
    return pageDescription;
  }

  public void setPageDescription(LocalizedProperty<String> pageDescription) {
    this.pageDescription = pageDescription;
  }

  public LocalizedProperty<String> getPageKeywords() {
    return pageKeywords;
  }

  public void setPageKeywords(LocalizedProperty<String> pageKeywords) {
    this.pageKeywords = pageKeywords;
  }

  public LocalizedProperty<String> getPageTitle() {
    return pageTitle;
  }

  public void setPageTitle(LocalizedProperty<String> pageTitle) {
    this.pageTitle = pageTitle;
  }

  public String getParentCategoryId() {
    return parentCategoryId;
  }

  public void setParentCategoryId(String parentCategoryId) {
    this.parentCategoryId = parentCategoryId;
  }

  public double getPosition() {
    return position;
  }

  public void setPosition(double position) {
    this.position = position;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }
}
