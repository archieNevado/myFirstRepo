package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.JSONRepresentation;

/**
 * Document representing a filter.
 * <p>
 * A filter contains a set of objects that define criteria used to select records.
 * A filter can contain one of the following:
 * <ul>
 * <li>term_filter - matches records where a field (or fields) exactly match some simple value (including null).</li>
 * <li>range_filter - matches records where a field value lies in a specified range.</li>
 * <li>query_filter - provides filtering based on a query.</li>
 * <li>bool_filter - provides filtering of records using a set of filters combined using a specified operator.</li>
 * </ul>
 */
public interface FilterDocument extends JSONRepresentation {

  String TERM_FILTER = "term_filter";
  String RANGE_FILTER = "range_filter";
  String QUERY_FILTER = "query_filter";
  String BOOL_FILTER = "bool_filter";

}
