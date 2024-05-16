import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ContentProvidingTestContainerBase from "./ContentProvidingTestContainerBase";

interface ContentProvidingTestContainerConfig extends Config<ContentProvidingTestContainerBase> {
}

class ContentProvidingTestContainer extends ContentProvidingTestContainerBase {
  declare Config: ContentProvidingTestContainerConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.analytics.contentProvidingTestContainer";

  constructor(config: Config<ContentProvidingTestContainer> = null) {
    super(ConfigUtils.apply(Config(ContentProvidingTestContainer), config));
  }
}

export default ContentProvidingTestContainer;
