<#-- @ftlvariable name="self" type="com.coremedia.blueprint.analytics.google.GoogleAnalytics" -->

<#if self.content?has_content>
  <#assign currentPageUrl= cm.getLink(self.content)/>
</#if>
<#-- google analytics -->
<#if self.enabled>

  <!-- Google tag (gtag.js) -->
  <script async src="https://www.googletagmanager.com/gtag/js?id=${self.measurementId}"></script>
  <script type="text/javascript">
    <#include "/WEB-INF/includes/js/alx-integration-googleanalytics.js">
    window.dataLayer = window.dataLayer || [];
    function gtag(){dataLayer.push(arguments);}
    gtag('js', new Date());

    var gaAccountData = new GaAccountData("${self.measurementId!""}");

    var gaPageData = new GaPageviewData(
            "${self.contentId!""}",
            "${self.contentType!""}",
            "${self.navigationPathIds?join('_')}",
            "${currentPageUrl!""}", null, null, null,
            ${self.advertisingFeaturesPluginDisabled?c}
    );

    gaTrackPageview(gtag, gaAccountData, gaPageData);
  </script>
</#if>
