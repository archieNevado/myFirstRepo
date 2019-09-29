package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.LocalizedStringDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

/**
 * Document representing a catalog.
 */
public class CatalogDocument extends AbstractOCDocument {

  /**
   * The count of products assigned to the catalog.
   */
  @JsonProperty("assigned_product_count")
  private int assignedProductCount;

  /**
   * The sites assigned to the catalog.
   */
  @JsonProperty("assigned_sites")
  private List<SiteDocument> assignedSites;

  /**
   * The category count of catalog.
   */
  @JsonProperty("category_count")
  private int categoryCount;

  /**
   * The creation date of catalog.
   */
  @JsonProperty("creation_date")
  private String/*Date*/ creationDate;

  /**
   * The description of catalog.
   */
  @JsonProperty("description")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty<String> description;

  /**
   * URL that is used to get this instance.
   */
  @JsonProperty("link")
  private String link;

  /**
   * The catalog name.
   */
  @JsonProperty("name")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty name;

  /**
   * The online status of catalog.
   */
  @JsonProperty("online")
  private boolean online;

  /**
   * The count of products owned by the catalog.
   */
  @JsonProperty("owned_product_count")
  private int ownedProductCount;

  /**
   * The recommendation count of the catalog.
   */
  @JsonProperty("recommendation_count")
  private int recommendationCount;

  /**
   * The root category of catalog.
   */
  @JsonProperty("root_category")
  private String rootCategoryId;


  public int getAssignedProductCount() {
    return assignedProductCount;
  }

  public void setAssignedProductCount(int assignedProductCount) {
    this.assignedProductCount = assignedProductCount;
  }

  public List<SiteDocument> getAssignedSites() {
    return assignedSites;
  }

  public void setAssignedSites(List<SiteDocument> assignedSites) {
    this.assignedSites = assignedSites;
  }

  public int getCategoryCount() {
    return categoryCount;
  }

  public void setCategoryCount(int categoryCount) {
    this.categoryCount = categoryCount;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public LocalizedProperty<String> getDescription() {
    return description;
  }

  public void setDescription(LocalizedProperty<String> description) {
    this.description = description;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public LocalizedProperty getName() {
    return name;
  }

  public void setName(LocalizedProperty name) {
    this.name = name;
  }

  public boolean isOnline() {
    return online;
  }

  public void setOnline(boolean online) {
    this.online = online;
  }

  public int getOwnedProductCount() {
    return ownedProductCount;
  }

  public void setOwnedProductCount(int ownedProductCount) {
    this.ownedProductCount = ownedProductCount;
  }

  public int getRecommendationCount() {
    return recommendationCount;
  }

  public void setRecommendationCount(int recommendationCount) {
    this.recommendationCount = recommendationCount;
  }

  public String getRootCategoryId() {
    return rootCategoryId;
  }

  public void setRootCategoryId(String rootCategoryId) {
    this.rootCategoryId = rootCategoryId;
  }
}
