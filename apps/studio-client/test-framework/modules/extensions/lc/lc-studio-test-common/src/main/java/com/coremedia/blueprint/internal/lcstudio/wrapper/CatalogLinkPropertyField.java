package com.coremedia.blueprint.internal.lcstudio.wrapper;

import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.grid.GridPanel;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@ExtJSObject
@Scope(SCOPE_PROTOTYPE)
public class CatalogLinkPropertyField extends GridPanel {
  public static final String XTYPE = "com.coremedia.ecommerce.studio.config.catalogLinkPropertyField";

}
