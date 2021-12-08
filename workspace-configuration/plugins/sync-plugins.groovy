#!/usr/bin/env groovy

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.SourceURI

@SourceURI
URI sourceUri
File scriptDir = new File(sourceUri).parentFile
JsonSlurper jsonSlurper = new JsonSlurper()
JsonOutput jsonOutput = new JsonOutput()

File pluginDescriptorsFile = new File(scriptDir, 'plugin-descriptors.json')
File checkedInDescriptorsDir = new File(scriptDir, 'plugin-descriptors')

boolean blueprint = properties.blueprint != null ? Boolean.valueOf(properties.blueprint) : true
File baseDir = new File(scriptDir, "${blueprint ? '' : '../'}../../")

Closure<File> getPluginsFile = { app ->
  String filename
  if (app.startsWith('studio-client.')) {
    filename = "apps/${app.replace('.', '/apps/')}/${blueprint ? '' : 'blueprint/'}app/plugins.json"
  } else {
    filename = "apps/${app}/${blueprint ? '' : 'blueprint/'}spring-boot/${app}-app/plugins.json"
  }
  return new File(baseDir, filename)
}

new File(baseDir, 'apps').listFiles().each { getPluginsFile(it.name).delete() }
new File(baseDir, 'apps/studio-client/apps').listFiles().each {getPluginsFile("studio-client.${it.name}").delete() }

List<String> pluginDescriptorsUrls = jsonSlurper.parse(pluginDescriptorsFile) as List<String>
List<Object> descriptorsFromUrls = pluginDescriptorsUrls.collect { jsonSlurper.parse(new URL(it)) }
List<Object> descriptorsFromFiles = checkedInDescriptorsDir.listFiles(new FilenameFilter() {
  @Override
  boolean accept(File dir, String name) {
    name.endsWith('.json')
  }
}).collect { jsonSlurper.parse(it) }

Map<String, TreeSet<String>> pluginUrlsByApp = [:]

(descriptorsFromUrls + descriptorsFromFiles).each { json ->
  json.plugins.each { appPlugin ->
    String app = appPlugin.key
    String url = appPlugin.value.url
    println("Adding plugin '${url}' to app '${app}'")
    TreeSet<String> urls = pluginUrlsByApp.get(app) ?: []
    urls << url
    pluginUrlsByApp.put(app, urls)
  }
}

pluginUrlsByApp.each { app, urls ->
  File appPluginFile = getPluginsFile(app)
  appPluginFile.parentFile.mkdirs()
  appPluginFile.write(jsonOutput.prettyPrint(jsonOutput.toJson(urls)))
  println("Updated plugin file '${appPluginFile.canonicalPath}'")
}
