package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.JSONRepresentation;

/**
 * Document representing a query.
 *
 * A query contains a set of objects that define criteria used to select records.
 * A query can contain one of the following:
 * <ul>
 *   <li>match_all_query - returns all records.</li>
 *   <li>term_query - matches records where a field (or fields) exactly match some simple value (including null).</li>
 *   <li>text_query - matches records where a field (or fields) contain a search phrase.</li>
 *   <li>boolean_query - formulates a complex boolean expression using query objects as criteria.</li>
 *   <li>filtered_query - allows for filtering of records based on both a query and a filter.</li>
 * </ul>
 */
public interface QueryDocument extends JSONRepresentation {

  String MATCH_ALL_QUERY = "match_all_query";
  String TERM_QUERY = "term_query";
  String TEXT_QUERY = "text_query";
  String BOOL_QUERY = "bool_query";
  String FILTERED_QUERY = "filtered_query";

}
