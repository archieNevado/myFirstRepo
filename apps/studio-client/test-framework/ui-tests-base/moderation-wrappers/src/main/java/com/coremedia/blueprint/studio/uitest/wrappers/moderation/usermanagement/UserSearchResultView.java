package com.coremedia.blueprint.studio.uitest.wrappers.moderation.usermanagement;

import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.grid.GridPanel;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class UserSearchResultView extends GridPanel<UserSearchResultRowSelectionModel> {

  public UserSearchResultRowSelectionModel getSelectionModel() {
    return getSelectionModel(UserSearchResultRowSelectionModel.class);
  }
}
