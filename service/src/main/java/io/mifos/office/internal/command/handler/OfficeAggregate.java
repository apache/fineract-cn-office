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
import io.mifos.office.ServiceConstants;
import io.mifos.office.api.v1.EventConstants;
import io.mifos.office.api.v1.domain.Office;
import io.mifos.office.internal.command.DeleteAddressOfOfficeCommand;
import io.mifos.office.internal.mapper.AddressMapper;
import io.mifos.office.internal.mapper.OfficeMapper;
import io.mifos.office.internal.repository.AddressEntity;
import io.mifos.office.internal.repository.AddressRepository;
import io.mifos.office.internal.repository.OfficeEntity;
import io.mifos.office.internal.repository.OfficeRepository;
import io.mifos.office.internal.command.AddBranchCommand;
import io.mifos.office.internal.command.CreateOfficeCommand;
import io.mifos.office.internal.command.DeleteOfficeCommand;
import io.mifos.office.internal.command.SetAddressForOfficeCommand;
import io.mifos.office.internal.command.UpdateOfficeCommand;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@SuppressWarnings({
    "unused"
})
@Aggregate
public class OfficeAggregate {

  private final Logger logger;
  private final OfficeRepository officeRepository;
  private final AddressRepository addressRepository;

  @Autowired
  public OfficeAggregate(@Qualifier(ServiceConstants.SERVICE_LOGGER_NAME) final Logger logger,
                         final OfficeRepository officeRepository,
                         final AddressRepository addressRepository) {
    super();
    this.logger = logger;
    this.officeRepository = officeRepository;
    this.addressRepository = addressRepository;
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.OPERATION_HEADER, selectorValue = EventConstants.OPERATION_POST_OFFICE)
  public String createOffice(final CreateOfficeCommand createOfficeCommand) throws ServiceException {
    this.createOffice(createOfficeCommand.office(), null);
    return createOfficeCommand.office().getIdentifier();
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.OPERATION_HEADER, selectorValue = EventConstants.OPERATION_PUT_OFFICE)
  public String updateOffice(final UpdateOfficeCommand updateOfficeCommand) throws ServiceException {
    final Office office = updateOfficeCommand.office();

    final Optional<OfficeEntity> optionalOfficeEntity = this.officeRepository.findByIdentifier(office.getIdentifier());

    if (optionalOfficeEntity.isPresent()) {
      final OfficeEntity officeEntity = optionalOfficeEntity.get();
      if (office.getName() != null) {
        officeEntity.setName(office.getName());
      }

      if (office.getDescription() != null) {
        officeEntity.setDescription(office.getDescription());
      }

      officeEntity.setCreatedBy(UserContextHolder.checkedGetUser());
      officeEntity.setCreatedOn(Utils.utcNow());

      this.officeRepository.save(officeEntity);

      if (office.getAddress() != null) {
        this.setAddress(new SetAddressForOfficeCommand(office.getIdentifier(), office.getAddress()));
      }
      return office.getIdentifier();
    } else {
      throw ServiceException.notFound("Office {0} not found.", office.getIdentifier());
    }
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.OPERATION_HEADER, selectorValue = EventConstants.OPERATION_POST_OFFICE)
  public String addBranch(final AddBranchCommand addBranchCommand) {
    final Office parentOffice = new Office();
    parentOffice.setIdentifier(addBranchCommand.parentIdentifier());

    final Office branch = addBranchCommand.branch();

    this.createOffice(branch, parentOffice);

    return branch.getIdentifier();
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.OPERATION_HEADER, selectorValue = EventConstants.OPERATION_DELETE_OFFICE)
  public String deleteOffice(final DeleteOfficeCommand deleteOfficeCommand) {
    final Optional<OfficeEntity> optionalOfficeEntity = this.officeRepository.findByIdentifier(deleteOfficeCommand.identifier());

    if (optionalOfficeEntity.isPresent()) {
      final OfficeEntity officeEntityToDelete = optionalOfficeEntity.get();
      final Optional<AddressEntity> optionalAddressEntity = this.addressRepository.findByOffice(officeEntityToDelete);
      if (optionalAddressEntity.isPresent()) {
        this.addressRepository.delete(optionalAddressEntity.get());
      }

      this.officeRepository.delete(officeEntityToDelete);
    }

    return deleteOfficeCommand.identifier();
  }

  @SuppressWarnings("WeakerAccess")
  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.OPERATION_HEADER, selectorValue = EventConstants.OPERATION_PUT_ADDRESS)
  public String setAddress(final SetAddressForOfficeCommand setAddressForOfficeCommand) {
    final Optional<OfficeEntity> optionalOfficeEntity = this.officeRepository.findByIdentifier(setAddressForOfficeCommand.identifier());

    if (optionalOfficeEntity.isPresent()) {
      final OfficeEntity officeEntity = optionalOfficeEntity.get();
      final Optional<AddressEntity> optionalAddressEntity = this.addressRepository.findByOffice(officeEntity);
      if (optionalAddressEntity.isPresent()) {
        this.addressRepository.delete(optionalAddressEntity.get());
      }

      final AddressEntity addressEntity = AddressMapper.map(setAddressForOfficeCommand.address());
      addressEntity.setOffice(officeEntity);
      this.addressRepository.save(addressEntity);

      officeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
      officeEntity.setLastModifiedOn(Utils.utcNow());
      this.officeRepository.save(officeEntity);

      return setAddressForOfficeCommand.identifier();
    } else {
      throw ServiceException.notFound("Office {0} not found.", setAddressForOfficeCommand.identifier());
    }
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.OPERATION_HEADER, selectorValue = EventConstants.OPERATION_DELETE_ADDRESS)
  public String deleteAddress(final DeleteAddressOfOfficeCommand deleteAddressOfOfficeCommand) {
    final Optional<OfficeEntity> optionalOfficeEntity = this.officeRepository.findByIdentifier(deleteAddressOfOfficeCommand.identifier());
    if (optionalOfficeEntity.isPresent()) {
      final OfficeEntity officeEntity = optionalOfficeEntity.get();
      if (officeEntity != null) {
        final Optional<AddressEntity> optionalAddressEntity = this.addressRepository.findByOffice(officeEntity);
        if (optionalAddressEntity.isPresent()) {
          this.addressRepository.delete(optionalAddressEntity.get());

          officeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
          officeEntity.setLastModifiedOn(Utils.utcNow());
          this.officeRepository.save(officeEntity);
          return deleteAddressOfOfficeCommand.identifier();
        }
      } else {
        this.logger.info("Office {} not found.", deleteAddressOfOfficeCommand.identifier());
      }
    }
    return null;
  }

  private void createOffice(final Office office, final Office parentOffice) {
    if (this.officeRepository.existsByIdentifier(office.getIdentifier())) {
      this.logger.info("Office {} already exists.", office.getIdentifier());
      throw ServiceException.conflict("Office {0} already exists.", office.getIdentifier());
    }

    final String modificationUser = UserContextHolder.checkedGetUser();
    final Date modificationDate = Utils.utcNow();

    final OfficeEntity officeEntity = OfficeMapper.map(office);
    if (parentOffice != null) {
      final Optional<OfficeEntity> optionalParentOfficeEntity = this.officeRepository.findByIdentifier(parentOffice.getIdentifier());
      if (optionalParentOfficeEntity.isPresent()) {
        final OfficeEntity parentOfficeEntity = optionalParentOfficeEntity.get();
        officeEntity.setParentOfficeId(parentOfficeEntity.getId());
        parentOfficeEntity.setLastModifiedBy(modificationUser);
        parentOfficeEntity.setLastModifiedOn(modificationDate);
        this.officeRepository.save(parentOfficeEntity);
      }
    }

    officeEntity.setCreatedBy(modificationUser);
    officeEntity.setCreatedOn(modificationDate);

    final OfficeEntity savedOfficeEntity = this.officeRepository.save(officeEntity);

    if (office.getAddress() != null) {
      final AddressEntity addressEntity = AddressMapper.map(office.getAddress());
      addressEntity.setOffice(savedOfficeEntity);
      this.addressRepository.save(addressEntity);
    }
  }
}
