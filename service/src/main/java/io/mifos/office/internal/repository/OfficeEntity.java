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
package io.mifos.office.internal.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "horus_offices")
public class OfficeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "parent_office_id")
  private Long parentOfficeId;
  @Column(name = "identifier")
  private String identifier;
  @Column(name = "a_name")
  private String name;
  @Column(name = "description")
  private String description;
  @Column(name = "created_by")
  private String createdBy;
  @Column(name = "created_on")
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdOn;
  @Column(name = "last_modified_by")
  private String lastModifiedBy;
  @Column(name = "last_modified_on")
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModifiedOn;

  public OfficeEntity() {
    super();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getParentOfficeId() {
    return parentOfficeId;
  }

  public void setParentOfficeId(Long parentOfficeId) {
    this.parentOfficeId = parentOfficeId;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
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

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public Date getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(Date createdOn) {
    this.createdOn = createdOn;
  }

  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public Date getLastModifiedOn() {
    return lastModifiedOn;
  }

  public void setLastModifiedOn(Date lastModifiedOn) {
    this.lastModifiedOn = lastModifiedOn;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final OfficeEntity that = (OfficeEntity) o;

    return identifier.equals(that.identifier);

  }

  @Override
  public int hashCode() {
    return identifier.hashCode();
  }
}
