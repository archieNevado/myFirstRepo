package com.coremedia.blueprint.themeimporter;

import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.content.Version;
import com.coremedia.cap.content.results.BulkOperationResult;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructBuilder;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Some theme importer related pure content utilities.
 */
class ThemeImporterContentHelper {
  private static final Logger LOGGER = LoggerFactory.getLogger(ThemeImporterContentHelper.class);
  private CapConnection capConnection;
  private Collection<Content> toBeCheckedIn = new HashSet<>();


  // --- Construct and configure ------------------------------------

  ThemeImporterContentHelper(CapConnection capConnection) {
    this.capConnection = capConnection;
  }


  // --- Features ---------------------------------------------------

  int id(Content content) {
    return IdHelper.parseContentId(content.getId());
  }

  Content fetchContent(String path) {
    return capConnection.getContentRepository().getChild(path);
  }

  int currentVersion(Content content) {
    return IdHelper.parseVersionId(getExistingVersion(content).getId());
  }

  Content updateContent(String newType, String folder, String path, Map<String, ?> properties) {
    return updateContent(newType, normalize(folder)+path, properties);
  }

  Content updateContent(String newType, String absolutePath, Map<String, ?> properties) {
    try {
      Content content = getOrCreateContent(newType, absolutePath);
      //Only do this if the document has not been checked out by somebody else.
      if (content != null) {
        content.setProperties(properties);
      }
      return content;
    } catch (Exception e) {
      LOGGER.error("Error creating content {} ", absolutePath, e);
      return null;
    }
  }

  Struct propertiesToStruct(String text) {
    StructBuilder structBuilder = capConnection.getStructService().createStructBuilder();
    for (StringTokenizer st = new StringTokenizer(text, "\n"); st.hasMoreTokens(); ) {
      String line = st.nextToken();
      try {
        KeyValue property = parseProperty(line);
        if (property != null) {
          structBuilder.declareString(property.key, Integer.MAX_VALUE, property.value);
        }
      } catch (Exception e) {
        LOGGER.error("Cannot handle property line \"{}\", ignore.", line, e);
      }
    }
    return structBuilder.build();
  }

  /**
   * I'll do my very best...
   */
  void deleteContent(Content content) {
    try {
      if (content.isCheckedOut()) {
        content.checkIn();
      }
      toBeCheckedIn.remove(content);
      BulkOperationResult result = content.delete();
      if (!result.isSuccessful()) {
        LOGGER.warn("Cannot delete content {}, you should clean up manually afterwards.", content);
      }
    } catch (Exception e) {
      LOGGER.warn("Cannot delete content {}, you should clean up manually afterwards.", content, e);
    }
  }

  void checkInAll() {
    toBeCheckedIn.forEach(Content::checkIn);
  }

  void revertAll() {
    toBeCheckedIn.forEach(Content::revert);
  }


  // --- internal ---------------------------------------------------

  @VisibleForTesting
  KeyValue parseProperty(String line) {
    String trimmed = line.trim();
    if (trimmed.isEmpty() || trimmed.startsWith("#")) {
      return null;
    }
    int index = line.indexOf('=');
    if (index<0) {
      throw new IllegalArgumentException("Illegal line in properties file: \"" + line + "\"");
    }
    String key = StringEscapeUtils.unescapeJava(line.substring(0, index).trim());
    String value = StringEscapeUtils.unescapeJava(line.substring(index+1).trim());
    return new KeyValue(key, value);
  }

  private Version getExistingVersion(Content content) {
    Version version = content.getCheckedInVersion();
    if (version == null) {
      version = content.getCheckedOutVersion();
    }
    return version;
  }

  /**
   * Returns the requested content in checked-out-by-me state, or null if this
   * is not possible for whatever reason.
   */
  private Content getOrCreateContent(String contentType, String absolutePath) {
    ContentRepository repository = capConnection.getContentRepository();
    Content content = repository.getChild(absolutePath);
    if (content != null) {
      if (!content.getType().getName().equals(contentType)) {
        //Set to null, because the type is different
        LOGGER.warn("Cannot update document {} since it is of type {} even though it should be of type {}", absolutePath, content.getType().getName(), contentType);
        content = null;
      } else if (content.isCheckedIn()) {
        content.checkOut();
        toBeCheckedIn.add(content);
      } else if (!content.isCheckedOutByCurrentSession()) {
        LOGGER.warn("Cannot update document {} since it has been checkout out by somebody else.", absolutePath);
        content = null;
      }
    } else {
      ContentType type = repository.getContentType(contentType);
      if (type != null) {
        content = type.create(repository.getRoot(), absolutePath);
        toBeCheckedIn.add(content);
      } else {
        LOGGER.warn("Cannot create document {} since there is no content type {}", absolutePath, contentType);
      }
    }
    return content;
  }

  private static String normalize(@Nonnull String folder) {
    return folder.endsWith("/") ? folder : folder + "/";
  }

  @VisibleForTesting
  class KeyValue {
    String key;
    String value;

    public KeyValue(String key, String value) {
      this.key = key;
      this.value = value;
    }
  }
}
