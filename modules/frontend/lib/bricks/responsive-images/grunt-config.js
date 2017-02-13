/* brick 'responsive images' */
module.exports = function (grunt, options) {
  'use strict';

  // add templates to theme templateset
  var existingTemplates = options.brickCompressFiles;
  existingTemplates.push({
    expand: true,
    cwd: options.brickDirectory + '/templates',
    src: '**',
    dest: 'META-INF/resources/WEB-INF/templates/bricks/'
  });

  return {
    tasks: {
      compress: {
        brick_templates: {
          files: existingTemplates
        }
      },
      copy: {
        brick_responsive_images_nm: {
          expand: true,
          cwd: '../../node_modules',
          src: 'imagesloaded/imagesloaded.pkgd.js',
          dest: '../../target/resources/themes/<%= themeConfig.name %>/vendor/'
        },
        brick_responsive_images_img: {
          expand: true,
          cwd: options.brickDirectory,
          src: 'img/**',
          dest: '../../target/resources/themes/<%= themeConfig.name %>/'
        },
        brick_responsive_images_js: {
          expand: true,
          cwd: options.brickDirectory,
          src: 'js/*.js',
          dest: '../../target/resources/themes/<%= themeConfig.name %>/'
        },
        brick_responsive_images_templates: {
          expand: true,
          cwd: options.brickDirectory + '/templates',
          src: '**',
          dest: options.brickTemplatesDest
        }
      },
      watch: {
        brick_responsive_images_img: {
          files: options.brickDirectory + "/img/**",
          tasks: ['copy:brick_responsive_images_img']
        },
        brick_responsive_images_js: {
          files: options.brickDirectory + "/js/**",
          tasks: ['copy:brick_responsive_images_js']
        },
        brick_responsive_images_templates: {
          files: options.brickDirectory + "/templates/**",
          tasks: ['copy:brick_responsive_images_templates', 'compress:brick_templates']
        },
        brick_responsive_images_sass: {
          options: {
            spawn: true
          },
          files: options.brickDirectory + '/sass/**/*.scss',
          tasks: ['sass', 'postcss']
        }
      }
    }
  };
};
