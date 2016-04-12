RewriteEngine on
RewriteLog "@STUDIO_APACHE_LOG_DIR@/perfectchef-preview-rewrite.log"
RewriteLogLevel @STUDIO_APACHE_REWRITE_LOGLEVEL@

# Internal services, served only by Tomcat. Deny external invocation.
RewriteRule ^/internal/(.*) - [F,PT,L]

RewriteRule ^/blueprint/servlet/internal/(.*) - [F,PT,L]

# robots.txt
RewriteRule ^/robots.txt /blueprint/servlet/service/robots/perfectchef [PT,L]

# Send empty URL to perfectchef home page
RewriteRule ^/$ /blueprint/servlet/perfectchef [R=302,L]
