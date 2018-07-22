package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.resources;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents.CustomObjectDocument;
import org.springframework.stereotype.Service;

import edu.umd.cs.findbugs.annotations.Nullable;

/*
 * Serves as generic resource to get any data type via shop API for
 * development and debugging purposes ("Schweizer Taschenmesser").
 */
@Service
public class CustomObjectsResource extends AbstractShopResource {

  private static final String OBJECT_TYPE_PLACEHOLDER = "{object_type}";
  private static final String KEY_PLACEHOLDER = "{key}";
  private static final String CUSTOM_OBJECTS_PATH = "/custom_objects/" + OBJECT_TYPE_PLACEHOLDER + "/" + KEY_PLACEHOLDER;

  /**
   * Reads a custom object with a given object type ID
   * and a value for the key attribute of the object which represents its unique identifier.
   *
   * @param objectType the ID of the object type
   * @param key        the key attribute value of the custom object
   * @return
   */
  @Nullable
  public CustomObjectDocument getCustomObject(String objectType, String key) {
    String requestPath = CUSTOM_OBJECTS_PATH.replace(OBJECT_TYPE_PLACEHOLDER, objectType);
    requestPath = requestPath.replace(KEY_PLACEHOLDER, key);

    return getConnector().getResource(requestPath, CustomObjectDocument.class)
            .orElse(null);
  }
}
