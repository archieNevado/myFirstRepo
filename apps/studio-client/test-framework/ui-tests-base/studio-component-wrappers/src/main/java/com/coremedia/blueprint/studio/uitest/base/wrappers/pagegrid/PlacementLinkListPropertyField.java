package com.coremedia.blueprint.studio.uitest.base.wrappers.pagegrid;

import com.coremedia.uitesting.cms.editor.components.premular.fields.LinkListPropertyFieldGridPanel;
import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.context.annotation.Scope;

import static java.util.Objects.requireNonNull;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@DefaultAnnotation(NonNull.class)
@ExtJSObject
@Scope(SCOPE_PROTOTYPE)
public class PlacementLinkListPropertyField extends Container {
  public static final String XTYPE = "com.coremedia.blueprint.base.pagegrid.config.placementLinkListPropertyField";

  @FindByExtJS(itemId = "linkGrid")
  @Nullable
  private LinkListPropertyFieldGridPanel linkGrid;

  public LinkListPropertyFieldGridPanel getLinkGrid() {
    return requireNonNull(linkGrid);
  }
}
