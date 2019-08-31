package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Document representing a schedule recurrence.
 */
public class RecurrenceDocument extends AbstractOCDocument {

  public enum DAY {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
  }

  /**
   * The days of week for recurrence.
   */
  @JsonProperty("day_of_week")
  private DAY dayOfWeek;

  /**
   * The time of the day for recurrence.
   */
  @JsonProperty("time_of_day")
  private Object/*TimeOfDay*/ timeOfDay;

  public DAY getDayOfWeek() {
    return dayOfWeek;
  }

  public void setDayOfWeek(DAY dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  public Object getTimeOfDay() {
    return timeOfDay;
  }

  public void setTimeOfDay(Object timeOfDay) {
    this.timeOfDay = timeOfDay;
  }
}
