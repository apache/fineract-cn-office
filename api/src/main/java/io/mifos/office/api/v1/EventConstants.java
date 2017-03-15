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
package io.mifos.office.api.v1;

@SuppressWarnings("unused")
public interface EventConstants {

  String DESTINATION = "office-v1";
  String OPERATION_HEADER = "operation";

  String INITIALIZE = "initialize";

  String OPERATION_POST_OFFICE = "post-office";
  String OPERATION_PUT_OFFICE = "put-office";
  String OPERATION_DELETE_OFFICE = "delete-office";
  String OPERATION_PUT_ADDRESS = "put-address";
  String OPERATION_DELETE_ADDRESS = "delete-address";

  String OPERATION_POST_EMPLOYEE = "post-employee";
  String OPERATION_PUT_EMPLOYEE = "put-employee";
  String OPERATION_DELETE_EMPLOYEE = "delete-employee";
  String OPERATION_PUT_CONTACT_DETAIL = "put-contact-detail";
  String OPERATION_DELETE_CONTACT_DETAIL = "delete-contact-detail";

  String SELECTOR_INITIALIZE = OPERATION_HEADER + " = '" + INITIALIZE + "'";

  String SELECTOR_POST_OFFICE = OPERATION_HEADER + " = '" + OPERATION_POST_OFFICE + "'";
  String SELECTOR_PUT_OFFICE = OPERATION_HEADER + " = '" + OPERATION_PUT_OFFICE + "'";
  String SELECTOR_DELETE_OFFICE = OPERATION_HEADER + " = '" + OPERATION_DELETE_OFFICE + "'";
  String SELECTOR_PUT_ADDRESS = OPERATION_HEADER + " = '" + OPERATION_PUT_ADDRESS + "'";
  String SELECTOR_DELETE_ADDRESS = OPERATION_HEADER + " = '" + OPERATION_DELETE_ADDRESS + "'";

  String SELECTOR_POST_EMPLOYEE = OPERATION_HEADER + " = '" + OPERATION_POST_EMPLOYEE + "'";
  String SELECTOR_PUT_EMPLOYEE = OPERATION_HEADER + " = '" + OPERATION_PUT_EMPLOYEE + "'";
  String SELECTOR_DELETE_EMPLOYEE = OPERATION_HEADER + " = '" + OPERATION_DELETE_EMPLOYEE + "'";
  String SELECTOR_PUT_CONTACT_DETAIL = OPERATION_HEADER + " = '" + OPERATION_PUT_CONTACT_DETAIL + "'";
  String SELECTOR_DELETE_CONTACT_DETAIL = OPERATION_HEADER + " = '" + OPERATION_DELETE_CONTACT_DETAIL + "'";
}
