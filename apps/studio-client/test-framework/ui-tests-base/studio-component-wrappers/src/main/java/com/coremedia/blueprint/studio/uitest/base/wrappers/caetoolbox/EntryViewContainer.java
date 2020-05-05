package com.coremedia.blueprint.studio.uitest.base.wrappers.caetoolbox;

import com.coremedia.uitesting.ui.components.SwitchingContainer;
import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

@ExtJSObject(id= EntryViewContainer.ID)
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class EntryViewContainer extends SwitchingContainer {
  public static final String ID = "entryViewContainer";

  @FindByExtJS(itemId = "blank")
  private Container blankContainer;

  @FindByExtJS(itemId = "view")
  private DetailView detailView;

  public Container getBlankContainer() {
    return blankContainer;
  }

  public DetailView getDetailView() {
    return detailView;
  }
}
