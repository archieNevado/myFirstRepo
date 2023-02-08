package com.coremedia.lc.studio.lib.validators;

import com.coremedia.cap.common.CapException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import static java.lang.invoke.MethodHandles.lookup;

@DefaultAnnotation(NonNull.class)
public class MasterLinkInAugmentationValidator extends PossiblyMissingMasterReferenceValidator {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

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
    Content content = aspectOfValidatedContent.getContent();
    String commerceReference = detectCommerceReference(content);
    if (commerceReference != null) {
      return augmentationService.getContentByExternalId(commerceReference, otherSite);
    } else {
      LOG.warn("Augmenting content is missing the commerce reference '{}'", content);
    }
    return null;
  }

  @Nullable
  private static String detectCommerceReference(@NonNull Content content) {
    try {
      String commerceReference = content.getString(COMMERCE_REFERENCE_PROPERTY_NAME);
      if (StringUtils.hasText(commerceReference)) {
        return commerceReference;
      }
    } catch (CapException e) {
      LOG.warn("Cannot read the commerce reference of an augmenting content '{}'", content);
    }
    return null;
  }
}
