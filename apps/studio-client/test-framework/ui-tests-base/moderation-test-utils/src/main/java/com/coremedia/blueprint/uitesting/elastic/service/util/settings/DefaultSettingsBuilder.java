package com.coremedia.blueprint.uitesting.elastic.service.util.settings;

import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Named
@Scope(SCOPE_PROTOTYPE)
public class DefaultSettingsBuilder implements SettingsBuilder {

  final Map<String, Object> settings = new HashMap<>();

  @Override
  public SettingsBuilder enabled(Boolean enabled) {
    if (null != enabled) {
      settings.put("enabled", enabled);
    } else {
      settings.remove("enabled");
    }
    return this;
  }

  @Override
  public SettingsBuilder commentType(String commentType) {
    if (null != commentType) {
      settings.put("commentType", commentType);
    } else {
      settings.remove("commentType");
    }
    return this;
  }

  @Override
  public SettingsBuilder reviewType(String reviewType) {
    if (null != reviewType) {
      settings.put("reviewType", reviewType);
    } else {
      settings.remove("reviewType");
    }
    return this;
  }

  @Override
  public Map<String, Object> build() {
    return settings;
  }
}
