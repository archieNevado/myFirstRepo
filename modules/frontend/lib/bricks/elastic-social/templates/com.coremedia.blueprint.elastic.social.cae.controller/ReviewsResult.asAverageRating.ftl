<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/><#-- could be used as fragment -->
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.controller.ReviewsResult" -->

<#if self.isEnabled()>
  <#assign numberOfReviews=self.getNumberOfOnlineReviews()!0 />
  <#if (numberOfReviews > 0)>
    <#assign averageRatingRounded=((self.getAverageRating())!0)?round />
    <div class="cm-rating cm-rating--average">
      <div class="cm-rating__stars">
        <#list es.getMaxRating()..1 as currentRating>
          <#assign classRatingIndicator="" />
          <#if currentRating == averageRatingRounded>
            <#assign classRatingIndicator=" cm-rating-indicator--active" />
          </#if>
          <div class="cm-rating-indicator${classRatingIndicator}">${currentRating}</div>
        </#list>
      </div>
      <span class="cm-rating__votes">${numberOfReviews}</span>
    </div>
  </#if>
</#if>
