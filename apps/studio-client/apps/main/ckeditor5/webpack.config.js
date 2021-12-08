const CKEditorWebpackPlugin = require( '@ckeditor/ckeditor5-dev-webpack-plugin' );
const { styles } = require( '@ckeditor/ckeditor5-dev-utils' );
const path = require('path');

module.exports = {

  entry: path.resolve(__dirname, 'src/ckeditor', 'ckeditor.ts'),

  externals: [
    "rxjs",
    "@coremedia/service-agent",
    "@coremedia/studio-client.ckeditor-constants",
  ],

  output: {
    // The name under which the editor will be exported.
    library: 'CKEditor5',

    path: path.resolve(__dirname, 'dist'),
    filename: 'bundled-ckeditor.js',
    libraryTarget: 'umd',
    libraryExport: 'default'
  },

  plugins: [

    new CKEditorWebpackPlugin( {
      // See https://ckeditor.com/docs/ckeditor5/latest/features/ui-language.html
      language: 'en',
      additionalLanguages: 'all'
    } )
  ],
  resolve: {
    extensions: ['.tsx', '.ts', '.js'],
  },

  module: {
    rules: [
      {
        test: /\.js$/,
        enforce: 'pre',
        use: ['source-map-loader'],
      },
      {
        test: /\.tsx?$/,
        loader: 'ts-loader',
        exclude: /node_modules/,
        options: {
          projectReferences: true,
        }
      },
      {
        test: /\.svg$/,
        use: ['raw-loader']
      },
      {
        test: /\.css$/,
        use: [
          {
            loader: 'style-loader',
            options: {
              injectType: 'singletonStyleTag',
              attributes: {
                'data-cke': true
              }
            }
          },
          {
            loader: 'css-loader'
          },
          {
            loader: 'postcss-loader',
            options: styles.getPostCssConfig( {
              themeImporter: {
                themePath: require.resolve( '@ckeditor/ckeditor5-theme-lark' )
              },
              minify: true
            } )
          },
        ]
      }
    ]
  }
};
