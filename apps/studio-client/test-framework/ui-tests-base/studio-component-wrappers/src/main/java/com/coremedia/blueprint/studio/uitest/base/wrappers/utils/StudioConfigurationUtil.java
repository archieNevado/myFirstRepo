package com.coremedia.blueprint.studio.uitest.base.wrappers.utils;

import com.coremedia.uitesting.webdriver.JsProxy;
import com.coremedia.uitesting.webdriver.access.JsProxyBean;
import net.joala.condition.Condition;

import javax.inject.Singleton;

@JsProxyBean(expression = "com.coremedia.cms.editor.sdk.util.StudioConfigurationUtil")
@Singleton
public class StudioConfigurationUtil extends JsProxy {

  public Condition<Object> getConfiguration(String bundle, String configuration, Object context) {
    return condition("self.getConfiguration(bundle, configuration, context)",
            "bundle", bundle, "configuration", configuration, "context", context);
  }

}
