package com.coremedia.livecontext.ecommerce.ibm.p13n;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.livecontext.ecommerce.p13n.SegmentService;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.SEGMENT;
import static com.coremedia.livecontext.ecommerce.ibm.common.IbmCommerceIdProvider.commerceId;

public class SegmentServiceImpl implements SegmentService {

  private WcSegmentWrapperService segmentWrapperService;
  private CommerceBeanFactory commerceBeanFactory;
  private CommerceCache commerceCache;

  @Required
  public void setSegmentWrapperService(WcSegmentWrapperService segmentWrapperService) {
    this.segmentWrapperService = segmentWrapperService;
  }

  @Required
  public void setCommerceBeanFactory(CommerceBeanFactory commerceBeanFactory) {
    this.commerceBeanFactory = commerceBeanFactory;
  }

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  @Nonnull
  @Override
  @SuppressWarnings("unchecked")
  public List<Segment> findAllSegments(@Nonnull StoreContext storeContext) {
    UserContext userContext = UserContextHelper.getCurrentContext();
    Map<String, Object> segments = commerceCache.get(
            new SegmentsCacheKey(storeContext, userContext, segmentWrapperService, commerceCache));

    return createSegmentBeansFor(segments, storeContext);
  }

  @Nullable
  @Override
  @SuppressWarnings("unchecked")
  public Segment findSegmentById(@Nonnull CommerceId id, @Nonnull StoreContext storeContext) {
    UserContext userContext = UserContextHelper.getCurrentContext();
    Map<String, Object> segment = commerceCache.get(
            new SegmentCacheKey(id, storeContext, userContext, segmentWrapperService, commerceCache));

    return createSegmentBeanFor(segment, storeContext);
  }

  @Nonnull
  @Override
  @SuppressWarnings("unchecked")
  public List<Segment> findSegmentsForCurrentUser(@Nonnull StoreContext storeContext) {
    UserContext userContext = UserContextHelper.getCurrentContext();
    Map<String, Object> segments = commerceCache.get(
          new SegmentsByUserCacheKey(storeContext, userContext, segmentWrapperService, commerceCache));

    return createSegmentBeansFor(segments, storeContext);
  }

  protected Segment createSegmentBeanFor(Map<String, Object> segmentMap, StoreContext storeContext) {
    if (segmentMap == null) {
      return null;
    }

    String segmentId = DataMapHelper.getValueForKey(segmentMap, "id", String.class);
    CommerceId commerceId = commerceId(SEGMENT).withExternalId(segmentId).build();
    Segment segment = (Segment) commerceBeanFactory.createBeanFor(commerceId, storeContext);
    ((AbstractIbmCommerceBean) segment).setDelegate(segmentMap);
    return segment;
  }

  @SuppressWarnings("unchecked")
  protected List<Segment> createSegmentBeansFor(Map<String, Object> segmentsMap, StoreContext storeContext) {
    if (segmentsMap == null || segmentsMap.isEmpty()) {
      return Collections.emptyList();
    }

    List<Segment> result = new ArrayList<>(segmentsMap.size());
    List<Map<String, Object>> memberGroups = DataMapHelper.getValueForPath(segmentsMap, "MemberGroup", List.class);
    for (Map<String, Object> memberGroup : memberGroups) {
      result.add(createSegmentBeanFor(memberGroup, storeContext));
    }
    return Collections.unmodifiableList(result);
  }

  @Nonnull
  @Override
  public SegmentService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, SegmentService.class);
  }
}
