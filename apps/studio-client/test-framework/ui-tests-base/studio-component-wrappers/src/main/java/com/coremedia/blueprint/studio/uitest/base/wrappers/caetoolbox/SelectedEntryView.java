package com.coremedia.blueprint.studio.uitest.base.wrappers.caetoolbox;

import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;

@ExtJSObject(id= "selectedEntryView")
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class SelectedEntryView extends Panel {
  @Inject
  private EntryListContainer entryListContainer;


  public EntryListContainer getEntryListContainer() {
    return entryListContainer;
  }
}
