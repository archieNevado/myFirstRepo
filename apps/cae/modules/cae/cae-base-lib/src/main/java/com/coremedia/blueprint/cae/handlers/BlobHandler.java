package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.contentbeans.BlobFromContentBeanSetting;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.activation.MimeType;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_NUMBER;
import static com.coremedia.blueprint.links.BlueprintUriConstants.Prefixes.PREFIX_RESOURCE;
import static com.coremedia.objectserver.web.HandlerHelper.notFound;

@Link
@RequestMapping
@DefaultAnnotation(NonNull.class)
public class BlobHandler {
  private static final String PREFIX_BLOB = "data"; // "blob" is already reserved by CapBlobHandler
  private static final String EMPTY_ETAG = "-";
  private static final String SEGMENT_ETAG = "etag";
  private static final String SEGMENT_FILENAME = "filename";
  private static final String DEFAULT_EXTENSION = "raw";
  private static final String SEGMENT_NAME = "propertyName";
  private static final String SEGMENT_ID = "contentId";

  public static final String ATTRIBUTE_FILENAME = SEGMENT_FILENAME;

  // resource/data/{contentId}/{propertyName}/{eTag}/file.jpg
  private static final String BLOB_PATTERN = "/" + PREFIX_RESOURCE + "/" + PREFIX_BLOB +
          "/{" + SEGMENT_ID + ":" + PATTERN_NUMBER + "}" +
          "/{" + SEGMENT_NAME + "}" +
          "/{" + SEGMENT_ETAG + "}" +
          "/{" + SEGMENT_FILENAME + "}";

  private MimeTypeService mimeTypeService;
  private SettingsService settingsService;
  private ValidationService<ContentBean> validationService;

  public BlobHandler(ValidationService<ContentBean> validationService,
                     MimeTypeService mimeTypeService,
                     SettingsService settingsService) {
    this.validationService = validationService;
    this.mimeTypeService = mimeTypeService;
    this.settingsService = settingsService;
  }

  @GetMapping(value = BLOB_PATTERN)
  public ModelAndView handleRequest(@PathVariable(SEGMENT_ID) ContentBean contentBean,
                                    @PathVariable(SEGMENT_NAME) String name,
                                    @PathVariable(SEGMENT_ETAG) String eTag) {
    if (contentBean == null || !validationService.validate(contentBean)) {
      return notFound();
    }

    Optional<Object> optionalSettingValue = settingsService.getSetting(name, Object.class, contentBean.getContent());
    Optional<Blob> optionalBlob = optionalSettingValue.filter(o -> o instanceof Blob).map(o -> (Blob) o);
    if (optionalBlob.isPresent() && eTagMatches(optionalBlob.get(), eTag)) {
      return HandlerHelper.createModelWithView(optionalBlob.get(), null);
    } else {
      return notFound();
    }
  }

  private boolean eTagMatches(Blob blob, String eTag) {
    String blobETag = blob.getETag();
    return blobETag != null ? blobETag.equals(eTag) : EMPTY_ETAG.equals(eTag);
  }

  private String getExtension(Blob blob) {
    MimeType contentType = blob.getContentType();
    if (contentType == null) {
      return DEFAULT_EXTENSION;
    }
    String extension = mimeTypeService.getExtensionForMimeType(contentType.toString());
    if (extension == null) {
      return DEFAULT_EXTENSION;
    }
    return extension;
  }

  @Link(type = BlobFromContentBeanSetting.class, uri = BLOB_PATTERN)
  @SuppressWarnings("unused")
  public UriComponents buildBlobLink(BlobFromContentBeanSetting blob,
                                     HttpServletRequest request) {
    String id = String.valueOf(IdHelper.parseContentId(blob.getContentBean().getContent().getId()));
    Object oFilename = request.getAttribute(ATTRIBUTE_FILENAME);
    String filename = oFilename instanceof String ? (String) oFilename : "file." + getExtension(blob);
    return UriComponentsBuilder.newInstance()
            .pathSegment(PREFIX_RESOURCE, PREFIX_BLOB, id, blob.getSettingsName(), blob.getETag(), filename)
            .build();
  }
}
