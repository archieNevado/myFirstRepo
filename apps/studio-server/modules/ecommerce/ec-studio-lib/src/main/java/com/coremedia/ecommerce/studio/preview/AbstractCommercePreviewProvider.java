package com.coremedia.ecommerce.studio.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.service.previewurl.Preview;
import com.coremedia.service.previewurl.PreviewProvider;
import com.coremedia.service.previewurl.PreviewSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Base implementation for PreviewProviders of {@link CommerceBean} objects.
 */
public abstract class AbstractCommercePreviewProvider implements PreviewProvider {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  public static final String COMMERCE_ID = "commerceId";
  public static final String SITE_ID = "siteId";
  public static final String EXTERNAL_ID = "externalId";

  /**
   * Checks that 'entity' is of type {@link CommerceBean} and matches the settings' site restrictions.
   */
  @Override
  public boolean isPreviewAvailable(PreviewSettings previewSettings, Object entity) {
    return (entity instanceof CommerceBean) && checkSiteRestrictions(previewSettings, (CommerceBean)entity);
  }

  protected static boolean checkSiteRestrictions(PreviewSettings previewSettings, CommerceBean entity) {
    String siteIdFromCommerceBean = entity.getContext().getSiteId();
    // either the settings has no site or it must be the site of the entity
    return previewSettings.getSite()
            .filter(site -> !siteIdFromCommerceBean.equals(site.getId()))
            .isEmpty();
  }

  protected abstract String getPreviewUrl(CommerceBean entity, PreviewSettings settings, Map<String, Object> parameters);

  @Override
  public Optional<Preview> getPreview(Object entity, PreviewSettings previewSettings, Map<String, String> additionalUrlParams, boolean finalPreviewUrl) {
    if (!(entity instanceof CommerceBean)) {
      return Optional.empty();
    }

    CommerceBean commerceBean = (CommerceBean) entity;
    Map<String, Object> uriVariables = new HashMap<>(additionalUrlParams);
    uriVariables.put(COMMERCE_ID, CommerceIdFormatterHelper.format(commerceBean.getId()));
    uriVariables.put(SITE_ID, commerceBean.getContext().getSiteId());
    uriVariables.put(EXTERNAL_ID, commerceBean.getExternalId());

    String url = getPreviewUrl(commerceBean, previewSettings, uriVariables);
    if (url == null) {
      // note: theoretically the url should never be null. see contract of PreviewProvider
      LOG.warn("The preview url provider delivered by provider {} is null (broken contract of interface).", previewSettings.getProviderId());
      return Optional.empty();
    }

    return Optional.of(
            new Preview(
                    previewSettings.getId(),
                    previewSettings.getDisplayName(),
                    url,
                    false,
                    previewSettings.isUrlTransformationsDisabled()));
  }
}
