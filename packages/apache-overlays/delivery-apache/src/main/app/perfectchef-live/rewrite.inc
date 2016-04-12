RewriteEngine on
RewriteLog "@DELIVERY_APACHE_LOG_DIR@/perfectchef-live-rewrite.log"
RewriteLogLevel @DELIVERY_APACHE_REWRITE_LOGLEVEL@

# Remove preview querystring
RewriteCond %{QUERY_STRING} ^.*view=fragmentPreview.*$
RewriteRule (.*) $1? [R=301,L]

# Internal services, served only by Tomcat. Deny external invocation.
RewriteRule ^/internal/(.*) - [F,PT,L]

RewriteRule ^/blueprint/servlet/internal/(.*) - [F,PT,L]

# robots.txt
RewriteRule ^/robots.txt /blueprint/servlet/service/robots/perfectchef [PT,L]

# Send empty URL to perfectchef home page
RewriteRule ^/$ /perfectchef [R=302,L]
