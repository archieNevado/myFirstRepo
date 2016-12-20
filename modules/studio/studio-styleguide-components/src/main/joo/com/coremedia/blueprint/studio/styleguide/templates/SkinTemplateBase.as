package com.coremedia.blueprint.studio.styleguide.templates {
import ext.panel.Panel;

public class SkinTemplateBase extends Panel {

  private var config:SkinTemplateBase;

  public function SkinTemplateBase(config:SkinTemplateBase = null) {
    super(config);
    this.config = config;
  }
}
}
