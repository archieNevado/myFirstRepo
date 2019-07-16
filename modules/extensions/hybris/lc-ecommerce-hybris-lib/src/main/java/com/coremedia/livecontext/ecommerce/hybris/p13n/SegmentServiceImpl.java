package com.coremedia.livecontext.ecommerce.hybris.p13n;

import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.common.AbstractHybrisService;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.UserGroupDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.UserGroupRefDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.resources.CatalogResource;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.livecontext.ecommerce.p13n.SegmentService;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.SEGMENT;
import static java.util.Collections.emptyList;

public class SegmentServiceImpl extends AbstractHybrisService implements SegmentService {

  @Inject
  private CatalogResource resource;

  @NonNull
  @Override
  public List<Segment> findAllSegments(@NonNull StoreContext storeContext) {
    List<UserGroupRefDocument> allUserGroupRefs = resource.getAllUserGroups(storeContext);

    if (allUserGroupRefs == null) {
      return emptyList();
    }

    return createBeansFor(allUserGroupRefs, storeContext, SEGMENT, Segment.class);
  }

  @Nullable
  @Override
  public Segment findSegmentById(@NonNull CommerceId commerceId, @NonNull StoreContext storeContext) {
    Optional<String> externalId = commerceId.getExternalId();
    if (!externalId.isPresent()) {
      return null;
    }

    UserGroupDocument userGroup = resource.getUserGroup(externalId.get(), storeContext);

    if (userGroup == null) {
      return null;
    }

    return createBeanFor(userGroup, storeContext, SEGMENT, Segment.class);
  }

  @NonNull
  @Override
  public List<Segment> findSegmentsForCurrentUser(@NonNull StoreContext storeContext) {
    return emptyList();
  }

}
