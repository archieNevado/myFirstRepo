import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import BreadcrumbElement from "@coremedia/studio-client.main.editor-components/sdk/components/breadcrumb/BreadcrumbElement";
import MetadataHelper from "@coremedia/studio-client.main.editor-components/sdk/preview/MetadataHelper";
import MetadataTreeNode from "@coremedia/studio-client.main.editor-components/sdk/preview/metadata/MetadataTreeNode";
import Component from "@jangaroo/ext-ts/Component";
import AbstractPlugin from "@jangaroo/ext-ts/plugin/Abstract";
import { as, cast, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import DisableStoreNodePlugin from "./DisableStoreNodePlugin";

interface DisableStoreNodePluginBaseConfig extends Config<AbstractPlugin> {
}

class DisableStoreNodePluginBase extends AbstractPlugin {
  declare Config: DisableStoreNodePluginBaseConfig;

  constructor(config: Config<DisableStoreNodePlugin> = null) {
    super(config);
  }

  override init(component: Component): void {
    const element = as(component, BreadcrumbElement);
    if (!element) {
      throw new Error("unsupported component type: " + component.xtype);
    }
    const elementConfig = cast(BreadcrumbElement, element.initialConfig);
    if (!element.disabled && elementConfig.disableElementStrategy) {
      const metadataNode = as(elementConfig.treeModel.getNodeModel(elementConfig.breadcrumbElementId), MetadataTreeNode);
      if (metadataNode) {
        const isStore = is(MetadataHelper.getBeanMetadataValue(metadataNode), Store);
        if (isStore) {
          element.disable();
        }
      }
    }
  }

}

export default DisableStoreNodePluginBase;
