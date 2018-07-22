<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->
<#assign rootNode=self.navigation.rootNavigation/>
<div class="cm-header navbar">
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
        <@cm.include self=self view="navigation"/>
    </ul>
  </div>
</div>