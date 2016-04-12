RewriteRule ^/editor-webstart/(.*) /editor-webstart/$1 [PT,L]
RewriteRule ^/coremedia/(.*) http://@CMS_HOST@:@CMS_HTTP_PORT@/coremedia/$1 [P,L]
RewriteRule ^/workflow/(.*) http://@WFS_HOST@:@WFS_HTTP_PORT@/workflow/$1 [P,L]
RewriteRule ^/(.*) /editor-webstart/$1 [PT,L]