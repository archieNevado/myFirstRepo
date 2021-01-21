package com.coremedia.blueprint.es.studio.controlroom.rest;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackages = {"com.coremedia.workflow.archive"})
public class ESControlRoomStudioConfiguration {}
