<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->

<div class="container">
  <header id="cm-${self.name!""}" class="cm-header cm-header--navigation navbar"<@preview.metadata data=[bp.getPlacementPropertyName(self)!"",bp.getPlacementHighlightingMetaData(self)!""]/>>
  <#-- mobile hamburger menu -->
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed pull-left" data-toggle="collapse" data-target="#navbar" aria-expanded="false">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
    </div>

    <div id="navbar" class="cm-header-navbar collapse navbar-collapse navbar-right">
      <ul class="nav navbar-nav">
        <#-- navigation -->
        <@cm.include self=cmpage view="navigation"/>
      </ul>
    </div>
  </header>
</div>