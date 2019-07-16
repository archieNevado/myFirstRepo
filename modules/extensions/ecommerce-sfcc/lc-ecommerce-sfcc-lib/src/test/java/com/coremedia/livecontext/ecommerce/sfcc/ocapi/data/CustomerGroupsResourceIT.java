package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CustomerGroupDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CustomerGroupsDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.CustomerGroupsResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static org.assertj.core.api.Assertions.assertThat;

public class CustomerGroupsResourceIT extends DataApiResourceTestBase {

  @Autowired
  private CustomerGroupsResource resource;

  @Test
  public void testGetRootCategory() {
    if (useBetamaxTapes()) {
      return;
    }

    Optional<CustomerGroupsDocument> customerGroups = resource.getAllCustomerGroups(storeContext);
    assertThat(customerGroups).isPresent();
    assertThat(customerGroups).hasValueSatisfying(c -> assertThat(c.getData().size()).isGreaterThanOrEqualTo(4));
  }

  @Test
  public void testGetCategoryById() {
    if (useBetamaxTapes()) {
      return;
    }

    Optional<CustomerGroupDocument> customerGroup = resource.getCustomerGroupById("Everyone", storeContext);
    assertThat(customerGroup).isPresent();
    assertThat(customerGroup).hasValueSatisfying(c -> assertThat(c.getId()).isEqualTo("Everyone"));
  }
}
