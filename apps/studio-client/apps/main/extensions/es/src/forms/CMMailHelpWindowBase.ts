import StringUtil from "@jangaroo/ext-ts/String";
import Template from "@jangaroo/ext-ts/Template";
import Window from "@jangaroo/ext-ts/window/Window";
import Config from "@jangaroo/runtime/Config";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import ElasticSocialStudioPlugin_properties from "../ElasticSocialStudioPlugin_properties";

interface CMMailHelpWindowBaseConfig extends Config<Window> {
}

class CMMailHelpWindowBase extends Window {
  declare Config: CMMailHelpWindowBaseConfig;

  static readonly ID: string = "cmmail-help-window";

  static readonly TABLE_BODY_ID: string = "cmmail-help-table";

  static readonly TABLE: string = StringUtil.format(
    "<table><thead><tr><th>{0}</th><th>{1}</th></tr></thead><tbody id='{2}'></tbody></table><br/>",
    ElasticSocialStudioPlugin_properties.cmmail_help_window_value,
    ElasticSocialStudioPlugin_properties.cmmail_help_window_description,
    CMMailHelpWindowBase.TABLE_BODY_ID,
  );

  static readonly ROW: string = "<tr><td>{value}</td><td>{description}</td></tr>";

  static readonly KEY_PREFIX: string = "cmmail_help_window_value_";

  static readonly KEY_PATTERN: string = CMMailHelpWindowBase.KEY_PREFIX + "(.+)";

  constructor(config: Config<CMMailHelpWindowBase> = null) {
    super(config);
    this.applyTemplate();
  }

  applyTemplate(): void {
    const rowTpl = new Template(CMMailHelpWindowBase.ROW);
    rowTpl.compile();

    //noinspection JSMismatchedCollectionQueryUpdateInspection,JSMismatchedCollectionQueryUpdate
    const keys = [];
    for (const key in resourceManager.getResourceBundle(null, ElasticSocialStudioPlugin_properties).content) {
      //noinspection JSUnfilteredForInLoop
      const match = key.match(CMMailHelpWindowBase.KEY_PATTERN);
      if (match) {
        keys.push(match[1]);
      }
    }
    keys.sort();
    keys.forEach((key: string): void => {
      //noinspection JSUnusedGlobalSymbols
      rowTpl.append(CMMailHelpWindowBase.TABLE_BODY_ID, {
        value: StringUtil.format("${{0}}", key),
        description: ElasticSocialStudioPlugin_properties[CMMailHelpWindowBase.KEY_PREFIX + key],
      });
    });
  }
}

export default CMMailHelpWindowBase;
