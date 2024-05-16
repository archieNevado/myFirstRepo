package com.coremedia.blueprint.assets.studio.upload;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.rest.cap.intercept.ContentWriteInterceptorBase;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import com.coremedia.rest.cap.intercept.InterceptorControlAttributes;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static com.coremedia.rest.cap.intercept.InterceptorControlAttributes.DO_NOTHING;

public class AMDoctypeRewriteUploadInterceptor extends ContentWriteInterceptorBase {

  static final String ASSET_ROOT_FOLDER = "/Assets";
  static final String ORIGINAL_ATTRIBUTE = "original";
  static final String DATA_ATTRIBUTE = "data";

  private ContentType targetDoctype;

  public AMDoctypeRewriteUploadInterceptor(@NonNull ContentType originalDoctype,
                                           @NonNull ContentType targetDoctype) {
    this.targetDoctype = targetDoctype;
    setType(originalDoctype);
    //do it early to prevent further processing of other interceptors.
    setPriority(-10000);
  }

  @Override
  public void intercept(ContentWriteRequest request) {
    Content entity = request.getEntity();

    if (entityAlreadyExist(entity)) {
      return ;
    }

    if (!isBelowAssetFolder(request)) {
      return ;
    }

    Content content = createContent(request);

    if (content != null) {
      request.setAttribute(InterceptorControlAttributes.UPLOADED_DOCUMENTS, List.of(content));
      disableFurtherProcession(request);
    }
  }


  @Nullable
  private Content createContent(@NonNull ContentWriteRequest request) {
    Map<String, Object> properties = request.getProperties();
    if (!properties.containsKey(DATA_ATTRIBUTE)) {
      return null;
    }
    Object data = properties.get(DATA_ATTRIBUTE);
    properties.remove(DATA_ATTRIBUTE);

    properties.put(ORIGINAL_ATTRIBUTE, data);
    return targetDoctype.createByTemplate(request.getParent(), request.getName(), "{3} ({1})", properties);
  }

  private void disableFurtherProcession(@NonNull ContentWriteRequest request) {
    request.setAttribute(DO_NOTHING, true);
  }

  private boolean entityAlreadyExist(@Nullable Content entity) {
    return entity != null;
  }

  private boolean isBelowAssetFolder(@NonNull ContentWriteRequest request) {
    String parentPath = request.getParent().getPath();
    return parentPath.startsWith(ASSET_ROOT_FOLDER);
  }
}
