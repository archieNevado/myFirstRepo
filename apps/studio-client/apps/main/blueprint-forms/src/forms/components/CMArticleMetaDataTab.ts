import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import DefaultExtraDataForm from "./DefaultExtraDataForm";

interface CMArticleMetaDataTabConfig extends Config<DefaultExtraDataForm> {
}

/**
 * @deprecated since 1804.1. Use {@link com.coremedia.blueprint.studio.forms.components.DefaultExtraDataForm} instead.
 */
class CMArticleMetaDataTab extends DefaultExtraDataForm {
  declare Config: CMArticleMetaDataTabConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmArticleMetaDataTab";

  constructor(config: Config<CMArticleMetaDataTab> = null) {
    super(ConfigUtils.apply(Config(CMArticleMetaDataTab), config));
  }
}

export default CMArticleMetaDataTab;
