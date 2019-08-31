package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.ContentAssetDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.ContentFolderAssignmentDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.ContentFolderDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.LocalizedProperty;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.MarkupTextDocument;
import com.google.common.collect.ImmutableListMultimap;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Libraries Resource
 */
@Service("ocapiLibrariesResource")
public class LibrariesResource extends AbstractDataResource {

  //Action to get all the customer groups with no filtering.
  private static final String CONTENT_BY_ID = "/libraries/{content-asset-library}/content/{id}";

  private static final String FOLDER_ASSIGNMENT = "/libraries/{content-asset-library}/folder_assignments/{content_id}/{content-asset-library-folder}";
  private static final String FOLDER_BY_ID = "/libraries/{content-asset-library}/folders/{content-asset-library-folder}";
  private static final String DEFAULT_LIBRARY = "SiteGenesisSharedLibrary";
  private static final String DEFAULT_FOLDER = "CoreMedia";

  public Optional<ContentAssetDocument> getContentById(@NonNull String id, @NonNull StoreContext storeContext) {
    Map<String, String> pathParameters = preparePathParameters(id, storeContext);
    return getConnector().getResource(CONTENT_BY_ID, pathParameters, ImmutableListMultimap.of(), ContentAssetDocument.class, storeContext);
  }

  public Optional<ContentAssetDocument> putContentById(@NonNull String id, @NonNull String name, @NonNull String description,
                                                       @NonNull String json, @NonNull StoreContext storeContext) {
    Map<String, String> pathParameters = preparePathParameters(id, storeContext);
    ContentAssetDocument contentAssetDocument = prepareContentAssetDocumentBeforePush(id, name, description, json, storeContext.getLocale());

    return getConnector().putResource(CONTENT_BY_ID, pathParameters, ImmutableListMultimap.of(),
            contentAssetDocument.toJSONString(), ContentAssetDocument.class, storeContext);
  }

  public Optional<ContentAssetDocument> patchContentById(@NonNull String id, @NonNull String name, @NonNull String description,
                                                         @NonNull String json, @NonNull StoreContext storeContext) {
    Map<String, String> pathParameters = preparePathParameters(id, storeContext);
    ContentAssetDocument contentAssetDocument = prepareContentAssetDocumentBeforePush(id, name, description, json, storeContext.getLocale());

    return getConnector().patchResource(CONTENT_BY_ID, pathParameters, ImmutableListMultimap.of(),
            contentAssetDocument.toJSONString(), ContentAssetDocument.class, storeContext);
  }

  public void deleteContentById(@NonNull String id, @NonNull StoreContext storeContext) {
    Map<String, String> pathParameters = preparePathParameters(id, storeContext);

    getConnector().deleteResource(CONTENT_BY_ID, pathParameters, ImmutableListMultimap.of(), storeContext);
  }

  @NonNull
  private static ContentAssetDocument prepareContentAssetDocumentBeforePush(@NonNull String id, @NonNull String name, @NonNull String description,
                                                                            @NonNull String json, @NonNull Locale locale) {
    ContentAssetDocument contentAssetDocument = new ContentAssetDocument();

    LocalizedProperty<String> nameProperty = new LocalizedProperty<>(Collections.singletonMap("default", name));
    contentAssetDocument.setName(nameProperty);

    LocalizedProperty<String> descriptionProperty = new LocalizedProperty<>(Collections.singletonMap("default", description));
    contentAssetDocument.setDescription(descriptionProperty);

    MarkupTextDocument bodyMarkup = new MarkupTextDocument();
    bodyMarkup.setMarkup(json);
    LocalizedProperty<MarkupTextDocument> bodyProperty = new LocalizedProperty<>(Collections.singletonMap(locale.toLanguageTag(), bodyMarkup));
    contentAssetDocument.setBody(bodyProperty);

    return contentAssetDocument;
  }

  private static Map<String, String> preparePathParameters(@NonNull String id, @NonNull StoreContext storeContext) {
    Map<String, String> pathParameters = new HashMap<>();
    storeContext.getReplacements().get("content-asset-library");

    pathParameters.put("content-asset-library", getLibrary(storeContext));
    pathParameters.put("id", id);
    return pathParameters;
  }

  public Optional<ContentFolderAssignmentDocument> assignContentToFolder(@NonNull String contentId, @NonNull StoreContext storeContext) {
    Map<String, String> pathParameters = new HashMap<>();
    String folder = getFolder(storeContext);
    pathParameters.put("content-asset-library", getLibrary(storeContext));
    pathParameters.put("content-asset-library-folder", folder);
    pathParameters.put("content_id", contentId);

    Optional<ContentFolderDocument> resource = getConnector().getResource(FOLDER_BY_ID, pathParameters,
            ImmutableListMultimap.of(), ContentFolderDocument.class, storeContext);

    if (!resource.isPresent()) {
      ContentFolderDocument contentFolderDocument = new ContentFolderDocument();
      contentFolderDocument.setId(getFolder(storeContext));
      contentFolderDocument.setName(Optional.of("CoreMedia Content Assets"));
      contentFolderDocument.setParentFolderId("root");
      Optional<ContentFolderAssignmentDocument> folderDocument = getConnector().putResource(FOLDER_BY_ID, pathParameters, ImmutableListMultimap.of(),
              contentFolderDocument.toJSONString(), ContentFolderAssignmentDocument.class, storeContext);
      if (!folderDocument.isPresent()) {
        throw new CommerceException("Cannot create '" + folder + "' folder.");
      }
    }

    ContentFolderAssignmentDocument contentFolderAssignmentDocument = new ContentFolderAssignmentDocument();
    contentFolderAssignmentDocument.setDefault(true);

    return getConnector().putResource(FOLDER_ASSIGNMENT, pathParameters, ImmutableListMultimap.of(),
            contentFolderAssignmentDocument.toJSONString(), ContentFolderAssignmentDocument.class, storeContext);

  }

  private static String getLibrary(StoreContext storeContext) {
    String libraryId = storeContext.getReplacements().get("content-asset-library");
    if (StringUtils.isEmpty(libraryId)) {
      libraryId = DEFAULT_LIBRARY;
    }
    return libraryId;
  }

  private static String getFolder(StoreContext storeContext) {
    String folderId = storeContext.getReplacements().get("content-asset-library-folder");
    if (StringUtils.isEmpty(folderId)) {
      folderId = DEFAULT_FOLDER;
    }
    return folderId;
  }
}
