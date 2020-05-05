package com.coremedia.blueprint.studio.uitest.wrappers.moderation.moderateditems;

import com.coremedia.blueprint.studio.uitest.wrappers.moderation.model.ModeratedItem;
import com.coremedia.elastic.core.api.models.Model;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ui.data.RemoteBeanStore;
import org.springframework.context.annotation.Scope;

/**
 * <p>
 * Specialized Store for {@link ModeratedItemsView}.
 * </p>
 *
 * @since 2013-02-19
 */
@ExtJSObject
@Scope("singleton")
public class ModeratedItemsViewStore extends RemoteBeanStore<Model, ModeratedItem<? extends Model>> {
}
