import PropertiesWithDefaultsAdapterBase from "@coremedia/studio-client.client-core/data/PropertiesWithDefaultsAdapterBase";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";

/**
 * Declares an observable with properties and their default values for the startTimeMillis feature.
 */
class TimelineSettings extends PropertiesWithDefaultsAdapterBase {

  static readonly TIMELINE_PROPERTY_NAME: string = "startTimeMillis";

  constructor(ve: ValueExpression) {
    super(ve,
      TimelineSettings.TIMELINE_PROPERTY_NAME, null,
    );
  }

  get startTimeMillis(): number {
    return this.getProperty(TimelineSettings.TIMELINE_PROPERTY_NAME);
  }

  set startTimeMillis(value: number) {
    this.setProperty(TimelineSettings.TIMELINE_PROPERTY_NAME, value);
  }
}

export default TimelineSettings;
