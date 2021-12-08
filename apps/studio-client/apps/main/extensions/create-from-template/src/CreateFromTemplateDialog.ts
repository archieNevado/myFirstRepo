import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import AdvancedFieldContainer from "@coremedia/studio-client.ext.ui-components/components/AdvancedFieldContainer";
import StatefulTextField from "@coremedia/studio-client.ext.ui-components/components/StatefulTextField";
import ConfigBasedValueExpression from "@coremedia/studio-client.ext.ui-components/data/ConfigBasedValueExpression";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import BlockEnterPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BlockEnterPlugin";
import VerticalSpacingPlugin from "@coremedia/studio-client.ext.ui-components/plugins/VerticalSpacingPlugin";
import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import LabelableSkin from "@coremedia/studio-client.ext.ui-components/skins/LabelableSkin";
import WindowSkin from "@coremedia/studio-client.ext.ui-components/skins/WindowSkin";
import NavigationLinkFieldWrapper from "@coremedia/studio-client.main.bpbase-studio-components/navigationlink/NavigationLinkFieldWrapper";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import FolderChooserListView from "@coremedia/studio-client.main.editor-components/sdk/folderchooser/FolderChooserListView";
import Button from "@jangaroo/ext-ts/button/Button";
import FieldContainer from "@jangaroo/ext-ts/form/FieldContainer";
import Labelable from "@jangaroo/ext-ts/form/Labelable";
import AnchorLayout from "@jangaroo/ext-ts/layout/container/Anchor";
import VBoxLayout from "@jangaroo/ext-ts/layout/container/VBox";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import { AnyFunction } from "@jangaroo/runtime/types";
import CreateFromTemplateDialogBase from "./CreateFromTemplateDialogBase";
import CreateFromTemplateStudioPluginSettings_properties from "./CreateFromTemplateStudioPluginSettings_properties";
import CreateFromTemplateStudioPlugin_properties from "./CreateFromTemplateStudioPlugin_properties";
import TemplateBeanListChooser from "./TemplateBeanListChooser";
import ProcessingData from "./model/ProcessingData";

interface CreateFromTemplateDialogConfig extends Config<CreateFromTemplateDialogBase>, Partial<Pick<CreateFromTemplateDialog,
  "positionFun"
>> {
}

class CreateFromTemplateDialog extends CreateFromTemplateDialogBase {
  declare Config: CreateFromTemplateDialogConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.template.config.createFromTemplateDialog";

  positionFun: AnyFunction = null;

  constructor(config: Config<CreateFromTemplateDialog> = null) {
    super((()=> ConfigUtils.apply(Config(CreateFromTemplateDialog, {
      title: CreateFromTemplateStudioPlugin_properties.text,
      id: "createFromTemplate",
      resizable: true,
      stateful: true,
      stateId: "createFromTemplateDialogState",
      resizeHandles: "e",
      minWidth: 401,
      width: 401,
      x: config.positionFun ? config.positionFun()[0] : 113,
      y: config.positionFun ? config.positionFun()[1] : 84,
      constrainHeader: true,
      ui: WindowSkin.GRID_200.getSkin(),

      items: [
        Config(Panel, {
          itemId: CreateFromTemplateDialogBase.EDITOR_CONTAINER_ITEM_ID,
          scrollable: "y",
          items: [
            Config(FieldContainer, {
              fieldLabel: CreateFromTemplateStudioPlugin_properties.name_label,
              items: [
                Config(AdvancedFieldContainer, {
                  ui: LabelableSkin.PLAIN_LABEL.getSkin(),
                  anchor: "100%",
                  labelAlign: "top",
                  labelSeparator: "",
                  fieldLabel: CreateFromTemplateStudioPlugin_properties.name_text,
                  hideLabel: false,
                  items: [
                    Config(StatefulTextField, {
                      name: ProcessingData.NAME_PROPERTY,
                      anchor: "100%",
                      itemId: CreateFromTemplateDialogBase.NAME_FIELD_ID,
                      allowBlank: false,
                      emptyText: CreateFromTemplateStudioPlugin_properties.name_empty_text,
                      checkChangeBuffer: 500,
                      plugins: [
                        Config(BindPropertyPlugin, {
                          bidirectional: true,
                          bindTo: new ConfigBasedValueExpression({
                            context: this.getModel(),
                            expression: ProcessingData.NAME_PROPERTY,
                          }),
                        }),
                        Config(BlockEnterPlugin),
                      ],
                    }),
                  ],
                  layout: Config(AnchorLayout),
                }),
              ],
              layout: Config(AnchorLayout),
            }),

            Config(NavigationLinkFieldWrapper, {
              label: CreateFromTemplateStudioPlugin_properties.parent_label,
              doctype: CreateFromTemplateStudioPluginSettings_properties.doctype,
              model: this.getModel(),
              labelAlign: "top",
              itemId: CreateFromTemplateDialogBase.PARENT_PAGE_FIELD_ID,
              propertyName: CreateFromTemplateStudioPluginSettings_properties.parent_property,
            }),

            Config(FieldContainer, {
              fieldLabel: CreateFromTemplateStudioPlugin_properties.choose_template_text,
              items: [
                Config(TemplateBeanListChooser, {
                  height: 230,
                  itemId: CreateFromTemplateDialogBase.TEMPLATE_CHOOSER_FIELD_ID,
                  validate: bind(this, this.templateChooserNonEmptyValidator),
                  configPaths: CreateFromTemplateStudioPlugin_properties.template_paths,
                  bindTo: ValueExpressionFactory.createFromValue(this.getModel()),
                }),
              ],
              layout: Config(AnchorLayout),
            }),
            Config(FieldContainer, {
              items: [
                Config(FolderChooserListView, {
                  bindTo: this.getFolderValueExpression(),
                  title: CreateFromTemplateStudioPlugin_properties.channel_folder_text,
                  itemId: CreateFromTemplateDialogBase.BASE_FOLDER_CHOOSER_ID,
                  contentTypeToCreate: this.getContentType(CreateFromTemplateStudioPluginSettings_properties.doctype),
                  folderPathsExpression: ValueExpressionFactory.createFromFunction(bind(this, this.getNavigationFolders)),
                }),
              ],
            }),
            Config(FieldContainer, {
              items: [
                Config(FolderChooserListView, {
                  title: CreateFromTemplateStudioPlugin_properties.editorial_folder_text,
                  bindTo: this.getEditorialFolderValueExpression(),
                  itemId: CreateFromTemplateDialogBase.CONTENT_BASE_FOLDER_CHOOSER_ID,
                  contentTypeToCreate: this.getContentType("CMArticle"),
                  folderPathsExpression: ValueExpressionFactory.createFromFunction(bind(this, this.getEditorialFolders)),
                }),
              ],
            }),
          ],
          layout: Config(VBoxLayout, { align: "stretch" }),
          defaultType: Labelable["xtype"],
          defaults: Config<Labelable>({
            labelSeparator: "",
            labelAlign: "top",
          }),
          plugins: [
            Config(VerticalSpacingPlugin),
          ],
        }),
      ],
      layout: Config(VBoxLayout, { align: "stretch" }),
      buttons: [
        Config(Button, {
          itemId: "createBtn",
          ui: ButtonSkin.FOOTER_PRIMARY.getSkin(),
          scale: "small",
          text: Editor_properties.dialog_defaultCreateButton_text,
          handler: bind(this, this.handleSubmit),
          plugins: [
            Config(BindPropertyPlugin, {
              componentProperty: "disabled",
              bindTo: this.getDisabledValueExpression(),
            }),
          ],
        }),
        Config(Button, {
          itemId: "cancelBtn",
          ui: ButtonSkin.FOOTER_SECONDARY.getSkin(),
          scale: "small",
          text: Editor_properties.dialog_defaultCancelButton_text,
          handler: bind(this, this.close),
        }),
      ],

    }), config))());
  }
}

export default CreateFromTemplateDialog;
