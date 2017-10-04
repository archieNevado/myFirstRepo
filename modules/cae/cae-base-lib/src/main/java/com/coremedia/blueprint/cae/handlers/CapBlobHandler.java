package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.coderesources.ThemeService;
import com.coremedia.blueprint.common.contentbeans.CMDownload;
import com.coremedia.blueprint.common.contentbeans.CMImage;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.cap.common.CapBlobRef;
import com.coremedia.cap.common.CapPropertyDescriptor;
import com.coremedia.cap.common.CapPropertyDescriptorType;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.transform.BlobHelper;
import com.coremedia.cap.user.User;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.web.UserVariantHelper;
import com.coremedia.objectserver.web.links.Link;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_EXTENSION;
import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_NUMBER;
import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_WORD;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ETAG;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_EXTENSION;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ID;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_NAME;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_PROPERTY;
import static com.coremedia.blueprint.links.BlueprintUriConstants.Prefixes.PREFIX_RESOURCE;
import static com.coremedia.objectserver.web.HandlerHelper.createModel;
import static com.coremedia.objectserver.web.HandlerHelper.notFound;
import static com.coremedia.objectserver.web.HandlerHelper.redirectTo;

/**
 * Handler and LinkScheme for
 * {@link com.coremedia.cap.common.CapBlobRef blobs}
 */
@Link
@RequestMapping
public class CapBlobHandler extends HandlerBase {

  private static final String FRAGMENT_PREVIEW = "fragmentPreview";
  private static final String CLASSIFIER_BLOB = "blob";
  private static final String CLASSIFIER_CODERESOURCEBLOB = "crblob";
  private static final String EMPTY_ETAG = "-";

  private static final String BLOB_URI_POSTFIX =
          "/{" + SEGMENT_ID + ":" + PATTERN_NUMBER + "}" +
          "/{" + SEGMENT_ETAG + "}" +
          "/{" + SEGMENT_NAME + "}" +
          "-{" + SEGMENT_PROPERTY + ":" + PATTERN_WORD + "}" +
          ".{" + SEGMENT_EXTENSION + ":" + PATTERN_EXTENSION + "}";

  //e.g. /resource/blob/126/4fb7741a1080d02953ac7d79c76c955c/media-favicon.ico
  public static final String URI_PATTERN = "/" + PREFIX_RESOURCE + "/" + CLASSIFIER_BLOB + BLOB_URI_POSTFIX;

  // Almost the same as URI_PATTERN, but the handler checks for a developer
  // variant and eventually replaces the bean. Useful for frontend development.
  // e.g. /resource/crblob/126/4fb7741a1080d02953ac7d79c76c955c/logo.svg
  public static final String CODERESOURCEBLOB_URI_PATTERN = "/" + PREFIX_RESOURCE + "/" + CLASSIFIER_CODERESOURCEBLOB + BLOB_URI_POSTFIX;

  private ValidationService<ContentBean> validationService;
  private ThemeService themeService;
  private ContentBeanFactory contentBeanFactory;


  // --- configure --------------------------------------------------

  @Required
  public void setValidationService(ValidationService<ContentBean> validationService) {
    this.validationService = validationService;
  }

  @Required
  public void setThemeService(ThemeService themeService) {
    this.themeService = themeService;
  }

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }


  // --- Handlers ------------------------------------------------------------------------------------------------------

  @RequestMapping(value = CODERESOURCEBLOB_URI_PATTERN)
  public ModelAndView handleCodeResourceBlobRequest(@PathVariable(SEGMENT_ID) ContentBean contentBean,
                                    @PathVariable(SEGMENT_ETAG) String eTag,
                                    @PathVariable(SEGMENT_PROPERTY) String propertyName,
                                    @PathVariable(SEGMENT_EXTENSION) String extension,
                                    WebRequest webRequest,
                                    HttpServletRequest request) {
    // Check for a developer variant first.
    // This can succeed on content management servers only.
    // Live servers do not support developer variants.
    User developer = UserVariantHelper.getUser(request);
    if (developer!=null) {
      CapBlobRef developerVariant = lookupDeveloperVariant(contentBean, propertyName, extension, developer);
      if (developerVariant != null) {
        // The eTag is meaningless in this case, since it refers to the
        // original blob.  Simply create a model.
        return createModel(developerVariant);
      }
    }

    // No developer variant found, return the standard result.
    // This is the standard case, esp. in production environments.
    return handleRequestInternal(contentBean, eTag, propertyName, extension, webRequest);
  }

  @RequestMapping(value = URI_PATTERN)
  public ModelAndView handleRequest(@PathVariable(SEGMENT_ID) ContentBean contentBean,
                                    @PathVariable(SEGMENT_ETAG) String eTag,
                                    @PathVariable(SEGMENT_PROPERTY) String propertyName,
                                    @PathVariable(SEGMENT_EXTENSION) String extension,
                                    WebRequest webRequest) {
    return handleRequestInternal(contentBean, eTag, propertyName, extension, webRequest);
  }


  // --- LinkSchemes ---------------------------------------------------------------------------------------------------

  @Link(type = CapBlobRef.class)
  public UriComponents buildLink(CapBlobRef bean) {
    String classifier = mayHaveDeveloperVariants(bean) ? CLASSIFIER_CODERESOURCEBLOB : CLASSIFIER_BLOB;
    String id = String.valueOf(IdHelper.parseContentId(bean.getCapObject().getId()));
    String etag = bean.getETag();
    if (etag==null) {
      etag = EMPTY_ETAG;
    }
    String name = getName(bean);
    String propertyName = bean.getPropertyName();
    String extension = getExtension(bean.getContentType(), BlobHelper.BLOB_DEFAULT_EXTENSION);
    String namepropext = name + "-" + propertyName + "." + extension;
    return UriComponentsBuilder.newInstance().pathSegment(PREFIX_RESOURCE, classifier, id, etag, namepropext).build();
  }

  @Link(type = CMDownload.class, uri = URI_PATTERN)
  @SuppressWarnings("unused")
  public String buildLinkForDownload(@Nonnull CMDownload download, @Nullable String viewName) {    
    if (FRAGMENT_PREVIEW.equals(viewName)) {
      // Do not build the download link for the fragment preview. Let other handlers build the link instead.
      return null;
    }
    CapBlobRef blob = (CapBlobRef) download.getData();
    return blob != null ? buildLink(blob).toUriString() : "#";
  }

  /**
   * Useful for custom link building.
   */
  public Map<String, String> linkParameters(CapBlobRef bean) {
    String etag = bean.getETag();
    return ImmutableMap.<String, String>builder()
            .put(SEGMENT_ID, String.valueOf(IdHelper.parseContentId(bean.getCapObject().getId())))
            .put(SEGMENT_ETAG, etag != null ? etag : EMPTY_ETAG)
            .put(SEGMENT_NAME, getName(bean))
            .put(SEGMENT_PROPERTY, bean.getPropertyName())
            .put(SEGMENT_EXTENSION, getExtension(bean.getContentType(), BlobHelper.BLOB_DEFAULT_EXTENSION))
            .build();
  }


  // === internal ======================================================================================================

  /**
   * Look for a developer variant of the blob.
   */
  private CapBlobRef lookupDeveloperVariant(ContentBean contentBean, String propertyName, String extension, User developer) {
    Content original = contentBean.getContent();
    Content developerVariant = themeService.developerVariant(original, developer);
    if (!original.equals(developerVariant)) {
      ContentBean developerVariantBean = contentBeanFactory.createBeanFor(developerVariant);
      return validateBlobRequest(developerVariantBean, propertyName, extension);
    }
    return null;
  }

  /**
   * Handle a standard blob request.
   */
  private ModelAndView handleRequestInternal(ContentBean contentBean, String eTag, String propertyName, String extension, WebRequest webRequest) {
    // URL validation: extension must be valid for this blob
    CapBlobRef blob = validateBlobRequest(contentBean, propertyName, extension);
    if (blob != null) {
      // URL validation: redirect to "correct" blob URL, if etag does not match.
      // The client may just have an old version of the URL.
      if (eTagMatches(blob, eTag)) {
        if (webRequest.checkNotModified(blob.getETag())) {
          // shortcut exit - no further processing necessary
          return null;
        }
        return createModel(blob);
      } else {
        return redirectTo(blob);
      }
    }
    return notFound();
  }

  private CapBlobRef validateBlobRequest(ContentBean contentBean, String propertyName, String extension) {
    boolean isValid = false;
    CapBlobRef blobRef = null;

    // check if the contentbean expired or something else
    if (contentBean != null && validationService.validate(contentBean)) {
      // the property has to be available for the bean
      Content content = contentBean.getContent();
      CapPropertyDescriptor propertyDescriptor = content.getType().getDescriptor(propertyName);
      if (propertyDescriptor != null && Objects.equals(propertyDescriptor.getType(), CapPropertyDescriptorType.BLOB)) {
        blobRef = contentBean.getContent().getBlobRef(propertyName);
      }

      // validate extension
      isValid = blobRef != null && BlobHelper.isValidExtension(extension, blobRef, getMimeTypeService());
    }

    return isValid ? blobRef : null;
  }

  private boolean eTagMatches(CapBlobRef blob, String eTag) {
    String blobETag = blob.getETag();
    return blobETag != null ? blobETag.equals(eTag) : EMPTY_ETAG.equals(eTag);
  }

  private String getName(CapBlobRef o) {
    if (BlobHelper.hasContentContainer(o)) {
      String contentName = ((Content) o.getCapObject()).getName();
      return removeSpecialCharacters(contentName);
    }
    return null;
  }

  /**
   * Checks whether the blob possibly has developer variants.
   * <p>
   * Currently only CMImage documents are used in themes.
   */
  private boolean mayHaveDeveloperVariants(CapBlobRef blobRef) {
    return blobRef.getCapObject().getType().isSubtypeOf(CMImage.NAME);
  }
}
