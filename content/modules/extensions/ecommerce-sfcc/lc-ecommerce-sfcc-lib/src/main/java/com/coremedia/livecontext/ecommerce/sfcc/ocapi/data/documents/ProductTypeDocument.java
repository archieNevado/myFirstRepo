package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Document representing a product type.
 */
public class ProductTypeDocument extends AbstractOCDocument {

  /**
   * A flag indicating whether the product is a bundle.
   */
  @JsonProperty("bundle")
  private boolean bundle;

  /**
   * A flag indicating whether the product is bundled.
   */
  @JsonProperty("bundled")
  private boolean bundled;

  /**
   * A flag indicating whether the product is a standard item.
   */
  @JsonProperty("item")
  private boolean item;

  /**
   * A flag indicating whether the product is a master.
   */
  @JsonProperty("master")
  private boolean master;

  /**
   * A flag indicating whether the product is an option.
   */
  @JsonProperty("option")
  private boolean option;

  /**
   * A flag indicating whether the product is part of product set.
   */
  @JsonProperty("part_of_product_set")
  private boolean partOfProductSet;

  /**
   * A flag indicating whether the product is part of retail set.
   */
  @JsonProperty("part_of_retail_set")
  private boolean partOfRetailSet;

  /**
   * A flag indicating whether the product is a retail set.
   */
  @JsonProperty("retail_set")
  private boolean retailSet;

  /**
   * A flag indicating whether the product is a set.
   */
  @JsonProperty("set")
  private boolean set;

  /**
   * A flag indicating whether the product is a variant.
   */
  @JsonProperty("variant")
  private boolean variant;

  /**
   * A flag indicating whether the product is a variation group.
   */
  @JsonProperty("variation_group")
  private boolean variationGroup;


  public boolean isBundle() {
    return bundle;
  }

  public void setBundle(boolean bundle) {
    this.bundle = bundle;
  }

  public boolean isBundled() {
    return bundled;
  }

  public void setBundled(boolean bundled) {
    this.bundled = bundled;
  }

  public boolean isItem() {
    return item;
  }

  public void setItem(boolean item) {
    this.item = item;
  }

  public boolean isMaster() {
    return master;
  }

  public void setMaster(boolean master) {
    this.master = master;
  }

  public boolean isOption() {
    return option;
  }

  public void setOption(boolean option) {
    this.option = option;
  }

  public boolean isPartOfProductSet() {
    return partOfProductSet;
  }

  public void setPartOfProductSet(boolean partOfProductSet) {
    this.partOfProductSet = partOfProductSet;
  }

  public boolean isPartOfRetailSet() {
    return partOfRetailSet;
  }

  public void setPartOfRetailSet(boolean partOfRetailSet) {
    this.partOfRetailSet = partOfRetailSet;
  }

  public boolean isRetailSet() {
    return retailSet;
  }

  public void setRetailSet(boolean retailSet) {
    this.retailSet = retailSet;
  }

  public boolean isSet() {
    return set;
  }

  public void setSet(boolean set) {
    this.set = set;
  }

  public boolean isVariant() {
    return variant;
  }

  public void setVariant(boolean variant) {
    this.variant = variant;
  }

  public boolean isVariationGroup() {
    return variationGroup;
  }

  public void setVariationGroup(boolean variationGroup) {
    this.variationGroup = variationGroup;
  }
}
