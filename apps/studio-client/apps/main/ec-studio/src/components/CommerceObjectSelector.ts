import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import { AnyFunction } from "@jangaroo/runtime/types";
import CommerceObjectSelectorBase from "./CommerceObjectSelectorBase";

interface CommerceObjectSelectorConfig extends Config<CommerceObjectSelectorBase>, Partial<Pick<CommerceObjectSelector,
  "contentValueExpression" |
  "selectedCatalogObjectsExpression" |
  "quote" |
  "getCommerceObjectsFunction" |
  "noStoreMessage"
>> {
}

class CommerceObjectSelector extends CommerceObjectSelectorBase {
  declare Config: CommerceObjectSelectorConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.commerceObjectSelector";

  constructor(config: Config<CommerceObjectSelector> = null) {
    super((()=> ConfigUtils.apply(Config(CommerceObjectSelector, {
      valueField: "id",
      displayField: "name",

      plugins: [
        Config(BindListPlugin, {
          bindTo: this.getSelectableCatalogObjectsExpression(config),
          fields: [
            Config(DataField, { name: "id" }),
            Config(DataField, { name: "name" }),
          ],
        }),
      ],

    }), config))());
  }

  contentValueExpression: ValueExpression = null;

  selectedCatalogObjectsExpression: ValueExpression<(Bean | CatalogObject)[]> = null;

  quote: boolean = false;

  getCommerceObjectsFunction: AnyFunction = null;

  noStoreMessage: string = null;
}

export default CommerceObjectSelector;
