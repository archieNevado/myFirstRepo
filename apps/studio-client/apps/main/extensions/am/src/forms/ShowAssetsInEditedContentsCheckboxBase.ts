import PreferencesUtil from "@coremedia/studio-client.cap-base-models/preferences/PreferencesUtil";
import CapListRepositoryImpl from "@coremedia/studio-client.cap-rest-client-impl/list/impl/CapListRepositoryImpl";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import ObjectUtils from "@coremedia/studio-client.client-core/util/ObjectUtils";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Checkbox from "@jangaroo/ext-ts/form/field/Checkbox";
import { bind, cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ShowAssetsInEditedContentsCheckbox from "./ShowAssetsInEditedContentsCheckbox";

interface ShowAssetsInEditedContentsCheckboxBaseConfig extends Config<Checkbox> {
}

class ShowAssetsInEditedContentsCheckboxBase extends Checkbox {
  declare Config: ShowAssetsInEditedContentsCheckboxBaseConfig;

  static readonly #CONTROL_ROOM: string = "controlRoom";

  static readonly #SHOW_ASSETS: string = "showAssets";

  static readonly #ASSET_TYPE: string = "AMAsset";

  static readonly #DEFAULT_VALUE: boolean = true;

  #checkedValueExpression: ValueExpression = null;

  constructor(config: Config<ShowAssetsInEditedContentsCheckbox> = null) {
    super(config);

    cast(CapListRepositoryImpl, CapListRepositoryImpl.getInstance()).registerEditedContentsFilterFn(bind(this, this.#filterAssets));
  }

  #filterAssets(content: Content): boolean {
    return !this.getCheckedValueExpression().getValue() && content.getType().isSubtypeOf(ShowAssetsInEditedContentsCheckboxBase.#ASSET_TYPE);
  }

  protected getCheckedValueExpression(): ValueExpression {
    if (!this.#checkedValueExpression) {
      this.#checkedValueExpression = ValueExpressionFactory.createFromValue(false);
      this.#checkedValueExpression.addChangeListener(ShowAssetsInEditedContentsCheckboxBase.#saveChecked);
      this.#checkedValueExpression.setValue(ShowAssetsInEditedContentsCheckboxBase.#loadShowAssets());
    }
    return this.#checkedValueExpression;
  }

  static #loadShowAssets(): boolean {
    return ! !ObjectUtils.getPropertyAt(editorContext._.getPreferences(), [ShowAssetsInEditedContentsCheckboxBase.#CONTROL_ROOM, ShowAssetsInEditedContentsCheckboxBase.#SHOW_ASSETS], ShowAssetsInEditedContentsCheckboxBase.#DEFAULT_VALUE);
  }

  static #saveChecked(source: ValueExpression): void {
    PreferencesUtil.updatePreferencesJSONProperty(source.getValue(), ShowAssetsInEditedContentsCheckboxBase.#CONTROL_ROOM, ShowAssetsInEditedContentsCheckboxBase.#SHOW_ASSETS);
  }

  protected override beforeDestroy(): void {
    this.getCheckedValueExpression() && this.getCheckedValueExpression().removeChangeListener(ShowAssetsInEditedContentsCheckboxBase.#saveChecked);

    super.beforeDestroy();
  }
}

export default ShowAssetsInEditedContentsCheckboxBase;
