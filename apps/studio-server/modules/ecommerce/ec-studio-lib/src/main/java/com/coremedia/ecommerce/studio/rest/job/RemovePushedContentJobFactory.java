package com.coremedia.ecommerce.studio.rest.job;

import com.coremedia.rest.cap.jobs.Job;
import edu.umd.cs.findbugs.annotations.NonNull;

import javax.inject.Named;

/**
 * @deprecated This class is part of the "push" implementation that is not supported by the
 * Commerce Hub architecture. It will be removed or changed in the future.
 */
@Named
@Deprecated
public class RemovePushedContentJobFactory extends AbstractPushContentJobFactory {

  public boolean accepts(@NonNull String jobType) {
    return "removePushedContent".equals(jobType);
  }

  @NonNull
  @Override
  public Job createJob() {
    return new RemovePushedContentJob(linkResolver, commerceConnectionSupplier, pushStateInvalidationSource, linker, commerceConnectionInitializer, sitesService, taskScheduler);
  }
}
