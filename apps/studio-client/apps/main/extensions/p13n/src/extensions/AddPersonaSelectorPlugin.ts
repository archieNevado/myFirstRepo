import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import NestedRulesPlugin from "@coremedia/studio-client.ext.ui-components/plugins/NestedRulesPlugin";
import PersonaSelector from "@coremedia/studio-client.main.cap-personalization-ui/persona/selector/PersonaSelector";
import PreviewPanel from "@coremedia/studio-client.main.editor-components/sdk/preview/PreviewPanel";
import PreviewPanelToolbar from "@coremedia/studio-client.main.editor-components/sdk/preview/PreviewPanelToolbar";
import { as, cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import PersonalizationPlugIn_properties from "../PersonalizationPlugIn_properties";
import Addsitespecificpath from "../plugin/Addsitespecificpath";

interface AddPersonaSelectorPluginConfig extends Config<NestedRulesPlugin> {
}

class AddPersonaSelectorPlugin extends NestedRulesPlugin {
  declare Config: AddPersonaSelectorPluginConfig;

  constructor(config: Config<AddPersonaSelectorPlugin> = null) {
    const myPreviewPanel = as(config.cmp, PreviewPanel);
    const previewPanelConfig = cast(PreviewPanel, myPreviewPanel.initialConfig);
    super(ConfigUtils.apply(Config(AddPersonaSelectorPlugin, {

      rules: [
        Config(PreviewPanelToolbar, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(PersonaSelector, {
                  paths: ["/Settings/Options/Personalization/Profiles"],
                  itemId: "testProfileSelector",
                  defaultGroupHeaderLabel: PersonalizationPlugIn_properties.group_header_label_system_specific,
                  contentValueExpression: myPreviewPanel.getCurrentPreviewContentValueExpression(),
                  ...ConfigUtils.append({
                    plugins: [
                      Config(Addsitespecificpath, {
                        path: "Options/Personalization/Profiles",
                        activeContentValueExpression: previewPanelConfig.bindTo,
                        groupHeaderLabel: PersonalizationPlugIn_properties.group_header_label_site_specific,
                      }),
                    ],
                  }),
                }),
              ],
            }),

          ],
        }),
      ],
    }), config));
  }
}

export default AddPersonaSelectorPlugin;
