package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.resources;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents.CustomObjectDocument;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.stereotype.Service;

/*
 * Serves as generic resource to get any data type via shop API for
 * development and debugging purposes ("Schweizer Taschenmesser").
 */
@DefaultAnnotation(NonNull.class)
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
  public CustomObjectDocument getCustomObject(String objectType, String key, StoreContext storeContext) {
    String requestPath = CUSTOM_OBJECTS_PATH.replace(OBJECT_TYPE_PLACEHOLDER, objectType);
    requestPath = requestPath.replace(KEY_PLACEHOLDER, key);

    return getConnector()
            .getResource(requestPath, CustomObjectDocument.class, storeContext)
            .orElse(null);
  }
}
