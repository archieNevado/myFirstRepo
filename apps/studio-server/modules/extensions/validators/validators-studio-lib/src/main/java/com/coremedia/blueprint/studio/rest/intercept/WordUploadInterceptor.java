package com.coremedia.blueprint.studio.rest.intercept;

import com.coremedia.blueprint.studio.rest.intercept.word.DocContentHandler;
import com.coremedia.blueprint.studio.rest.intercept.word.DocTitleHandler;
import com.coremedia.blueprint.taxonomies.Taxonomy;
import com.coremedia.blueprint.taxonomies.TaxonomyNode;
import com.coremedia.blueprint.taxonomies.TaxonomyResolver;
import com.coremedia.blueprint.taxonomies.TaxonomyUtil;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.BlobService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.content.search.SearchResult;
import com.coremedia.cap.multisite.ContentObjectSiteAspect;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.rest.cap.intercept.ContentWriteInterceptor;
import com.coremedia.rest.cap.intercept.ContentWriteInterceptorBase;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import com.coremedia.rest.cap.intercept.impl.ContentWriteRequestImpl;
import com.coremedia.rest.validation.impl.IssuesImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Office;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.XHTMLContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.activation.MimeType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This is a prototype implementation of a MS Word to CMArticle converter, implemented as
 * ContentWriteInterceptor to be executed during bulk uploads.
 * <p>
 * Be aware that this kind of conversion will never be a 100 percent accurate: it's most likely
 * that the original Word document may have some special characters or formats that are not properly
 * converted to CoreMedia richtext.
 * <p>
 * If you want to tweak these conversions in detail, you have to modify the DocContentHandler class for this.
 */
public class WordUploadInterceptor extends ContentWriteInterceptorBase {
  private static final Logger LOG = LoggerFactory.getLogger(WordUploadInterceptor.class);

  private static final String DOC_MIMETYPE = "application/msword";
  static final String DOCX_MIMETYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
  private static final String OFFICE_OPEN_XML_MIMETYPE = "application/x-tika-ooxml";
  private static final List<String> INVALID_IMAGE_MIME_TYPES = Arrays.asList("bmp", "image/x-emf");

  static final String DATA_PROPERTY = "data";
  private static final String PICTURES = "pictures";
  private static final String SUBJECT_TAXONOMY = "subjectTaxonomy";
  private static final String SUBJECT_TAXONOMY_ID = "Subject";
  private static final String LOCATION_TAXONOMY = "locationTaxonomy";
  private static final String LOCATION_TAXONOMY_ID = "Location";
  private static final String AUTHORS = "authors";

  //content type properties
  private static final String PICTURE_CONTENT_TYPE = "CMPicture";
  private static final String PICTURE_BLOB_PROPETY_NAME = "data";

  private final TikaConfig tikaConfig;
  private final ContentRepository repository;
  private final SitesService sitesService;
  private final TaxonomyResolver taxonomyResolver;
  private final List<ContentWriteInterceptor> contentWriteInterceptors;

  public WordUploadInterceptor(ContentRepository repository, SitesService sitesService, TaxonomyResolver taxonomyResolver, List<ContentWriteInterceptor> contentWriteInterceptors) {
    super();
    this.repository = repository;
    this.sitesService = sitesService;
    this.taxonomyResolver = taxonomyResolver;
    this.contentWriteInterceptors = contentWriteInterceptors;
    this.tikaConfig = TikaConfig.getDefaultConfig();
  }

  @Override
  public void intercept(ContentWriteRequest request) {
    Map<String, Object> properties = request.getProperties();
    if (properties.containsKey(DATA_PROPERTY)) {
      Object value = properties.get(DATA_PROPERTY);
      // remove data property since articles dont have it. data is only needed to transport blob to the interceptor
      properties.remove(DATA_PROPERTY);
      if (value instanceof Blob) {
        Blob blob = (Blob) value;
        try {
          extract(blob.getInputStream(), blob.getContentType(), request.getName(), request);
        } catch (Exception e) {
          LOG.error("Error while extracting word file", e);
        }
      }
    }
  }

  private void extract(InputStream in, MimeType contentType, String defaultTitle, ContentWriteRequest request) throws IOException, TikaException, SAXException {
    byte[] bytes = IOUtils.toByteArray(in);

    Optional.of(sitesService.getContentSiteAspect(request.getParent()))
            .map(ContentObjectSiteAspect::getSite)
            .map(Site::getLocale)
            .ifPresent(value -> request.getProperties().put("locale", value.toLanguageTag()));

    // extract title
    DocTitleHandler titleExtractor = new DocTitleHandler(defaultTitle);
    extractFromContentHandler(bytes, titleExtractor);
    String title = titleExtractor.getTitle();
    request.getProperties().put("title", title);


    //extract pictures
    Map<String, Blob> images = new HashMap<>();
    if (contentType.getBaseType().equals(DOC_MIMETYPE)) {
      images = extractDocImages(bytes);
    } else if (contentType.getBaseType().equals(DOCX_MIMETYPE) || contentType.getBaseType().equals(OFFICE_OPEN_XML_MIMETYPE)) {
      images = extractDocxImages(bytes);
    }

    Map<String, Content> createdImages = images.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, pic -> createPicture(request.getParent(), title, pic.getKey(), pic.getValue())));

    request.getProperties().put(PICTURES, createdImages.values().stream().collect(Collectors.toUnmodifiableList()));

    // extract paragraphs
    DocContentHandler wordextractor = new DocContentHandler(repository, createdImages);
    extractFromContentHandler(bytes, wordextractor);
    request.getProperties().put("detailText", wordextractor.getMarkup());


    // extract metadata
    Metadata metadata = extractMetadata(bytes);

    // Add tags
    Optional<String[]> extractedKeywords = Optional.ofNullable(metadata.get(Office.KEYWORDS)).map(s -> s.split(","));
    if (extractedKeywords.isPresent() && taxonomyResolver != null) {
      Collection<Taxonomy> rootTaxonomies = taxonomyResolver.getTaxonomies();
      for (Taxonomy<Content> rootTaxonomy : rootTaxonomies) {
        switch (rootTaxonomy.getTaxonomyId()) {
          case SUBJECT_TAXONOMY_ID:
            Collection<Content> subjectTags = findTagsMatchingKeywords(rootTaxonomy, extractedKeywords.get());
            request.getProperties().put(SUBJECT_TAXONOMY, subjectTags);
            break;
          case LOCATION_TAXONOMY_ID:
            Collection<Content> locationTags = findTagsMatchingKeywords(rootTaxonomy, extractedKeywords.get());
            request.getProperties().put(LOCATION_TAXONOMY, locationTags);
            break;
          default:
            break;
        }
      }
    }

    // Extract authors
    request.getProperties().put(AUTHORS, findMatchingAuthors(metadata.getValues(Office.AUTHOR), request));
  }

  private List<Content> findMatchingAuthors(String[] extractedAuthors, ContentWriteRequest context) {
    List<Content> result = new ArrayList<>();
    Content folder = Optional.of(sitesService.getContentSiteAspect(context.getParent()))
            .map(ContentObjectSiteAspect::getSite)
            .map(Site::getSiteRootFolder)
            .orElse(null);
    for (String author : extractedAuthors) {
      SearchResult authorSearchResult = repository.getSearchService().search(author, "name", true, folder, true, repository.getContentType("CMPerson"), false, 0, 10);
      result.addAll(authorSearchResult.getMatches());
    }
    return result;
  }

  private Content createPicture(Content parent, String imageName, String documentName, Blob imageData) {
    ContentType contentType = repository.getContentType(PICTURE_CONTENT_TYPE);
    Map<String, Object> properties = new HashMap<>();
    properties.put(PICTURE_BLOB_PROPETY_NAME, imageData);

    Optional.of(sitesService.getContentSiteAspect(parent))
            .map(ContentObjectSiteAspect::getSite)
            .map(Site::getLocale)
            .ifPresent(value -> properties.put("locale", value.toLanguageTag()));
    String uniqueFileName = resolveUniqueFilename(parent, imageName, documentName);

    //TODO not public API yet so may change in the future
    IssuesImpl<Object> issues = new IssuesImpl<>(null, Collections.emptySet());
    ContentWriteRequest writeRequest = new ContentWriteRequestImpl(null, parent, uniqueFileName, contentType, properties, issues);
    contentWriteInterceptors.stream()
            .filter(interceptor -> interceptor.getType().isSubtypeOf(contentType))
            .forEach(interceptor -> interceptor.intercept(writeRequest));


    Content picture = contentType.createByTemplate(parent, uniqueFileName, "{3} ({1})", properties);
    picture.checkIn();
    return picture;
  }

  /**
   * Find taxonomy contents matching the provided array of keywords.
   */
  private Collection<Content> findTagsMatchingKeywords(Taxonomy<Content> taxonomy, String[] extractedKeywords) {
    List<String> terms = Arrays.stream(extractedKeywords)
            .map(String::toLowerCase)
            .map(String::trim)
            .collect(Collectors.toUnmodifiableList());

    List<TaxonomyNode> allChildren = taxonomy.getAllChildren();
    return allChildren
            .stream()
            .filter(t -> terms.contains(t.getName().toLowerCase()))
            .map(node -> repository.getContent(TaxonomyUtil.asContentId(node.getRef())))
            .collect(Collectors.toUnmodifiableList());
  }

  private void extractFromContentHandler(byte[] bytes, ContentHandlerDecorator resolver) throws IOException, SAXException, TikaException {
    org.apache.tika.parser.Parser parser = new AutoDetectParser(tikaConfig);
    Metadata metadata = new Metadata();
    XHTMLContentHandler handler = new XHTMLContentHandler(resolver, metadata);
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
    ParseContext context = new ParseContext();
    parser.parse(byteArrayInputStream, handler, metadata, context);
  }

  /**
   * Extract metadata from byte array.
   */
  public static Metadata extractMetadata(byte[] bytes) throws IOException, SAXException, TikaException {
    Parser parser = new AutoDetectParser();
    ContentHandler handler = new BodyContentHandler();
    Metadata metadata = new Metadata();
    ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
    ParseContext context = new ParseContext();
    parser.parse(stream, handler, metadata, context);
    return metadata;
  }

  private Map<String, Blob> extractDocxImages(byte[] bytes) {
    Map<String, Blob> result = new HashMap<>();
    try {
      ByteArrayInputStream byteArrayInputStream;
      byteArrayInputStream = new ByteArrayInputStream(bytes);
      XWPFDocument doc = new XWPFDocument(byteArrayInputStream);
      List<XWPFPictureData> allPictures = doc.getAllPictures();
      BlobService blobService = repository.getConnection().getBlobService();
      for (XWPFPictureData p : allPictures) {
        String mimeType = p.getPackagePart().getContentType();
        if (isValidMimeType(mimeType)) {
          byte[] rawContent = p.getData();
          Blob blob = blobService.fromBytes(rawContent, mimeType);
          result.put(p.getFileName(), blob);
        }
      }
    } catch (Exception e) {
      LOG.warn("Failed to extract images: {}", e.getMessage());
    }
    return result;
  }

  private Map<String, Blob> extractDocImages(byte[] bytes) {
    Map<String, Blob> result = new HashMap<>();
    try {
      ByteArrayInputStream byteArrayInputStream;
      byteArrayInputStream = new ByteArrayInputStream(bytes);
      HWPFDocument doc = new HWPFDocument(byteArrayInputStream);
      List<Picture> allPictures = doc.getPicturesTable().getAllPictures();
      BlobService blobService = repository.getConnection().getBlobService();
      for (Picture p : allPictures) {
        String mimeType = p.getMimeType();
        if (isValidMimeType(mimeType)) {
          byte[] rawContent = p.getRawContent();
          Blob blob = blobService.fromBytes(rawContent, mimeType);

          String name = null;
          try {
            name = p.getDescription();
          } catch (Exception e) {
            //ignore
          }
          result.put(name, blob);
        }
      }
    } catch (Exception e) {
      LOG.warn("Failed to extract images: {}", e.getMessage());
    }
    return result;
  }

  /**
   * Not all image can be processed by the Studio, so filter them
   *
   * @param mimeType the mime type of the image
   * @return true if the image should be imported
   */
  private boolean isValidMimeType(String mimeType) {
    return !INVALID_IMAGE_MIME_TYPES.contains(mimeType);
  }


  /**
   * Ensures that the file with the given name not exists yet.
   *
   * @param folder    The folder to create the unique name for.
   * @param imageName The default file name
   * @return The unique file name for the given folder.
   */
  private String resolveUniqueFilename(Content folder, String imageName, String name) {
    String uniqueFilename = name;
    if (StringUtils.isEmpty(uniqueFilename)) {
      uniqueFilename = imageName;
    } else {
      uniqueFilename = imageName + " - " + name;
    }
    int index = 1;
    while (folder.getChild(uniqueFilename) != null) {
      uniqueFilename = uniqueFilename + " (" + index + ")";
      index++;
    }
    return uniqueFilename;
  }

}
