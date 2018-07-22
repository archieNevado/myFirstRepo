package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CustomerGroupDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CustomerGroupsDocument;
import com.google.common.collect.ImmutableListMultimap;
import org.springframework.stereotype.Service;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * CustomerGroups Resource
 */
@Service("ocapiCustomerGroupsResource")
public class CustomerGroupsResource extends AbstractDataResource {
  //Action to get all the customer groups with no filtering.
  private static final String ALL_CUSTOMER_GROUPS = "/sites/{site_id}/customer_groups";

  //Action to get customer group information.
  private static final String CUSTOMER_GROUP_BY_ID = "/sites/{site_id}/customer_groups/{id}";

  public Optional<CustomerGroupDocument> getCustomerGroupById(@NonNull String customerId, @NonNull StoreContext storeContext) {
    Map<String, String> pathParameters = new HashMap<>();
    pathParameters.put("site_id", storeContext.getStoreId());
    pathParameters.put("id", customerId);
    return getConnector().getResource(CUSTOMER_GROUP_BY_ID, pathParameters, ImmutableListMultimap.of(), CustomerGroupDocument.class);
  }

  public Optional<CustomerGroupsDocument> getAllCustomerGroups(@NonNull StoreContext storeContext) {
    Map<String, String> pathParameters = new HashMap<>();
    pathParameters.put("site_id", storeContext.getStoreId());
    return getConnector().getResource(ALL_CUSTOMER_GROUPS, pathParameters, ImmutableListMultimap.of(), CustomerGroupsDocument.class);
  }
}
