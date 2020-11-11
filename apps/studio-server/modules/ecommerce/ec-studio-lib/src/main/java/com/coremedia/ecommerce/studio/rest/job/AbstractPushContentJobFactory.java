package com.coremedia.ecommerce.studio.rest.job;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.rest.cap.jobs.JobFactory;
import com.coremedia.rest.invalidations.SimpleInvalidationSource;
import com.coremedia.rest.linking.LinkResolver;
import com.coremedia.rest.linking.Linker;
import org.springframework.scheduling.TaskScheduler;

import javax.inject.Inject;

/**
 * @deprecated This class is part of the "push" implementation that is not supported by the
 * Commerce Hub architecture. It will be removed or changed in the future.
 */
@Deprecated
abstract class AbstractPushContentJobFactory implements JobFactory {

  @Inject
  LinkResolver linkResolver;

  @Inject
  CommerceConnectionSupplier commerceConnectionSupplier;

  @Inject
  SimpleInvalidationSource pushStateInvalidationSource;

  @Inject
  Linker linker;

  @Inject
  CommerceConnectionInitializer commerceConnectionInitializer;

  @Inject
  SitesService sitesService;

  @Inject
  TaskScheduler taskScheduler;

}
