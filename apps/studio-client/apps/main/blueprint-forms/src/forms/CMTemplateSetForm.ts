import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import BlobPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/BlobPropertyField";
import FormSpacerElement from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/FormSpacerElement";
import Ext from "@jangaroo/ext-ts";
import Container from "@jangaroo/ext-ts/container/Container";
import Label from "@jangaroo/ext-ts/form/Label";
import Column from "@jangaroo/ext-ts/grid/column/Column";
import DateColumn from "@jangaroo/ext-ts/grid/column/Date";
import TableLayout from "@jangaroo/ext-ts/layout/container/Table";
import TreeColumn from "@jangaroo/ext-ts/tree/Column";
import TreePanel from "@jangaroo/ext-ts/tree/Panel";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import BlobMetadataUtil from "../util/BlobMetadataUtil";
import CollapsibleStringPropertyForm from "./containers/CollapsibleStringPropertyForm";
import MetaDataWithoutSettingsForm from "./containers/MetaDataWithoutSettingsForm";

interface CMTemplateSetFormConfig extends Config<DocumentTabPanel> {
}

/**
 * This is a custom form to upload template sets, showing some metadata within the template archive (if any).
 */
class CMTemplateSetForm extends DocumentTabPanel {
  declare Config: CMTemplateSetFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmTemplateSetForm";

  #metadata: ValueExpression = null;

  constructor(config: Config<CMTemplateSetForm> = null) {
    super((()=>{
      this.#metadata = config.bindTo.extendBy("properties.archive.metadata");
      return ConfigUtils.apply(Config(CMTemplateSetForm, {

        items: [
          Config(DocumentForm, {
            title: BlueprintTabs_properties.Tab_content_title,
            itemId: "contentTab",
            items: [
              Config(CollapsibleStringPropertyForm, {
                propertyName: "description",
                title: BlueprintDocumentTypes_properties.CMTemplateSet_description_text,
              }),
              Config(PropertyFieldGroup, {
                title: BlueprintDocumentTypes_properties.CMTemplateSet_archive_text,
                itemId: "cmTemplateArchiveForm",
                items: [
                  Config(BlobPropertyField, {
                    propertyName: "archive",
                    hideLabel: true,
                    helpText: BlueprintDocumentTypes_properties.CMTemplateSet_archive_helpText,
                  }),
                  Config(Container, {
                    items: [
                      Config(Label, { text: BlueprintDocumentTypes_properties.CMTemplateSet_metadata_archiveLabel_text + ": " }),
                      Config(FormSpacerElement),
                      Config(Label, {
                        plugins: [
                          Config(BindPropertyPlugin, {
                            componentProperty: "text",
                            bindTo: this.#metadata.extendBy("archive.label"),
                            ifUndefined: "",
                          }),
                        ],
                      }),
                    ],
                    layout: Config(TableLayout, { columns: 3 }),
                  }),
                  Config(TreePanel, {
                    ...{ colspan: 3 },
                    collapsible: false,
                    rootVisible: false,
                    bodyBorder: true,
                    useArrows: true,
                    listeners: { "rowdblclick": BlobMetadataUtil.rowDblClick },
                    lines: true,
                    plugins: [
                      Config(BindPropertyPlugin, {
                        componentProperty: "rootNode",
                        bindTo: this.#metadata.extendBy("archive.files"),
                        ifUndefined: BlobMetadataUtil.emptyRootNode(),
                        transformer: BlobMetadataUtil.convertDirectoryTree,
                      }),
                      Config(BindPropertyPlugin, {
                        componentProperty: "visible",
                        bindTo: this.#metadata.extendBy("archive.files"),
                        ifUndefined: false,
                        transformer: bind(Ext, Ext.isArray),
                      }),
                    ],
                    columns: [
                      Config(TreeColumn, {
                        dataIndex: "name",
                        flex: 1,
                        width: 300,
                        header: BlueprintDocumentTypes_properties.CMTemplateSet_metadata_files_nameHeader_text,
                        renderer: BlobMetadataUtil.fileNameRenderer,
                      }),
                      Config(DateColumn, {
                        dataIndex: "time",
                        width: 130,
                        header: BlueprintDocumentTypes_properties.CMTemplateSet_metadata_files_timeHeader_text,
                        renderer: BlobMetadataUtil.fileDateRenderer,
                      }),
                      Config(Column, {
                        dataIndex: "size",
                        width: 80,
                        align: "right",
                        header: BlueprintDocumentTypes_properties.CMTemplateSet_metadata_files_sizeHeader_text,
                        renderer: BlobMetadataUtil.fileSizeRenderer,
                      }),
                    ],
                  }),
                ],
              }),
            ],
          }),

          Config(MetaDataWithoutSettingsForm),
        ],

      }), config);
    })());
  }
}

export default CMTemplateSetForm;
