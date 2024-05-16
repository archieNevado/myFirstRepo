package com.coremedia.ecommerce.studio.preview;

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
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@DefaultAnnotation(NonNull.class)
public class CommerceHeadlessPreviewProvider extends AbstractCommercePreviewProvider {

  private static final String PREVIEW_PATH = "/preview?commerceId={commerceId}&siteId={siteId}";

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
  public String getPreviewUrl(CommerceBean commerceBean, PreviewSettings settings, Map<String, Object> parameters) {
    UriTemplate uriTemplate = getEffectiveUriTemplate(settings, defaultPreviewUriTemplate);

    return (uriTemplate != null) ? uriTemplate.expand(parameters).toString() : null;
  }

  @Override
  public boolean validate(PreviewSettings settings) {
    return true;
  }

  protected static UriTemplate getEffectiveUriTemplate(PreviewSettings settings, UriTemplate defaultUriTemplate) {
    String settingsHost = (String) settings.getConfigValues().get(CONFIG_KEY_PREVIEW_HOST);
    if (StringUtils.isEmpty(settingsHost)) {
      String settingUriTemplate = (String) settings.getConfigValues().get(CONFIG_KEY_URI_TEMPLATE);
      return isNotEmpty(settingUriTemplate) ? new UriTemplate(settingUriTemplate) : defaultUriTemplate;
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
