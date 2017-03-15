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
package io.mifos.office.internal.command.handler;

import io.mifos.core.api.util.UserContextHolder;
import io.mifos.core.command.annotation.Aggregate;
import io.mifos.core.command.annotation.CommandHandler;
import io.mifos.core.command.annotation.EventEmitter;
import io.mifos.core.lang.ServiceException;
import io.mifos.office.api.v1.EventConstants;
import io.mifos.office.api.v1.domain.ContactDetail;
import io.mifos.office.api.v1.domain.Employee;
import io.mifos.office.internal.command.CreateEmployeeCommand;
import io.mifos.office.internal.command.DeleteEmployeeCommand;
import io.mifos.office.internal.command.SetContactDetailsCommand;
import io.mifos.office.internal.command.UpdateEmployeeCommand;
import io.mifos.office.internal.mapper.ContactDetailMapper;
import io.mifos.office.internal.mapper.EmployeeMapper;
import io.mifos.office.internal.command.DeleteContactDetailCommand;
import io.mifos.office.internal.repository.ContactDetailEntity;
import io.mifos.office.internal.repository.ContactDetailRepository;
import io.mifos.office.internal.repository.EmployeeEntity;
import io.mifos.office.internal.repository.EmployeeRepository;
import io.mifos.office.internal.repository.OfficeEntity;
import io.mifos.office.internal.repository.OfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({
    "unused"
})
@Aggregate
public class EmployeeAggregate {

  private final EmployeeRepository employeeRepository;
  private final ContactDetailRepository contactDetailRepository;
  private final OfficeRepository officeRepository;

  @Autowired
  public EmployeeAggregate(final EmployeeRepository employeeRepository,
                           final ContactDetailRepository contactDetailRepository,
                           final OfficeRepository officeRepository) {
    super();
    this.employeeRepository = employeeRepository;
    this.contactDetailRepository = contactDetailRepository;
    this.officeRepository = officeRepository;
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.OPERATION_HEADER, selectorValue = EventConstants.OPERATION_POST_EMPLOYEE)
  public String createEmployee(final CreateEmployeeCommand createEmployeeCommand)
      throws ServiceException {
    final Employee employee = createEmployeeCommand.employee();

    if (this.employeeRepository.existsByIdentifier(employee.getIdentifier())) {
      throw ServiceException.conflict("Employee {0} already exists.", employee.getIdentifier());
    }

    final EmployeeEntity employeeEntity = EmployeeMapper.map(employee);

    if (employee.getAssignedOffice() != null) {
      final Optional<OfficeEntity> officeEntity = this.officeRepository.findByIdentifier(employee.getAssignedOffice());
      if (officeEntity.isPresent()) {
        employeeEntity.setAssignedOffice(officeEntity.get());
      } else {
        throw ServiceException.notFound("Assigned office {0} not found.", employee.getAssignedOffice());
      }
    }
    employeeEntity.setCreatedBy(UserContextHolder.checkedGetUser());
    employeeEntity.setCreatedOn(Utils.utcNow());
    final EmployeeEntity savedEmployeeEntity = this.employeeRepository.save(employeeEntity);

    if (employee.getContactDetails() != null) {
      this.saveContactDetail(savedEmployeeEntity, employee.getContactDetails());
    }

    return employee.getIdentifier();
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.OPERATION_HEADER, selectorValue = EventConstants.OPERATION_DELETE_EMPLOYEE)
  public String deleteEmployee(final DeleteEmployeeCommand deleteEmployeeCommand) {

    final EmployeeEntity employeeEntityToDelete = this.employeeRepository.findByIdentifier(deleteEmployeeCommand.code());
    if (employeeEntityToDelete != null) {
      this.deleteContactDetails(employeeEntityToDelete);
      this.employeeRepository.delete(employeeEntityToDelete);
    }
    return deleteEmployeeCommand.code();
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.OPERATION_HEADER, selectorValue = EventConstants.OPERATION_PUT_EMPLOYEE)
  public String updateEmployee(final UpdateEmployeeCommand updateEmployeeCommand) {
    final Employee employee = updateEmployeeCommand.employee();

    final EmployeeEntity employeeEntity = this.employeeRepository.findByIdentifier(employee.getIdentifier());

    if (employee.getGivenName() != null) {
      employeeEntity.setGivenName(employee.getGivenName());
    }

    if (employee.getMiddleName() != null) {
      employeeEntity.setMiddleName(employee.getMiddleName());
    }

    if (employee.getSurname() != null) {
      employeeEntity.setSurname(employee.getSurname());
    }

    final OfficeEntity assignedOffice = employeeEntity.getAssignedOffice();
    final String currentIdentifier = assignedOffice != null ? assignedOffice.getIdentifier() : null;

    if (!Objects.equals(employee.getAssignedOffice(), currentIdentifier)) {
      final Optional<OfficeEntity> officeEntity = this.officeRepository.findByIdentifier(employee.getAssignedOffice());
      if (officeEntity.isPresent()) {
        employeeEntity.setAssignedOffice(officeEntity.get());
      } else {
        throw ServiceException.notFound("Assigned office {0} not found.", employee.getAssignedOffice());
      }
    }

    employeeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
    employeeEntity.setLastModifiedOn(Utils.utcNow());
    this.employeeRepository.save(employeeEntity);

    return updateEmployeeCommand.employee().getIdentifier();
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.OPERATION_HEADER, selectorValue = EventConstants.OPERATION_PUT_CONTACT_DETAIL)
  public String setContactDetail(final SetContactDetailsCommand setContactDetailsCommand)
      throws ServiceException {

    final EmployeeEntity employeeEntity = this.employeeRepository.findByIdentifier(setContactDetailsCommand.identifier());
    if (employeeEntity == null) {
      throw ServiceException.notFound("Employee {0} not found.", setContactDetailsCommand.identifier());
    }

    this.deleteContactDetails(employeeEntity);

    final List<ContactDetail> contactDetails = setContactDetailsCommand.contactDetails();

    if (contactDetails != null && !contactDetails.isEmpty()) {
      this.saveContactDetail(employeeEntity, contactDetails);
    }

    employeeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
    employeeEntity.setLastModifiedOn(Utils.utcNow());
    this.employeeRepository.save(employeeEntity);

    return setContactDetailsCommand.identifier();
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.OPERATION_HEADER, selectorValue = EventConstants.OPERATION_DELETE_CONTACT_DETAIL)
  public String deleteContactDetail(final DeleteContactDetailCommand deleteContactDetailCommand)
      throws ServiceException {

    final EmployeeEntity employeeEntity = this.employeeRepository.findByIdentifier(deleteContactDetailCommand.identifier());
    if (employeeEntity == null) {
      throw ServiceException.notFound("Employee {0} not found.", deleteContactDetailCommand.identifier());
    }

    if (this.deleteContactDetails(employeeEntity)) {
      employeeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
      employeeEntity.setLastModifiedOn(Utils.utcNow());
      this.employeeRepository.save(employeeEntity);
    }

    return deleteContactDetailCommand.identifier();
  }

  private void saveContactDetail(final EmployeeEntity employeeEntity, List<ContactDetail> contactDetails) {
    if (contactDetails != null && !contactDetails.isEmpty()) {
      contactDetails.forEach(contactDetail -> {
        final ContactDetailEntity contactDetailEntity = ContactDetailMapper.map(contactDetail);
        contactDetailEntity.setEmployee(employeeEntity);
        this.contactDetailRepository.save(contactDetailEntity);
      });
    }
  }

  private boolean deleteContactDetails(final EmployeeEntity employeeEntity) {
    final List<ContactDetailEntity> contactDetailEntities = this.contactDetailRepository.findByEmployeeOrderByPreferenceLevelAsc(employeeEntity);
    if (contactDetailEntities != null && !contactDetailEntities.isEmpty()) {
      contactDetailEntities.forEach(this.contactDetailRepository::delete);
      return true;
    }
    return false;
  }
}
