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

import io.mifos.office.api.v1.domain.Address;
import io.mifos.office.internal.repository.AddressEntity;

public final class AddressMapper {

  private AddressMapper() {
    super();
  }

  public static AddressEntity map(final Address address) {
    final AddressEntity addressEntity = new AddressEntity();
    addressEntity.setStreet(address.getStreet());
    addressEntity.setCity(address.getCity());
    addressEntity.setRegion(address.getRegion());
    addressEntity.setPostalCode(address.getPostalCode());
    addressEntity.setCountry(address.getCountry());
    addressEntity.setCountryCode(address.getCountryCode());
    return addressEntity;
  }

  public static Address map(final AddressEntity addressEntity) {
    final Address address = new Address();
    address.setStreet(addressEntity.getStreet());
    address.setCity(addressEntity.getCity());
    address.setRegion(addressEntity.getRegion());
    address.setPostalCode(addressEntity.getPostalCode());
    address.setCountry(addressEntity.getCountry());
    address.setCountryCode(addressEntity.getCountryCode());
    return address;
  }
}
