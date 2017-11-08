<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->

<#assign numberOfItems=self.items?size />
<#assign localizations=cmpage.content.localizations![] />
<#assign cartAction=bp.setting(cmpage.context,"cartAction", {})/>
<#assign searchAction=bp.setting(cmpage.context,"searchAction", {})/>

<div class="container">
  <header id="cm-${self.name!""}" class="cm-header navbar"<@cm.metadata data=[bp.getPlacementPropertyName(self)!"",bp.getPlacementHighlightingMetaData(self)!""]/>>
    <#-- mobile hamburger menu -->
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed pull-left" data-toggle="collapse" data-target="#navbar" aria-expanded="false">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <#-- logo -->
      <a class="cm-header__logo navbar-brand" href="${cm.getLink(cmpage.navigation.rootNavigation!cm.UNDEFINED)}"></a>
    </div>

    <#-- cart widget -->
    <#if cartAction?has_content>
      <div class="cm-header__cart navbar-right">
        <@cm.include self=cartAction view="asHeader" />
      </div>
    </#if>

    <#-- search widget -->
    <#if searchAction?has_content>
      <div class="cm-search__magnifier--large mobile-search">
        <span></span>
      </div>
      <div id="cmSearchWrapper" class="cm-search navbar-form navbar-right">
        <@cm.include self=searchAction view="asHeader" />
      </div>
    </#if>

    <div id="navbar" class="cm-header-navbar collapse navbar-collapse navbar-right">
      <ul class="nav navbar-nav">

        <#-- login/logout -->
        <li class="cm-header__login" data-cm-loginstatus="${lc.getStatusUrl()}">
          <a id="cm-login" class="cm-header__login-status" href="${lc.getLoginFormUrl()}" title="${bp.getMessage("login_title")}">${bp.getMessage("login_title")}</a>
          <a id="cm-logout" class="cm-header__login-status" href="${lc.getLogoutUrl()}" title="${bp.getMessage("logout_title")}">${bp.getMessage("logout_title")}</a>
        </li>

        <#-- navigation -->
        <li class="cm-header-navbar__divider"></li>
          <@cm.include self=self view="navigation"/>
        <li class="cm-header-navbar__divider"></li>

        <#-- language/country chooser widget -->
        <#if (localizations?size > 1) >
          <li class="dropdown cm-language-chooser">
            <a href="#" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
              ${cmpage.locale.displayCountry} (${cmpage.locale.language?upper_case})
            </a>
            <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
              <span class="caret"></span>
            </a>
            <#list localizations>
              <ul class="dropdown-menu">
                <#items as localization>
                  <#assign variantLink=cm.getLink(localization)!"" />
                  <#if localization.locale != cmpage.content.locale && variantLink?has_content>
                    <li><a href="${variantLink}">${localization.locale.displayCountry} (${localization.locale.language?upper_case})</a></li>
                  </#if>
                </#items>
              </ul>
            </#list>
          </li>
        </#if>

        <#-- additional header items -->
        <#if (numberOfItems > 0)>
          <#list self.items![] as item>
            <li class="cm-header__item">
              <@cm.include self=item view="asHeader" />
            </li>
          </#list>
        </#if>
      </ul>
    </div>
  </header>
</div>
