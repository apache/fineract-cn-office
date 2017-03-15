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
package io.mifos.office.listener;

import com.google.gson.Gson;
import io.mifos.core.command.util.CommandConstants;
import io.mifos.core.lang.config.TenantHeaderFilter;
import io.mifos.core.test.listener.EventRecorder;
import io.mifos.office.api.v1.EventConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class OfficeEventListener {

  private final Gson gson;
  private final EventRecorder eventRecorder;

  @Autowired
  public OfficeEventListener(@Qualifier(CommandConstants.SERIALIZER) final Gson gson,
                             final EventRecorder eventRecorder) {
    super();
    this.gson = gson;
    this.eventRecorder = eventRecorder;
  }

  @JmsListener(
      subscription = EventConstants.DESTINATION,
      destination = EventConstants.DESTINATION,
      selector = EventConstants.SELECTOR_POST_OFFICE
  )
  public void onCreateOffice(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                             final String payload)
      throws Exception {
    this.eventRecorder.event(tenant, EventConstants.OPERATION_POST_OFFICE, payload, String.class);
  }

  @JmsListener(
      subscription = EventConstants.DESTINATION,
      destination = EventConstants.DESTINATION,
      selector = EventConstants.SELECTOR_DELETE_OFFICE
  )
  public void onDeleteOffice(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                             final String payload)
      throws Exception {
    this.eventRecorder.event(tenant, EventConstants.OPERATION_DELETE_OFFICE, payload, String.class);
  }

  @JmsListener(
      subscription = EventConstants.DESTINATION,
      destination = EventConstants.DESTINATION,
      selector = EventConstants.SELECTOR_PUT_OFFICE
  )
  public void onUpdateOffice(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                             final String payload)
      throws Exception {
    this.eventRecorder.event(tenant, EventConstants.OPERATION_PUT_OFFICE, payload, String.class);
  }

  @JmsListener(
      subscription = EventConstants.DESTINATION,
      destination = EventConstants.DESTINATION,
      selector = EventConstants.SELECTOR_PUT_ADDRESS
  )
  public void onSetAddress(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                           final String payload)
      throws Exception {
    this.eventRecorder.event(tenant, EventConstants.OPERATION_PUT_ADDRESS, payload, String.class);
  }

  @JmsListener(
      subscription = EventConstants.DESTINATION,
      destination = EventConstants.DESTINATION,
      selector = EventConstants.SELECTOR_DELETE_ADDRESS
  )
  public void onDeleteAddress(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                              final String payload)
      throws Exception {
    this.eventRecorder.event(tenant, EventConstants.OPERATION_DELETE_ADDRESS, payload, String.class);
  }
}
