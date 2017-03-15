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
package io.mifos.office.internal.mapper;

import io.mifos.office.api.v1.domain.Employee;
import io.mifos.office.internal.repository.EmployeeEntity;

public class EmployeeMapper {

  private EmployeeMapper() {
    super();
  }

  public static EmployeeEntity map(final Employee employee) {
    final EmployeeEntity employeeEntity = new EmployeeEntity();
    employeeEntity.setIdentifier(employee.getIdentifier());
    employeeEntity.setGivenName(employee.getGivenName());
    employeeEntity.setMiddleName(employee.getMiddleName());
    employeeEntity.setSurname(employee.getSurname());
    return employeeEntity;
  }

  public static Employee map(final EmployeeEntity employeeEntity) {
    final Employee employee = new Employee();
    employee.setIdentifier(employeeEntity.getIdentifier());
    employee.setGivenName(employeeEntity.getGivenName());
    employee.setMiddleName(employeeEntity.getMiddleName());
    employee.setSurname(employeeEntity.getSurname());
    if (employeeEntity.getAssignedOffice() != null)
      employee.setAssignedOffice(employeeEntity.getAssignedOffice().getIdentifier());
    return employee;
  }
}
