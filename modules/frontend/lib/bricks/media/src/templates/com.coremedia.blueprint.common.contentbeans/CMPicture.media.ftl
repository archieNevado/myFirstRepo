<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->
<#-- @ftlvariable name="limitAspectRatios" type="java.util.List" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->
<#-- @ftlvariable name="classMedia" type="java.lang.String" -->
<#-- @ftlvariable name="disableCropping" type="java.lang.Boolean" -->
<#-- @ftlvariable name="background" type="java.lang.Boolean" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->
<#-- @ftlvariable name="metadataMedia" type="java.util.List" -->
<#-- @ftlvariable name="additionalAttr" type="java.util.Map" -->

<#import "../../freemarkerLibs/media.ftl" as media />

<#assign limitAspectRatios=cm.localParameters().limitAspectRatios![]/>
<#assign classBox=cm.localParameters().classBox!""/>
<#assign classMedia=cm.localParameters().classMedia!""/>
<#assign disableCropping=cm.localParameters().disableCropping!false/>
<#assign background=cm.localParameters().background!false/>
<#assign metadata=cm.localParameters().metadata![]/>
<#assign metadataMedia=cm.localParameters().metadataMedia![]/>
<#assign additionalAttr=cm.localParameters().additionalAttr!{}/>

<div class="${classBox}"<@preview.metadata data=metadata + [self.content]/>>
  <#if self.data?has_content>
    <#assign imageLink=""/>
    <#assign responsiveData=""/>

    <#-- decide if responsiveImage functionality is to be used or uncropped image will be shown -->
    <#if self.disableCropping || disableCropping>
      <#-- A) Cropping disabled, display image in full size -->
      <#assign imageLink=bp.uncroppedImageLink(self)/>
    <#else>
      <#-- B) display responsive image -->
      <#-- imageLink is empty in this case -->
      <#assign responsiveData=bp.responsiveImageLinksData(self, limitAspectRatios)!""/>
    </#if>

    <#-- use high resolution images -->
    <#assign retinaEnabled=bp.setting(cmpage, "enableRetinaImages", false)/>

    <#-- alt is the content name by default -->
    <#assign alt=(self.content.name)!""/>
    <#-- if alt property is set, use it as alt -->
    <#if self.alt?has_content>
      <#assign alt=self.alt />
    </#if>

    <#-- title (and copyright) -->
    <#assign title=self.title!""/>
    <#if self.copyright?has_content>
      <#assign title += title?has_content?then(" ", "") + "(© " + self.copyright + ")"/>
    </#if>

    <@media.renderPicture asBackground=background
                          additionalClass=classMedia
                          src=imageLink
                          alt=alt
                          title=title
                          enableRetina=retinaEnabled
                          responsiveData=responsiveData
                          metadata=metadataMedia + ["properties.data"]
                          additionalAttributes=additionalAttr />
  </#if>
</div>
