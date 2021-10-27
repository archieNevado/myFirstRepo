package com.coremedia.blueprint.boot.studio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * we need to exclude the autoconfiguration for the default springSecurityFilterChain to enable ours
 */
@SpringBootApplication(exclude = {
        FreeMarkerAutoConfiguration.class,
        MongoAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        //Used to disable editorial comments feature.
        //EditorialCommentsAutoConfiguration.class, //part of module com.coremedia.cms:editorial-comments-rest
        //DataSourceAutoConfiguration.class,
}, excludeName = {
        "net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration",
        "net.devh.boot.grpc.client.autoconfigure.GrpcClientHealthAutoConfiguration",
        "net.devh.boot.grpc.client.autoconfigure.GrpcClientMetricAutoConfiguration",
})
@EnableScheduling
@EnableWebSecurity
public class StudioServerApp {

  // ... Bean definitions
  public static void main(String[] args) {
    SpringApplication.run(StudioServerApp.class, args);
  }

  @Bean
  public static FilterRegistrationBean<AvailabilityFilter> availabilityFilter(ApplicationAvailability applicationAvailability) {
    var filterRegistrationBean = new FilterRegistrationBean<>(new AvailabilityFilter(applicationAvailability));
    filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return filterRegistrationBean;
  }

  /**
   * Reject all requests until spring boot considers the application live and ready
   */
  static class AvailabilityFilter implements Filter {

    private final ApplicationAvailability applicationAvailability;

    AvailabilityFilter(ApplicationAvailability applicationAvailability) {
      this.applicationAvailability = applicationAvailability;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
      if (ReadinessState.ACCEPTING_TRAFFIC.equals(applicationAvailability.getReadinessState())
              && LivenessState.CORRECT.equals(applicationAvailability.getLivenessState())) {
        filterChain.doFilter(servletRequest, servletResponse);
      } else if (servletResponse instanceof HttpServletResponse) {
        ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Application not ready or broken.");
      } else {
        throw new IllegalStateException("Unable to serve request.");
      }
    }
  }
}
