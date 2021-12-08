import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import { as, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import CommerceDetailsForm from "./CommerceDetailsForm";

interface CommerceDetailsFormBaseConfig extends Config<PropertyFieldGroup>, Partial<Pick<CommerceDetailsFormBase,
  "contentBindTo"
>> {
}

class CommerceDetailsFormBase extends PropertyFieldGroup {
  declare Config: CommerceDetailsFormBaseConfig;

  contentBindTo: ValueExpression = null;

  constructor(config: Config<CommerceDetailsForm> = null) {
    super((()=>{
    //need to use a different stateSaveExpression for ecommerce objects as the default impl in PropertyFieldGroupBase expects catalogObject
      const formType = this.#getFormType(config);
      if (formType) {
        this.stateSaveExpression = ValueExpressionFactory.create("forms." + formType + "." + config.itemId, editorContext._.getPreferences());
      }
      return config;
    })());
  }

  protected override getCollapsedStateFromPremularConfig(config: Config<PropertyFieldGroup>): boolean {
    if (this.stateSaveExpression && this.stateSaveExpression.isLoaded()) {
      const value = as(this.stateSaveExpression.getValue(), Boolean);
      if (value !== undefined && value !== null) {
        return value;
      }
    }
    return config.collapsed;
  }

  #getFormType(config: Config<CommerceDetailsForm>): string {
    if (config.contentBindTo) {
      const content = as(config.contentBindTo.getValue(), Content);
      if (content) {
        return content.getType().getName();
      }
    }
    const catalogObject = as(config.bindTo.getValue(), CatalogObject);
    if (is(catalogObject, Category)) {
      // legacy name
      return "com_coremedia_ecommerce_studio_model_CategoryImpl";
    }
    if (is(catalogObject, Product)) {
      // legacy name
      return "com_coremedia_ecommerce_studio_model_ProductImpl";
    }
    return null;
  }
}

export default CommerceDetailsFormBase;
