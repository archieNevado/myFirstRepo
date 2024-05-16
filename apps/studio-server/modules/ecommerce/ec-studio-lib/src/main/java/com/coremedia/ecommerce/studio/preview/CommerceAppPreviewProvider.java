package com.coremedia.ecommerce.studio.preview;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.service.previewurl.PreviewSettings;
import com.coremedia.service.previewurl.impl.UriTemplateUtils;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.service.previewurl.UriTemplatePreviewProvider.CONFIG_KEY_URI_TEMPLATE;

@DefaultAnnotation(NonNull.class)
public class CommerceAppPreviewProvider extends AbstractCommercePreviewProvider {

  static final String SEGMENT_PROPERTY = "segment";
  @Value("${environment.fqdn:localhost}")
  private String environmentFqdn = "localhost";

  private static final String URI_VAR_FQDN = "fqdn";
  private static final String ROOT_SEGMENT = "rootSegment";
  private static final String COMMERCE_BEAN_TYPE = "type";
  private final SitesService sitesService;

  public CommerceAppPreviewProvider(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  public String getPreviewUrl(CommerceBean entity, PreviewSettings settings, Map<String, Object> additionalUrlParams) {
    Map<String, Object> uriVars = new HashMap<>(additionalUrlParams);
    UriTemplate template = new UriTemplate((String) settings.getConfigValues().get(CONFIG_KEY_URI_TEMPLATE));
    Optional<Site> site = sitesService.findSite(entity.getContext().getSiteId());
    if(site.isPresent()) {
      Content siteRootDocument = site.get().getSiteRootDocument();
      if(siteRootDocument != null) {
        String segment = siteRootDocument.getString(SEGMENT_PROPERTY);
        if(StringUtils.isNotBlank(segment)) {
          uriVars.put(ROOT_SEGMENT, segment);
        }
      }
    }
    // Add parameters
    uriVars.put(URI_VAR_FQDN, environmentFqdn);
    uriVars.put(COMMERCE_BEAN_TYPE, entity.getId().getCommerceBeanType().value());
    return template.expand(uriVars).toString();
  }

  @Override
  public boolean validate(PreviewSettings settings) {
    return true;
  }

  @Override
  public List<String> getPreviewUrlAllowList(PreviewSettings settings) {
    UriTemplate template = new UriTemplate((String) settings.getConfigValues().get(CONFIG_KEY_URI_TEMPLATE));
    List<String> endorsedPreviewUrls = new ArrayList<>(settings.getPreviewUrlAllowList());
    Map<String, String> defaultUriVars = new HashMap<>();
    defaultUriVars.put(URI_VAR_FQDN, environmentFqdn);
    defaultUriVars.put(COMMERCE_ID, "");
    defaultUriVars.put(EXTERNAL_ID, "");
    defaultUriVars.put(SITE_ID, "");
    defaultUriVars.put(ROOT_SEGMENT, "");
    defaultUriVars.put(COMMERCE_BEAN_TYPE, "");
    URI expand = template.expand(defaultUriVars);
    Optional<String> extractedHost = UriTemplateUtils.extractHostFromUriTemplate(new UriTemplate(expand.toString()));
    extractedHost.ifPresent(endorsedPreviewUrls::add);

    return endorsedPreviewUrls;
  }

  @Override
  public List<String> getConnectSrcAllowList(PreviewSettings settings) {
    return getPreviewUrlAllowList(settings);
  }

  public void setEnvironmentFqdn(String environmentFqdn) {
    this.environmentFqdn = environmentFqdn;
  }
}
