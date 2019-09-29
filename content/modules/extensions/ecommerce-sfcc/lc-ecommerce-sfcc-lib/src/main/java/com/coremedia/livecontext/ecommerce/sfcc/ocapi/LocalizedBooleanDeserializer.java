package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.LocalizedProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Iterator;

/**
 * Custom deserializer for localized booleans.
 */
public class LocalizedBooleanDeserializer extends JsonDeserializer<LocalizedProperty<Boolean>> {

  @Override
  public LocalizedProperty<Boolean> deserialize(JsonParser parser, DeserializationContext context)
          throws IOException {
    LocalizedProperty<Boolean> result = new LocalizedProperty<>();

    JsonNode node = parser.readValueAsTree();

    Iterator<String> fieldNames = node.fieldNames();
    while (fieldNames.hasNext()) {
      String fieldName = fieldNames.next();
      Boolean fieldValue = node.get(fieldName).booleanValue();
      result.addValue(fieldName, fieldValue);
    }

    return result;
  }
}
