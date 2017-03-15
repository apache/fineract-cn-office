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

import javax.validation.Valid;
import java.util.List;

@SuppressWarnings("unused")
public class EmployeePage {

  @Valid
  private List<Employee> employees;
  private Integer totalPages;
  private Long totalElements;

  public EmployeePage() {
    super();
  }

  public List<Employee> getEmployees() {
    return this.employees;
  }

  public void setEmployees(final List<Employee> employees) {
    this.employees = employees;
  }

  public Integer getTotalPages() {
    return this.totalPages;
  }

  public void setTotalPages(final Integer totalPages) {
    this.totalPages = totalPages;
  }

  public Long getTotalElements() {
    return this.totalElements;
  }

  public void setTotalElements(final Long totalElements) {
    this.totalElements = totalElements;
  }
}
