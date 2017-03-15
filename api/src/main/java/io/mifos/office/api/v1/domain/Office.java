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

@SuppressWarnings("unused")
public class Office {

  @ValidIdentifier
  private String identifier;
  private String parentIdentifier;
  private String name;
  private String description;
  private Address address;

  public Office() {
    super();
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getParentIdentifier() {
    return this.parentIdentifier;
  }

  public void setParentIdentifier(final String parentIdentifier) {
    this.parentIdentifier = parentIdentifier;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }
}
