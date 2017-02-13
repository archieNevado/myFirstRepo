# CoreMedia Blueprint

## Asset Management Download Portal as a Brick

This brick acts as an entry point into CoreMedia Asset Managment Download Portal.
  

### Usage

#### Loading the Brick

Just add following line to your Gruntfile.js to load the brick.

Just add following config to your Gruntfile.js to load the brick.

```
    // load bricks into theme
    bricks: {
      src: [
        'download-portal',
        ...
      ]
    }
```

#### Using the brick

The Elastic Social Brick does not come with any templates or JavaScript code. By loading the brick existing templates are copied from the elastic social extension.
To also load the Elastic Social Extensions CSS files you only need to import the following line of scss into your scss file:

```
    @import "../../../../../lib/bricks/download-portal/sass/download-portal";
```

The path might need some adaption, depending on your folder structure.

By adding the following statement to your theme-descriptor you can load all required localizations:

```
      <resourceBundles>
        ...
        <resourceBundle>l10n/AssetManagement_en.properties</resourceBundle>
        ...
      </resourceBundles>
```
