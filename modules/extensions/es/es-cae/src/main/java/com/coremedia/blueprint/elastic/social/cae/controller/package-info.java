/**
 * <h2>Plug-in controllers</h2>
 * <p>
 * Plug-in specific controllers accept HTTP requests, bind the request parameters, and typically delegate to
 * a {@link com.coremedia.blueprint.elastic.social.blueprint.ElasticSocialService}.
 * </p>
 * <h2>Registered controller mappings</h2>
 * <table>
 * <thead>
 * <tr>
 * <td>URL pattern</td>
 * <td>Controller class</td>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>/elastic/social/comments</td>
 * <td>{@link com.coremedia.blueprint.elastic.social.blueprint.controller.CommentsController}</td>
 * </tr>
 * <tr>
 * <td>/elastic/social/complaint</td>
 * <td>{@link com.coremedia.blueprint.elastic.social.blueprint.controller.ComplaintController}</td>
 * </tr>
 * <tr>
 * <td>/elastic/social/like</td>
 * <td>{@link com.coremedia.blueprint.elastic.social.blueprint.controller.LikeController}</td>
 * </tr>
 * <tr>
 * <td>/elastic/social/ratings</td>
 * <td>{@link com.coremedia.blueprint.elastic.social.blueprint.controller.RatingsController}</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @cm.template.api
 */
package com.coremedia.blueprint.elastic.social.cae.controller;
