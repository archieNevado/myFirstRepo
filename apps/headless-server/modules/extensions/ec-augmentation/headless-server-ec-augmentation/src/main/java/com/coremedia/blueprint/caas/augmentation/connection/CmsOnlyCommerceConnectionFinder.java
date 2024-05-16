package com.coremedia.blueprint.caas.augmentation.connection;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionFinder;
import com.coremedia.blueprint.caas.augmentation.CommerceSettingsHelper;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.lang.invoke.MethodHandles.lookup;

@DefaultAnnotation(NonNull.class)
public class CmsOnlyCommerceConnectionFinder implements CommerceConnectionFinder {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  private final CommerceSettingsHelper commerceSettingsHelper;

  public CmsOnlyCommerceConnectionFinder(CommerceSettingsHelper commerceSettingsHelper) {
    this.commerceSettingsHelper = commerceSettingsHelper;
  }

  @Override
  public Optional<CommerceConnection> findConnection(Site site) {
    LOG.warn("Setting up CmsOnly commerce connection for site '{}' because no other commerce connection could be established. " +
            "The CmsOnly commerce connection is not a fully fledged commerce connection and should only be used " +
            "for development purposes.", site.getId());
    return commerceSettingsHelper.findVendor(site)
            .map(vendor -> new CmsOnlyCommerceConnection(vendor, site, commerceSettingsHelper));
  }
}
