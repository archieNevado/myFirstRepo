package com.coremedia.lc.studio.lib.validators;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.ContentSiteAspect;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.rest.cap.validators.PossiblyMissingMasterReferenceValidator;
import com.coremedia.rest.validation.Severity;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

@DefaultAnnotation(NonNull.class)
public class MasterLinkInAugmentationValidator extends PossiblyMissingMasterReferenceValidator {

  private static final String ISSUE_CODE_FROM_MASTER_AUGMENTATION = "possibly_missing_master_reference_from_master_augmentation";
  private static final String ISSUE_CODE_FROM_DERIVED_AUGMENTATION = "possibly_missing_master_reference_from_derived_augmentation";

  private static final String COMMERCE_REFERENCE_PROPERTY_NAME = "externalId";

  private final AugmentationService augmentationService;

  public MasterLinkInAugmentationValidator(@NonNull ContentType type,
                                           @Nullable Boolean isValidatingSubtypes,
                                           @NonNull SitesService sitesService,
                                           @NonNull AugmentationService augmentationService,
                                           @NonNull Severity severity,
                                           long maxIssues) {
    super(type, isValidatingSubtypes, sitesService, severity, maxIssues);
    this.augmentationService = augmentationService;
  }

  @NonNull
  @Override
  protected String getIssueCode(@NonNull SiteRelation relation) {
    switch (relation) {
      case I_AM_MASTER_OTHER_IS_DERIVED:
        return ISSUE_CODE_FROM_MASTER_AUGMENTATION;
      case I_AM_DERIVED_OTHER_IS_MASTER:
        return ISSUE_CODE_FROM_DERIVED_AUGMENTATION;
      default:
        throw new UnsupportedOperationException("We don't support relation type yet: " + relation);
    }
  }

  @Nullable
  @Override
  protected Content getPossiblyRelatedInSite(@NonNull ContentSiteAspect aspectOfValidatedContent, @NonNull Site otherSite, @NonNull SiteRelation relation) {
    String commerceReference = aspectOfValidatedContent.getContent().getString(COMMERCE_REFERENCE_PROPERTY_NAME);
    return augmentationService.getContentByExternalId(commerceReference, otherSite);
  }

}
