import BoundRadioGroup from "@coremedia/studio-client.ext.ui-components/components/BoundRadioGroup";
import StatefulRadio from "@coremedia/studio-client.ext.ui-components/components/StatefulRadio";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import PropertyFieldPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldPlugin";
import DateTimePropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/DateTimePropertyField";
import DateTimePropertyFieldBase from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/DateTimePropertyFieldBase";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import ShowIssuesPlugin from "@coremedia/studio-client.main.editor-components/sdk/validation/ShowIssuesPlugin";
import Component from "@jangaroo/ext-ts/Component";
import Container from "@jangaroo/ext-ts/container/Container";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../../BlueprintDocumentTypes_properties";
import ExternallyVisibleDateFormBase from "./ExternallyVisibleDateFormBase";

interface ExternallyVisibleDateFormConfig extends Config<ExternallyVisibleDateFormBase> {
}

/**
 * This is a form panel which combines several form elements to an editor for one property named
 * externallyDisplayedDate. A combination of a radio group and a date picker field.
 */
class ExternallyVisibleDateForm extends ExternallyVisibleDateFormBase {
  declare Config: ExternallyVisibleDateFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.externallyVisibleDateForm";

  constructor(config: Config<ExternallyVisibleDateForm> = null) {
    super((()=> ConfigUtils.apply(Config(ExternallyVisibleDateForm, {
      itemId: "externallyVisibleDateForm",
      title: BlueprintDocumentTypes_properties.CMLinkable_externally_visible_date_text,
      propertyNames: ["extDisplayedDate"],
      collapsed: config.collapsed || true,

      ...ConfigUtils.append({
        plugins: [
          Config(BindPropertyPlugin, {
            bidirectional: true,
            bindTo: config.bindTo.extendBy("properties", "extDisplayedDate"),
            componentProperty: "externallyDisplayedDate",
            componentEvent: "externallyDisplayedDateChanged",
          }),
          Config(PropertyFieldPlugin, { propertyName: "extDisplayedDate" }),

        ],
      }),
      items: [
        Config(BoundRadioGroup, {
          itemId: "externallyVisibleDate",
          width: "auto",
          columns: 1,
          hideLabel: true,
          toValue: ExternallyVisibleDateFormBase.toValue,
          defaultValue: "publicationDate",
          bindTo: this.getModelExpression().extendBy("innerUseCustomExternalDisplayedDate"),
          ...ConfigUtils.append({
            plugins: [
              Config(BindDisablePlugin, {
                bindTo: config.bindTo,
                forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
              }),
              Config(ShowIssuesPlugin, {
                propertyName: "extDisplayedDate",
                bindTo: config.bindTo,
                statefulSubComponentsFunction: (): Array<any> =>
                  [
                    this.queryById(ExternallyVisibleDateFormBase.PUBLICATION_DATE_RADIO_ITEM_ID),
                    this.queryById(ExternallyVisibleDateFormBase.OWN_DATE_RADIO_ITEM_ID),
                  ],
              }),
            ],
          }),
          items: [
            Config(StatefulRadio, {
              itemId: ExternallyVisibleDateFormBase.PUBLICATION_DATE_RADIO_ITEM_ID,
              inputValue: "publicationDate",
              boxLabel: BlueprintDocumentTypes_properties.CMLinkable_externally_visible_date_use_publication_date_text,
            }),
            Config(StatefulRadio, {
              itemId: ExternallyVisibleDateFormBase.OWN_DATE_RADIO_ITEM_ID,
              inputValue: "ownDate",
              boxLabel: BlueprintDocumentTypes_properties.CMLinkable_externally_visible_date_use_custom_date_text,
            }),
          ],
        }),
        Config(Container, {
          layout: Config(HBoxLayout),
          items: [
            Config(Component, {
              width: 18,
              height: 22,
            }),
            Config(DateTimePropertyField, {
              bindTo: this.getModelExpression(),
              flex: 1,
              hideLabel: true,
              forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
              itemId: "customExternalDisplayDate",
              propertyName: "innerExternallyDisplayedDate",
              timeZoneHidden: false,
              ...ConfigUtils.append({
                plugins: [
                  Config(ShowIssuesPlugin, {
                    propertyName: "extDisplayedDate",
                    bindTo: config.bindTo,
                    statefulSubComponentsFunction: (): Array<any> =>
                      [
                        this.queryById(DateTimePropertyFieldBase.DATE_ITEM_ID),
                        this.queryById(DateTimePropertyFieldBase.TIME_ITEM_ID),
                        this.queryById(DateTimePropertyFieldBase.TIME_ZONE_ITEM_ID),
                      ],
                  }),
                ],
              }),
            }),
          ],
        }),
      ],

    }), config))());
  }
}

export default ExternallyVisibleDateForm;
