package com.coremedia.blueprint.studio.uitest.wrappers.moderation.archive.archiveditems;

import com.coremedia.blueprint.studio.uitest.wrappers.moderation.moderateditems.ModeratedItemsRowSelectionModel;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.moderateditems.ModeratedItemsViewStore;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.grid.GridPanel;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class ArchivedItemsView extends GridPanel<ModeratedItemsRowSelectionModel> {

  public ModeratedItemsRowSelectionModel getSelectionModel() {
    getIdleIndicators().idle().waitUntilTrue();
    return getSelectionModel(ModeratedItemsRowSelectionModel.class);
  }

  @Override
  public ModeratedItemsViewStore getStore() {
    return super.getStore().evalJsProxyProxy(ModeratedItemsViewStore.class);
  }

}
