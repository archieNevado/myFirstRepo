package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.LocalizedProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Iterator;

/**
 * Custom deserializer for localized strings.
 */
public class LocalizedStringDeserializer extends JsonDeserializer<LocalizedProperty<String>> {

  @Override
  public LocalizedProperty<String> deserialize(JsonParser parser, DeserializationContext context)
          throws IOException {
    LocalizedProperty<String> result = new LocalizedProperty<>();

    JsonNode node = parser.readValueAsTree();

    Iterator<String> fieldNames = node.fieldNames();
    while (fieldNames.hasNext()) {
      String fieldName = fieldNames.next();
      String fieldValue = node.get(fieldName).asText();
      result.addValue(fieldName, fieldValue);
    }

    return result;
  }
}
