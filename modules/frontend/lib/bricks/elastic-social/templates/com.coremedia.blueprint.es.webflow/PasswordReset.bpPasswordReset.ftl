<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.webflow.WebflowActionState" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->
<#-- @ftlvariable name="flowExecutionKey" type="java.lang.String" -->
<#-- @ftlvariable name="nextUrl" type="java.lang.String" -->
<#import "/spring.ftl" as spring>

<#assign passwordResetAction=self.action />

<div class="cm-box"<@cm.metadata data=[(passwordResetAction.content)!"", "properties.id"]/>>
    <h3 class="cm-box__header cm-heading3 cm-heading3--boxed"><@bp.message "passwordReset_title" /></h3>

    <div class="cm-box__content">

        <form method="post" class="cm-form form-horizontal" data-cm-form--forgot="">
            <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}"/>
            <input type="hidden" name="execution" value="${flowExecutionKey!""}"/>
            <input type="hidden" name="nextUrl" value="${nextUrl!""}"/>
            <input type="hidden" name="_eventId_submit"/>

            <div class="form-group">
            <@spring.bind path="bpPasswordReset.emailAddress"/>
                <label for="${spring.status.expression?replace('[','')?replace(']','')}"
                       class="col-sm-2 control-label cm-form__label">${bp.getMessage("passwordReset_email_label")}</label>

                <div class="col-sm-10 cm-form__value">
                    <div class="input-group">
                        <span class="input-group-addon">@</span>
                    <@spring.formInput path="bpPasswordReset.emailAddress" attributes='class="form-control" placeholder="${bp.getMessage("passwordReset_email_label")}"' fieldType="email"/>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10 cm-form__value">
                <@bp.button text=bp.getMessage("passwordReset_button") attr={"type": "submit", "classes": ["btn","btn-primary"]} />
                </div>
            </div>
        </form>
    </div>
</div>
