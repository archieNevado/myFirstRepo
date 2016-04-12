<#-- @ftlvariable name="self" type="com.coremedia.blueprint.assets.contentbeans.AMTaxonomy" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->
<#-- @ftlvariable name="classImage" type="java.lang.String" -->
<#-- @ftlvariable name="scalePicture" type="java.lang.Boolean" -->

<#assign classBox=cm.localParameters().classBox!"" />
<#assign classImage=cm.localParameters().classImage!"" />
<#assign scalePicture=cm.localParameters().scalePicture!false />

<#if self.assetThumbnail?has_content && self.assetThumbnail.thumbnail?has_content>
  <div class="am-picture-box ${classBox}"<@cm.metadata data=["properties.assetThumbnail", self.assetThumbnail.content, "properties.thumbnail"]/>>
    <#assign imageSrc=cm.getLink(self.assetThumbnail.thumbnail)!"" />
    <img src="${imageSrc}"
         alt="${(self.title)!""}"
         class="am-picture-box__picture ${classImage}"
         <#if scalePicture>
           <@cm.dataAttribute name="data-cm-non-adaptive-content" data={"overflow": false} />
         </#if>
         />
  </div>
<#else>
  <div class="am-picture-box am-picture-box--empty ${classBox}"<@cm.metadata data=["properties.assetThumbnail"]/>>
    <div class="am-picture-box__picture ${classImage}"></div>
  </div>
</#if>
