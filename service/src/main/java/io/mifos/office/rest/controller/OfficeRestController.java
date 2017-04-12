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
package io.mifos.office.rest.controller;

import io.mifos.anubis.annotation.AcceptedTokenType;
import io.mifos.anubis.annotation.Permittable;
import io.mifos.core.command.gateway.CommandGateway;
import io.mifos.core.lang.ServiceException;
import io.mifos.office.api.v1.PermittableGroupIds;
import io.mifos.office.api.v1.domain.*;
import io.mifos.office.internal.command.DeleteAddressOfOfficeCommand;
import io.mifos.office.internal.service.EmployeeService;
import io.mifos.office.internal.service.OfficeService;
import io.mifos.office.internal.command.AddBranchCommand;
import io.mifos.office.internal.command.CreateEmployeeCommand;
import io.mifos.office.internal.command.CreateOfficeCommand;
import io.mifos.office.internal.command.DeleteContactDetailCommand;
import io.mifos.office.internal.command.DeleteEmployeeCommand;
import io.mifos.office.internal.command.DeleteOfficeCommand;
import io.mifos.office.internal.command.InitializeServiceCommand;
import io.mifos.office.internal.command.SetAddressForOfficeCommand;
import io.mifos.office.internal.command.SetContactDetailsCommand;
import io.mifos.office.internal.command.UpdateEmployeeCommand;
import io.mifos.office.internal.command.UpdateOfficeCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class OfficeRestController {

  private final CommandGateway commandGateway;
  private final OfficeService officeService;
  private final EmployeeService employeeService;

  @Autowired
  public OfficeRestController(final CommandGateway commandGateway,
                              final OfficeService officeService,
                              final EmployeeService employeeService) {
    super();
    this.commandGateway = commandGateway;
    this.officeService = officeService;
    this.employeeService = employeeService;
  }

  @Permittable(value = AcceptedTokenType.SYSTEM)
  @RequestMapping(
      value = "/initialize",
      method = RequestMethod.POST,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> initialize() {
    this.commandGateway.process(new InitializeServiceCommand());
    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.OFFICE_MANAGEMENT)
  @RequestMapping(
      value = "/offices",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> createOffice(@RequestBody @Valid final Office office) throws InterruptedException {
    if (office == null) {
      throw ServiceException.badRequest("An office must be given.");
    }

    if (this.officeService.officeExists(office.getIdentifier())) {
      throw ServiceException.conflict("Office {0} already exists.", office.getIdentifier());
    }
    this.commandGateway.process(new CreateOfficeCommand(office));
    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.OFFICE_MANAGEMENT)
  @RequestMapping(
      value = "/offices",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<OfficePage> fetchOffices(@RequestParam(value = "term", required = false) final String term,
                                          @RequestParam(value = "pageIndex", required = false) final Integer pageIndex,
                                          @RequestParam(value = "size", required = false) final Integer size,
                                          @RequestParam(value = "sortColumn", required = false) final String sortColumn,
                                          @RequestParam(value = "sortDirection", required = false) final String sortDirection) {
    return ResponseEntity.ok(this.officeService.fetchOffices(term, this.createPageRequest(pageIndex, size, sortColumn, sortDirection)));
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.OFFICE_MANAGEMENT)
  @RequestMapping(
      value = "/offices/{identifier}",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Office> findOffice(@PathVariable("identifier") final String identifier) {
    final Optional<Office> office = this.officeService.findOfficeByIdentifier(identifier);
    if (office.isPresent()) {
      return ResponseEntity.ok(office.get());
    } else {
      throw ServiceException.notFound("Office with identifier {0} not found.", identifier);
    }
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.OFFICE_MANAGEMENT)
  @RequestMapping(
      value = "/offices/{identifier}",
      method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> updateOffice(@PathVariable("identifier") final String identifier,
                                    @RequestBody @Valid final Office office) throws InterruptedException {
    if (!this.officeService.officeExists(identifier)) {
      throw ServiceException.notFound("Office {0} not found.", identifier);
    }

    if (office.getIdentifier() != null && !identifier.equals(office.getIdentifier())) {
      throw ServiceException.badRequest("Office identifier must match resource identifier");
    }

    this.commandGateway.process(new UpdateOfficeCommand(office));
    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.OFFICE_MANAGEMENT)
  @RequestMapping(
      value = "/offices/{identifier}",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> addBranch(@PathVariable("identifier") final String identifier,
                                 @RequestBody @Valid final Office office) {
    if (!this.officeService.officeExists(identifier)) {
      throw ServiceException.notFound("Parent office {0} not found.", identifier);
    }

    if (office == null) {
      throw ServiceException.badRequest("An office must be given.");
    }

    if (this.officeService.officeExists(office.getIdentifier())) {
      throw ServiceException.conflict("Office {0} already exists.", office.getIdentifier());
    }

    this.commandGateway.process(new AddBranchCommand(identifier, office));
    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.OFFICE_MANAGEMENT)
  @RequestMapping(
      value = "/offices/{identifier}/branches",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<OfficePage> getBranches(@PathVariable("identifier") final String identifier,
                                         @RequestParam(value = "pageIndex", required = false) final Integer pageIndex,
                                         @RequestParam(value = "size", required = false) final Integer size,
                                         @RequestParam(value = "sortColumn", required = false) final String sortColumn,
                                         @RequestParam(value = "sortDirection", required = false) final String sortDirection) {
    if (!this.officeService.officeExists(identifier)) {
      throw ServiceException.notFound("Parent office {0} not found.", identifier);
    }
    return ResponseEntity.ok(this.officeService.fetchBranches(identifier, this.createPageRequest(pageIndex, size, sortColumn, sortDirection)));
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.OFFICE_MANAGEMENT)
  @RequestMapping(
      value = "/offices/{identifier}",
      method = RequestMethod.DELETE,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> deleteOffice(@PathVariable("identifier") final String identifier)
      throws InterruptedException {
    if (!this.officeService.officeExists(identifier)) {
      throw ServiceException.notFound("Office {0} not found.", identifier);
    }

    if (this.officeService.branchExists(identifier)) {
      throw ServiceException.conflict("Office {0} has children.", identifier);
    }

    if(this.officeService.hasEmployees(identifier)){
      throw ServiceException.conflict("Office {0} has employees.", identifier);
    }

    this.commandGateway.process(new DeleteOfficeCommand(identifier));

    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.OFFICE_MANAGEMENT)
  @RequestMapping(
      value = "/offices/{identifier}/address",
      method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> setAddressForOffice(@PathVariable("identifier") final String identifier,
                                           @RequestBody @Valid final Address address) {
    if (!this.officeService.officeExists(identifier)) {
      throw ServiceException.notFound("Office {0} not found.", identifier);
    }

    this.commandGateway.process(new SetAddressForOfficeCommand(identifier, address));
    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.OFFICE_MANAGEMENT)
  @RequestMapping(
      value = "/offices/{identifier}/address",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity getAddressOfOffice(@PathVariable("identifier") final String identifier) {
    final Optional<Address> addressOfOffice = this.officeService.findAddressOfOffice(identifier);
    if (addressOfOffice.isPresent()) {
      return ResponseEntity.ok(addressOfOffice.get());
    } else {
      return ResponseEntity.noContent().build();
    }
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.OFFICE_MANAGEMENT)
  @RequestMapping(
      value = "/offices/{identifier}/address",
      method = RequestMethod.DELETE,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> deleteAddressOfOffice(@PathVariable("identifier") final String identifier) {
    if (!this.officeService.officeExists(identifier)) {
      throw ServiceException.notFound("Parent office {0} not found.", identifier);
    }

    this.commandGateway.process(new DeleteAddressOfOfficeCommand(identifier));
    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.EMPLOYEE_MANAGEMENT)
  @RequestMapping(
      value = "/employees",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> createEmployee(@RequestBody @Valid final Employee employee) throws InterruptedException {
    if (employee.getAssignedOffice() != null && !this.officeService.officeExists(employee.getAssignedOffice())) {
      throw ServiceException.notFound("Office {0} to assign not found.", employee.getAssignedOffice());
    }

    if (this.employeeService.employeeExists(employee.getIdentifier())) {
      throw ServiceException.conflict("Employee {0} already exists.", employee.getIdentifier());
    }

    this.commandGateway.process(new CreateEmployeeCommand(employee));
    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.EMPLOYEE_MANAGEMENT)
  @RequestMapping(
      value = "/employees",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<EmployeePage> fetchEmployees(@RequestParam(value = "term", required = false) final String term,
                                              @RequestParam(value = "office", required = false) final String officeIdentifier,
                                              @RequestParam(value = "pageIndex", required = false) final Integer pageIndex,
                                              @RequestParam(value = "size", required = false) final Integer size,
                                              @RequestParam(value = "sortColumn", required = false) final String sortColumn,
                                              @RequestParam(value = "sortDirection", required = false) final String sortDirection) {
    if (officeIdentifier != null && !this.officeService.officeExists(officeIdentifier)) {
      throw ServiceException.notFound("Office {0} not found.", officeIdentifier);
    }
    return ResponseEntity.ok(this.employeeService.findEmployees(term, officeIdentifier, this.createPageRequest(pageIndex, size, sortColumn, sortDirection)));
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.EMPLOYEE_MANAGEMENT)
  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.SELF_MANAGEMENT, permittedEndpoint = "/employees/{useridentifier}")
  @RequestMapping(
      value = "/employees/{useridentifier}",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Employee> findEmployee(@PathVariable("useridentifier") final String identifier) {
    final Optional<Employee> employee = this.employeeService.findByCode(identifier);
    if (employee.isPresent()) {
      return ResponseEntity.ok(employee.get());
    } else {
      throw ServiceException.notFound("Employee with identifier {0} not found.", identifier);
    }
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.EMPLOYEE_MANAGEMENT)
  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.SELF_MANAGEMENT, permittedEndpoint = "/employees/{useridentifier}")
  @RequestMapping(
      value = "/employees/{useridentifier}",
      method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> updateEmployee(@PathVariable("useridentifier") final String identifier,
                                      @RequestBody @Valid final Employee employee) {
    if (!this.employeeService.employeeExists(identifier)) {
      throw ServiceException.notFound("Employee {0} not found.", identifier);
    }

    if (employee.getIdentifier() != null && !identifier.equals(employee.getIdentifier())) {
      throw ServiceException.badRequest("Employee code must match resource identifier");
    }

    if (employee.getAssignedOffice() != null && !this.officeService.officeExists(employee.getAssignedOffice())) {
      throw ServiceException.notFound("Office {0} to assign not found.", employee.getAssignedOffice());
    }

    this.commandGateway.process(new UpdateEmployeeCommand(employee));

    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.EMPLOYEE_MANAGEMENT)
  @RequestMapping(
      value = "/employees/{useridentifier}",
      method = RequestMethod.DELETE,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> deleteEmployee(@PathVariable("useridentifier") final String identifier) {
    if (this.employeeService.employeeExists(identifier)) {
      this.commandGateway.process(new DeleteEmployeeCommand(identifier));
    }
    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.EMPLOYEE_MANAGEMENT)
  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.SELF_MANAGEMENT, permittedEndpoint = "/employees/{useridentifier}/contacts")
  @RequestMapping(
      value = "/employees/{useridentifier}/contacts",
      method = RequestMethod.PUT,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> setContactDetails(@PathVariable("useridentifier") final String identifier,
                                         @RequestBody @Valid final List<ContactDetail> contactDetails) {
    if (!this.employeeService.employeeExists(identifier)) {
      throw ServiceException.notFound("Employee {0} not found.", identifier);
    }

    this.commandGateway.process(new SetContactDetailsCommand(identifier, contactDetails));

    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.EMPLOYEE_MANAGEMENT)
  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.SELF_MANAGEMENT, permittedEndpoint = "/employees/{useridentifier}/contacts")
  @RequestMapping(
      value = "/employees/{useridentifier}/contacts",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<List<ContactDetail>> fetchContactDetails(@PathVariable("useridentifier") final String identifier) {
    if (!this.employeeService.employeeExists(identifier)) {
      throw ServiceException.notFound("Employee {0} not found.", identifier);
    }
    return ResponseEntity.ok(this.employeeService.findContactDetailsByEmployee(identifier));
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.EMPLOYEE_MANAGEMENT)
  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.SELF_MANAGEMENT, permittedEndpoint = "/employees/{useridentifier}/contacts")
  @RequestMapping(
      value = "/employees/{useridentifier}/contacts",
      method = RequestMethod.DELETE,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> deleteContactDetails(@PathVariable("useridentifier") final String identifier) {
    if (!this.employeeService.employeeExists(identifier)) {
      throw ServiceException.notFound("Employee {0} not found.", identifier);
    }

    this.commandGateway.process(new DeleteContactDetailCommand(identifier));

    return ResponseEntity.accepted().build();
  }

  private Pageable createPageRequest(final Integer pageIndex, final Integer size, final String sortColumn, final String sortDirection) {
    final Integer pageIndexToUse = pageIndex != null ? pageIndex : 0;
    final Integer sizeToUse = size != null ? size : 20;
    final String sortColumnToUse = sortColumn != null ? sortColumn : "identifier";
    final Sort.Direction direction = sortDirection != null ? Sort.Direction.valueOf(sortDirection.toUpperCase()) : Sort.Direction.ASC;
    return new PageRequest(pageIndexToUse, sizeToUse, direction, sortColumnToUse);
  }
}
