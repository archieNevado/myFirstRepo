package com.coremedia.blueprint.studio.uitest.base.wrappers.pagegrid;

import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

@DefaultAnnotation(NonNull.class)
@ExtJSObject
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PlacementInheritStateButton extends Container {
  public static final String XTYPE = "com.coremedia.blueprint.base.pagegrid.config.placementInheritStateButton";
}
