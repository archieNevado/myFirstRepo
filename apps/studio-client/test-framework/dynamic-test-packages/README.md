# Dynamic Packages for Blueprint Studio Tests

This module contains dynamic packages for Blueprint Studio Tests.
As Blueprint Studio is pre-configured and cannot be directly modified
for testing purpose, you can do so via a workaround using
Dynamic Packages.

To do so, the Blueprint Studio needs to be started via a Proxy
URL which also serves `dynamic-packages.json` from the local
machine.

- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

* [Module Structure](#module-structure)
* [Usage](#usage)
* [Advanced Usage](#advanced-usage)
    * [Plugin for Plugins](#plugin-for-plugins)

- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

## Module Structure

Each plugin resides in its own SWC module. This ensures, that you can toggle
plugins individually.

Module `studio-test-app-overlays` will be used as dependency in `studio-itest`
and provides the JAR to read the app-overlays from. If you add a new module,
just add it as runtime dependency to `studio-test-app-overlays`. During test,
you can then individually select the extension plugins to load by their
package name, like for example
`com.coremedia.blueprint.internal__enable-disapprove-plugin.`
 
## Usage

The following usage documentation is reversed in execution order, as the steps
at the bottom are most likely already taken, so that you can just skip them.
What you always need to do is the last step (which is first here), i. e. to
tell your Studio within the test to use a proxy URL instead.

### Step 4: Use Dynamic Packages Proxy in Test

Add the following lines to your test:

```java
class StoryMyTest {
  @Value("${studio.main.url}")
  private String jooProxyTargetUri;
  
  // Default "0" will use a random port.
  @Value("${studio.proxy.port:0}")
  private Integer studioProxyPort;
  
  private DynamicModulesTestService dynamicModulesTestService;
  
  @Before
  public void setUp() {
    dynamicModulesTestService = DynamicModulesTestService.builder(jooProxyTargetUri)
            .setProxyPort(studioProxyPort)
            .setDynamicPackagesIncludes("com.coremedia.blueprint.internal__enable-disapprove-plugin")
            .start();
  }
  
  @After
  public void tearDown() {
    if (dynamicModulesTestService != null) {
      dynamicModulesTestService.stop(true);
    }
  }

  /* ... */
}
```

and ensure that your login step uses this proxy prior to calling `studio.get()`:

```java
class Steps {
  @Inject
  private AutoLoginStudio studio;

  public void given_I_am_logged_in_to_CoreMedia_Studio_via_Proxy(DynamicModulesTestService service) {
    studio.registerDynamicModulesTestService(dynamicModulesTestService);
    studio.get();
  }

  /* ... */
}
```

**Note:** The include filter is important, as many more packages will be available
in classpath which shall not be served by the proxy. Thus, it is recommended
to always explicitly set the packages to include.

### Step 3: Add Your Dynamic Packages Plugin To Dependencies

Add your dynamic packages plugin module to `studio-test-app-overlays`:

````xml
<dependencies>
  <!-- ... -->
  <dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>enable-disapprove-plugin</artifactId>
    <version>${project.version}</version>
    <type>swc</type>
    <scope>runtime</scope>
  </dependency>
  <!-- ... -->
</dependencies>
````

### Step 2: Add Your Dynamic Packages Plugin Module

At minimum the plugin module requires:

* A POM file with packaging type `swc`.
* A `package.json` file along with the POM file which sets the main-class, like:
```json
{
  "studioPlugins": [
    {
      "mainClass": "com.acme.coremedia.studio.disapprove.AcmeEnableDisapprovePlugin",
      "name": "AcmeEnableDisapprovePlugin"
    }
  ]
}
```
* The main class, like `AcmeEnableDisapprovePlugin.mxml`.

### Step 1: Add Dependencies to Test Module POM

The main dependency to add is `studio-test-app-overlays` which bundles all
overlays.  `dynamic-packages-helper` contains the proxy to use.

```xml
<dependencies>
  <!-- ... -->
  <dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>studio-test-app-overlays</artifactId>
    <version>${project.version}</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>com.coremedia.ui</groupId>
    <artifactId>dynamic-packages-helper</artifactId>
    <scope>test</scope>
  </dependency>
  <!-- ... -->
</dependencies>
```

In order to be able to run the test from within IntelliJ Idea it is required
to add the following execution to your POM:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-dependency-plugin</artifactId>
      <executions>
        <execution>
          <id>idea-workaround-extract-app</id>
          <phase>generate-test-resources</phase>
          <goals>
            <goal>unpack-dependencies</goal>
          </goals>
          <configuration>
            <outputDirectory>${project.build.testOutputDirectory}</outputDirectory>
            <includeArtifactIds>studio-test-app-overlays</includeArtifactIds>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

**Note:** This also requires to run Maven build prior to running the test
within your IDE.

## Advanced Usage

### Plugin for Plugins

By default all specified plugins in a dynamic packages plugin are enabled.
Sometimes you do not want to enable them all at once, for example, if they
are even conflicting (like providing different forms for the very same
content type). In this case, you can provide a plugin containing plugins
and activate them later on with the helper method
`com.coremedia.uitesting.cms.editor.EditorContext.loadEditorPlugin`.

The plugin itself is not required to do anything (so it is quite empty).
It only serves to add the required plugin classes to the _classpath_ within
Studio.

Example Module: `custom-forms-plugin`

### Plugin for Resources

Similar to a [Plugin for Plugins](#plugin-for-plugins) you can provide a plugin
which just serves resources. Again, you just create a dummy Studio plugin file
not doing anything.

Different to [Plugin for Plugins](#plugin-for-plugins), you will not add the
resources to the `joo` directory, but instead place your resources into
`src/main/sencha/resources`, like for example static HTML pages.

To resolve the URLs to access the static files, you can use the methods
`absolutePackageResource` and `relativePackageResource` in
`DynamicModulesTestService`.

Example Module: `preview-plugin`
