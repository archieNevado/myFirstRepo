package com.coremedia.ecommerce.studio.rest;

import com.coremedia.ecommerce.studio.rest.model.ChildRepresentation;
import com.coremedia.ecommerce.studio.rest.model.Store;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

/**
 * Catalog representation for JSON.
 */
public class CatalogRepresentation extends AbstractCatalogRepresentation {

  private String name;
  private boolean _default;
  private List<Category> topCategories;
  private Store store;
  private Category rootCategory;

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public List<Category> getTopCategories() {
    return topCategories;
  }

  public void setTopCategories(List<Category> topCategories) {
    this.topCategories = topCategories;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public List<ChildRepresentation> getChildrenData() {
    List<ChildRepresentation> result = new ArrayList<>();
    List<Category> subCategories = new ArrayList<>(topCategories);
    for (Category child : subCategories) {
      ChildRepresentation childRepresentation = new ChildRepresentation();
      childRepresentation.setChild(child);
      childRepresentation.setDisplayName(child.getDisplayName());
      result.add(childRepresentation);
    }
    return result;
  }

  public Store getStore() {
    return store;
  }

  public void setStore(Store store) {
    this.store = store;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setRootCategory(Category rootCategory) {
    this.rootCategory = rootCategory;
  }

  public Category getRootCategory() {
    return rootCategory;
  }

  public boolean isDefault() {
    return _default;
  }

  public void setDefault(boolean _default) {
    this._default = _default;
  }


}
