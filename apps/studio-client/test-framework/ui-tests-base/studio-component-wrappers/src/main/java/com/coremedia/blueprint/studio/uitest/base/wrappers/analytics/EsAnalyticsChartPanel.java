package com.coremedia.blueprint.studio.uitest.base.wrappers.analytics;

import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.Label;
import com.coremedia.uitesting.ext3.wrappers.form.field.ComboBoxField;

@ExtJSObject
public class EsAnalyticsChartPanel extends Panel {

  public static final java.lang.String XTYPE = "com.coremedia.blueprint.studio.config.esanalytics.esAnalyticsChartPanel";

  @FindByExtJS(itemId = "esAlxChartComponentItemId", global = false)
  private Panel esAnalyticsChartPanel;

  @FindByExtJS(itemId = "noDataLabelItemId", global = false)
  private Label noDataLabel;

  @FindByExtJS(itemId = "timeStampItemId", global = false)
  private Label timeStampLabel;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "esAlxTimeRangeComboId", global = false)
  private ComboBoxField timeRangeCombo;

  @SuppressWarnings("UnusedDeclaration")
  public Panel getChartPanel() {
    return esAnalyticsChartPanel;
  }

  @SuppressWarnings("UnusedDeclaration")
  public Label getNoDataLabel() {
    return noDataLabel;
  }

  public Label getTimeStampLabel() {
    return timeStampLabel;
  }

  @SuppressWarnings("UnusedDeclaration")
  public ComboBoxField getTimeRangeComboBox() {
    return timeRangeCombo;
  }

  public void selectTimeRange(String timeRange) {
    getTimeRangeComboBox().select(timeRange);
  }
}
