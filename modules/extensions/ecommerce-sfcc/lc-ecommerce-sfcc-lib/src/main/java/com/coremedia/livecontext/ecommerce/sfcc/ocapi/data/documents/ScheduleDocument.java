package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Document representing a time schedule for slots.
 */
public class ScheduleDocument extends AbstractOCDocument {

  /**
   * The date to end of validity.
   * ISO8601 date time format: yyyy-MM-dd'T'HH:mm:ssZ.
   */
  @JsonProperty("end_date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATA_API_DATE_PATTERN)
  private Date endDate;

  /**
   * The recurrence of the schedule by day of week and time of day.
   * Not all schedules will support a recurrence.
   */
  @JsonProperty("recurrence")
  private RecurrenceDocument recurrence;

  /**
   * The date to start validity.
   * ISO8601 date time format: yyyy-MM-dd'T'HH:mm:ssZ.
   */
  @JsonProperty("startDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATA_API_DATE_PATTERN)
  private Date startDate;

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public RecurrenceDocument getRecurrence() {
    return recurrence;
  }

  public void setRecurrence(RecurrenceDocument recurrence) {
    this.recurrence = recurrence;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }
}
