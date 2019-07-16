package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents;

import com.coremedia.livecontext.ecommerce.search.SearchFacet;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.LocalizedStringDeserializer;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.LocalizedProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Document representing a product search refinement value.
 */
public class ProductSearchRefinementValueDocument extends AbstractOCDocument implements SearchFacet {

  /**
   * The localized description of the refinement value.
   */
  @JsonProperty("description")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty<String> description;

  /**
   * The number of search hits when selecting the refinement value. Can be 0.
   */
  @JsonProperty("hit_count")
  private int hitCount;

  /**
   * The localized label of the refinement value.
   */
  @JsonProperty("label")
  private String label;

  /**
   * The optional presentation id associated with the refinement value.
   * The presentation id can be used, for example, to associate an id with an HTML widget.
   */
  @JsonProperty("presentation_id")
  private String presentationId;

  /**
   * The refinement value.
   * In the case of an attribute refinement, this is the bucket, the attribute value, or a value range.
   * In the case of a category refinement, this is the category id.
   * In the case of a price refinement,k this is the price range.
   * Ranges are enclosed by parentheses and separated by ".."; for example, "(100..999)" and "(Aa..Fa)" are valid ranges.
   */
  @JsonProperty("value")
  private String value;

  /**
   * The array of hierarchical refinement values.
   * This array can be empty.
   */
  @JsonProperty("values")
  private List<ProductSearchRefinementValueDocument> values;

  private boolean selected;

  private String url;



  public LocalizedProperty<String> getDescription() {
    return description;
  }

  public void setDescription(LocalizedProperty<String> description) {
    this.description = description;
  }

  public int getHitCount() {
    return hitCount;
  }

  public void setHitCount(int hitCount) {
    this.hitCount = hitCount;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getPresentationId() {
    return presentationId;
  }

  public void setPresentationId(String presentationId) {
    this.presentationId = presentationId;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public List<ProductSearchRefinementValueDocument> getValues() {
    return values;
  }

  public void setValues(List<ProductSearchRefinementValueDocument> values) {
    this.values = values;
  }

  @Override
  public int getCount() {
    return hitCount;
  }

  @Override
  public boolean isSelected() {
    return selected;
  }

  @NonNull
  @Override
  public String getQuery() {
    return value;
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
    return Collections.emptyList();
  }
}
