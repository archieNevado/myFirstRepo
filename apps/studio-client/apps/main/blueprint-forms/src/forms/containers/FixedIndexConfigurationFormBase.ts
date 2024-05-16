import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import IAnnotatedLinkListForm from "@coremedia/studio-client.ext.ui-components/components/IAnnotatedLinkListForm";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import { mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import FixedIndexConfigurationForm from "./FixedIndexConfigurationForm";
import FixedIndexSettings from "./FixedIndexSettings";
import FixedIndexViewModel from "./FixedIndexViewModel";

interface FixedIndexConfigurationFormBaseConfig extends Config<PropertyFieldGroup>, Partial<Pick<FixedIndexConfigurationFormBase,
  "indexSettings" |
  "indexViewModel" |
  "settingsVE"
>> {
}

class FixedIndexConfigurationFormBase extends PropertyFieldGroup implements IAnnotatedLinkListForm {
  declare Config: FixedIndexConfigurationFormBaseConfig;

  #_settingsVE: ValueExpression = null;

  #_indexSettings: FixedIndexSettings = null;

  #_indexViewModel: FixedIndexViewModel = null;

  constructor(config: Config<FixedIndexConfigurationForm> = null) {
    super(config);
  }

  get indexSettings(): FixedIndexSettings {
    if (!this.#_indexSettings) {
      this.#_indexSettings = new FixedIndexSettings(this.settingsVE);
    }
    return this.#_indexSettings;
  }

  get indexViewModel(): FixedIndexViewModel {
    if (!this.#_indexViewModel) {
      this.#_indexViewModel = new FixedIndexViewModel();
    }
    return this.#_indexViewModel;
  }

  protected override onDestroy(): void {
    this.indexViewModel.destroy();
    this.indexSettings.destroy();
    super.onDestroy();
  }

  set settingsVE(settingsVE: ValueExpression) {
    this.#_settingsVE = settingsVE;
  }

  get settingsVE(): ValueExpression {
    return this.#_settingsVE;
  }
}
mixin(FixedIndexConfigurationFormBase, IAnnotatedLinkListForm);

export default FixedIndexConfigurationFormBase;
