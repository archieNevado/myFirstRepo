package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.LocalizedStringDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Category product assignment.
 */
public class CategoryProductAssignmentDocument extends AbstractOCDocument {

  /**
   * The id of the catalog.
   */
  @JsonProperty("catalog_id")
  private String catalogId;

  /**
   * The id of the category.
   */
  @JsonProperty("category_id")
  private String categoryId;

  /**
   * Link for convenience.
   */
  @JsonProperty("link")
  private String link;

  /**
   * Localized name of the owning catalog.
   */
  @JsonProperty("owning_catalog_name")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty owningCatalogName;

  /**
   * The position of product category assignment
   */
  @JsonProperty("position")
  private double position;

  /**
   * The id of the Product.
   */
  @JsonProperty("product_id")
  private String productId;

  /**
   * The localized name of the Product.
   */
  @JsonProperty("product_name")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty productName;

  /**
   * The assigned product information.
   */
  @JsonProperty("product")
  private ProductDocument product;

  public String getCatalogId() {
    return catalogId;
  }

  public void setCatalogId(String catalogId) {
    this.catalogId = catalogId;
  }

  public String getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public LocalizedProperty getOwningCatalogName() {
    return owningCatalogName;
  }

  public void setOwningCatalogName(LocalizedProperty owningCatalogName) {
    this.owningCatalogName = owningCatalogName;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public LocalizedProperty getProductName() {
    return productName;
  }

  public void setProductName(LocalizedProperty productName) {
    this.productName = productName;
  }

  public ProductDocument getProduct() {
    return product;
  }

  public void setProduct(ProductDocument product) {
    this.product = product;
  }

  public double getPosition() {
    return position;
  }

  public void setPosition(double position) {
    this.position = position;
  }
}
