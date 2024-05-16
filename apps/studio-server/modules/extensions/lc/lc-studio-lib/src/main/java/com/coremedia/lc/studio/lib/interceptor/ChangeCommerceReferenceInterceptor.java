package com.coremedia.lc.studio.lib.interceptor;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.cap.common.DuplicateNameException;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentException;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.content.NotAuthorizedException;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cotopaxi.content.ContentImpl;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.rest.cap.intercept.ContentWriteInterceptorBase;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper.parseCommerceId;
import static java.lang.invoke.MethodHandles.lookup;

/**
 * {@link com.coremedia.rest.cap.intercept.ContentWriteInterceptor}
 */
abstract class ChangeCommerceReferenceInterceptor extends ContentWriteInterceptorBase {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  @VisibleForTesting
  static final String EXTERNAL_ID = "externalId";

  private final CommerceConnectionSupplier commerceConnectionSupplier;
  private final AugmentationService augmentationService;
  private final ContentRepository contentRepository;
  private final SitesService sitesService;

  public ChangeCommerceReferenceInterceptor(ContentType contentType,
                                            CommerceConnectionSupplier commerceConnectionSupplier,
                                            AugmentationService augmentationService,
                                            SitesService sitesService,
                                            ContentRepository contentRepository) {
    this.commerceConnectionSupplier = commerceConnectionSupplier;
    this.augmentationService = augmentationService;
    this.sitesService = sitesService;
    this.contentRepository = contentRepository;
    setType(contentType);
  }

  private boolean isInterceptNeeded(ContentWriteRequest request) {
    Content modifiedContent = request.getEntity();
    if (modifiedContent == null) {
      return false;
    }
    Map<String, Object> properties = request.getProperties();
    if (!properties.containsKey(EXTERNAL_ID)) {
      return false;
    }
    var externalId = properties.get(EXTERNAL_ID);
    return externalId != null && !externalId.equals("");
  }

  private Optional<Content> getModifiedContentFromRequest(ContentWriteRequest request) {
    if (!isInterceptNeeded(request)) {
      return Optional.empty();
    }

    return Optional.of(request.getEntity());
  }

  protected boolean isModifiedContentAlreadyReferenced(@NonNull String contentId,
                                                       @NonNull Site site,
                                                       @NonNull String propertyValue,
                                                       @NonNull Issues issues) {
    var foundContent = this.augmentationService.getContentByExternalId(propertyValue, site);

    if (foundContent != null && !foundContent.getId().equals(contentId)) {
      issues.addIssue(Severity.ERROR, EXTERNAL_ID,
              String.format("The selected commerce reference is already linked to the content %d",
                      ((ContentImpl) foundContent).getNumericId()));
      return true;
    }

    return false;
  }

  private void renameAndMoveChangedAugmentedContent(@NonNull Content modifiedContent,
                                                    @NonNull Site site,
                                                    @NonNull CommerceId commerceId,
                                                    @NonNull CommerceConnection commerceConnection,
                                                    @NonNull Issues issues) {
    var commerceBean = getCommerceBeanById(commerceConnection, commerceId, issues);
    if (commerceBean == null) {
      return;
    }

    var newAugmentedName = computeNewAugmentedCommerceBeanName(commerceBean);
    var computedFolderPath = computeNewAugmentedDestinationPath(commerceBean, site);

    var resultPath = String.format("%s/%s", computedFolderPath, newAugmentedName);
    if (resultPath.equals(modifiedContent.getPath())) {
      return;
    }

    var destinationFolderContent = getMoveDestinationContentFolder(computedFolderPath, issues);
    if (destinationFolderContent == null) {
      return;
    }

    performRenameAndMoveOperation(modifiedContent, newAugmentedName, destinationFolderContent, issues);
  }

  @Nullable
  private CommerceConnection getCommerceConnection(@NonNull Content content, @NonNull Issues issues) {
    var commerceConnection = commerceConnectionSupplier.findConnection(content).orElse(null);
    if (commerceConnection == null) {
      issues.addIssue(Severity.ERROR, EXTERNAL_ID, "Commerce connection could not be found.");
      return null;
    }

    return commerceConnection;
  }

  @Nullable
  private CommerceId getCommerceId(@NonNull String externalId, Issues issues) {
    var optionalCommerceId = parseCommerceId(externalId).orElse(null);
    if (optionalCommerceId == null) {
      issues.addIssue(Severity.ERROR, EXTERNAL_ID, "Commerce information could not be retrieved.");
      return null;
    }
    return optionalCommerceId;
  }

  @Nullable
  private Content getMoveDestinationContentFolder(@NonNull String computeFolderPath,
                                                  @NonNull Issues issues) {
    var destinationFolderContent = this.contentRepository.createSubfolders(computeFolderPath);
    if (destinationFolderContent == null) {
      issues.addIssue(Severity.ERROR, EXTERNAL_ID, "Destination folder could not be determinate and/or create.");
      return null;
    }

    return destinationFolderContent;
  }

  private void performRenameAndMoveOperation(@NonNull Content modifiedContent,
                                             @NonNull String newAugmentedName,
                                             @NonNull Content destinationFolderContent,
                                             @NonNull Issues issues) {
    try {
      modifiedContent.rename(newAugmentedName);
      modifiedContent.moveTo(destinationFolderContent);
    } catch (ContentException | DuplicateNameException ex) {
      issues.addIssue(Severity.ERROR, EXTERNAL_ID, ex.getLocalizedMessage());
    }
  }

  @Nullable
  private Site getSite(@NonNull Content content) {
    try {
      return this.sitesService.getContentSiteAspect(content).getSite();
    } catch (NotAuthorizedException e) {
      LOG.debug("cannot determine site of content {}, ignoring exception", content, e);
    }

    return null;
  }

  @Nullable
  protected abstract CommerceBean getCommerceBeanById(@NonNull CommerceConnection commerceConnection,
                                                      @NonNull CommerceId commerceId,
                                                      @NonNull Issues issues);

  @NonNull
  protected abstract String computeNewAugmentedCommerceBeanName(@NonNull CommerceBean bean);

  @NonNull
  protected abstract String computeNewAugmentedDestinationPath(@NonNull CommerceBean commerceBean, @NonNull Site site);

  @Override
  public void intercept(ContentWriteRequest request) {
    Content modifiedContent = getModifiedContentFromRequest(request).orElse(null);
    if (modifiedContent == null) {
      return;
    }

    var propertyValue = request.getProperties().get(EXTERNAL_ID);

    var issues = request.getIssues();
    if (!(propertyValue instanceof String)) {
      issues.addIssue(Severity.ERROR, EXTERNAL_ID, "Operation cannot be fulfilled. Invalid value type.");
      return;
    }

    var externalId = (String) propertyValue;

    var site = getSite(modifiedContent);
    if (site == null) {
      issues.addIssue(Severity.ERROR, EXTERNAL_ID, "Content site could not be determinated.");
      return;
    }

    if (isModifiedContentAlreadyReferenced(modifiedContent.getId(), site, externalId, issues)) {
      return;
    }

    var commerceConnection = getCommerceConnection(modifiedContent, issues);
    if (commerceConnection == null) {
      return;
    }

    var commerceId = getCommerceId(externalId, issues);
    if (commerceId == null) {
      return;
    }

    renameAndMoveChangedAugmentedContent(modifiedContent, site, commerceId, commerceConnection, issues);
  }
}
