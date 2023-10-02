import CMLocTaxonomyForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/CMLocTaxonomyForm";
import ContentTypes_properties from "@coremedia/studio-client.cap-base-models/content/ContentTypes_properties";
import NestedRulesPlugin from "@coremedia/studio-client.ext.ui-components/plugins/NestedRulesPlugin";
import ReplaceItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/ReplaceItemsPlugin";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import OSMPropertyField from "./OSMPropertyField";
import OSMStudioPlugin_properties from "./OSMStudioPlugin_properties";

interface OSMStudioPluginConfig extends Config<StudioPlugin> {
}

class OSMStudioPlugin extends StudioPlugin {
  declare Config: OSMStudioPluginConfig;

  constructor(config: Config<OSMStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(OSMStudioPlugin, {

      rules: [

        Config(CMLocTaxonomyForm, {
          plugins: [
            Config(NestedRulesPlugin, {
              rules: [
                Config(DocumentForm, {
                  itemId: "CMLocTaxonomy",
                  plugins: [
                    Config(ReplaceItemsPlugin, {
                      items: [
                        Config(OSMPropertyField, {
                          propertyName: "latitudeLongitude",
                          itemId: "latitudeLongitude",
                        }),
                      ],
                    }),
                  ],
                }),
              ],
            }),
          ],
        }),

      ],

      configuration: [

        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ContentTypes_properties),
          source: resourceManager.getResourceBundle(null, OSMStudioPlugin_properties),
        }),

      ],

    }), config));
  }
}

export default OSMStudioPlugin;
