package com.coremedia.blueprint.uitesting.lc;

import com.coremedia.cap.common.CapObjectDestroyedException;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.query.QueryService;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SiteDestroyedException;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructBuilder;
import com.coremedia.cap.struct.StructService;
import com.coremedia.cap.undoc.common.CapConnection;
import com.coremedia.uitesting.doctypes.CMPicture;
import com.coremedia.uitesting.uapi.helper.CapConnectionProvider;
import com.coremedia.uitesting.uapi.helper.ContentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.NonNull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Named
@Singleton
public class UapiConnectionUtils {

  private static final Logger LOG = LoggerFactory.getLogger(UapiConnectionUtils.class);

  private static final String ASSET_NAME_REFERENCES = "references";
  private static final String ASSET_NAME_COMMERCE = "commerce";

  @Inject
  private ContentUtils contentUtils;
  @Inject
  private CapConnectionProvider connectionProvider;
  @Inject
  private SitesService sitesService;

  @NonNull
  public Site getSite(@NonNull TestSiteConfiguration testSiteConfiguration) {
    final String siteName = testSiteConfiguration.getName();
    final Locale siteLocale = testSiteConfiguration.getLocale();
    Set<Site> sites = sitesService.getSites();
    for (Site site : sites) {
      try {
        if (site != null && siteName.equals(site.getName()) && siteLocale.equals(site.getLocale())) {
          return site;
        }
      } catch (CapObjectDestroyedException | SiteDestroyedException e) {
        LOG.debug("ignoring destroyed site '{}'", site.getId(), e);
      }
    }
    throw new IllegalStateException(format("unable to find site named %s for locale %s", siteName, siteLocale));
  }

  public Content getPictureDocument(TestSiteConfiguration testSiteConfiguration, String documentName) {
    Site site = getSite(testSiteConfiguration);
    Content siteRootFolder = site.getSiteRootFolder();
    QueryService queryService = getContentRepository().getQueryService();
    Object[] parameters = {documentName, siteRootFolder};
    Collection<Content> pictureDocuments = queryService.poseContentQuery("TYPE " + CMPicture.NAME + " : name = ?0 AND BELOW ?1 AND isInProduction", parameters);
    return pictureDocuments.isEmpty() ? null : (Content) pictureDocuments.toArray()[0];
  }

  private ContentRepository getContentRepository() {
    return connectionProvider.get().getContentRepository();
  }

  public Struct getStructWithProductReference(final String productId) {
      StructService structService = getContentRepository().getConnection().getStructService();

      Struct outerStruct = structService.emptyStruct();
      Struct commerceStruct = structService.emptyStruct();
      StructBuilder builder = outerStruct.builder().declareStruct(ASSET_NAME_COMMERCE, commerceStruct);
      builder.enter(ASSET_NAME_COMMERCE);
      builder.declareStrings(ASSET_NAME_REFERENCES, Integer.MAX_VALUE, Collections.singletonList(productId));

      return builder.build();
  }

  public Content createImageWithProductReference(Content parent, String documentNamePrefix, String productId) {
    try {
      Map<String, Object> properties = new HashMap<>();
      properties.put("data", contentUtils.getImage(600, 600, "image/png"));
      properties.put("localSettings", getStructWithProductReference(productId));
      Content content = contentUtils.createContent(CMPicture.NAME,
              generateRandomDocumentName(documentNamePrefix, CMPicture.NAME), parent, properties);
      return content;
    } catch (Exception e) {
      // AssertionError must not have a cause as constructor argument. #java6 #compat
      final AssertionError error = new AssertionError("test could not create image");
      e.initCause(e);
      throw error;
    }
  }

  private String generateRandomDocumentName(String prefix, final String documentType) {
    return format("%s_%s_%s", prefix, randomAlphabetic(5), documentType);
  }

  public long getLatestContentEventSequenceNumber() {
    return ((CapConnection) contentUtils.getContentRepository().getConnection()).getLatestContentEventSequenceNumber();
  }

}
