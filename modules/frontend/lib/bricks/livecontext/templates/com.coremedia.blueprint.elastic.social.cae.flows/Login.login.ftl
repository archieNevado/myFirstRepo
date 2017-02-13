<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState" -->

<#assign loginAction=self.loginAction!cm.UNDEFINED />

<div class="cm-box"<@cm.metadata data=[loginAction.content!"", "properties.id"] />>

<@cm.include self=loginAction view="headline" params={"classHeadline": "cm-box__header cm-headline--small"} />

    <h3 class="cm-box__header cm-heading3 cm-heading3--boxed"><@bp.message es.messageKeys.LOGIN_SIGN_IN /></h3>

    <div class="cm-box__content">

        <div class="cm-container cm-collection--tiles-login">
            <div class="row-grid row">
                <div class="col-sm-6 col-xs-12">
                <@cm.include self=self view="loginForm" params={
                "classContainer": "cm-collection__item"
                } />
                </div>
                <div class="col-sm-6 col-xs-12">
                <@cm.include self=self view="socialmedia" params={
                "classContainer": "cm-collection__item"
                } />
                </div>
            </div>
        </div>
    </div>
</div>

<@cm.include self=self view="signUp" />
