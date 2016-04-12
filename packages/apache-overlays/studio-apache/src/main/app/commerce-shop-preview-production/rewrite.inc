RewriteEngine on
RewriteLog "@STUDIO_APACHE_LOG_DIR@/commerce-shop-preview-production-rewrite.log"
RewriteLogLevel @STUDIO_APACHE_REWRITE_LOGLEVEL@

# Send empty URL to aurora home page preview
RewriteRule ^/$ /webapp/wcs/preview/servlet/en/auroraesite [R=302,L]
