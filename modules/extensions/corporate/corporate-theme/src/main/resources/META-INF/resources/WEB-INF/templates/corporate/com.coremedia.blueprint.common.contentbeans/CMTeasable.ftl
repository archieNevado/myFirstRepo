<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign media=self.media![] />
<#assign carouselId=bp.generateId("carousel") />

<article class="cm-details"<@cm.metadata self.content />>

  <#-- title -->
  <h1 class="cm-details__headline"<@cm.metadata "properties.title"/>>${self.title!""}</h1>

  <#-- media -->
  <#if (media?size > 1)>
    <#-- partial template to render a list of items as part of a carousel -->
    <div id="${carouselId}" class="cm-details__medias cm-carousel carousel slide" data-cm-carousel='{"pause":"true", "interval":"6000"}'>
      <#-- Wrapper for slides -->
      <div class="cm-carousel-inner carousel-inner" role="listbox">
        <#list media as item>
          <#assign additionalClass="" />
          <#if item_index == 0>
            <#assign additionalClass="item active"/>
          <#else>
            <#assign additionalClass="item"/>
          </#if>
          <div class="cm-carousel__item ${additionalClass}">
            <@cm.include self=item view="asRichMedia" params={
              "limitAspectRatios": ["landscape_ratio16x9", "landscape_ratio5x2"],
              "classBox": "cm-details__media-box",
              "classImage": "cm-details__media"
            }/>
            <div class="cm-details__caption carousel-caption">
              <#-- picture title -->
              <#if item.title?has_content>
                <div class="cm-caption__title"<@cm.metadata "properties.title"/>>
                  ${item.title!""}
                </div>
              </#if>
              <#-- picture caption -->
              <#if item.detailText?has_content>
                <div class="cm-caption__text cm-richtext"<@cm.metadata "properties.detailText"/>>
                  <@cm.include self=item.detailText!cm.UNDEFINED />
                </div>
              </#if>
            </div>
          </div>
        </#list>
      </div>

      <#-- Controls -->
      <div class="cm-carousel__control cm-carousel__control--details">
        <a class="cm-carousel-control carousel-control left" href="#${carouselId}" role="button" data-slide="prev">
          <span class="glyphicon glyphicon-triangle-left" aria-hidden="true"></span>
          <span class="sr-only"><@bp.message "previous" /></span>
        </a>
        <p class="cm-carousel__pagination">
          <span class="cm-carousel__pagination-index">1</span>
          /
          <span class="cm-carousel__pagination-total">${media?size}</span>
        </p>
        <a class="cm-carousel-control carousel-control right" href="#${carouselId}" role="button" data-slide="next">
          <span class="glyphicon glyphicon-triangle-right" aria-hidden="true"></span>
          <span class="sr-only"><@bp.message "next" /></span>
        </a>
      </div>
    </div>
  <#-- single item, no slideshow -->
  <#elseif media?size == 1>
    <div class="cm-details__medias">
      <@cm.include self=self.media[0] view="asRichMedia" params={
        "limitAspectRatios": ["landscape_ratio16x9", "landscape_ratio5x2"],
        "classBox": "cm-details__media-box",
        "classImage": "cm-details__media"
      }/>
      <div class="cm-details__caption">
        <#-- picture title -->
        <#if self.media[0].title?has_content>
          <div class="cm-caption__title"<@cm.metadata "properties.title"/>>
            ${self.media[0].title!""}
          </div>
        </#if>
        <#-- picture caption -->
        <#if self.media[0].detailText?has_content>
          <div class="cm-caption__text cm-richtext"<@cm.metadata "properties.detailText"/>>
            <@cm.include self=self.media[0].detailText!cm.UNDEFINED />
          </div>
        </#if>
      </div>
    </div>
  </#if>

  <#-- text -->
  <#if self.detailText?has_content>
    <div class="cm-details__text cm-richtext"<@cm.metadata "properties.detailText"/>>
      <@cm.include self=self.detailText!cm.UNDEFINED />
    </div>
  </#if>

  <#-- date -->
  <#if self.externallyDisplayedDate?has_content>
    <div class="cm-details__date"<@cm.metadata "properties.externallyDisplayedDate"/>>
      <@bp.renderDate self.externallyDisplayedDate.time "cm-details__time" />
    </div>
  </#if>

  <#-- tags -->
  <@cm.include self=self view="asTagList"/>
</article>

<#-- related -->
<#if self.related?has_content>
  <div class="cm-related"<@cm.metadata "properties.related"/>>
    <h2>${bp.getMessage("related.label")}</h2>
    <@cm.include self=bp.getContainer(self.related) view="asMedialist" />
  </div>
</#if>

<#-- extensions -->
<@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_END />
