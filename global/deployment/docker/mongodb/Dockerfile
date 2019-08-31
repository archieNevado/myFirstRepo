FROM mongo:3.6.7
CMD ["mongod", "--wiredTigerCacheSizeGB=1"]
HEALTHCHECK --start-period=30s --interval=30s --timeout=3s \
  CMD mongo --quiet "$(hostname --ip-address || echo '127.0.0.1')/test" --eval 'quit(db.runCommand({ ping: 1 }).ok ? 0 : 2)' || exit 1
