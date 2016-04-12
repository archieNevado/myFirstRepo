RewriteEngine on
RewriteLog "@DELIVERY_APACHE_LOG_DIR@/commerce-shop-rewrite.log"
RewriteLogLevel @DELIVERY_APACHE_REWRITE_LOGLEVEL@

# Send empty URL to aurora home page
RewriteRule ^/$ /webapp/wcs/stores/servlet/en/auroraesite [R=302,L]
