<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" uri="http://www.coremedia.com/2004/objectserver-1.0-2.0" %>
<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%--@elvariable id="self" type="com.coremedia.blueprint.common.contentbeans.Page"--%>
<%--
  Example JSP to check if JSP is supported by the current deployment.
  Activate the 'test' view repository in your test site's settings by adding the
  'viewRepositoryNames' property of type String List with a single 'test' item.
  Then navigate to that site and add the 'view=jspTest' query param to the URL.
--%>
<!DOCTYPE html>
<html>
<head>
  <title>JSP Test view</title>
</head>
<body>
<cm:link var="target" target="${self.context}"/>
<div>
  JSP works.

  <a href="${target}">Go back</a> to the main page.

  Content path of the main page is <c:out value="${self.content.content.path}"/>.
</div>
</body>
</html>

