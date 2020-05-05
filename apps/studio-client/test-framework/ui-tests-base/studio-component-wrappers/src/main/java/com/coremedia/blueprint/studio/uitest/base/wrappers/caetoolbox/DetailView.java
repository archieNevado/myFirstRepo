package com.coremedia.blueprint.studio.uitest.base.wrappers.caetoolbox;

import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class DetailView extends Panel {
}
