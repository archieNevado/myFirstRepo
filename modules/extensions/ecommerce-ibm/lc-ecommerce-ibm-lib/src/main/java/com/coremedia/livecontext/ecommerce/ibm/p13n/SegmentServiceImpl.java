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

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.SEGMENT;
import static com.coremedia.livecontext.ecommerce.ibm.common.IbmCommerceIdProvider.commerceId;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

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

  @NonNull
  @Override
  @SuppressWarnings("unchecked")
  public List<Segment> findAllSegments(@NonNull StoreContext storeContext) {
    UserContext userContext = UserContextHelper.getCurrentContext();
    Map<String, Object> segments = commerceCache.get(
            new SegmentsCacheKey(storeContext, userContext, segmentWrapperService, commerceCache));

    return createSegmentBeansFor(segments, storeContext);
  }

  @Nullable
  @Override
  @SuppressWarnings("unchecked")
  public Segment findSegmentById(@NonNull CommerceId id, @NonNull StoreContext storeContext) {
    UserContext userContext = UserContextHelper.getCurrentContext();
    Map<String, Object> segment = commerceCache.get(
            new SegmentCacheKey(id, storeContext, userContext, segmentWrapperService, commerceCache));

    return createSegmentBeanFor(segment, storeContext);
  }

  @NonNull
  @Override
  @SuppressWarnings("unchecked")
  public List<Segment> findSegmentsForCurrentUser(@NonNull StoreContext storeContext) {
    UserContext userContext = UserContextHelper.getCurrentContext();
    Map<String, Object> segments = commerceCache.get(
            new SegmentsByUserCacheKey(storeContext, userContext, segmentWrapperService, commerceCache));

    return createSegmentBeansFor(segments, storeContext);
  }

  protected Segment createSegmentBeanFor(Map<String, Object> segmentMap, StoreContext storeContext) {
    if (segmentMap == null) {
      return null;
    }

    String segmentId = DataMapHelper.findStringValue(segmentMap, "id").orElse(null);
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

    List<Map<String, Object>> memberGroups = DataMapHelper.findValue(segmentsMap, "MemberGroup", List.class)
            .orElseGet(Collections::emptyList);

    return memberGroups.stream()
            .map(memberGroup -> createSegmentBeanFor(memberGroup, storeContext))
            .collect(collectingAndThen(toList(), Collections::unmodifiableList));
  }

}
