<#ftl strip_whitespace=true>
<#-- @ftlvariable name="blueprintFreemarkerFacade" type="com.coremedia.blueprint.cae.web.taglib.BlueprintFreemarkerFacade" -->
<#-- @ftlvariable name="cmFacade" type="com.coremedia.objectserver.view.freemarker.CAEFreemarkerFacade" -->
<#-- @ftlvariable name="cmpage" type="com.coremedia.blueprint.common.contentbeans.Page" -->
<#-- @ftlvariable name="settingsService" type="com.coremedia.blueprint.base.settings.SettingsService" -->

<#-- createBeanFor -->
<#function createBeanFor content>
  <#return blueprintFreemarkerFacade.createBeanFor(content)>
</#function>

<#-- createBeansFor -->
<#function createBeansFor contents>
  <#return blueprintFreemarkerFacade.createBeansFor(contents)>
</#function>

<#function isActiveNavigation navigation navigationPathList>
    <#return blueprintFreemarkerFacade.isActiveNavigation(navigation, navigationPathList)>
</#function>

<#-- SettingsFunction -->
<#function setting self key default=cm.UNDEFINED>
  <#return blueprintFreemarkerFacade.setting(self, key, default)>
</#function>

<#-- TransformationsFunction -->
<#function transformations self>
  <#return blueprintFreemarkerFacade.getTransformations(self)>
</#function>

<#-- GenerateUniqueId -->
<#function generateId prefix="">
  <#return blueprintFreemarkerFacade.generateId(prefix)>
</#function>

<#-- Truncate Text -->
<#function truncateText text maxLength>
  <#return blueprintFreemarkerFacade.truncateText(text, maxLength)>
</#function>

<#-- Truncate Text and closes last opened but not closed <b> tag -->
<#function truncateHighlightedText text maxLength>
  <#return blueprintFreemarkerFacade.truncateHighlightedText(text, maxLength)>
</#function>

<#-- Get fragments for Preview -->
<#function previewTypes page self defaultFragmentViews=[]>
  <#return blueprintFreemarkerFacade.getPreviewViews(self, page, defaultFragmentViews)>
</#function>

<#--
 * Builds a link to Video.
 * dataUrl is default, if dataUrl is empty blob-data is used
 * @param CMVideo
 -->
<#function getVideoLink video>
  <#assign videoLink=video.dataUrl!"" />
  <#if !videoLink?has_content && video.data?has_content>
    <#assign videoLink=cm.getLink(video.data)!"" />
  </#if>
  <#return videoLink />
</#function>

<#-- deprecated -->
<#function substitute id original>
  <#return cmFacade.substitute(id, original)>
</#function>

<#-- ErrorReporterHelper -->
<#function getStackTraceAsString exception>
  <#return blueprintFreemarkerFacade.getStackTraceAsString(exception)>
</#function>

<#function isWebflowRequest>
  <#return blueprintFreemarkerFacade.isWebflowRequest()!false>
</#function>

<#function getDisplaySize size>
  <#return blueprintFreemarkerFacade.getDisplaySize(size) />
</#function>

<#function getDisplayFileFormat mimeType>
  <#return blueprintFreemarkerFacade.getDisplayFileFormat(mimeType) />
</#function>

<#function isDisplayableImage blob>
  <#return blueprintFreemarkerFacade.isDisplayableImage(blob) />
</#function>

<#function isDisplayableVideo blob>
  <#return blueprintFreemarkerFacade.isDisplayableVideo(blob) />
</#function>

<#assign viewHookEventNames=blueprintFreemarkerFacade.getViewHookEventNames()/>

<#--
 * Retrieves the URL path that belongs to a theme resource (image, webfont, etc.) defined by its path within the
 * theme folder. The path must not contain any <strong>..</strong>
 * descending path segments.
 *
 * @param path to the resource within the theme folder
 -->
<#function getLinkToThemeResource path>
  <#return blueprintFreemarkerFacade.getLinkToThemeResource(path)>
</#function>