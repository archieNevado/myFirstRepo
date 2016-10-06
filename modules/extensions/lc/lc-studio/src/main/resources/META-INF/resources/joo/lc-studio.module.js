joo.loadStyleSheet("joo/resources/css/livecontext-plugin.css");
joo.loadStyleSheet('joo/resources/css/icons/lc-studio-icons.css');

joo.loadModule("${project.groupId}", "${project.artifactId}");
//noinspection JSUnusedGlobalSymbols
coremediaEditorPlugins.push({
  name:"Livecontext Extensions",
  mainClass:"com.coremedia.livecontext.studio.LivecontextStudioPlugin"
});


