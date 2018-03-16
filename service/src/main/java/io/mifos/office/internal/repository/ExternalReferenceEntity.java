/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.mifos.office.internal.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "horus_external_references")
public class ExternalReferenceEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "office_identifier", nullable = false, length = 32)
  private String officeIdentifier;
  @Column(name = "a_type", nullable = false, length = 32)
  private String type;
  @Column(name = "a_state", nullable = false, length = 256)
  private String state;

  public ExternalReferenceEntity() {
    super();
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getOfficeIdentifier() {
    return this.officeIdentifier;
  }

  public void setOfficeIdentifier(final String officeIdentifier) {
    this.officeIdentifier = officeIdentifier;
  }

  public String getType() {
    return this.type;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public String getState() {
    return this.state;
  }

  public void setState(final String state) {
    this.state = state;
  }
}
