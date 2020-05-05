package com.coremedia.blueprint.studio.uitest.base.wrappers.components;

import com.coremedia.blueprint.studio.uitest.base.wrappers.taxonomyeditor.TaxonomySearchField;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.grid.GridPanel;
import net.joala.condition.BooleanCondition;
import net.joala.condition.Condition;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * Wrapper for the taxonomy search filter in the collection view.
 */
@ExtJSObject
@Scope(SCOPE_PROTOTYPE)
public class TaxonomyFilterPanel extends Panel {
  public static final String XTYPE = "com.coremedia.blueprint.studio.config.taxonomy.taxonomyFilterPanel";

  @FindByExtJS(itemId = "taxonomyFilterSearchField", global = false)
  private TaxonomySearchField searchField;

  @FindByExtJS(itemId = "taxonomyFilterSelection", global = false)
  private GridPanel selectionGrid;

  public BooleanCondition activeStyle() {
    return hasCls("cm-validation-state-success");
  }

  /**
   * Searches the given taxonomy and adds it to the selection.
   * @param taxonomy
   */
  public void select(Content taxonomy) {
    expand(false);
    searchField.searchAndSelect(taxonomy);
  }

  /**
   * Returns the data length of the selection list.
   * @return data length
   */
  public Condition<Long> dataLength() {
    return selectionGrid.getStore().dataLength();
  }

  /**
   * Remove applied filter settings.
   */
  public void deselect(Content content) {
    int id = IdHelper.parseContentId(content.getId());
    evalVoid("self.plusMinusClicked('content/" + id + "')");
  }
}
