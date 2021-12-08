import ContentPropertyNames from "@coremedia/studio-client.cap-rest-client/content/ContentPropertyNames";
import StatefulCheckbox from "@coremedia/studio-client.ext.ui-components/components/StatefulCheckbox";
import StatefulCheckboxGroup from "@coremedia/studio-client.ext.ui-components/components/StatefulCheckboxGroup";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import PropertyFieldPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldPlugin";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import SetPropertyLabelPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/SetPropertyLabelPlugin";
import ShowIssuesPlugin from "@coremedia/studio-client.main.editor-components/sdk/validation/ShowIssuesPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import PlayerSettingsPropertyGroupBase from "./PlayerSettingsPropertyGroupBase";

interface PlayerSettingsPropertyGroupConfig extends Config<PlayerSettingsPropertyGroupBase>, Partial<Pick<PlayerSettingsPropertyGroup,
  "columns" |
  "hideAutoplayCheckbox" |
  "hideMuteCheckbox" |
  "hideLoopCheckbox" |
  "hideHideControlsCheckbox"
>> {
}

class PlayerSettingsPropertyGroup extends PlayerSettingsPropertyGroupBase {
  declare Config: PlayerSettingsPropertyGroupConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.playersettingspropertygroup";

  /**
   * The columns of the CheckboxGroup. Defaults to 1.
   */
  columns: number = NaN;

  /**
   * Defines if the Autoplay Checkbox should be hidden. Defaults to false.
   */
  hideAutoplayCheckbox: boolean = false;

  /**
   * Defines if the Mute Checkbox should be hidden. Defaults to false.
   */
  hideMuteCheckbox: boolean = false;

  /**
   * Defines if the Loop Checkbox should be hidden. Defaults to false.
   */
  hideLoopCheckbox: boolean = false;

  /**
   * Defines if the Hide Controls Checkbox should be hidden. Defaults to false.
   */
  hideHideControlsCheckbox: boolean = false;

  static readonly #LOCAL_SETTINGS: string = "localSettings";

  static readonly #PLAYER_SETTINGS: string = "playerSettings";

  readonly #CHECKBOXGROUP_ITEM_ID: string = "playerSettingsCheckBoxGroup";

  static readonly #PLAYER_SETTINGS_PROPERTY: string = PlayerSettingsPropertyGroup.#LOCAL_SETTINGS + "." + PlayerSettingsPropertyGroup.#PLAYER_SETTINGS;

  static readonly #AUTOPLAY_PROPERTY_NAME: string = PlayerSettingsPropertyGroup.#PLAYER_SETTINGS_PROPERTY + ".autoplay";

  static readonly #MUTE_PROPERTY_NAME: string = PlayerSettingsPropertyGroup.#PLAYER_SETTINGS_PROPERTY + ".muted";

  static readonly #LOOP_PROPERTY_NAME: string = PlayerSettingsPropertyGroup.#PLAYER_SETTINGS_PROPERTY + ".loop";

  static readonly #HIDE_CONTROLS_PROPERTY_NAME: string = PlayerSettingsPropertyGroup.#PLAYER_SETTINGS_PROPERTY + ".hideControls";

  constructor(config: Config<PlayerSettingsPropertyGroup> = null) {
    super((()=> ConfigUtils.apply(Config(PlayerSettingsPropertyGroup, {

      items: [
        Config(StatefulCheckboxGroup, {
          itemId: this.#CHECKBOXGROUP_ITEM_ID,
          columns: config.columns || 1,
          items: [
            Config(StatefulCheckbox, {
              itemId: "Autoplay",
              hidden: config.hideAutoplayCheckbox,
              plugins: [
                Config(SetPropertyLabelPlugin, {
                  bindTo: config.bindTo,
                  propertyName: PlayerSettingsPropertyGroup.#AUTOPLAY_PROPERTY_NAME,
                  labelProperty: "boxLabel",
                }),
                Config(BindPropertyPlugin, {
                  bidirectional: true,
                  bindTo: config.bindTo.extendBy(ContentPropertyNames.PROPERTIES, PlayerSettingsPropertyGroup.#AUTOPLAY_PROPERTY_NAME),
                  ifUndefined: false,
                }),
                Config(ShowIssuesPlugin, {
                  bindTo: config.bindTo,
                  hideIssues: false,
                  ifUndefined: false,
                  propertyName: PlayerSettingsPropertyGroup.#AUTOPLAY_PROPERTY_NAME,
                }),
                Config(BindDisablePlugin, {
                  forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                  bindTo: config.bindTo,
                }),
                Config(PropertyFieldPlugin, { propertyName: PlayerSettingsPropertyGroup.#AUTOPLAY_PROPERTY_NAME }),
              ],
            }),
            Config(StatefulCheckbox, {
              itemId: "Mute",
              hidden: config.hideMuteCheckbox,
              plugins: [
                Config(SetPropertyLabelPlugin, {
                  bindTo: config.bindTo,
                  propertyName: PlayerSettingsPropertyGroup.#MUTE_PROPERTY_NAME,
                  labelProperty: "boxLabel",
                }),
                Config(BindPropertyPlugin, {
                  bidirectional: true,
                  ifUndefined: false,
                  bindTo: config.bindTo.extendBy(ContentPropertyNames.PROPERTIES, PlayerSettingsPropertyGroup.#MUTE_PROPERTY_NAME),
                }),
                Config(ShowIssuesPlugin, {
                  bindTo: config.bindTo,
                  ifUndefined: false,
                  hideIssues: false,
                  propertyName: PlayerSettingsPropertyGroup.#MUTE_PROPERTY_NAME,
                }),
                Config(BindDisablePlugin, {
                  forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                  bindTo: config.bindTo,
                }),
                Config(PropertyFieldPlugin, { propertyName: PlayerSettingsPropertyGroup.#MUTE_PROPERTY_NAME }),
              ],
            }),
            Config(StatefulCheckbox, {
              itemId: "Loop",
              hidden: config.hideLoopCheckbox,
              plugins: [
                Config(SetPropertyLabelPlugin, {
                  bindTo: config.bindTo,
                  propertyName: PlayerSettingsPropertyGroup.#LOOP_PROPERTY_NAME,
                  labelProperty: "boxLabel",
                }),
                Config(BindPropertyPlugin, {
                  bidirectional: true,
                  ifUndefined: false,
                  bindTo: config.bindTo.extendBy(ContentPropertyNames.PROPERTIES, PlayerSettingsPropertyGroup.#LOOP_PROPERTY_NAME),
                }),
                Config(ShowIssuesPlugin, {
                  bindTo: config.bindTo,
                  ifUndefined: false,
                  hideIssues: false,
                  propertyName: PlayerSettingsPropertyGroup.#LOOP_PROPERTY_NAME,
                }),
                Config(BindDisablePlugin, {
                  forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                  bindTo: config.bindTo,
                }),
                Config(PropertyFieldPlugin, { propertyName: PlayerSettingsPropertyGroup.#LOOP_PROPERTY_NAME }),
              ],
            }),
            Config(StatefulCheckbox, {
              itemId: "HideControls",
              hidden: config.hideHideControlsCheckbox,
              plugins: [
                Config(SetPropertyLabelPlugin, {
                  bindTo: config.bindTo,
                  propertyName: PlayerSettingsPropertyGroup.#HIDE_CONTROLS_PROPERTY_NAME,
                  labelProperty: "boxLabel",
                }),
                Config(BindPropertyPlugin, {
                  bidirectional: true,
                  ifUndefined: false,
                  bindTo: config.bindTo.extendBy(ContentPropertyNames.PROPERTIES, PlayerSettingsPropertyGroup.#HIDE_CONTROLS_PROPERTY_NAME),
                }),
                Config(ShowIssuesPlugin, {
                  bindTo: config.bindTo,
                  hideIssues: false,
                  ifUndefined: false,
                  propertyName: PlayerSettingsPropertyGroup.#HIDE_CONTROLS_PROPERTY_NAME,
                }),
                Config(BindDisablePlugin, {
                  forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                  bindTo: config.bindTo,
                }),
                Config(PropertyFieldPlugin, { propertyName: PlayerSettingsPropertyGroup.#HIDE_CONTROLS_PROPERTY_NAME }),
              ],
            }),
          ],
          defaultType: StatefulCheckbox.xtype,
          defaults: Config<StatefulCheckbox>({ hideLabel: true }),
        }),
      ],
    }), config))());
  }
}

export default PlayerSettingsPropertyGroup;
