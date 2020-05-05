package com.coremedia.blueprint.studio.uitest.wrappers.moderation.usermanagement;

import com.coremedia.blueprint.studio.uitest.wrappers.moderation.model.ModeratedUser;
import com.coremedia.elastic.core.api.models.Model;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ui.data.RemoteBeanStore;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("singleton")
public class UserManagementSearchResultStore extends RemoteBeanStore<Model, ModeratedUser> {
}
