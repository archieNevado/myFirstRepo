package com.coremedia.ecommerce.studio.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.service.previewurl.PreviewSettings;
import com.coremedia.service.previewurl.impl.PreviewUrlServiceConfigurationProperties;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

import static com.coremedia.service.previewurl.HeadlessPreviewProvider.CONFIG_KEY_PREVIEW_HOST;
import static com.coremedia.service.previewurl.UriTemplatePreviewProvider.CONFIG_KEY_URI_TEMPLATE;

@DefaultAnnotation(NonNull.class)
public class CommerceHeadlessPreviewProvider extends AbstractCommercePreviewProvider {

  private static final String PREVIEW_PATH = "/preview?commerceId={commerceId}&siteId={siteId}";
  public static final String COMMERCE_ID = "commerceId";
  public static final String SITE_ID = "siteId";

  @Nullable
  private UriTemplate defaultPreviewUriTemplate;

  public CommerceHeadlessPreviewProvider(PreviewUrlServiceConfigurationProperties previewUrlServiceConfigurationProperties) {
    String headlessHost = previewUrlServiceConfigurationProperties.getHeadlessPreviewHost();
    if (StringUtils.isEmpty(headlessHost)) {
      return;
    }
    if (!headlessHost.endsWith("/")) {
      headlessHost += "/";
    }
    setDefaultPreviewUriTemplate(new UriTemplate(headlessHost + PREVIEW_PATH));
  }

  @Nullable
  @Override
  public String getPreviewUrl(Object entity, PreviewSettings settings, Map<String, Object> parameters) {
    if (!(entity instanceof CommerceBean)) {
      return null;
    }
    CommerceBean commerceBean = (CommerceBean) entity;
    UriTemplate uriTemplate = getEffectiveUriTemplate(settings, defaultPreviewUriTemplate);
    parameters.put(COMMERCE_ID, CommerceIdFormatterHelper.format(commerceBean.getId()));
    parameters.put(SITE_ID, commerceBean.getContext().getSiteId());

    return (uriTemplate != null) ? uriTemplate.expand(parameters).toString() : null;
  }

  @Override
  public boolean validate(PreviewSettings settings) {
    return true;
  }

  protected UriTemplate getEffectiveUriTemplate(PreviewSettings settings, UriTemplate defaultUriTemplate) {
    String settingsHost = (String) settings.getConfigValues().get(CONFIG_KEY_PREVIEW_HOST);
    if (StringUtils.isEmpty(settingsHost)) {
      String settingUriTemplate = (String) settings.getConfigValues().get(CONFIG_KEY_URI_TEMPLATE);
      return (StringUtils.isNotEmpty(settingUriTemplate)) ? new UriTemplate(settingUriTemplate) : defaultUriTemplate;
    }
    if (!settingsHost.endsWith("/")) {
      settingsHost += "/";
    }
    return new UriTemplate(settingsHost + PREVIEW_PATH);
  }

  @Nullable
  public UriTemplate getDefaultPreviewUriTemplate() {
    return defaultPreviewUriTemplate;
  }

  public void setDefaultPreviewUriTemplate(@Nullable UriTemplate defaultPreviewUriTemplate) {
    this.defaultPreviewUriTemplate = defaultPreviewUriTemplate;
  }
}
