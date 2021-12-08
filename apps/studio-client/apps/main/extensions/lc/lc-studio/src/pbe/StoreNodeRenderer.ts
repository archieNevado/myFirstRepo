import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import MetadataHelper from "@coremedia/studio-client.main.editor-components/sdk/preview/MetadataHelper";
import ContentMetadataNodeRenderer from "@coremedia/studio-client.main.editor-components/sdk/preview/metadata/ContentMetadataNodeRenderer";
import MetadataNodeRenderer from "@coremedia/studio-client.main.editor-components/sdk/preview/metadata/MetadataNodeRenderer";
import MetadataTreeNode from "@coremedia/studio-client.main.editor-components/sdk/preview/metadata/MetadataTreeNode";
import Ext from "@jangaroo/ext-ts";
import { cast, is, mixin } from "@jangaroo/runtime";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";

class StoreNodeRenderer implements MetadataNodeRenderer {
  static readonly #PROPERTY_NODE_ICON_CLASS: string = CoreIcons_properties.arrow_right;

  constructor() {
  }

  canRender(metadataNode: MetadataTreeNode): boolean {
    // this one cares for store node and first property node.
    return is(MetadataHelper.getBeanMetadataValue(metadataNode), Store) || is(MetadataHelper.getBeanMetadataValue(metadataNode.getParent()), Store);
  }

  renderText(metadataNode: MetadataTreeNode): string {
    if (MetadataHelper.isPropertyMetadataNode(metadataNode)) {
      return LivecontextStudioPlugin_properties.Commerce_shopUrl;
    }
    const store = cast(Store, MetadataHelper.getBeanMetadataValue(metadataNode));

    const children = metadataNode.getChildren();
    if (!Ext.isEmpty(children)) {
      const properties = MetadataHelper.getAllProperties(children[0]);
      if (properties.shopUrl) {
        const remoteBean = store.resolveShopUrlForPbe(properties.shopUrl);
        if (undefined === remoteBean) {
          return undefined;
        } else if (is(remoteBean, Content)) {
          return ContentMetadataNodeRenderer.renderTextForContent(cast(Content, remoteBean));
        } else if (undefined !== properties.pageId) {
          return properties.pageId;
        }
      }
    }

    return store.getName();
  }

  renderIconCls(metadataNode: MetadataTreeNode): string {
    if (MetadataHelper.isPropertyMetadataNode(metadataNode)) {
      return StoreNodeRenderer.#PROPERTY_NODE_ICON_CLASS;
    }
    const children = metadataNode.getChildren();
    if (!Ext.isEmpty(children)) {
      const properties = MetadataHelper.getAllProperties(children[0]);
      const store = cast(Store, MetadataHelper.getBeanMetadataValue(metadataNode));
      if (properties.shopUrl) {
        const remoteBean = store.resolveShopUrlForPbe(properties.shopUrl);
        if (undefined === remoteBean) {
          return undefined;
        } else if (is(remoteBean, Content)) {
          return ContentMetadataNodeRenderer.renderIconClsForContent(cast(Content, remoteBean));
        }
      }
    }
    return LivecontextStudioPlugin_properties.CMExternalPage_icon;
  }
}
mixin(StoreNodeRenderer, MetadataNodeRenderer);

export default StoreNodeRenderer;
