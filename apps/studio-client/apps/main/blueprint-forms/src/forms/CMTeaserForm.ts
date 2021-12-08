import StructContentLinkListWrapper from "@coremedia/studio-client.content-link-list-models/StructContentLinkListWrapper";
import NameColumn from "@coremedia/studio-client.ext.cap-base-components/columns/NameColumn";
import StatusColumn from "@coremedia/studio-client.ext.cap-base-components/columns/StatusColumn";
import TypeIconColumn from "@coremedia/studio-client.ext.cap-base-components/columns/TypeIconColumn";
import LinkListThumbnailColumn from "@coremedia/studio-client.ext.content-link-list-components/columns/LinkListThumbnailColumn";
import AnnotatedLinkListWidget from "@coremedia/studio-client.ext.ui-components/components/AnnotatedLinkListWidget";
import RemoveItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/RemoveItemsPlugin";
import PanelSkin from "@coremedia/studio-client.ext.ui-components/skins/PanelSkin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import ILinkListWrapper from "@coremedia/studio-client.link-list-models/ILinkListWrapper";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import QuickCreateToolbarButton from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreateToolbarButton";
import AnnotatedLinkListHelper from "@coremedia/studio-client.main.editor-components/sdk/util/AnnotatedLinkListHelper";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import ValidityColumn from "./columns/ValidityColumn";
import DefaultExtraDataForm from "./components/DefaultExtraDataForm";
import CallToActionConfigurationForm from "./containers/CallToActionConfigurationForm";
import MediaDocumentForm from "./containers/MediaDocumentForm";
import MetaDataWithoutSearchableForm from "./containers/MetaDataWithoutSearchableForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";
import TeaserDocumentForm from "./containers/TeaserDocumentForm";
import ValidityDocumentForm from "./containers/ValidityDocumentForm";
import ViewTypeSelectorForm from "./containers/ViewTypeSelectorForm";

interface CMTeaserFormConfig extends Config<DocumentTabPanel> {
}

class CMTeaserForm extends DocumentTabPanel {
  declare Config: CMTeaserFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmTeaserForm";

  static readonly TARGET_ANNOTATION_WIDGET_ITEM_ID: string = "target-annotation-widget";

  #structContentLinkListWrapper: StructContentLinkListWrapper = null;

  constructor(config: Config<CMTeaserForm> = null) {
    super((()=> ConfigUtils.apply(Config(CMTeaserForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMTeaser_targets_text,
              itemId: "cmTeaserTargetsForm",
              items: [
                Config(LinkListPropertyField, {
                  propertyName: "targets",
                  showThumbnails: true,
                  hideLabel: true,
                  bindTo: config.bindTo,
                  linkListWrapper: this.#getStructContentLinkListWrapper(config),
                  linkType: "CMTeasable",
                  rowWidgetsAnnotatedPredicates: [CallToActionConfigurationForm.isAnnotated],
                  additionalToolbarItems: [
                    Config(Separator),
                    Config(QuickCreateToolbarButton, {
                      contentType: "CMArticle",
                      bindTo: config.bindTo,
                      linkListWrapper: this.#getStructContentLinkListWrapper(config),
                    }),
                  ],
                  fields: [
                    Config(DataField, {
                      name: ValidityColumn.STATUS_ID,
                      mapping: "",
                      convert: ValidityColumn.convert,
                    }),
                  ],
                  columns: [
                    Config(LinkListThumbnailColumn),
                    Config(TypeIconColumn),
                    Config(NameColumn, { flex: 1 }),
                    Config(ValidityColumn),
                    Config(StatusColumn),
                  ],
                  rowWidget: Config(AnnotatedLinkListWidget, {
                    itemId: CMTeaserForm.TARGET_ANNOTATION_WIDGET_ITEM_ID,
                    items: [
                      Config(CallToActionConfigurationForm, {
                        bindTo: config.bindTo,
                        ui: PanelSkin.DEFAULT.getSkin(),
                        header: false,
                        forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                      }),
                    ],
                  }),
                }),
              ],
            }),
            Config(TeaserDocumentForm, {
              ...ConfigUtils.append({
                plugins: [
                  Config(RemoveItemsPlugin, {
                    recursive: true,
                    items: [
                      Config(CallToActionConfigurationForm),
                    ],
                  }),
                ],
              }),
            }),
            Config(MediaDocumentForm),
            Config(ViewTypeSelectorForm),
            Config(ValidityDocumentForm),
          ],
        }),
        Config(DefaultExtraDataForm),
        Config(MultiLanguageDocumentForm),
        Config(MetaDataWithoutSearchableForm),
      ],

    }), config))());
  }

  #getStructContentLinkListWrapper(config: Config<CMTeaserForm>): ILinkListWrapper {
    if (!this.#structContentLinkListWrapper) {
      this.#structContentLinkListWrapper = AnnotatedLinkListHelper.createStructContentLinkListWrapper(config.bindTo, config.forceReadOnlyValueExpression);
    }
    return this.#structContentLinkListWrapper;
  }
}

export default CMTeaserForm;
