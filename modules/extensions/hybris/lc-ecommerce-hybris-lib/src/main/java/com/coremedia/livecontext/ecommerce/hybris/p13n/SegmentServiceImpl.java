package com.coremedia.livecontext.ecommerce.hybris.p13n;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.common.AbstractCommerceService;
import com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.UserGroupDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.UserGroupRefDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.resources.CatalogResource;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.livecontext.ecommerce.p13n.SegmentService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;


public class SegmentServiceImpl extends AbstractCommerceService implements SegmentService{

  @Inject
  private CatalogResource resource;

  @Nonnull
  @Override
  public List<Segment> findAllSegments() throws CommerceException {
    List<UserGroupRefDocument> allUserGroupRefs = resource.getAllUserGroups(StoreContextHelper.getCurrentContext());
    List<UserGroupDocument> userGroupDocuments = new ArrayList<>();
   /* for (UserGroupRefDocument userGroupRef : allUserGroupRefs) {
      userGroupRef.get

    }*/
    return getCommerceBeanHelper().createBeansFor(allUserGroupRefs, Segment.class);
  }

  @Nullable
  @Override
  public Segment findSegmentById(@Nonnull String id) throws CommerceException {
    UserGroupDocument userGroup = resource.getUserGroup(id, StoreContextHelper.getCurrentContext());
    return getCommerceBeanHelper().createBeanFor(userGroup, Segment.class);
  }

  @Nonnull
  @Override
  public List<Segment> findSegmentsForCurrentUser() throws CommerceException {
    return Collections.emptyList();
  }

  @Nonnull
  @Override
  public SegmentService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, SegmentService.class);
  }
}
