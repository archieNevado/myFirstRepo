import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import BindComponentsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindComponentsPlugin";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import DisplayField from "@jangaroo/ext-ts/form/field/Display";
import VBoxLayout from "@jangaroo/ext-ts/layout/container/VBox";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import { AnyFunction } from "@jangaroo/runtime/types";
import CommerceObjectField from "../components/CommerceObjectField";
import CommerceObjectSelector from "../components/CommerceObjectSelector";
import CommerceCatalogObjectsSelectFormBase from "./CommerceCatalogObjectsSelectFormBase";

interface CommerceCatalogObjectsSelectFormConfig extends Config<CommerceCatalogObjectsSelectFormBase>, Partial<Pick<CommerceCatalogObjectsSelectForm,
  "catalogObjectIdsExpression" |
  "catalogObjectIdListName" |
  "invalidMessage" |
  "emptyText" |
  "removeActionName" |
  "getCommerceObjectsFunction" |
  "noStoreMessage"
>> {
}

class CommerceCatalogObjectsSelectForm extends CommerceCatalogObjectsSelectFormBase {
  declare Config: CommerceCatalogObjectsSelectFormConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.commerceCatalogObjectsSelectForm";

  static readonly SELECTOR_ITEM_ID: string = "selector";

  static readonly PLACEHOLDER_LABEL_ITEM_ID: string = "placeholderLabel";

  static readonly NO_STORE_LABEL: string = "noStoreLabel";

  constructor(config: Config<CommerceCatalogObjectsSelectForm> = null) {
    super((()=> ConfigUtils.apply(Config(CommerceCatalogObjectsSelectForm, {

      layout: Config(VBoxLayout, { align: "stretch" }),
      plugins: [
        Config(BindComponentsPlugin, {
          configBeanParameterName: "commerceObject",
          valueExpression: this.getCatalogObjectsExpression(config),
          reuseComponents: false,
          removeFunction: bind(this, this.removeCommerceObjectFields),
          addFunction: bind(this, this.addCommerceObjectFields),
          getKey: CommerceCatalogObjectsSelectFormBase.getCatalogObjectKey,
          template: Config(CommerceObjectField, {
            catalogObjectIdListName: config.catalogObjectIdListName,
            removeActionName: config.removeActionName,
            contentValueExpression: config.bindTo,
            catalogObjectIdsExpression: config.catalogObjectIdsExpression,
            forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
          }),
        }),
      ],
      items: [
        Config(CommerceObjectSelector, {
          itemId: CommerceCatalogObjectsSelectForm.SELECTOR_ITEM_ID,
          forceSelection: true,
          queryMode: "local",
          triggerAction: "all",
          listeners: { "select": this.getHandleSelectFunction(config) },
          contentValueExpression: config.bindTo,
          selectedCatalogObjectsExpression: this.getCatalogObjectsExpression(config),
          getCommerceObjectsFunction: config.getCommerceObjectsFunction,
          emptyText: config.emptyText,
          noStoreMessage: config.noStoreMessage,
          ...ConfigUtils.append({
            plugins: [
              Config(BindDisablePlugin, {
                bindTo: config.bindTo,
                forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
              }),
            ],
          }),
        }),
        Config(DisplayField, {
          itemId: CommerceCatalogObjectsSelectForm.NO_STORE_LABEL,
          hidden: true,
          value: config.noStoreMessage,
        }),
      ],

    }), config))());
  }

  catalogObjectIdsExpression: ValueExpression = null;

  catalogObjectIdListName: string = null;

  invalidMessage: string = null;

  emptyText: string = null;

  removeActionName: string = null;

  getCommerceObjectsFunction: AnyFunction = null;

  noStoreMessage: string = null;
}

export default CommerceCatalogObjectsSelectForm;
