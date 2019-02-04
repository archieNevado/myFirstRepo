package com.coremedia.blueprint.elastic.social.cae.springsocial;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;

import static com.coremedia.blueprint.elastic.social.cae.springsocial.Requests.getServletRequest;
import static org.apache.commons.lang3.StringUtils.isBlank;

@RequestMapping("/signup")
public class SignUpController {

  @GetMapping
  public RedirectView signUp(NativeWebRequest request) {
    HttpSession session = getServletRequest(request).getSession();

    String registerUrl = (String) session.getAttribute("registerUrl");

    return new RedirectView(isBlank(registerUrl) ? request.getContextPath() : registerUrl);
  }
}
