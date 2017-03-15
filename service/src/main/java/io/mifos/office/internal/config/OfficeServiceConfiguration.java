/*
 * Copyright 2017 The Mifos Initiative
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mifos.office.internal.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mifos.core.mariadb.config.EnableMariaDB;
import io.mifos.office.ServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableMariaDB
@ComponentScan(
    basePackages = {
        "io.mifos.office.internal.command.handler",
        "io.mifos.office.internal.repository",
        "io.mifos.office.internal.service"
    }
)
@EnableJpaRepositories(
    basePackages = {
        "io.mifos.office.internal.repository"
    }
)
public class OfficeServiceConfiguration {

  public OfficeServiceConfiguration() {
    super();
  }

  @Bean(name = ServiceConstants.SERVICE_LOGGER_NAME)
  public Logger logger() {
    return LoggerFactory.getLogger(ServiceConstants.SERVICE_LOGGER_NAME);
  }

  @Bean(name = ServiceConstants.JSON_SERIALIZER_NAME)
  public Gson gson() {
    return new GsonBuilder().create();
  }
}
