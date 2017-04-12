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
package io.mifos.office.api.v1.client;

import io.mifos.core.api.annotation.ThrowsException;
import io.mifos.core.api.annotation.ThrowsExceptions;
import io.mifos.core.api.util.CustomFeignClientsConfiguration;
import io.mifos.office.api.v1.domain.Address;
import io.mifos.office.api.v1.domain.ContactDetail;
import io.mifos.office.api.v1.domain.Employee;
import io.mifos.office.api.v1.domain.EmployeePage;
import io.mifos.office.api.v1.domain.Office;
import io.mifos.office.api.v1.domain.OfficePage;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@SuppressWarnings("unused")
@FeignClient(name="office-v1", path="/office/v1", configuration=CustomFeignClientsConfiguration.class)
public interface OrganizationManager {

  @RequestMapping(
      value = "/offices",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.NOT_FOUND, exception = NotFoundException.class),
      @ThrowsException(status = HttpStatus.CONFLICT, exception = AlreadyExistsException.class)
  })
  void createOffice(@RequestBody final Office office);

  @RequestMapping(
      value = "/offices",
      method = RequestMethod.GET,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  OfficePage fetchOffices(@RequestParam(value = "term", required = false) final String term,
                          @RequestParam(value = "pageIndex", required = false) final Integer pageIndex,
                          @RequestParam(value = "size", required = false) final Integer size,
                          @RequestParam(value = "sortColumn", required = false) final String sortColumn,
                          @RequestParam(value = "sortDirection", required = false) final String sortDirection);

  @RequestMapping(
      value = "/offices/{identifier}",
      method = RequestMethod.GET,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = NotFoundException.class)
  Office findOfficeByIdentifier(@PathVariable("identifier") final String identifier);

  @RequestMapping(
      value = "/offices/{identifier}",
      method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.NOT_FOUND, exception = NotFoundException.class),
      @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = BadRequestException.class)
  })
  void updateOffice(@PathVariable("identifier") final String identifier, @RequestBody final Office office);

  @RequestMapping(
      value = "/offices/{identifier}",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
          @ThrowsException(status = HttpStatus.NOT_FOUND, exception = NotFoundException.class),
          @ThrowsException(status = HttpStatus.CONFLICT, exception = AlreadyExistsException.class)
  })
  void addBranch(@PathVariable("identifier") final String identifier, @RequestBody final Office office);

  @RequestMapping(
      value = "/offices/{identifier}/branches",
      method = RequestMethod.GET,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = NotFoundException.class)
  OfficePage getBranches(@PathVariable("identifier") final String identifier,
                         @RequestParam(value = "pageIndex", required = false) final Integer pageIndex,
                         @RequestParam(value = "size", required = false) final Integer size,
                         @RequestParam(value = "sortColumn", required = false) final String sortColumn,
                         @RequestParam(value = "sortDirection", required = false) final String sortDirection);

  @RequestMapping(
      value = "/offices/{identifier}",
      method = RequestMethod.DELETE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  @ThrowsException(status = HttpStatus.CONFLICT, exception = ChildrenExistException.class)
  void deleteOffice(@PathVariable("identifier") final String identifier);

  @RequestMapping(
      value = "/offices/{identifier}/address",
      method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = NotFoundException.class)
  void setAddressForOffice(@PathVariable("identifier") final String identifier, @RequestBody final Address address);

  @RequestMapping(
      value = "/offices/{identifier}/address",
      method = RequestMethod.GET,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  Address getAddressOfOffice(@PathVariable("identifier") final String identifier);

  @RequestMapping(
      value = "/offices/{identifier}/address",
      method = RequestMethod.DELETE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = NotFoundException.class)
  void deleteAddressOfOffice(@PathVariable("identifier") final String identifier);

  @RequestMapping(
      value = "/employees",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsException(status = HttpStatus.CONFLICT, exception = AlreadyExistsException.class)
  void createEmployee(@RequestBody final Employee employee);

  @RequestMapping(
      value = "/employees",
      method = RequestMethod.GET,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = NotFoundException.class)
  EmployeePage fetchEmployees(@RequestParam(value = "term", required = false) final String term,
                              @RequestParam(value = "office", required = false) final String officeIdentifier,
                              @RequestParam(value = "pageIndex", required = false) final Integer pageIndex,
                              @RequestParam(value = "size", required = false) final Integer size,
                              @RequestParam(value = "sortColumn", required = false) final String sortColumn,
                              @RequestParam(value = "sortDirection", required = false) final String sortDirection);

  @RequestMapping(
      value = "/employees/{useridentifier}",
      method = RequestMethod.GET,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = NotFoundException.class)
  Employee findEmployee(@PathVariable("useridentifier") final String identifier);

  @RequestMapping(
      value = "/employees/{useridentifier}",
      method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.NOT_FOUND, exception = NotFoundException.class),
      @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = BadRequestException.class)
  })
  void updateEmployee(@PathVariable("useridentifier") final String identifier, @RequestBody final Employee employee);

  @RequestMapping(
      value = "/employees/{useridentifier}",
      method = RequestMethod.DELETE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  void deleteEmployee(@PathVariable("useridentifier") final String identifier);

  @RequestMapping(
      value = "/employees/{useridentifier}/contacts",
      method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = NotFoundException.class)
  void setContactDetails(@PathVariable("useridentifier") final String identifier,
                         @RequestBody final List<ContactDetail> contactDetails);

  @RequestMapping(
      value = "/employees/{useridentifier}/contacts",
      method = RequestMethod.GET,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = NotFoundException.class)
  List<ContactDetail> fetchContactDetails(@PathVariable("useridentifier") final String identifier);

  @RequestMapping(
      value = "/employees/{useridentifier}/contacts",
      method = RequestMethod.DELETE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = NotFoundException.class)
  void deleteContactDetails(@PathVariable("useridentifier") final String identifier);
}
