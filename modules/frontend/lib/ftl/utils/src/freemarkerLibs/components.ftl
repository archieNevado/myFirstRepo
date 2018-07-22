<#--
 Renders a button based on given parameters.

 @param text If the button should contain text, this defines the text to be rendered.
 @param href If the button should be a link, this defines the href. If href is empty the button tag will
             be used, otherwise an anchor tag.
 @param baseClass If the button and its elements should have a baseClass attached, this defines the base class.
                  If empty, no baseClasses will be attached.
 @param iconClass Defines a class attached to the icon element of the button.
 @param iconText Defines the fallback text for the icon, if the icon cannot be displayed.
 @param textClass Defines a class attached to the text element of the button.
 @param attr Additional attributes to be rendered with the button element.

 Example:
 <@bp.button text="Like"
             iconClass="icon-checkmark"
             attr={"type": "submit", "class": "cm-button cm-button--small"}/>

  Note:
  For now uses the deprecated function to avoid code duplication as we cannot depend from "cae-base-lib" to
  the frontend workspace.
-->
<#macro button text="" href="" baseClass="cm-button" iconClass="" iconText="" textClass="" attr={}>
  <@bp.button text=text href=href baseClass=baseClass iconClass=iconClass iconText=iconText textClass=textClass attr=attr />
</#macro>
