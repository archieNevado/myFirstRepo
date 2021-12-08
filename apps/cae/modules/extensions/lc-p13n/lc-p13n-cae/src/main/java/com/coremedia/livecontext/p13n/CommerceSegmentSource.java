package com.coremedia.livecontext.p13n;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentUserContext;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.MapPropertyMaintainer;
import com.coremedia.personalization.context.collector.AbstractContextSource;
import edu.umd.cs.findbugs.annotations.NonNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper.format;
import static java.util.stream.Collectors.toList;

/**
 * A {@link com.coremedia.personalization.context.collector.ContextSource} that reads the commerce user id
 * from the current commerce user context and asks the commerce system for memberships in customer
 * segments. Such customer segments in which the user is a member will be provided in the context collection
 * to evaluate personalization rules based on commerce segments.
 */
public class CommerceSegmentSource extends AbstractContextSource {

  private static final String SEGMENT_ID_LIST_CONTEXT_KEY = "usersegments";

  private String contextName = "commerce";

  @SuppressWarnings("unused")
  public void setContextName(String contextName) {
    this.contextName = contextName;
  }

  @Override
  public void preHandle(HttpServletRequest request, HttpServletResponse response, ContextCollection contextCollection) {
    var storeContext = CurrentStoreContext.find(request).orElse(null);
    if (storeContext == null) {
      return;
    }

    var userContext = CurrentUserContext.find(request).orElse(null);
    if (userContext == null) {
      return;
    }

    var userSegments = storeContext.getUserSegments()
            .map(str -> Arrays.asList(str.split(",")))
            .orElse(Collections.emptyList());

    if (userSegments.isEmpty() && isEmpty(userContext)) {
      return;
    }

    var segmentIds = getSegmentIds(storeContext, userContext, userSegments);
    var segmentIdsJoinedStr = joinSegmentIds(segmentIds);

    MapPropertyMaintainer segmentContext = new MapPropertyMaintainer();
    segmentContext.setProperty(SEGMENT_ID_LIST_CONTEXT_KEY, segmentIdsJoinedStr);

    contextCollection.setContext(contextName, segmentContext);
  }

  private static boolean isEmpty(@NonNull UserContext userContext) {
    return userContext.getUserId() == null
            && userContext.getUserName() == null
            && userContext.getCookies().isEmpty();
  }

  @NonNull
  private static List<String> getSegmentIds(@NonNull StoreContext storeContext,
                                            @NonNull UserContext userContext,
                                            @NonNull List<String> userSegments) {
    var segmentIdList = userSegments;
    if (segmentIdList.isEmpty()) {
      segmentIdList = readSegmentIdListFromCommerceSystem(storeContext, userContext);
    }

    var commerceIdProvider = storeContext.getConnection().getIdProvider();

    return segmentIdList.stream()
            .map(segment -> format(commerceIdProvider.formatSegmentId(segment)))
            .collect(toList());
  }

  @NonNull
  private static List<String> readSegmentIdListFromCommerceSystem(@NonNull StoreContext storeContext,
                                                                  @NonNull UserContext userContext) {
    var commerceConnection = storeContext.getConnection();
    return commerceConnection.getSegmentService()
            .map(segmentService -> segmentService.findSegmentsForCurrentUser(storeContext, userContext))
            .map(CommerceSegmentSource::getExternalIds)
            .orElseGet(Collections::emptyList);
  }

  @NonNull
  private static List<String> getExternalIds(List<Segment> segments) {
    return segments.stream()
            .map(CommerceBean::getId)
            .map(CommerceId::getExternalId)
            .flatMap(Optional::stream)
            .collect(toList());
  }

  @NonNull
  private static String joinSegmentIds(@NonNull List<String> segmentIds) {
    // The following format (comma-seperated list of ids) demands that not a id can be part of another id (like
    // 1234 is part of 123456). This is guaranteed if all ids have the same length (as it is the case). If not,
    // the format of ids can be changed to a more robust one.
    StringBuilder builder = new StringBuilder();

    for (String segmentId : segmentIds) {
      builder.append(segmentId).append(",");
    }

    return builder.toString();
  }
}
