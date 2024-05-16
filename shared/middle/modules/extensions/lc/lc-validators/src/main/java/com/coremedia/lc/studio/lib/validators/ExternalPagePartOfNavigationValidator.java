package com.coremedia.lc.studio.lib.validators;

import com.coremedia.blueprint.base.rest.validators.ChannelIsPartOfNavigationValidator;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Validates if link list properties of the given content do reference themselves.
 */
public class ExternalPagePartOfNavigationValidator extends ChannelIsPartOfNavigationValidator {

  private final SitesService sitesService;

  public ExternalPagePartOfNavigationValidator(@NonNull ContentType type,
                                               boolean isValidatingSubtypes,
                                               SitesService sitesService) {
    super(type, isValidatingSubtypes);
    this.sitesService = sitesService;
  }

  @Override
  protected boolean isNotInNavigation(Content content) {
    Site site = sitesService.getContentSiteAspect(content).getSite();
    boolean notInNavigation = super.isNotInNavigation(content);
    // check if the preferred site exists for this content and if teh external page is in navigation of this site
    return (site == null) || notInNavigation && !content.equals(site.getSiteRootDocument());
  }
}
