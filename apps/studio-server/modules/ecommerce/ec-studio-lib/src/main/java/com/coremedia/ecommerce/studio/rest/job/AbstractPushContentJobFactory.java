package com.coremedia.ecommerce.studio.rest.job;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.rest.cap.jobs.JobFactory;
import com.coremedia.rest.invalidations.SimpleInvalidationSource;
import com.coremedia.rest.linking.LinkResolver;
import com.coremedia.rest.linking.Linker;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.inject.Inject;

/**
 * @deprecated This class is part of the "push" implementation that is not supported by the
 * Commerce Hub architecture. It will be removed or changed in the future.
 */
@Deprecated
abstract class AbstractPushContentJobFactory implements JobFactory, InitializingBean, DisposableBean {

  private boolean localTaskScheduler;

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

  @Autowired(required = false)
  TaskScheduler taskScheduler;

  @Override
  public void afterPropertiesSet() {
    if (taskScheduler == null) {
      localTaskScheduler = true;
      var scheduler = new ThreadPoolTaskScheduler();
      scheduler.setBeanName("content-push");
      scheduler.initialize();
      taskScheduler = scheduler;
    }
  }

  @Override
  public void destroy() {
    if (localTaskScheduler) {
      ((ThreadPoolTaskScheduler)taskScheduler).destroy();
    }
  }
}
