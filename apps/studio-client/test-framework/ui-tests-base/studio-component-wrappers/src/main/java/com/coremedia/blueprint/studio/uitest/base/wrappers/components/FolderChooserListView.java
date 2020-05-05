package com.coremedia.blueprint.studio.uitest.base.wrappers.components;

import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.uitesting.cms.editor.CollectionViewManager;
import com.coremedia.uitesting.cms.editor.ContentStore;
import com.coremedia.uitesting.cms.editor.EditorContext;
import com.coremedia.uitesting.cms.editor.components.premular.fields.LinkListPropertyFieldGridPanel;
import com.coremedia.uitesting.cms.editor.components.premular.fields.SimpleSuggestionsComboBox;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.uapi.helper.conditions.SearchConditions;
import net.joala.condition.BooleanCondition;
import org.openqa.selenium.Keys;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;

@ExtJSObject
@Scope("prototype")
public class FolderChooserListView extends LinkListPropertyFieldGridPanel {

  @Inject
  private SearchConditions searchConditions;

  @Inject
  private EditorContext editorContext;

  public static final String XTYPE = "com.coremedia.cms.editor.sdk.folderchooser.FolderChooserListView";

  public void selectFolder(Content folder){
    // if no defaults are set then the folder chooser tries to set the preferred sites root folder (if available).
    // in this case the folder could have already been set and the test can skip the manual selection.
    if (!getStore().capObjectContained(folder).await()) {
      CollectionViewManager collectionView = editorContext.getCollectionViewManager();
      collectionView.showInRepository(folder);
      getIdleIndicators().idle().waitUntilTrue();
      SimpleSuggestionsComboBox comboBox = getDropArea().getComboBox();
      comboBox.focus();
      getIdleIndicators().idle().waitUntilTrue();
      // check again if the folder is now the default selection. this happens when the list was empty before.
      // then the new folder automatically becomes the default selection
      if (!getStore().capObjectContained(folder).await()) {
        ContentStore contentStore = comboBox.getStore().evalJsProxyProxy(ContentStore.class);
        contentStore.capObjectContained(folder).waitUntilTrue();
        comboBox.select(folder);
        comboBox.click();
        comboBox.sendKeys(Keys.ENTER);
      }
    }
  }

  /**
   *
   * As for now the folderChooserListView does not return results for a folder search with chars like "-" etc.
   * So this method will return a value that will find the folder 8^)
   *
   * @param folder the folder that needs to be found by the folderFooser
   * @return a search term that can be used to find the folder via the folderChooserListView
   */
  private String getFolderSearchQuery(Content folder) {
    String path = folder.getPath();

    if(path.contains(String.valueOf(IdHelper.parseContentId(folder.getId())))){
      path = String.valueOf(IdHelper.parseContentId(folder.getId()));
    }else {
      path = folder.getPath().replace("-", " ");
      path = path.replace("+", " ");
      path = path.replace("/", " ");
    }
    return path;
  }

  /**
   * Checks if the folder is contained in the suggestions or if it is the selected value.
   * @param folder the folder that needs to be part of the suggestions or the selection
   * @return <code>true</code> if it is part of the chooser otherwise <code>false</code>
   */
  public boolean containsFolder(Content folder){
    ContentStore contentStore = getDropArea().getComboBox().getStore().evalJsProxyProxy(ContentStore.class);
    return getStore().capObjectContained(folder).await() || contentStore.capObjectContained(folder).await();
  }


}
