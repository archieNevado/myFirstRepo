import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import RemoteServiceMethod from "@coremedia/studio-client.client-core-impl/data/impl/RemoteServiceMethod";
import RemoteServiceMethodResponse from "@coremedia/studio-client.client-core-impl/data/impl/RemoteServiceMethodResponse";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import WorkArea from "@coremedia/studio-client.main.editor-components/sdk/desktop/WorkArea";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import GridPanel from "@jangaroo/ext-ts/grid/Panel";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import EsAnalyticsChart from "./EsAnalyticsChart";
import EsAnalyticsChartPanel from "./EsAnalyticsChartPanel";
import EsAnalyticsImpl from "./EsAnalyticsImpl";
import EsChart from "./EsChart";

interface EsAnalyticsChartPanelBaseConfig extends Config<PropertyFieldGroup> {
}

class EsAnalyticsChartPanelBase extends PropertyFieldGroup {
  declare Config: EsAnalyticsChartPanelBaseConfig;

  protected timeRangeValueExpression: ValueExpression = null;

  #esChart: EsChart = null;

  #tenantVE: ValueExpression = null;

  constructor(config: Config<EsAnalyticsChartPanel> = null) {
    super(config);
  }

  protected getTimeRangeValueExpression(): ValueExpression {
    if (!this.timeRangeValueExpression) {
      this.timeRangeValueExpression = ValueExpressionFactory.create("timerange", beanFactory._.createLocalBean({ "timerange": 7 }));
    }
    return this.timeRangeValueExpression;
  }

  protected override afterRender(): void {
    super.afterRender();

    const systemTabPanel = as(this.findParentByType(DocumentTabPanel), Panel);
    const versionHistoryListView = as(systemTabPanel.queryById("versionHistory"), GridPanel);
    if (versionHistoryListView) {
      this.mon(versionHistoryListView, "mouseenter", bind(this, this.#markEventInChartPanel));
    }
  }

  #markEventInChartPanel(historyPanel: GridPanel, index: number): void {
    if (this.#getEsChart().getLineChart()) {
      const record = historyPanel.getStore().getAt(index);
      const lifecycleStatus = as(record.data.lifecycleStatus, String);
      if ("published" === lifecycleStatus) {
        const date = as(record.data.editionDate, Date);
        const now = new Date();
        const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
        const diff = Math.ceil((today.getTime() - date.getTime()) / 86400000); //in days
        const interval: number = this.#getEsChart().getLineChartData().length;
        if (interval - diff >= 0) {
          const pos: number = interval - diff;
          this.#getEsChart().getLineChart().displayHoverForPublication(pos - 1);
        }
      }
    }
  }

  protected static getCurrentContent(): Content {
    return as(WorkArea.ACTIVE_CONTENT_VALUE_EXPRESSION.getValue(), Content);
  }

  #getEsChart(): EsChart {
    if (!this.#esChart) {
      this.#esChart = as(this.queryById(EsAnalyticsChart.ES_CHART_ITEM_ID), EsChart);
    }
    return this.#esChart;
  }

  getAlxData(propertyName: string): ValueExpression {
    return ValueExpressionFactory.createFromFunction((propertyName1: string): RemoteBean => {
      const currentContent = EsAnalyticsChartPanelBase.getCurrentContent();
      if (EsAnalyticsChartPanelBase.#validContent(currentContent) && this.#getTenantVE() && this.#getTenantVE().getValue()) {
        const alxPageViewsVE = EsAnalyticsImpl.getAlxPageViews(this.#getTenantVE().getValue(),
          propertyName1, currentContent.getId(), this.getTimeRangeValueExpression().getValue());
        if (alxPageViewsVE) {
          return alxPageViewsVE.getValue();
        }
      }
      // must not be undefined to trigger the BindPropertyPlugin in ExChart
      return null;
    }, propertyName);
  }

  static #validContent(content: Content): boolean {
    return !!content && !!content.getPath();
  }

  #getTenantVE(): ValueExpression {
    if (!this.#tenantVE) {
      this.#tenantVE = ValueExpressionFactory.createFromValue(undefined);

      if (EsAnalyticsChartPanelBase.getCurrentContent()) {
        const site = editorContext._.getSitesService().getSiteFor(EsAnalyticsChartPanelBase.getCurrentContent());
        if (site) {
          const siteId = site.getId();

          const rsm = new RemoteServiceMethod(EsAnalyticsImpl.ELASTIC_API_BASE_URL + EsAnalyticsImpl.TENANT_URI_SEGMENT + "?siteId=" + siteId, "GET");
          rsm.request(null,
            (response: RemoteServiceMethodResponse): void => {
              const tenantInfo = response.getResponseJSON();
              this.#tenantVE.setValue(tenantInfo["tenant"]);
            },
          );
        }
      }
    }
    return this.#tenantVE;
  }
}

export default EsAnalyticsChartPanelBase;
