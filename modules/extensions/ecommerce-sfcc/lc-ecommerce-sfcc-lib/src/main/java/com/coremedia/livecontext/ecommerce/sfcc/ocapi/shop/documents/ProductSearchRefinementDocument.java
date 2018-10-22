package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents;

import com.coremedia.livecontext.ecommerce.search.SearchFacet;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * Document representing a product search refinement attribute.
 */
public class ProductSearchRefinementDocument extends AbstractOCDocument implements SearchFacet{

  /**
   * The id of the search refinement attribute.
   * In the case of an attribute refinement, this is the attribute id.
   * Custom attributes are marked by the prefix "c_" (for example, "c_refinementColor").
   * In the case of a category refinement, the id must be "cgid".
   * In the case of a price refinement, the id must be "price".
   */
  @JsonProperty("attribute_id")
  private String attributeId;

  /**
   * The localized label of the refinement.
   */
  @JsonProperty("label")
  private String label;

  /**
   * The sorted array of search refinements.
   * This array can be empty.
   */
  @JsonProperty("values")
  private List<ProductSearchRefinementValueDocument> values;

  private List<SearchFacet> extendedValues;

  private int count;

  private boolean selected;

  private String query;

  private String url;

  public String getAttributeId() {
    return attributeId;
  }

  public void setAttributeId(String attributeId) {
    this.attributeId = attributeId;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @NonNull
  public List<SearchFacet> getValues() {
    if (extendedValues == null) {
      extendedValues = values.stream()
        .map(this::extendRefinementValue)
        .collect(collectingAndThen(toList(), Collections::unmodifiableList));
    }
    return extendedValues;
  }

  private ProductSearchRefinementValueDocument extendRefinementValue(ProductSearchRefinementValueDocument productSearchRefinementValueDocument) {
    productSearchRefinementValueDocument.setValue(attributeId + "=" + productSearchRefinementValueDocument.getValue());
    return productSearchRefinementValueDocument;
  }

  public void setValues(List<ProductSearchRefinementValueDocument> values) {
    this.values = values;
  }

  @Override
  public int getCount() {
    return count;
  }

  @Override
  public boolean isSelected() {
    return selected;
  }

  @NonNull
  @Override
  public String getQuery() {
    return query;
  }

  @Override
  public String getUrl() {
    return url;
  }

  @NonNull
  @Override
  public Map<String, Object> getExtendedData() {
    return customAttributes();
  }

  @NonNull
  @Override
  public List<SearchFacet> getChildFacets() {
    return values != null ? getValues() : Collections.emptyList();
  }
}
