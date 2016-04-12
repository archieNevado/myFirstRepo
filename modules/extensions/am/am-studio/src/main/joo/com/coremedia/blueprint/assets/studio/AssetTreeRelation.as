package com.coremedia.blueprint.assets.studio {
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.content.impl.ContentRepositoryImpl;
import com.coremedia.cms.editor.sdk.RepositoryContentTreeRelation;

public class AssetTreeRelation extends RepositoryContentTreeRelation {

  override public function mayCreate(folder:Content, contentType:ContentType):Boolean {
    var mayCreate:Boolean = super.mayCreate(folder, contentType);
    if(mayCreate === undefined) {
      return undefined;
    }
    if(!mayCreate) {
      return false;
    }

    return contentType.isSubtypeOf(ContentRepositoryImpl.FOLDER_CONTENT_TYPE) ||
            contentType.isSubtypeOf(AssetConstants.DOCTYPE_ASSET);
  }
}
}