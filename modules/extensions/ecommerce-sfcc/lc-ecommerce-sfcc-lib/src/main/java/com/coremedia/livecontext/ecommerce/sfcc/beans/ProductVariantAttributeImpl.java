package com.coremedia.livecontext.ecommerce.sfcc.beans;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.VariationAttributeDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.VariationAttributeValueDocument;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class ProductVariantAttributeImpl extends ProductAttributeImpl {
  private String variantValue;

  ProductVariantAttributeImpl(VariationAttributeDocument delegate, String value, boolean isDefining) {
    super(delegate, isDefining);
    variantValue = value;
  }

  @Override
  public List<Object> getValues() {
    List<VariationAttributeValueDocument> values = delegate.getValues();

    if (values == null || values.isEmpty()) {
      return Collections.emptyList();
    }

    List<Object> result = values.stream()
            .map(VariationAttributeValueDocument::getValue)
            .filter(value -> variantValue.equals(value))
            .collect(toList());

    return Collections.unmodifiableList(result);
  }

  @Override
  public Object getValue() {
    Object value = super.getValue();
    if (value instanceof String){
      //try to convert to human readable format
      return getDisplayValueForValue((String) value);
    }
    return value;
  }

  /**
   * Translate technical value into human readable value
   * e.g. Color: "0001TG250001" -> "Black"
   * @param technicalValue
   * @return human readable value or original technical as fallback
   */
  private String getDisplayValueForValue(@NonNull String technicalValue) {
    Optional<VariationAttributeValueDocument> valueDocument = getDelegate().getValues().stream()
            .filter(value -> technicalValue.equals(value.getValue()))
            .findFirst();

    if (valueDocument.isPresent()) {
      return valueDocument.get().getName().getDefaultValue();
    }
    return technicalValue;
  }
}
