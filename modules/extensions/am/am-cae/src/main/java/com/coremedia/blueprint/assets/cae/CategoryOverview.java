package com.coremedia.blueprint.assets.cae;

import com.coremedia.blueprint.assets.contentbeans.AMTaxonomy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * This model provides an overview of the subcategories of the given category.
 */
public class CategoryOverview implements DownloadPortalContext {

  private AMTaxonomy category;
  private List<Subcategory> subcategories;

  /**
   * Creates an instance of the AMCategoryOverview
   * @param category the selected category or <code>null</code> for the root category
   * @param subcategories all subcategories of the selected category
   */
  public CategoryOverview(@Nullable AMTaxonomy category, @Nonnull List<Subcategory> subcategories) {
    this.category = category;
    this.subcategories = Collections.unmodifiableList(subcategories);
  }

  /**
   * Returns the selected category or <code>null</code> for the root category.
   * @return the selected category or <code>null</code> for the root category.
   */
  @Nullable
  public AMTaxonomy getCategory() {
    return category;
  }

  /**
   * Returns a list of all subcategories that contain assets.
   * @return a list of all subcategories that contain assets.
   */
  @Nonnull
  public List<Subcategory> getSubcategories() {
    return subcategories;
  }

  @Override
  public String getSearchTerm() {
    return "";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CategoryOverview)) {
      return false;
    }

    CategoryOverview that = (CategoryOverview) o;

    if (category != null ? !category.equals(that.category) : that.category != null) {
      return false;
    }
    return subcategories.equals(that.subcategories);
  }

  @Override
  public int hashCode() {
    int result = category != null ? category.hashCode() : 0;
    result = 31 * result + subcategories.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" +
            "category=" + category +
            ", subcategories=" + subcategories +
            '}';
  }
}
