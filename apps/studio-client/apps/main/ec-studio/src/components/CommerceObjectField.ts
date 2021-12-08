import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import CatalogObjectPropertyNames from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObjectPropertyNames";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import Container from "@jangaroo/ext-ts/container/Container";
import DisplayField from "@jangaroo/ext-ts/form/field/Display";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import RemoveCommerceObjectAction from "../action/RemoveCommerceObjectAction";

interface CommerceObjectFieldConfig extends Config<Container>, Partial<Pick<CommerceObjectField,
  "commerceObject" |
  "catalogObjectIdsExpression" |
  "contentValueExpression" |
  "forceReadOnlyValueExpression" |
  "catalogObjectIdListName" |
  "removeActionName"
>> {
}

class CommerceObjectField extends Container {
  declare Config: CommerceObjectFieldConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.commerceObjectField";

  static readonly DISPLAYFIELD_ITEM_ID: string = "displayField";

  static readonly REMOVE_BUTTON_ITEM_ID: string = "removeButtton";

  constructor(config: Config<CommerceObjectField> = null) {
    super(ConfigUtils.apply(Config(CommerceObjectField, {
      layout: Config(HBoxLayout),
      items: [
        Config(DisplayField, {
          itemId: CommerceObjectField.DISPLAYFIELD_ITEM_ID,
          flex: 1,
          plugins: [
            Config(BindPropertyPlugin, {
              componentProperty: "value",
              bindTo: ValueExpressionFactory.create(CatalogObjectPropertyNames.NAME, config.commerceObject),
            }),
          ],
        }),
        Config(IconButton, {
          itemId: CommerceObjectField.REMOVE_BUTTON_ITEM_ID,
          baseAction: new RemoveCommerceObjectAction({
            commerceObject: config.commerceObject,
            catalogObjectIdListName: config.catalogObjectIdListName,
            actionName: config.removeActionName,
            contentValueExpression: config.contentValueExpression,
            forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
            catalogObjectIdsExpression: config.catalogObjectIdsExpression,
          }),
        }),
      ],

    }), config));
  }

  commerceObject: Bean | CatalogObject = null;

  catalogObjectIdsExpression: ValueExpression = null;

  contentValueExpression: ValueExpression = null;

  forceReadOnlyValueExpression: ValueExpression = null;

  catalogObjectIdListName: string = null;

  removeActionName: string = null;
}

export default CommerceObjectField;
