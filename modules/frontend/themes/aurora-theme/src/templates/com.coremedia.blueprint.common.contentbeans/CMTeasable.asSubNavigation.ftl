<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#assign uniqueId=cm.localParameters().parentLevelId+"_"+bp.generateId("dm")/>
<#assign idPrefix=cm.localParameters().idPrefix!""/>
<#assign link=cm.getLink(self!cm.UNDEFINED)/>
<#assign target=self.openInNewTab?then("_blank", "") />
<#assign rel=self.openInNewTab?then("noopener", "") />

<a id="${idPrefix}categoryLink_${uniqueId}" href="${link}"<#if target?has_content> target="${target?no_esc}" rel="${rel?no_esc}" </#if> aria-label="${self.teaserTitle}" class="menuLink" role="menuitem" tabindex="-1"<@preview.metadata self.content/>>${self.teaserTitle}</a>
<ul class="subcategoryList">
  <li>
    <div class="cm-dropdown-image">
      <@bp.optionalLink href=link attr={"target":target,"rel":rel}>
         <@cm.include self=self view="asPicture" params={"blockClass": "cm-teaser--megamenu"}/>
      </@bp.optionalLink>
    </div>
  </li>
</ul>
