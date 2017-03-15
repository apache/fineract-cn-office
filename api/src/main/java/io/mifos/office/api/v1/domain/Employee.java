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
package io.mifos.office.api.v1.domain;

import io.mifos.core.lang.validation.constraints.ValidIdentifier;

import javax.validation.Valid;
import java.util.List;

@SuppressWarnings("unused")
public class Employee {

  @ValidIdentifier
  private String identifier;
  private String givenName;
  private String middleName;
  private String surname;
  private String assignedOffice;
  @Valid
  private List<ContactDetail> contactDetails;

  public Employee() {
    super();
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getAssignedOffice() {
    return assignedOffice;
  }

  public void setAssignedOffice(String assignedOffice) {
    this.assignedOffice = assignedOffice;
  }

  public List<ContactDetail> getContactDetails() {
    return contactDetails;
  }

  public void setContactDetails(List<ContactDetail> contactDetails) {
    this.contactDetails = contactDetails;
  }
}
