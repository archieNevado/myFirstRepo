import ImageUtil from "@coremedia/studio-client.cap-base-models/util/ImageUtil";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import BlobPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/BlobPropertyField";
import BlobPropertyFieldBase from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/BlobPropertyFieldBase";
import Container from "@jangaroo/ext-ts/container/Container";
import Checkbox from "@jangaroo/ext-ts/form/field/Checkbox";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import int from "@jangaroo/runtime/int";
import AssetDetailsBlobPropertyField from "./AssetDetailsBlobPropertyField";

interface AssetDetailsBlobPropertyFieldBaseConfig extends Config<BlobPropertyField> {
}

class AssetDetailsBlobPropertyFieldBase extends BlobPropertyField {
  declare Config: AssetDetailsBlobPropertyFieldBaseConfig;

  #blobPropertyVE: ValueExpression = null;

  #checkboxVisibleVE: ValueExpression = null;

  readonly CHECKBOX_ITEM_ID: string = "checkBoxItemId";

  constructor(config: Config<AssetDetailsBlobPropertyField> = null) {
    super(config);

    this.getBlobPropertyVE(config.bindTo, config.propertyName).addChangeListener(bind(this, this.#updateCheckbox));
  }

  protected getBlobPropertyVE(bindTo: ValueExpression, propertyName: string): ValueExpression {
    if (!this.#blobPropertyVE) {
      this.#blobPropertyVE = bindTo.extendBy("properties", propertyName);
    }
    return this.#blobPropertyVE;
  }

  #updateCheckbox(): void {
    if (!this.#blobPropertyVE.getValue()) {
      const checkbox = this.#findCheckBox();
      if (checkbox) {
        checkbox.setValue(false);
      }
    }
  }

  protected getCheckboxVisibleVE(visiblePropertyName: string, bindTo: ValueExpression, propertyName: string): ValueExpression {
    if (!this.#checkboxVisibleVE) {
      this.#checkboxVisibleVE = ValueExpressionFactory.createFromFunction(AssetDetailsBlobPropertyFieldBase.visible, visiblePropertyName, this.getBlobPropertyVE(bindTo, propertyName));
    }

    return this.#checkboxVisibleVE;
  }

  /**
   * If no property or no blob exist, hide component.
   *
   * @param property
   * @param blobVE
   * @return false if no property or blob exist.
   */
  protected static visible(property: string, blobVE: ValueExpression): boolean {
    if (!property) {
      return false;
    }

    return blobVE && blobVE.getValue() && blobVE.getValue().getSize() !== undefined;
  }

  protected static findBlobDetailsContainer(container: Container): Container {
    return container.queryById(BlobPropertyFieldBase.BLOB_DETAILS_ITEM_ID) as Container;
  }

  #findCheckBox(): Checkbox {
    const checkbox = as(this.queryById(this.CHECKBOX_ITEM_ID), Checkbox);
    return (checkbox) ? checkbox : null;
  }

  protected override onDestroy(): void {
    this.#blobPropertyVE.removeChangeListener(bind(this, this.#updateCheckbox));

    super.onDestroy();
  }

  protected override getBlobImage(uri: string, width: int, height: int): string {
    if (uri) {
      const url = ImageUtil.getCroppingUri(uri, width, height);
      if (url) {
        return url;
      }
    }
    return uri;
  }

}

export default AssetDetailsBlobPropertyFieldBase;
