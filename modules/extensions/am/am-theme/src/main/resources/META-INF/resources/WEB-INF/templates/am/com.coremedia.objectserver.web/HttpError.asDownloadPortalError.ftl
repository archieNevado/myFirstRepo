<#-- @ftlvariable name="self" type="com.coremedia.objectserver.web.HttpError" -->

<div class="am-download-portal">

  <div class="am-download-portal__header">
    <div class="am-download-portal__breadcrumb"></div>
    <h1 class="am-download-portal__title am-heading-1">${bp.getMessage(am.messageKeys.DOWNLOAD_PORTAL)}</h1>
  </div>

  <div class="am-download-portal__content am-error">
    <h1 class="am-error__title am-heading-2">${bp.getMessage(am.messageKeys.ERROR_NOT_FOUND_TITLE)}</h1>
    <div class="am-error__message">
      <p >${bp.getMessage(am.messageKeys.ERROR_NOT_FOUND_MESSAGE)}</p>
      <p><a class="am-text-link" data-hash-based-fragment-link="">${bp.getMessage(am.messageKeys.BACK_TO_DOWNLOAD_PORTAL)}</a></p>
    </div>
  </div>
</div>
