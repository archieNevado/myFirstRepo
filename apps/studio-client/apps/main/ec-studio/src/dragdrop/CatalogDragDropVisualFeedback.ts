import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import DraggableItemsUtils from "@coremedia/studio-client.ext.ui-components/util/DraggableItemsUtils";
import StringUtil from "@jangaroo/ext-ts/String";
import Template from "@jangaroo/ext-ts/Template";
import XTemplate from "@jangaroo/ext-ts/XTemplate";
import { is } from "@jangaroo/runtime";
import ECommerceStudioPlugin_properties from "../ECommerceStudioPlugin_properties";
import AugmentationUtil from "../helper/AugmentationUtil";
import CatalogHelper from "../helper/CatalogHelper";

/**
 * A helper class to create drag and drop visual feedback HTML
 */
class CatalogDragDropVisualFeedback {

  static #simpleDragDropTemplate: Template = new XTemplate(
    "<span>{text:htmlEncode}</span>").compile();

  static getHtmlFeedback(items: Array<any>): string {
    if (!items || items.length === 0) {
      return null;
    }

    if (items.length === 1) {
      //the item can be a CatalogObject or a BeanRecord
      const catalogObject: CatalogObject = (is(items[0], CatalogObject)) ? items[0] : items[0].getBean();
      return DraggableItemsUtils.DRAG_GHOST_TEMPLATE.apply({
        title: CatalogHelper.getInstance().getDecoratedName(catalogObject),
        icon: AugmentationUtil.getTypeCls(catalogObject),
      });
    } else {
      return CatalogDragDropVisualFeedback.#simpleDragDropTemplate.apply({ text: StringUtil.format(ECommerceStudioPlugin_properties.Catalog_DragDrop_multiSelect_text, items.length) });
    }
  }
}

export default CatalogDragDropVisualFeedback;
