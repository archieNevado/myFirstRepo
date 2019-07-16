package com.coremedia.livecontext.ecommerce.sfcc.p13n;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.livecontext.ecommerce.p13n.SegmentService;
import com.coremedia.livecontext.ecommerce.sfcc.catalog.CustomerGroupCacheKey;
import com.coremedia.livecontext.ecommerce.sfcc.catalog.CustomerGroupsCacheKey;
import com.coremedia.livecontext.ecommerce.sfcc.common.CommerceBeanUtils;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CustomerGroupDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CustomerGroupsDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.CustomerGroupsResource;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SegmentServiceImpl implements SegmentService {

  private final CustomerGroupsResource resource;
  private final CommerceBeanFactory commerceBeanFactory;
  private final CommerceCache commerceCache;


  public SegmentServiceImpl(CustomerGroupsResource resource, CommerceBeanFactory commerceBeanFactory, CommerceCache commerceCache) {
    this.resource = resource;
    this.commerceBeanFactory = commerceBeanFactory;
    this.commerceCache = commerceCache;
  }

  @NonNull
  @Override
  public List<Segment> findAllSegments(@NonNull StoreContext storeContext) throws CommerceException {
    CustomerGroupsCacheKey customerGroupsCacheKey = new CustomerGroupsCacheKey(storeContext, resource, commerceCache);
    CustomerGroupsDocument delegate = commerceCache.get(customerGroupsCacheKey);
    if (delegate == null || delegate.getData().isEmpty()) {
      return Collections.emptyList();
    }
    return CommerceBeanUtils.createLightweightBeansFor(commerceBeanFactory, delegate.getData(), storeContext, BaseCommerceBeanType.SEGMENT, Segment.class);
  }

  @Nullable
  @Override
  public Segment findSegmentById(@NonNull CommerceId commerceId, @NonNull StoreContext storeContext) throws CommerceException {
    Optional<String> externalId = commerceId.getExternalId();
    if (!externalId.isPresent()) {
      return null;
    }
    CustomerGroupCacheKey customerGroupCacheKey = new CustomerGroupCacheKey(commerceId, storeContext, resource, commerceCache);
    CustomerGroupDocument delegate = commerceCache.get(customerGroupCacheKey);
    if (delegate == null) {
      return null;
    }
    return CommerceBeanUtils.createBeanFor(commerceBeanFactory, delegate, storeContext, BaseCommerceBeanType.SEGMENT, Segment.class);
  }

  @NonNull
  @Override
  public List<Segment> findSegmentsForCurrentUser(@NonNull StoreContext storeContext) throws CommerceException {
    return Collections.emptyList();
  }
}
