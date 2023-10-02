import ContentPropertyNames from "@coremedia/studio-client.cap-rest-client/content/ContentPropertyNames";
import StatefulCheckbox from "@coremedia/studio-client.ext.ui-components/components/StatefulCheckbox";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import PanelSkin from "@coremedia/studio-client.ext.ui-components/skins/PanelSkin";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import SetPropertyLabelPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/SetPropertyLabelPlugin";
import ShowIssuesPlugin from "@coremedia/studio-client.main.editor-components/sdk/validation/ShowIssuesPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CustomLabels_properties from "../../CustomLabels_properties";
import CallToActionConfigurationForm from "./CallToActionConfigurationForm";

interface TeaserSettingsPropertyFieldGroupConfig extends Config<PropertyFieldGroup> {
}

class TeaserSettingsPropertyFieldGroup extends PropertyFieldGroup {
  declare Config: TeaserSettingsPropertyFieldGroupConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.teaserSettingsPropertyFieldGroup";

  static readonly #TEASER_SETTINGS_PATH: string = "localSettings.teaserSettings";

  static readonly #RENDER_LINK_TO_DETAIL_PAGE_PATH: string = TeaserSettingsPropertyFieldGroup.#TEASER_SETTINGS_PATH + ".renderLinkToDetailPage";

  constructor(config: Config<TeaserSettingsPropertyFieldGroup> = null) {
    super(ConfigUtils.apply(Config(TeaserSettingsPropertyFieldGroup, {
      itemId: "teaserSettings",
      title: CustomLabels_properties.PropertyGroup_TeaserSettings_label,
      collapsed: true,

      items: [
        Config(StatefulCheckbox, {
          itemId: "renderLinkToDetailPage",
          plugins: [
            Config(SetPropertyLabelPlugin, {
              bindTo: config.bindTo,
              labelProperty: "boxLabel",
              propertyName: TeaserSettingsPropertyFieldGroup.#RENDER_LINK_TO_DETAIL_PAGE_PATH,
            }),
            Config(BindPropertyPlugin, {
              bidirectional: true,
              ifUndefined: true,
              bindTo: config.bindTo.extendBy(ContentPropertyNames.PROPERTIES, TeaserSettingsPropertyFieldGroup.#RENDER_LINK_TO_DETAIL_PAGE_PATH),
            }),
            Config(ShowIssuesPlugin, {
              propertyName: TeaserSettingsPropertyFieldGroup.#RENDER_LINK_TO_DETAIL_PAGE_PATH,
              bindTo: config.bindTo,
            }),
            Config(BindDisablePlugin, {
              bindTo: config.bindTo,
              forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
            }),
          ],
        }),
        Config(CallToActionConfigurationForm, {
          header: false,
          useLegacyCTASettings: true,
          collapsible: false,
          ui: PanelSkin.DEFAULT.getSkin(),
        }),
      ],
    }), config));
  }
}

export default TeaserSettingsPropertyFieldGroup;
