import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AddSiteSpecificPathPlugin from "./AddSiteSpecificPathPlugin";

interface AddsitespecificpathConfig extends Config<AddSiteSpecificPathPlugin>, Partial<Pick<Addsitespecificpath,
  "activeContentValueExpression" |
  "path" |
  "groupHeaderLabel"
>> {
}

/**
 * Plugin that adds a path relative to the current user's home folder to a PersonaSelector.
 */
class Addsitespecificpath extends AddSiteSpecificPathPlugin {
  declare Config: AddsitespecificpathConfig;

  constructor(config: Config<Addsitespecificpath> = null) {
    super(ConfigUtils.apply(Config(Addsitespecificpath), config));
  }

  /**
   * A value expression evaluating to the active Content of the preview panel.
   */
  activeContentValueExpression: ValueExpression = null;

  /**
   * path containing a placeholder for the site that will be added to a 'PersonaSelector'
   */
  path: string = null;

  /**
   * optional header label that will visible inside the PersonaSelector
   */
  groupHeaderLabel: string = null;
}

export default Addsitespecificpath;
