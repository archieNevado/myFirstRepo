import Struct from "@coremedia/studio-client.cap-rest-client/struct/Struct";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import StatefulCheckbox from "@coremedia/studio-client.ext.ui-components/components/StatefulCheckbox";
import StatefulTextField from "@coremedia/studio-client.ext.ui-components/components/StatefulTextField";
import HelpIconMixin from "@coremedia/studio-client.ext.ui-components/mixins/HelpIconMixin";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import Binding from "@coremedia/studio-client.ext.ui-components/plugins/Binding";
import BlockEnterPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BlockEnterPlugin";
import RemoveItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/RemoveItemsPlugin";
import CheckboxSkin from "@coremedia/studio-client.ext.ui-components/skins/CheckboxSkin";
import IAnnotatedLinkListProvider from "@coremedia/studio-client.link-list-models/IAnnotatedLinkListProvider";
import OnlyIf from "@coremedia/studio-client.main.editor-components/sdk/plugins/OnlyIf";
import PropertyFieldPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldPlugin";
import BindReadOnlyPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindReadOnlyPlugin";
import ShowIssuesPlugin from "@coremedia/studio-client.main.editor-components/sdk/validation/ShowIssuesPlugin";
import Ext from "@jangaroo/ext-ts";
import Component from "@jangaroo/ext-ts/Component";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../../BlueprintDocumentTypes_properties";
import CallToActionConfigurationFormBase from "./CallToActionConfigurationFormBase";
import CallToActionViewModel from "./CallToActionViewModel";

interface CallToActionConfigurationFormConfig extends Config<CallToActionConfigurationFormBase> {
}

/**
 * This is a form panel which combines several form elements to an editor for call to action behaviour.
 */
class CallToActionConfigurationForm extends CallToActionConfigurationFormBase {
  declare Config: CallToActionConfigurationFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.callToActionConfigurationForm";

  constructor(config: Config<CallToActionConfigurationForm> = null) {
    super((()=> ConfigUtils.apply(Config(CallToActionConfigurationForm, {
      itemId: "callToActionConfigurationForm",
      title: BlueprintDocumentTypes_properties.CMTeasable_callToActionConfiguration_text,
      propertyNames: [],
      collapsed: config.collapsed || true,
      settingsVE: config.bindTo.extendBy("properties", "localSettings"),

      ...ConfigUtils.append({
        plugins: [
          Config(Binding, {
            source: "ctaSettings.callToActionEnabled",
            destination: "ctaViewModel.CTAEnabled",
            twoWay: true,
          }),
          Config(Binding, {
            source: "ctaSettings.callToActionCustomText",
            destination: "ctaViewModel.CTAText",
            twoWay: true,
          }),
          Config(Binding, {
            source: "ctaSettings.callToActionHash",
            destination: "ctaViewModel.CTAHash",
            twoWay: true,
          }),
          Config(OnlyIf, {
            condition: (): boolean => config.useLegacyCTASettings,
            then: [
              Config(PropertyFieldPlugin, { propertyName: "callToActionConfiguration" }),
              Config(ShowIssuesPlugin, {
                propertyName: "callToActionConfiguration",
                bindTo: config.bindTo,
              }),
              Config(RemoveItemsPlugin, {
                items: [
                  Config(Component, { itemId: CallToActionConfigurationFormBase.HASH_ITEM_ID }),
                ],
              }),
            ],
          }),
        ],
      }),
      items: [
        Config(StatefulCheckbox, {
          ui: ConfigUtils.asString(!config.useLegacyCTASettings ? CheckboxSkin.BOLD.getSkin() : CheckboxSkin.DEFAULT.getSkin()),
          boxLabel: BlueprintDocumentTypes_properties.CMTeasable_callToActionConfiguration_enable_cta_text,
          plugins: [
            Config(BindPropertyPlugin, {
              bidirectional: true,
              bindTo: ValueExpressionFactory.create(CallToActionViewModel.CTA_ENABLED_PROPERTY_NAME, this.ctaViewModel),
            }),
            Config(BindReadOnlyPlugin, {
              bindTo: config.bindTo,
              forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
            }),
            Config(OnlyIf, {
              condition: (): boolean => config.useLegacyCTASettings,
              then: Config(ShowIssuesPlugin, {
                propertyName: "callToActionConfiguration",
                bindTo: config.bindTo,
              }),
            }),
          ],
        }),
        Config(StatefulTextField, {
          fieldLabel: BlueprintDocumentTypes_properties.CMTeasable_CTAText_text,
          flex: 1,
          hideLabel: config.useLegacyCTASettings,
          itemId: CallToActionConfigurationFormBase.TEXT_ITEM_ID,
          style: { marginLeft: "22px" },
          checkChangeBuffer: 500,
          plugins: [
            Config(BindPropertyPlugin, {
              bindTo: ValueExpressionFactory.create(CallToActionViewModel.CTA_TEXT_PROPERTY_NAME, this.ctaViewModel),
              bidirectional: true,
            }),
            /* Do not use BindDisablePlugin here, as it expects content as a value of bindTo.getValue() */
            Config(BindPropertyPlugin, {
              componentProperty: "disabled",
              bindTo: ValueExpressionFactory.create(CallToActionViewModel.CTA_ENABLED_PROPERTY_NAME, this.ctaViewModel),
              transformer: (value: boolean): boolean => !value,
            }),
            Config(BindPropertyPlugin, {
              componentProperty: "emptyText",
              bindTo: ValueExpressionFactory.create(CallToActionViewModel.CTA_ENABLED_PROPERTY_NAME, this.ctaViewModel),
              transformer: bind(this, this.#transformEnabledToEmptyTextForText),
            }),
            Config(BlockEnterPlugin),
            Config(BindReadOnlyPlugin, {
              bindTo: config.bindTo,
              forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
            }),
            Config(OnlyIf, {
              condition: (): boolean => config.useLegacyCTASettings,
              then: Config(ShowIssuesPlugin, {
                propertyName: "callToActionConfiguration",
                bindTo: config.bindTo,
              }),
            }),
          ],
        }),
        Config(StatefulTextField, {
          fieldLabel: BlueprintDocumentTypes_properties.CMTeasable_CTAHash_text,
          flex: 1,
          itemId: CallToActionConfigurationFormBase.HASH_ITEM_ID,
          style: { marginLeft: "22px" },
          checkChangeBuffer: 500,
          helpIconTarget: HelpIconMixin.HELP_ICON_TARGET_LABEL,
          helpIconText: BlueprintDocumentTypes_properties.CMTeasable_CTAHash_helpText,
          plugins: [
            Config(BindPropertyPlugin, {
              bindTo: ValueExpressionFactory.create(CallToActionViewModel.CTA_HASH_PROPERTY_NAME, this.ctaViewModel),
              bidirectional: true,
            }),
            /* Do not use BindDisablePlugin here, as it expects content as a value of bindTo.getValue() */
            Config(BindPropertyPlugin, {
              componentProperty: "disabled",
              bindTo: ValueExpressionFactory.create(CallToActionViewModel.CTA_ENABLED_PROPERTY_NAME, this.ctaViewModel),
              transformer: (value: boolean): boolean => !value,
            }),
            Config(BindPropertyPlugin, {
              componentProperty: "emptyText",
              bindTo: ValueExpressionFactory.create(CallToActionViewModel.CTA_ENABLED_PROPERTY_NAME, this.ctaViewModel),
              transformer: bind(this, this.#transformEnabledToEmptyTextForHash),
            }),
            Config(BlockEnterPlugin),
            Config(BindReadOnlyPlugin, {
              bindTo: config.bindTo,
              forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
            }),
            Config(OnlyIf, {
              condition: (): boolean => config.useLegacyCTASettings,
              then: Config(ShowIssuesPlugin, {
                propertyName: "callToActionConfiguration",
                bindTo: config.bindTo,
              }),
            }),
          ],
        }),
      ],

    }), config))());
  }

  #transformEnabledToEmptyTextForText(enabled: boolean): string {
    return enabled
      ? BlueprintDocumentTypes_properties.CMTeasable_CTAText_emptyText
      : null;
  }

  #transformEnabledToEmptyTextForHash(enabled: boolean): string {
    return enabled
      ? BlueprintDocumentTypes_properties.CMTeasable_CTAHash_emptyText
      : null;
  }

  static isAnnotated(annotatedLinkListProvider: IAnnotatedLinkListProvider, rowIndex: number): boolean {
    const linkList: Array<any> = annotatedLinkListProvider.getAnnotatedLinkListVE().getValue();
    let struct: Struct = null;
    if (!Ext.isEmpty(linkList)) {
      struct = as(linkList[rowIndex], Struct);
    }
    return struct ? struct.get("callToActionEnabled") === true : false;
  }
}

export default CallToActionConfigurationForm;
