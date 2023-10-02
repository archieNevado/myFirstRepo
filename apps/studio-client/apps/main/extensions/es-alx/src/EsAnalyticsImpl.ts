import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";

class EsAnalyticsImpl {
  static readonly TENANT_URI_SEGMENT: string = "tenant";

  static readonly ES_ALX_CHART: string = "esAlxChartItemId";

  static readonly ELASTIC_API_BASE_URL: string = "elastic/";

  static readonly ALX_API_BASE_URL: string = EsAnalyticsImpl.ELASTIC_API_BASE_URL + "alx/";

  static readonly #ALX_PAGEVIEWS_API_BASE_URL: string = "pageviews/";

  static readonly #PUBLICATIONS_API_BASE_URL: string = "publications/";

  static getAlxPageViews(tenant: string, propertyName: string, contentId: string, timeRange: string): ValueExpression {
    const pageviewsUriPrefix = EsAnalyticsImpl.#getTenantAwareAlxPageviewsUriPrefix(tenant);
    if (pageviewsUriPrefix) {
      return ValueExpressionFactory.create(propertyName, beanFactory._.getRemoteBean(pageviewsUriPrefix + EsAnalyticsImpl.convertIdField(contentId)
              + "?timeRange=" + timeRange));
    }
    return null;
  }

  static getPublicationData(tenant: string, propertyName: string, contentId: string, timeRange: string): ValueExpression {
    const publicationsUriPrefix = EsAnalyticsImpl.#getTenantAwarePublicationsUriPrefix(tenant);
    if (publicationsUriPrefix) {
      return ValueExpressionFactory.create(propertyName, beanFactory._.getRemoteBean(publicationsUriPrefix + EsAnalyticsImpl.convertIdField(contentId)
              + "?timeRange=" + timeRange));
    }
  }

  static convertIdField(id: string): string {
    return id.substr(id.lastIndexOf("/") + 1, id.length);
  }

  static #getTenantAwareAlxUriPrefix(tenant: string): string {
    return tenant + "/" + EsAnalyticsImpl.ALX_API_BASE_URL;
  }

  static #getTenantAwareAlxPageviewsUriPrefix(tenant: string): string {
    if (tenant) {
      return EsAnalyticsImpl.#getTenantAwareAlxUriPrefix(tenant) + EsAnalyticsImpl.#ALX_PAGEVIEWS_API_BASE_URL;
    }
    return null;
  }

  static #getTenantAwarePublicationsUriPrefix(tenant: string): string {
    if (tenant) {
      return EsAnalyticsImpl.#getTenantAwareAlxUriPrefix(tenant) + EsAnalyticsImpl.#PUBLICATIONS_API_BASE_URL;
    }
    return null;
  }
}

export default EsAnalyticsImpl;
