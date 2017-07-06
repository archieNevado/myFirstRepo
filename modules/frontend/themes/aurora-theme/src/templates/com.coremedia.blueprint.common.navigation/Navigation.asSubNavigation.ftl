<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.navigation.Navigation" -->
<#assign uniqueId=cm.localParameters().parentLevelId+"_"+bp.generateId("dm")/>
<#assign idPrefix=cm.localParameters().idPrefix!""/>
<#assign level=cm.localParameters().level!1/>
<#assign link=cm.getLink(self!cm.UNDEFINED)/>
<#assign navigationDepth=bp.setting(self, "livecontext.navigation.depth", 2)/>
<#if level <= navigationDepth>
  <a id="${idPrefix}categoryLink_${uniqueId}" href="${link}" aria-label="${self.title}" class="menuLink" role="menuitem" tabindex="-1"<#if self.content?has_content><@cm.metadata self.content /></#if>>${self.title}</a>
  <#list self.visibleChildren>
  <ul class="subcategoryList" <@cm.metadata "properties.visibleChildren" />>
    <#items as child>
      <li>
        <@cm.include self=child view="asSubNavigation" params={"parentLevelId": uniqueId, "idPrefix": "sub", "level":level+1}/>
      </li>
    </#items>
  </ul>
  <#else>
    <#if level==1>
    <ul class="subcategoryList">
      <li>

        <div class="cm-dropdown-image">
          <@bp.optionalLink href=cm.getLink(self!cm.UNDEFINED)>
                  <@cm.include self=self view="asPicture" params={"additionalClass": "cm-teaser--megamenu"}/>
                </@bp.optionalLink>
        </div>

      </li>
    </ul>
    </#if>
  </#list>
</#if>
