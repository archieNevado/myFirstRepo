package com.coremedia.blueprint.studio.uitest.base.wrappers.pagegrid;

import com.coremedia.uitesting.cms.editor.components.premular.PropertyFieldGroup;
import com.coremedia.uitesting.cms.editor.components.premular.fields.LinkListPropertyFieldGridPanel;
import com.coremedia.uitesting.cms.editor.components.premular.fields.LinkListPropertyFieldToolbar;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import com.coremedia.uitesting.ext3.wrappers.form.field.ComboBoxField;
import com.coremedia.uitesting.ui.IconDisplayField;
import net.joala.condition.BooleanCondition;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * Wrapper for a placement, including all actions of the toolbar.
 */
@ExtJSObject
@Scope(SCOPE_PROTOTYPE)
public class PlacementField extends PropertyFieldGroup {
  @FindByExtJS(itemId = "inherited")
  private PlacementInheritStateButton inheritedPlacementInheritStateButton;
  @FindByExtJS(itemId = "locked")
  private PlacementInheritStateButton lockedPlacementInheritStateButton;
  @FindByExtJS(itemId = "missing")
  private IconDisplayField missingPlacementInheritStateButton;

  @FindByExtJS(xtype = PlacementLinkListPropertyField.XTYPE)
  private PlacementLinkListPropertyField linkListPropertyField;

  public BooleanCondition inheritanceStateInherited() {
    return inheritedPlacementInheritStateButton
            .visible()
            .withMessage("Expected placements 'inherited' state.")
            ;
  }

  public BooleanCondition inheritanceStateLocked() {
    return inheritedPlacementInheritStateButton
            .visible()
            .withMessage("Expected placements 'locked' state.")
            ;
  }

  public BooleanCondition inheritanceStateMissing() {
    return missingPlacementInheritStateButton
            .visible()
            .withMessage("Expected placements 'missing' state.")
            ;
  }

  public LinkListPropertyFieldGridPanel getGrid() {
    return linkListPropertyField.getLinkGrid();
  }

  @Override
  public PlacementFieldToolbar getTopToolbar() {
    final LinkListPropertyFieldGridPanel gridPanel = getGrid();
    final LinkListPropertyFieldToolbar topToolbar = gridPanel.getTopToolbar();
    return topToolbar.evalJsProxyProxy(PlacementFieldToolbar.class);
  }

  public Button getInheritButton() {
    return getTopToolbar().getInheritButton();
  }

  public Button getLockButton() {
    return getTopToolbar().getLockButton();
  }

  public ComboBoxField getViewtypeSelector() {
    return getTopToolbar().getViewtypeSelector();
  }


}
