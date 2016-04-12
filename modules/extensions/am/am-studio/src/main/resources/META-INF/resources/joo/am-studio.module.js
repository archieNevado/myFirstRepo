joo.loadModule("${project.groupId}", "${project.artifactId}");
joo.loadStyleSheet('joo/resources/css/${project.artifactId}.css');
joo.loadStyleSheet('joo/resources/css/icons/${project.artifactId}-icons.css');

//noinspection JSUnusedGlobalSymbols
coremediaEditorPlugins.push({
  name:"Asset Management Extensions",
  mainClass:"com.coremedia.blueprint.assets.studio.AMStudioPlugin"
});


