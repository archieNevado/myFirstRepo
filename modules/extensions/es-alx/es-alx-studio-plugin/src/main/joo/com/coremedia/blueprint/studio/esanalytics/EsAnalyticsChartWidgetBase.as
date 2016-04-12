package com.coremedia.blueprint.studio.esanalytics {
import com.coremedia.blueprint.studio.config.esanalytics.esAnalyticsChartWidget;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.config.widgetWrapper;
import com.coremedia.cms.editor.sdk.dashboard.WidgetWrapper;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.sites.Site;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.impl.RemoteServiceMethod;
import com.coremedia.ui.data.impl.RemoteServiceMethodResponse;

import ext.Container;
import ext.Panel;
import ext.Toolbar;
import ext.form.Label;

public class EsAnalyticsChartWidgetBase extends Container {

  public native function get content():Content;

  private var tenantVE:ValueExpression;

  protected var timeRangeValueExpression:ValueExpression;

  public function EsAnalyticsChartWidgetBase(config:esAnalyticsChartWidget = null) {
    super(config);

    mon(this, "afterlayout", function ():void {
      var title:String = EsAnalyticsStudioPlugin_properties.INSTANCE.widget_title;
      if (config.content) {
        var content:Content = config.content;
        if (content) {
          content.load(function (cont:Content):void {
            getWidgetLabel().setText(title + ": " + cont.getName());
          });
        }
      } else {
        getWidgetLabel().setText(title + ": " + EsAnalyticsStudioPlugin_properties.INSTANCE.widget_title_channel_undefined);
      }
    }, null, {single: true});
  }

  private function getWidgetLabel():Label {
    var wrapper:WidgetWrapper = findParentByType(widgetWrapper.xtype) as WidgetWrapper;
    var innerWrapper:Panel = wrapper.find("itemId", "innerWrapper")[0];
    var widgetToolbar:Toolbar = innerWrapper.getTopToolbar();
    return widgetToolbar.find("itemId", "widgetWrapperLabel")[0] as Label;
  }

  public function getAlxData(serviceName:String, propertyName:String):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function (serviceName1:String, propertyName1:String):Object {

      if (content && content.getId() && getTenantVE().getValue()) {
        var alxPageViewsVE:ValueExpression = EsAnalyticsImpl.getAlxPageViews(getTenantVE().getValue(), serviceName1, propertyName1, content.getId(), getTimeRangeValueExpression().getValue());
        if (alxPageViewsVE) {
          return alxPageViewsVE.getValue();
        }
      }
      return null;
    }, serviceName, propertyName);
  }

  public function getPublicationData(propertyName:String):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function (propertyName1:String):Object {
      if (content && content.getId() && getTenantVE().getValue()) {
        var publicationDataVE:ValueExpression = EsAnalyticsImpl.getPublicationData(getTenantVE().getValue(), propertyName1, content.getId(), getTimeRangeValueExpression().getValue());
        if (publicationDataVE) {
          return publicationDataVE.getValue();
        }
      }
      return null;
    }, propertyName);
  }

  protected function getTimeRangeValueExpression():ValueExpression {
    if (!timeRangeValueExpression) {
      timeRangeValueExpression = ValueExpressionFactory.create('timerange', beanFactory.createLocalBean({'timerange': 7}));
    }
    return timeRangeValueExpression;
  }

  private function getTenantVE():ValueExpression {
    if (!tenantVE) {
      tenantVE = ValueExpressionFactory.createFromValue(undefined);

      if (content) {
        content.load(function ():void {
          var site:Site = editorContext.getSitesService().getSiteFor(content);
          var tenantInfoUrl:String = EsAnalyticsImpl.ELASTIC_API_BASE_URL + EsAnalyticsImpl.TENANT_URI_SEGMENT + "?siteId=";
          // remote call can handle empty site id and would return the default tenant
          if (site) {
            tenantInfoUrl = tenantInfoUrl + EsAnalyticsImpl.convertIdField(site.getId());
          }
          var rsm:RemoteServiceMethod = new RemoteServiceMethod(tenantInfoUrl, "GET");
          rsm.request(null,
                  function (response:RemoteServiceMethodResponse):void {
                    var tenantInfo:Object = response.getResponseJSON();
                    tenantVE.setValue(tenantInfo['tenant']);
                  }
          );
        })
      }
    }
    return tenantVE;
  }
}
}