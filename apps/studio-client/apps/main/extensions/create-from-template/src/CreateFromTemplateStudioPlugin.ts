import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import NewContentMenu from "@coremedia/studio-client.main.editor-components/sdk/newcontent/NewContentMenu";
import Component from "@jangaroo/ext-ts/Component";
import Separator from "@jangaroo/ext-ts/menu/Separator";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CreateFromTemplateMenuItem from "./CreateFromTemplateMenuItem";
import CreateFromTemplateStudioPlugin_properties from "./CreateFromTemplateStudioPlugin_properties";
import OpenDialogAction from "@coremedia/studio-client.ext.ui-components/actions/OpenDialogAction";
import ConfigureQuickSearchPlugin
  from "@coremedia/studio-client.main.quicksearch-components/ConfigureQuickSearchPlugin";
import QuickSearchActionConfiguration from "@coremedia/studio-client.quicksearch-models/QuickSearchActionConfiguration";
import CreateFromTemplateDialog from "./CreateFromTemplateDialog";
import { typePage } from "@coremedia/studio-client.common-icons";

interface CreateFromTemplateStudioPluginConfig extends Config<StudioPlugin> {
}

class CreateFromTemplateStudioPlugin extends StudioPlugin {
  declare Config: CreateFromTemplateStudioPluginConfig;

  constructor(config: Config<CreateFromTemplateStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(CreateFromTemplateStudioPlugin, {

      rules: [

        Config(NewContentMenu, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(Separator, { itemId: "createFromTemplateSeparator" }),
                Config(CreateFromTemplateMenuItem),
              ],
              before: [
                Config(Component, { itemId: NewContentMenu.DYNAMIC_SEPARATOR }),
              ],
            }),
          ],
        }),
      ],

      configuration: [
        new ConfigureQuickSearchPlugin({
          actions: [
            new QuickSearchActionConfiguration({
              svgIcon: typePage,
              label: CreateFromTemplateStudioPlugin_properties.text,
              additionalKeywords: [],
              action: (): void => {
                new OpenDialogAction({
                  dialogDefaults: Config(CreateFromTemplateDialog, {}),
                }).execute();
              },
            })
          ]
        }),
      ],
    }), config));
  }
}

export default CreateFromTemplateStudioPlugin;
