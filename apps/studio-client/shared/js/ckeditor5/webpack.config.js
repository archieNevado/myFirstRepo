const { CKEditorTranslationsPlugin } = require("@ckeditor/ckeditor5-dev-translations");
const { loaders } = require( "@ckeditor/ckeditor5-dev-utils" );
const path = require('path');

module.exports = {

  entry: path.resolve(__dirname, 'src/ckeditor', 'ckeditor.ts'),

  externals: [
    "rxjs",
    "@coremedia/service-agent",
    "@coremedia/studio-client.ckeditor-common",
    "@coremedia/studio-client.cap-base-models",
  ],

  output: {
    // The name under which the editor will be exported.
    library: 'CKEditor5',

    path: path.resolve(__dirname, 'dist'),
    filename: 'bundled-ckeditor.js',
    libraryTarget: 'umd',
  },

  plugins: [

    new CKEditorTranslationsPlugin( {
      // See https://ckeditor.com/docs/ckeditor5/latest/features/ui-language.html
      language: 'en',
      additionalLanguages: ['de', 'ja'],
      translationsOutputFile: "bundled-ckeditor.js"
    } )
  ],
  resolve: {
    extensions: ['.tsx', '.ts', '.js'],
  },

  module: {
    rules: [
      loaders.getTypeScriptLoader({ configFile: "src/tsconfig.json" }),
      loaders.getIconsLoader({ matchExtensionOnly: true }),
      loaders.getStylesLoader({
        themePath: require.resolve("@ckeditor/ckeditor5-theme-lark"),
        minify: true,
      }),
    ]
  }
};
