package com.coremedia.livecontext.ecommerce.sfcc.beans;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.livecontext.ecommerce.sfcc.catalog.CustomerGroupCacheKey;
import com.coremedia.livecontext.ecommerce.sfcc.configuration.SfccConfigurationProperties;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CustomerGroupDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.CustomerGroupsResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import edu.umd.cs.findbugs.annotations.NonNull;
import javax.inject.Named;

@Named("sfccCommerceBeanFactory:segment")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SegmentImpl extends AbstractSfccCommerceBean implements Segment {


  private CustomerGroupsResource resource;

  public SegmentImpl(@NonNull SfccConfigurationProperties sfccConfigurationProperties) {
    super(sfccConfigurationProperties);
  }

  @Override
  public CustomerGroupDocument getDelegate() {
    return (CustomerGroupDocument) super.getDelegate();
  }

  @Override
  public void load() throws CommerceException {
    CustomerGroupCacheKey customerGroupCacheKey = new CustomerGroupCacheKey(getId(), getContext(), resource, getCommerceCache());
    CustomerGroupDocument delegate = getCommerceCache().get(customerGroupCacheKey);
    if (delegate == null) {
      throw new NotFoundException("Commerce object not found with id " + getId());
    }
    setDelegate(delegate);
  }

  @Override
  public String getName() {
    return getDelegate().getId();
  }

  @Override
  public String getDescription() {
    return getDelegate().getDescription();
  }

  @Autowired
  public void setResource(CustomerGroupsResource resource) {
    this.resource = resource;
  }
}
