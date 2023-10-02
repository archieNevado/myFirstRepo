import BeanImpl from "@coremedia/studio-client.client-core-impl/data/impl/BeanImpl";

/**
 * Declares an observable with properties and their default values for the startTimeMillis feature.
 */
class TimelineViewModel extends BeanImpl {

  static readonly TIMELINE_PROPERTY_NAME: string = "startTimeMillis";

  constructor() {
    super();
    //empty constructor
  }

  startTimeMillis: number;
}

export default TimelineViewModel;
