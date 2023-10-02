import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import RemoteServiceMethod from "@coremedia/studio-client.client-core-impl/data/impl/RemoteServiceMethod";
import RemoteServiceMethodResponse from "@coremedia/studio-client.client-core-impl/data/impl/RemoteServiceMethodResponse";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import WidgetWrapper from "@coremedia/studio-client.main.editor-components/sdk/dashboard/WidgetWrapper";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Container from "@jangaroo/ext-ts/container/Container";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import TextItem from "@jangaroo/ext-ts/toolbar/TextItem";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import EsAnalyticsImpl from "./EsAnalyticsImpl";
import EsAnalyticsStudioPlugin_properties from "./EsAnalyticsStudioPlugin_properties";

interface EsAnalyticsChartWidgetBaseConfig extends Config<Container>, Partial<Pick<EsAnalyticsChartWidgetBase,
  "content"
>> {
}

class EsAnalyticsChartWidgetBase extends Container {
  declare Config: EsAnalyticsChartWidgetBaseConfig;

  /**
   * The content id of the root channel to show.
   */
  content: Content = null;

  #tenantVE: ValueExpression = null;

  protected timeRangeValueExpression: ValueExpression = null;

  constructor(config: Config<EsAnalyticsChartWidgetBase> = null) {
    super(config);

    this.on("afterlayout", (): void => {
      const title = EsAnalyticsStudioPlugin_properties.widget_title;
      if (config.content) {
        const content = config.content;
        if (content) {
          content.load((cont: Content): void =>
            this.#getWidgetLabel().update(title + ": " + cont.getName()),
          );
        }
      } else {
        this.#getWidgetLabel().update(title + ": " + EsAnalyticsStudioPlugin_properties.widget_title_channel_undefined);
      }
    }, null, { single: true });
  }

  #getWidgetLabel(): TextItem {
    const wrapper = as(this.findParentByType(WidgetWrapper.xtype), WidgetWrapper);
    const innerWrapper = as(wrapper.queryById("innerWrapper"), Panel);
    const widgetToolbar = as(innerWrapper.getDockedItems("toolbar[dock=\"top\"]")[0], Toolbar);
    return as(widgetToolbar.down("tbtext"), TextItem);
  }

  getAlxData(propertyName: string): ValueExpression {
    return ValueExpressionFactory.createFromFunction((propertyName1: string): any => {

      if (this.content && this.content.getId() && this.#getTenantVE().getValue()) {
        const alxPageViewsVE = EsAnalyticsImpl.getAlxPageViews(this.#getTenantVE().getValue(),
          propertyName1, this.content.getId(), this.getTimeRangeValueExpression().getValue());
        if (alxPageViewsVE) {
          return alxPageViewsVE.getValue();
        }
      }
      return null;
    }, propertyName);
  }

  getPublicationData(propertyName: string): ValueExpression {
    return ValueExpressionFactory.createFromFunction((propertyName1: string): any => {
      if (this.content && this.content.getId() && this.#getTenantVE().getValue()) {
        const publicationDataVE = EsAnalyticsImpl.getPublicationData(this.#getTenantVE().getValue(), propertyName1, this.content.getId(), this.getTimeRangeValueExpression().getValue());
        if (publicationDataVE) {
          return publicationDataVE.getValue();
        }
      }
      return null;
    }, propertyName);
  }

  protected getTimeRangeValueExpression(): ValueExpression {
    if (!this.timeRangeValueExpression) {
      this.timeRangeValueExpression = ValueExpressionFactory.create("timerange", beanFactory._.createLocalBean({ "timerange": 7 }));
    }
    return this.timeRangeValueExpression;
  }

  #getTenantVE(): ValueExpression {
    if (!this.#tenantVE) {
      this.#tenantVE = ValueExpressionFactory.createFromValue(undefined);

      if (this.content) {
        this.content.load((): void => {
          const site = editorContext._.getSitesService().getSiteFor(this.content);
          let tenantInfoUrl = EsAnalyticsImpl.ELASTIC_API_BASE_URL + EsAnalyticsImpl.TENANT_URI_SEGMENT + "?siteId=";
          // remote call can handle empty site id and would return the default tenant
          if (site) {
            tenantInfoUrl = tenantInfoUrl + EsAnalyticsImpl.convertIdField(site.getId());
          }
          const rsm = new RemoteServiceMethod(tenantInfoUrl, "GET");
          rsm.request(null,
            (response: RemoteServiceMethodResponse): void => {
              const tenantInfo = response.getResponseJSON();
              this.#tenantVE.setValue(tenantInfo["tenant"]);
            },
          );
        });
      }
    }
    return this.#tenantVE;
  }
}

export default EsAnalyticsChartWidgetBase;
