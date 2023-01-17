import CatalogObjectPropertyNames from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObjectPropertyNames";
import CKEditorTypes from "@coremedia/studio-client.ckeditor-common/CKEditorTypes";
import RichTextAreaConstants from "@coremedia/studio-client.ckeditor-common/RichTextAreaConstants";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import richTextAreaRegistry
  from "@coremedia/studio-client.ext.richtext-components-toolkit/richtextArea/richTextAreaRegistry";
import AdvancedFieldContainer from "@coremedia/studio-client.ext.ui-components/components/AdvancedFieldContainer";
import TextFieldContainer from "@coremedia/studio-client.ext.ui-components/components/TextFieldContainer";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import createComponentSelector from "@coremedia/studio-client.ext.ui-components/util/createComponentSelector";
import PropertyFieldPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldPlugin";
import BindReadOnlyPlugin
  from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindReadOnlyPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";
import CommerceDetailsFormBase from "./CommerceDetailsFormBase";

interface CommerceDetailsFormConfig extends Config<CommerceDetailsFormBase> {
}

class CommerceDetailsForm extends CommerceDetailsFormBase {
  declare Config: CommerceDetailsFormConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.commerceDetailsForm";

  static readonly SHORT_DESCRIPTION_COLLAPSIBLE_ITEM_ID: string = "shortDescriptionCollapsibleItemId";

  static readonly SHORT_DESCRIPTION_FIELDCONTAINER_ITEM_ID: string = "shortDescriptionFieldContainerItemId";

  static readonly LONG_DESCRIPTION_FIELDCONTAINER_ITEM_ID: string = "longDescriptionFieldContainerItemId";

  constructor(config: Config<CommerceDetailsForm> = null) {
    super(ConfigUtils.apply(Config(CommerceDetailsForm, {
      title: LivecontextStudioPlugin_properties.Commerce_PropertyGroup_details_title,

      items: [
        Config(TextFieldContainer, {
          itemId: CatalogObjectPropertyNames.NAME,
          fieldLabel: LivecontextStudioPlugin_properties.Commerce_title_label,
          propertyPath: CatalogObjectPropertyNames.NAME,
          readOnly: true,
        }),
        Config(AdvancedFieldContainer, {
          itemId: CommerceDetailsForm.LONG_DESCRIPTION_FIELDCONTAINER_ITEM_ID,
          fieldLabel: LivecontextStudioPlugin_properties.Commerce_description_label,
          defaultField: createComponentSelector().itemId(CatalogObjectPropertyNames.LONG_DESCRIPTION).build(),
          items: [
            richTextAreaRegistry.getRichTextAreaConfig(RichTextAreaConstants.CKE5_EDITOR, {
              itemId: CatalogObjectPropertyNames.LONG_DESCRIPTION,
              ...{ focusable: true },
              tabIndex: 0,
              minHeight: 23 * 5,
              editorType: CKEditorTypes.HTML_VIEWER_EDITOR_TYPE,
              plugins: [
                Config(BindPropertyPlugin, {
                  bidirectional: false,
                  bindTo: config.bindTo.extendBy(CatalogObjectPropertyNames.LONG_DESCRIPTION),
                }),
                Config(PropertyFieldPlugin, { propertyName: CatalogObjectPropertyNames.LONG_DESCRIPTION }),
                Config(BindReadOnlyPlugin, {
                  forceReadOnlyValueExpression: ValueExpressionFactory.createFromValue(true),
                  bindTo: config.bindTo,
                }),
              ],
            }),
          ],
        }),
        Config(CommerceDetailsFormBase, {
          itemId: CommerceDetailsForm.SHORT_DESCRIPTION_COLLAPSIBLE_ITEM_ID,
          contentBindTo: config.contentBindTo,
          title: LivecontextStudioPlugin_properties.Commerce_shortDescription_label,
          collapsed: true,
          items: [
            Config(AdvancedFieldContainer, {
              itemId: CommerceDetailsForm.SHORT_DESCRIPTION_FIELDCONTAINER_ITEM_ID,
              fieldLabel: LivecontextStudioPlugin_properties.Commerce_shortDescription_label,
              defaultField: createComponentSelector().itemId(CatalogObjectPropertyNames.SHORT_DESCRIPTION).build(),
              items: [
                richTextAreaRegistry.getRichTextAreaConfig(RichTextAreaConstants.CKE5_EDITOR, {
                  itemId: CatalogObjectPropertyNames.SHORT_DESCRIPTION,
                  ...{ focusable: true },
                  tabIndex: 0,
                  editorType: CKEditorTypes.HTML_VIEWER_EDITOR_TYPE,
                  height: 100,
                  plugins: [
                    Config(BindPropertyPlugin, {
                      bidirectional: false,
                      bindTo: config.bindTo.extendBy(CatalogObjectPropertyNames.SHORT_DESCRIPTION),
                    }),
                    Config(PropertyFieldPlugin, { propertyName: CatalogObjectPropertyNames.SHORT_DESCRIPTION }),
                    Config(BindReadOnlyPlugin, {
                      forceReadOnlyValueExpression: ValueExpressionFactory.createFromValue(true),
                      bindTo: config.bindTo,
                    }),
                  ],
                }),
              ],
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default CommerceDetailsForm;
