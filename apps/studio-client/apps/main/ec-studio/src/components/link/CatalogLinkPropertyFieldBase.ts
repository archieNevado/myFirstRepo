import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import CatalogObjectPropertyNames
  from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObjectPropertyNames";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import ImageLinkListRenderer
  from "@coremedia/studio-client.ext.content-link-list-components/util/ImageLinkListRenderer";
import ThumbnailImage from "@coremedia/studio-client.ext.ui-components/util/ThumbnailImage";
import ILinkListWrapper from "@coremedia/studio-client.link-list-models/ILinkListWrapper";
import LinkListGridPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListGridPanel";
import PropertyEditorUtil from "@coremedia/studio-client.main.editor-components/sdk/util/PropertyEditorUtil";
import { as, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";
import AugmentationUtil from "../../helper/AugmentationUtil";
import CatalogHelper from "../../helper/CatalogHelper";
import CatalogLinkListWrapper from "./CatalogLinkListWrapper";
import CatalogLinkPropertyField from "./CatalogLinkPropertyField";
import ConfirmChangeReferenceDialog from "./ConfirmChangeReferenceDialog";

interface CatalogLinkPropertyFieldBaseConfig extends Config<LinkListGridPanel>, Partial<Pick<CatalogLinkPropertyFieldBase,
  "bindTo" |
  "model" |
  "propertyName" |
  "maxCardinality" |
  "createStructFunction" |
  "linkTypeNames" |
  "forceReadOnlyValueExpression" |
  "showChangeReferenceButton">> {
}

class CatalogLinkPropertyFieldBase extends LinkListGridPanel {
  declare Config: CatalogLinkPropertyFieldBaseConfig;

  showChangeReferenceButton: boolean = false;

  #_localWrapper: ILinkListWrapper = null;

  bindTo: ValueExpression = null;

  model: Bean = null;

  propertyName: string = null;

  maxCardinality: number = NaN;

  createStructFunction: AnyFunction = null;

  linkTypeNames: Array<any> = null;

  forceReadOnlyValueExpression: ValueExpression = null;

  changeReferenceButtonDisabledVE: ValueExpression;

  constructor(config: Config<CatalogLinkPropertyField> = null) {
    super(config);
    this.bindTo = config.bindTo;
  }

  protected getLinkListWrapper(config: Config<CatalogLinkPropertyFieldBase>): ILinkListWrapper {
    if (!this.#_localWrapper) {
      if (config.linkListWrapper) {
        this.#_localWrapper = config.linkListWrapper;
      } else {
        const wrapperCfg = Config<CatalogLinkListWrapper>({});
        wrapperCfg.bindTo = config.bindTo;
        wrapperCfg.model = config.model;
        wrapperCfg.propertyName = config.propertyName;
        wrapperCfg.maxCardinality = config.maxCardinality;
        wrapperCfg.createStructFunction = config.createStructFunction;
        wrapperCfg.linkTypeNames = config.linkTypeNames;
        wrapperCfg.readOnlyVE = this.getReadOnlyVE(config);
        wrapperCfg.acceptAugmentedContent = !config.showChangeReferenceButton;
        this.#_localWrapper = new CatalogLinkListWrapper(wrapperCfg);
      }
    }
    return this.#_localWrapper;
  }

  protected getReadOnlyVE(config: Config<CatalogLinkPropertyFieldBase>): ValueExpression {
    if (!this.readOnlyValueExpression) {
      if (config.readOnlyValueExpression) {
        this.readOnlyValueExpression = config.readOnlyValueExpression;
      } else {
        this.readOnlyValueExpression = PropertyEditorUtil.createReadOnlyValueExpression(config.bindTo, config.forceReadOnlyValueExpression);
      }

    }
    return this.readOnlyValueExpression;
  }

  protected removeCategoryReference(): void {

    const selectedPositions = this.getSelectedPositionsVE().getValue();
    if (!selectedPositions || selectedPositions.length === 0) {
      return;
    }

    const dialogConfig = Config(ConfirmChangeReferenceDialog, { changeFunction: () => this.#_localWrapper?.removeLinksAtIndexes(selectedPositions) });
    const confirmChangeReferenceDialog = new ConfirmChangeReferenceDialog(dialogConfig);
    confirmChangeReferenceDialog.show();
  }

  protected isChangeReferenceDisabledVE(): ValueExpression<boolean> {
    if (!this.changeReferenceButtonDisabledVE) {
      this.changeReferenceButtonDisabledVE = ValueExpressionFactory.createFromFunction(() => {
        return this.#isNothingSelected() || this.#_localWrapper.isReadOnly();
      });
    }
    return this.changeReferenceButtonDisabledVE;
  }

  #isNothingSelected(): boolean {
    const selection: Array<any> = this.getSelectedPositionsVE().getValue();
    return !selection || selection.length === 0;
  }

  static convertTypeLabel(v: string, catalogObject: CatalogObject): string {
    if (is(catalogObject, CatalogObject)) {
      return AugmentationUtil.getTypeLabel(catalogObject);
    }
  }

  static convertTypeCls(v: string, catalogObject: CatalogObject): string {
    if (is(catalogObject, CatalogObject)) {
      return AugmentationUtil.getTypeCls(catalogObject);
    }
  }

  static convertIdLabel(v: string, catalogObject: CatalogObject): string {
    if (!catalogObject) {
      return undefined;
    }
    const extId = catalogObject.getExternalId();
    if (extId) {
      return extId;
    } else if (extId === null) {
      return CatalogHelper.getInstance().getExternalIdFromId(catalogObject.getUri());
    }
    return undefined;
  }

  static convertNameLabel(v: string, catalogObject: CatalogObject): string {
    let name: string = undefined;
    if (!catalogObject) {
      return name;
    }
    if (is(catalogObject, CatalogObject)) {
      try {
        name = CatalogHelper.getInstance().getDecoratedName(catalogObject);
      } catch (e) {
        if (is(e, Error)) {
          //ignore
        } else throw e;
      }
    }
    if (!name) {
      name = CatalogHelper.getInstance().getExternalIdFromId(catalogObject.getUri());
    }
    return name;
  }

  static convertLifecycleStatus(v: string, catalogObject: CatalogObject): string {
    if (is(catalogObject, CatalogObject)) {
      const augmentingContent = as(catalogObject.get(CatalogObjectPropertyNames.CONTENT), Content);
      if (augmentingContent) { // the commerce object has been augmented
        return augmentingContent.getLifecycleStatus();
      }
    }
    return undefined;
  }

  static convertThumbnail(model: any): ThumbnailImage {
    return ImageLinkListRenderer.convertThumbnailFor(model, "CatalogObject");
  }
}

export default CatalogLinkPropertyFieldBase;
