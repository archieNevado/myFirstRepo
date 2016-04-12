RewriteEngine on
RewriteLog "@STUDIO_APACHE_LOG_DIR@/commerce-shop-preview-rewrite.log"
RewriteLogLevel @STUDIO_APACHE_REWRITE_LOGLEVEL@

RewriteRule ^/lobtools/(.*) /lobtools/$1 [PT,L]

# Send empty URL to aurora home page
RewriteRule ^/$ /webapp/wcs/stores/servlet/en/auroraesite [R=302,L]
