package com.coremedia.blueprint.caas.p13n;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(P13nConfig.class)
public class P13nAutoConfiguration {
}
