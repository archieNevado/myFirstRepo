package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.BoolQueryDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.FilteredQueryDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.MatchAllQueryDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.Operator;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.QueryDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.TermQueryDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.TextQueryDocument;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Streams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.QueryDocument.BOOL_QUERY;
import static com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.QueryDocument.FILTERED_QUERY;
import static com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.QueryDocument.MATCH_ALL_QUERY;
import static com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.QueryDocument.TERM_QUERY;
import static com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.QueryDocument.TEXT_QUERY;
import static java.util.Collections.emptyList;

/**
 * Custom deserializer for {@link QueryDocument}s.
 */
public class QueryDocumentDeserializer extends JsonDeserializer<QueryDocument> {

  @Override
  public QueryDocument deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    JsonNode node = parser.readValueAsTree();

    if (node.has(BOOL_QUERY)) {
      return new BoolQueryDocument();
    } else if (node.has(FILTERED_QUERY)) {
      return new FilteredQueryDocument();
    } else if (node.has(MATCH_ALL_QUERY)) {
      return new MatchAllQueryDocument();
    } else if (node.has(TERM_QUERY)) {
      JsonNode termQueryNode = node.get(TERM_QUERY);

      TermQueryDocument termQuery = new TermQueryDocument();
      termQuery.setFields(deserializeStringArray(termQueryNode.get(TermQueryDocument.FIELDS)));
      termQuery.setOperator(Operator.valueOf(termQueryNode.get(TermQueryDocument.OPERATOR).textValue()));
      List<Object> values = new ArrayList<>();
      values.addAll(deserializeStringArray(termQueryNode.get(TermQueryDocument.VALUES)));
      termQuery.setValues(values);

      return termQuery;
    } else if (node.has(TEXT_QUERY)) {
      JsonNode textQueryNode = node.get(TEXT_QUERY);

      TextQueryDocument textQuery = new TextQueryDocument();
      textQuery.setSearchPhrase(textQueryNode.get(TextQueryDocument.SEARCH_PHRASE).textValue());
      JsonNode fieldsNode = textQueryNode.get(TextQueryDocument.FIELDS);
      textQuery.setFields(deserializeStringArray(fieldsNode));

      return textQuery;
    }

    return null;
  }

  private static List<String> deserializeStringArray(JsonNode node) {
    if (node == null || !node.isArray()) {
      return emptyList();
    }

    return Streams.stream(node)
            .map(JsonNode::textValue)
            .collect(Collectors.toList());
  }
}
