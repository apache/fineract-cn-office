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
package io.mifos.office.internal.service;

import io.mifos.core.lang.ServiceException;
import io.mifos.office.ServiceConstants;
import io.mifos.office.api.v1.domain.ContactDetail;
import io.mifos.office.api.v1.domain.Employee;
import io.mifos.office.api.v1.domain.EmployeePage;
import io.mifos.office.internal.mapper.ContactDetailMapper;
import io.mifos.office.internal.mapper.EmployeeMapper;
import io.mifos.office.internal.repository.ContactDetailEntity;
import io.mifos.office.internal.repository.ContactDetailRepository;
import io.mifos.office.internal.repository.EmployeeEntity;
import io.mifos.office.internal.repository.EmployeeRepository;
import io.mifos.office.internal.repository.OfficeEntity;
import io.mifos.office.internal.repository.OfficeRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

  private final Logger logger;
  private final EmployeeRepository employeeRepository;
  private final ContactDetailRepository contactDetailRepository;
  private final OfficeRepository officeRepository;

  @Autowired
  public EmployeeService(@Qualifier(ServiceConstants.REST_LOGGER_NAME) final Logger logger,
                         final EmployeeRepository employeeRepository,
                         final ContactDetailRepository contactDetailRepository,
                         final OfficeRepository officeRepository) {
    super();
    this.logger = logger;
    this.employeeRepository = employeeRepository;
    this.contactDetailRepository = contactDetailRepository;
    this.officeRepository = officeRepository;
  }

  public Boolean employeeExists(final String code) {
    return this.employeeRepository.existsByIdentifier(code);
  }

  public Optional<Employee> findByCode(final String code) {
    final EmployeeEntity employeeEntity = this.employeeRepository.findByIdentifier(code);
    if (employeeEntity != null) {
      final Employee employee = EmployeeMapper.map(employeeEntity);
      employee.setContactDetails(this.findContactDetailsByEmployee(employeeEntity.getIdentifier()));
      return Optional.of(employee);
    } else {
      return Optional.empty();
    }
  }

  public EmployeePage findEmployees(final String term, final String officeIdentifier, final Pageable pageRequest) {

    final Page<EmployeeEntity> employeeEntityPage;
    if (term != null) {
      employeeEntityPage = this.employeeRepository.findByIdentifierContaining(term, pageRequest);
    } else if (officeIdentifier != null) {
      final OfficeEntity officeEntity = this.officeRepository.findByIdentifier(officeIdentifier)
          .orElseThrow(() -> ServiceException.notFound("Office {0} not found.", officeIdentifier));
      employeeEntityPage = this.employeeRepository.findByAssignedOffice(officeEntity, pageRequest);
    } else {
      employeeEntityPage = this.employeeRepository.findAll(pageRequest);
    }

    final EmployeePage employeePage = new EmployeePage();
    employeePage.setTotalPages(employeeEntityPage.getTotalPages());
    employeePage.setTotalElements(employeeEntityPage.getTotalElements());

    final List<Employee> employees = new ArrayList<>();
    employeePage.setEmployees(employees);
    employeeEntityPage.forEach(employeeEntity -> {
      final Employee employee = EmployeeMapper.map(employeeEntity);
      employees.add(employee);

      employee.setContactDetails(this.findContactDetailsByEmployee(employeeEntity.getIdentifier()));

    });

    return employeePage;
  }


  public List<ContactDetail> findContactDetailsByEmployee(final String identifier) {
    final EmployeeEntity employeeEntity = this.employeeRepository.findByIdentifier(identifier);
    if (employeeEntity == null) {
      throw ServiceException.notFound("Employee {0} not found.", identifier);
    }

    final List<ContactDetailEntity> contactDetailEntities = this.contactDetailRepository.findByEmployeeOrderByPreferenceLevelAsc(employeeEntity);
    if (contactDetailEntities != null && !contactDetailEntities.isEmpty()) {
      return contactDetailEntities
          .stream()
          .map(ContactDetailMapper::map)
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }
}
