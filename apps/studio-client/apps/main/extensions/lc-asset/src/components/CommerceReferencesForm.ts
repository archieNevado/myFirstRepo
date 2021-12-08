import CatalogModel from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogModel";
import ECommerceStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin_properties";
import CatalogLinkPropertyField from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/CatalogLinkPropertyField";
import LivecontextStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.lc-studio/LivecontextStudioPlugin_properties";
import BindVisibilityPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindVisibilityPlugin";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CommerceReferencesFormBase from "./CommerceReferencesFormBase";

interface CommerceReferencesFormConfig extends Config<CommerceReferencesFormBase>, Partial<Pick<CommerceReferencesForm,
  "additionalToolbarItems"
>> {
}

class CommerceReferencesForm extends CommerceReferencesFormBase {
  declare Config: CommerceReferencesFormConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.asset.studio.config.commerceReferencesForm";

  constructor(config: Config<CommerceReferencesForm> = null) {
    super((()=> ConfigUtils.apply(Config(CommerceReferencesForm, {
      itemId: "commerceReferencesForm",
      title: LivecontextStudioPlugin_properties.CMPicture_propertyGroup_commerce,

      ...ConfigUtils.append({
        plugins: [
          Config(BindVisibilityPlugin, { bindTo: this.getShopExpression(config) }),
        ],
      }),
      items: [
        Config(CatalogLinkPropertyField, {
          readOnlyValueExpression: this.getReadOnlyExpression(config),
          propertyName: CommerceReferencesFormBase.PROPERTY_NAME,
          hideCatalog: true,
          linkTypeNames: [CatalogModel.TYPE_CATEGORY, CatalogModel.TYPE_PRODUCT],
          createStructFunction: bind(this, this.createStructs),
          dropAreaText: ECommerceStudioPlugin_properties.Categorys_Products_Link_empty_text,
          additionalToolbarItems: config.additionalToolbarItems,
        }),
      ],

    }), config))());
  }

  additionalToolbarItems: Array<any> = null;
}

export default CommerceReferencesForm;
