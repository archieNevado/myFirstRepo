<#--
  Renders a label associated to Spring forms.

  @param path The path name of the field to bind to.
  @param text The text of the label. By default, the localization for the bound field will be applied.
  @param bindPath Prevents the rebinding of the path, for example, if you already know that the path is bound.
  @param attr Additional attributes for the label tag.

  Example:
  <@labelFromSpring path="bpLoginForm.name" text=bp.getMessage("login_name_label") />

  Note:
  For now uses the deprecated function to avoid code duplication as we cannot depend from "cae-base-lib" to
  the frontend workspace.
-->
<#macro labelFromSpring path text="" bindPath=true attr={}>
  <@bp.labelFromSpring path=path text=text bindPath=bindPath attr=attr />
</#macro>

<#--
  Renders a notification.

  @param type Defines the type of the notification. Must be one of these values: error, warn, info, success or inactive.
  @param baseClass Defines the base class to be used for the notification and all its child elements.
  @param additionalClasses Additional classes to be attached to the root element of the notification.
  @param title Defines the title of the notification.
  @param text Defines the text of the notification.
  @param dismissable Defines if the notification is dismissable.
  @param iconClass Defines an the CSS class of an icon to be attached to the notification.
  @param attr Defines additional attributes to be attached to the root element of the notification.

  Example:
  <@notification type="inactive"
                 text=""
                 dismissable=false
                 additionalClasses=["cm-comment__notification"]
                 attr={"data-cm-notification": '{"path": ""}'}/>

  Note:
  For now uses the deprecated function to avoid code duplication as we cannot depend from "cae-base-lib" to
  the frontend workspace.
-->
<#macro notification type baseClass="cm-notification" additionalClasses=[] title="" text="" dismissable=false iconClass="" attr={}>
  <@bp.notification type=type
                    baseClass=baseClass
                    additionalClasses=additionalClasses
                    title=title
                    text=text
                    dismissable=dismissable
                    iconClass=iconClass
                    attr=attr />
</#macro>

<#--
  Renders a notification associated to Spring forms. The text will be determined automatically.

  @param path The name of the field to bind to.
  @param baseClass Defines the base class to be used for the notification and all its child elements.
  @param additionalClasses Additional classes to be attached to the root element of the notification.
  @param ignoreIfEmpty Specifies if the notification will not be rendered if Spring error messages are empty.
  @param type Defines the type of the notification. Must be one of these values: error, warn, info, success or inactive.
  @param title Defines the title of the notification.
  @param bindPath If false it prevents the rebinding of the path, for example, if you already know that the path is bound.
  @param attr Defines additional attributes to be attached to the root element of the notification.

  Example:
  <@notificationFromSpring path="bpLoginForm" additionalClasses=["alert alert-danger"]/>

  Note:
  For now uses the deprecated function to avoid code duplication as we cannot depend from "cae-base-lib" to
  the frontend workspace.
-->
<#macro notificationFromSpring path baseClass="cm-notification" additionalClasses=[] ignoreIfEmpty=true type="error" title="" bindPath=true attr={}>
  <@bp.notificationFromSpring path=path
                              baseClass=baseClass
                              additionalClasses=additionalClasses
                              ignoreIfEmpty=ignoreIfEmpty
                              type=type
                              title=title
                              bindPath=bindPath
                              attr=attr />
</#macro>
