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
import io.mifos.office.api.v1.domain.Address;
import io.mifos.office.api.v1.domain.Office;
import io.mifos.office.api.v1.domain.OfficePage;
import io.mifos.office.internal.mapper.AddressMapper;
import io.mifos.office.internal.mapper.OfficeMapper;
import io.mifos.office.internal.repository.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OfficeService {

  private final Logger logger;
  private final OfficeRepository officeRepository;
  private final AddressRepository addressRepository;
  private final EmployeeRepository employeeRepository;

  @Autowired
  public OfficeService(@Qualifier(ServiceConstants.SERVICE_LOGGER_NAME) final Logger logger,
                       final OfficeRepository officeRepository,
                       final AddressRepository addressRepository,
                       final EmployeeRepository employeeRepository) {
    super();
    this.logger = logger;
    this.officeRepository = officeRepository;
    this.addressRepository = addressRepository;
    this.employeeRepository = employeeRepository;
  }

  public boolean officeExists(final String identifier) {
    return this.officeRepository.existsByIdentifier(identifier);
  }

  public boolean branchExists(final String identifier) {
    final Optional<OfficeEntity> officeEntityOptional = this.officeRepository.findByIdentifier(identifier);
    return officeEntityOptional.map(officeEntity -> this.officeRepository.existsByParentOfficeId(officeEntity.getId())).orElse(false);
  }

  public boolean hasEmployees(final String officeIdentifier){
    return this.officeRepository.findByIdentifier(officeIdentifier)
            .map(this.employeeRepository::existsByAssignedOffice)
            .orElse(false);
  }

  @Transactional(readOnly = true)
  public OfficePage fetchOffices(final String term, final Pageable pageRequest) {
    final Page<OfficeEntity> officeEntityPage;
    if (term != null) {
      officeEntityPage = this.officeRepository.findByIdentifierContainingOrNameContaining(term, term, pageRequest);
    } else {
      officeEntityPage = this.officeRepository.findByParentOfficeIdIsNull(pageRequest);
    }

    final OfficePage officePage = new OfficePage();
    officePage.setTotalPages(officeEntityPage.getTotalPages());
    officePage.setTotalElements(officeEntityPage.getTotalElements());
    officePage.setOffices(this.extractOfficeEntities(officeEntityPage, null));

    return officePage;
  }

  public Optional<Office> findOfficeByIdentifier(final String identifier) {
    final Optional<OfficeEntity> officeEntityOptional = this.officeRepository.findByIdentifier(identifier);

    if (officeEntityOptional.isPresent()) {
      final Optional<Office> officeOptional = officeEntityOptional.map(OfficeMapper::map);

      officeOptional.ifPresent(office -> {
        final Long parentOfficeId = officeEntityOptional.get().getParentOfficeId();
        if(parentOfficeId != null) {
          final OfficeEntity parentEntity = this.officeRepository.getOne(parentOfficeId);
          office.setParentIdentifier(parentEntity.getIdentifier());
        }

        final Optional<AddressEntity> addressEntityOptional = this.addressRepository.findByOffice(officeEntityOptional.get());
        addressEntityOptional.ifPresent(addressEntity -> office.setAddress(AddressMapper.map(addressEntity)));
      });

      return officeOptional;
    }
    return Optional.empty();
  }

  public Optional<Address> findAddressOfOffice(final String identifier) {
    final Optional<OfficeEntity> officeEntityOptional = this.officeRepository.findByIdentifier(identifier);

    if (!officeEntityOptional.isPresent()) {
      throw ServiceException.notFound("Office {0} not found.", identifier);
    }

    final Optional<AddressEntity> addressEntityOptional = this.addressRepository.findByOffice(officeEntityOptional.get());

    return addressEntityOptional.map(AddressMapper::map);
  }

  @Transactional(readOnly = true)
  public OfficePage fetchBranches(final String parentIdentifier, final Pageable pageRequest) {
    final OfficeEntity parentOfficeEntity = this.officeRepository.findByIdentifier(parentIdentifier)
        .orElseThrow(() -> ServiceException.notFound("Parent office {0} not found!", parentIdentifier));

    final Page<OfficeEntity> officeEntityPage = this.officeRepository.findByParentOfficeId(parentOfficeEntity.getId(), pageRequest);
    final OfficePage officePage = new OfficePage();
    officePage.setTotalPages(officeEntityPage.getTotalPages());
    officePage.setTotalElements(officeEntityPage.getTotalElements());
    officePage.setOffices(this.extractOfficeEntities(officeEntityPage, parentIdentifier));

    return officePage;
  }

  public List<Office> extractOfficeEntities(final Page<OfficeEntity> officeEntityPage, final String parentIdentifier) {
    final List<Office> offices = new ArrayList<>(officeEntityPage.getSize());
    officeEntityPage.forEach(officeEntity -> {
      final Office office = OfficeMapper.map(officeEntity);
      if (parentIdentifier != null) {
        office.setParentIdentifier(parentIdentifier);
      }
      offices.add(office);

      final Optional<AddressEntity> addressEntityOptional = this.addressRepository.findByOffice(officeEntity);
      addressEntityOptional.ifPresent(addressEntity -> office.setAddress(AddressMapper.map(addressEntity)));
    });
    return offices;
  }
}
