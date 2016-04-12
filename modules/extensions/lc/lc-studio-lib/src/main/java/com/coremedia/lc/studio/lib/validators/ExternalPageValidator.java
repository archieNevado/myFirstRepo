package com.coremedia.lc.studio.lib.validators;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.rest.validation.Issues;
import org.springframework.beans.factory.annotation.Required;

import static com.coremedia.rest.validation.Severity.ERROR;

public class ExternalPageValidator extends CatalogLinkValidator {

  private static final String CODE_ISSUE_EXTERNAL_PAGE_ID_EMPTY = "EmptyExternalPageId";

  private SitesService sitesService;

  @Override
  protected void emptyPropertyValue(Content content, Issues issues) {
    Site site = getSite(content);
    // the property externalId can be empty for the site root page and the catalog root
    if (null != site) {
      if (!content.equals(site.getSiteRootDocument())) {
        addIssue(issues, ERROR, CODE_ISSUE_EXTERNAL_PAGE_ID_EMPTY);
      }
    }
  }

  @Override
  protected void invalidExternalId(Issues issues, Object... arguments) {
    // validation for external id not applicable for external pages
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  protected Site getSite(Content content) {
    return sitesService.getContentSiteAspect(content).getSite();
  }

}
