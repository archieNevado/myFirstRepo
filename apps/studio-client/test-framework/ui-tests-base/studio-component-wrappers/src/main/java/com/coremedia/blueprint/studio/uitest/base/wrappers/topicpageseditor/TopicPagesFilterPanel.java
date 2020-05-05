package com.coremedia.blueprint.studio.uitest.base.wrappers.topicpageseditor;

import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import com.coremedia.uitesting.ext3.wrappers.form.field.ComboBoxField;
import com.coremedia.uitesting.ext3.wrappers.form.field.TextField;

/**
 * Contains the filter components for the topic pages editor.
 */
@ExtJSObject(id="topicPagesFilterPanel")
public class TopicPagesFilterPanel extends Container {
  public static final String XTYPE = "com.coremedia.blueprint.studio.topicpages.config.topicPagesFilterPanel";

  @FindByExtJS(itemId = "filterTextField")
  private TextField topicPagesFilterText;

  @FindByExtJS(itemId = "resetFilter")
  private Button resetFilterButton;

  @FindByExtJS(itemId = "startSearch")
  private Button startSearchButton;

  @FindByExtJS(id = "topicPagesTaxonomyCombo")
  private ComboBoxField topicsComboBox;

  public ComboBoxField getTopicsCombo() {
    return topicsComboBox;
  }

  public TextField getTopicPagesFilterText() {
    return topicPagesFilterText;
  }

  public Button getResetFilterButton() {
    return resetFilterButton;
  }

  public Button getStartSearchButton() {
    return startSearchButton;
  }

  public void searchFor(String str) {
    topicPagesFilterText.clear();
    getIdleIndicators().idle().waitUntilTrue();
    topicPagesFilterText.writeString(str);
    startSearchButton.click();
  }
}
