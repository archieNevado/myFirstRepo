<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->

<#if self.data?has_content>
  <#-- display the image uncropped in original size -->
  <#if self.disableCropping>
    <#-- id may not be generated using bp.generateId, as persisting toggle state in local storage will not work -->
    <#assign toggleId="toggle-0-details" />
    <div class="toggle-item cm-preview-item" data-id="${toggleId}">
      <a href="#" class="toggle-button cm-preview-item__headline">
        <@bp.message "Image_originalSize" />
      </a>
      <div class="toggle-container cm-preview-item__container">
        <@cm.include self=self view="details" />
      </div>
    </div>

  <#else>
    <#-- image as teaser -->
    <#-- id may not be generated using bp.generateId, as persisting toggle state in local storage will not work -->
    <#assign toggleId="toggle-1-asTeaser" />
    <div class="toggle-item cm-preview-item" data-id="${toggleId}">
      <a href="#" class="toggle-button cm-preview-item__headline">
        <@bp.message "Image_teaser" />
      </a>
      <div class="toggle-container cm-preview-item__container">
        <div class="cm-preview-content cm-clearfix">
          <div class="content">
            <@cm.include self=self view="asTeaser" />
          </div>
        </div>
      </div>
    </div>
    <#-- display every aspect ratio as preview item -->
    <#assign transformations=bp.transformations(self.content) />
    <#list transformations as transformation>
      <#-- id may not be generated using bp.generateId, as persisting toggle state in local storage will not work -->
      <#assign toggleId="toggle-crop-" + transformation.name />
      <div class="toggle-item cm-preview-item" data-id="${toggleId}">
        <a href="#" class="toggle-button cm-preview-item__headline">
          <@bp.message "Image_"+transformation.name />
        </a>
        <div class="toggle-container cm-preview-item__container">
          <div class="cm-image-box cm-image-box--preview" style="max-width: ${transformation.previewWidth}px;">
            <@cm.include self=self params={
              "limitAspectRatios": [transformation.name],
              "crop": transformation.name,
              "classBox": "cm-image-box__image"
            }/>
          </div>
        </div>
      </div>
    </#list>
  </#if>
</#if>
