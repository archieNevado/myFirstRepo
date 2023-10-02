import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import IAnnotatedLinkListForm from "@coremedia/studio-client.ext.ui-components/components/IAnnotatedLinkListForm";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import { mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import FixedIndexViewModel from "./FixedIndexViewModel";
import TimelineConfigurationForm from "./TimelineConfigurationForm";
import TimelineSettings from "./TimelineSettings";

interface TimelineConfigurationFormBaseConfig extends Config<PropertyFieldGroup>, Partial<Pick<TimelineConfigurationFormBase,
  "timelineSettings" |
  "timelineViewModel" |
  "settingsVE"
>> {
}

class TimelineConfigurationFormBase extends PropertyFieldGroup implements IAnnotatedLinkListForm {
  declare Config: TimelineConfigurationFormBaseConfig;

  #_settingsVE: ValueExpression = null;

  #_timelineSettings: TimelineSettings = null;

  #_timelineViewModel: FixedIndexViewModel = null;

  constructor(config: Config<TimelineConfigurationForm> = null) {
    super(config);
  }

  get timelineSettings(): TimelineSettings {
    if (!this.#_timelineSettings) {
      this.#_timelineSettings = new TimelineSettings(this.settingsVE);
    }
    return this.#_timelineSettings;
  }

  get timelineViewModel(): FixedIndexViewModel {
    if (!this.#_timelineViewModel) {
      this.#_timelineViewModel = new FixedIndexViewModel();
    }
    return this.#_timelineViewModel;
  }

  protected override onDestroy(): void {
    this.timelineViewModel.destroy();
    this.timelineSettings.destroy();
    super.onDestroy();
  }

  set settingsVE(settingsVE: ValueExpression) {
    this.#_settingsVE = settingsVE;
  }

  get settingsVE(): ValueExpression {
    return this.#_settingsVE;
  }
}
mixin(TimelineConfigurationFormBase, IAnnotatedLinkListForm);

export default TimelineConfigurationFormBase;
