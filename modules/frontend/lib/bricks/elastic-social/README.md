# CoreMedia Blueprint

## Elastic Social as a Brick

This brick acts as an entry point into CoreMedia Elatic Social.
  

### Usage

#### Loading the Brick

Just add following line to your Gruntfile.js to load the brick.

```
    // Load the "cta" brick
    utils.loadBrick(grunt, "elastic-social");
```

If you want to load multiple bricks, you may also use the following syntax:
```
    utils.loadBricks(grunt, [..., "elastic-social", ...]);
```

#### Using the brick

The Elastic Social Brick does not come with any templates or JavaScript code. By loading the brick existing templates are copied from the elastic social extension.
To also load the Elastic Social Extensions CSS files you only need to import the following line of scss into your scss file:

```
    @import "../../../../../lib/bricks/elastic-social/sass/elastic-social";
```

The path might need some adaption, depending on your folder structure.

By adding the following statement to your theme-descriptor you can load all required localizations:

```
      <resourceBundles>
        ...
        <resourceBundle>l10n/ElasticSocial_en.properties</resourceBundle>
        ...
      </resourceBundles>
```