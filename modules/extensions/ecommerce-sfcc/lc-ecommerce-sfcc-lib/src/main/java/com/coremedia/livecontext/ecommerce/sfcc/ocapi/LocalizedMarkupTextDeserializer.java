package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.LocalizedProperty;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.MarkupTextDocument;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.IOException;
import java.util.Iterator;

/**
 * Custom deserializer for localized markup texts.
 */
public class LocalizedMarkupTextDeserializer extends JsonDeserializer<LocalizedProperty<MarkupTextDocument>> {

  @Override
  public LocalizedProperty<MarkupTextDocument> deserialize(JsonParser parser, DeserializationContext context)
          throws IOException {
    LocalizedProperty<MarkupTextDocument> result = new LocalizedProperty<>();

    JsonNode node = parser.readValueAsTree();
    Iterator<String> fieldNames = node.fieldNames();
    while (fieldNames.hasNext()) {
      String fieldName = fieldNames.next();
      result.addValue(fieldName, parseNode(node.get(fieldName)));
    }

    return result;
  }

  @NonNull
  private MarkupTextDocument parseNode(@NonNull JsonNode node) {
    MarkupTextDocument result = new MarkupTextDocument();

    try {
      result.setMarkup(node.get(MarkupTextDocument.MARKUP).asText());
      result.setSource(node.get(MarkupTextDocument.SOURCE).asText());
    } catch (Exception e) {
      // do nothing
    }

    return result;
  }
}
