package com.coremedia.blueprint.studio.uitest.base.wrappers.qcreate;

import com.coremedia.uitesting.cms.editor.components.premular.fields.LinkListPropertyField;
import com.coremedia.uitesting.cms.editor.components.premular.fields.LinkListPropertyFieldGridPanel;
import com.coremedia.uitesting.cms.editor.components.premular.fields.LinkListPropertyFieldToolbar;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import org.springframework.context.annotation.Scope;

/**
 * A Linklist-Property-Field which is enabled for quick content creation.
 *
 * @since 2013-08-09
 */
@ExtJSObject
@Scope("prototype")
public class LinkListPropertyQuickCreateField extends LinkListPropertyField {
  public LinkListPropertyFieldQuickCreateToolbar getTopToolbar() {
    final LinkListPropertyFieldGridPanel gridPanel = getGridPanel();
    final LinkListPropertyFieldToolbar topToolbar = gridPanel.getTopToolbar();
    return topToolbar.evalJsProxyProxy(LinkListPropertyFieldQuickCreateToolbar.class);
  }
}
