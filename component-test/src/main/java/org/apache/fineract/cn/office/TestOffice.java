/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.cn.office;

import org.apache.fineract.cn.office.api.v1.EventConstants;
import org.apache.fineract.cn.office.api.v1.client.AlreadyExistsException;
import org.apache.fineract.cn.office.api.v1.client.BadRequestException;
import org.apache.fineract.cn.office.api.v1.client.ChildrenExistException;
import org.apache.fineract.cn.office.api.v1.client.NotFoundException;
import org.apache.fineract.cn.office.api.v1.domain.Address;
import org.apache.fineract.cn.office.api.v1.domain.Employee;
import org.apache.fineract.cn.office.api.v1.domain.ExternalReference;
import org.apache.fineract.cn.office.api.v1.domain.Office;
import org.apache.fineract.cn.office.api.v1.domain.OfficePage;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.fineract.cn.office.util.AddressFactory;
import org.apache.fineract.cn.office.util.EmployeeFactory;
import org.apache.fineract.cn.office.util.OfficeFactory;
import org.junit.Assert;
import org.junit.Test;

public class TestOffice extends AbstractOfficeTest {

  @Test
  public void shouldCreateOffice() throws Exception {
    final Office office = OfficeFactory.createRandomOffice();
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    final Office savedOffice = this.organizationManager.findOfficeByIdentifier(office.getIdentifier());
    Assert.assertNotNull(savedOffice);
    Assert.assertEquals(office.getIdentifier(), savedOffice.getIdentifier());
    Assert.assertEquals(office.getName(), savedOffice.getName());
    Assert.assertEquals(office.getDescription(), savedOffice.getDescription());

    this.organizationManager.deleteOffice(office.getIdentifier());
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_OFFICE, office.getIdentifier());
  }

  @Test
  public void shouldNotCreateOfficeDuplicate() throws Exception {
    final Office office = OfficeFactory.createRandomOffice();
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());
    try {
      this.organizationManager.createOffice(office);
      Assert.fail();
    } catch (final AlreadyExistsException ex) {
      // do nothing, expected
    }

    this.organizationManager.deleteOffice(office.getIdentifier());
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_OFFICE, office.getIdentifier());
  }

  @Test
  public void shouldUpdateOffice() throws Exception {
    final Office office = OfficeFactory.createRandomOffice();
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    final String modifiedOfficeName = RandomStringUtils.randomAlphanumeric(32);
    office.setName(modifiedOfficeName);

    this.organizationManager.updateOffice(office.getIdentifier(), office);
    this.eventRecorder.wait(EventConstants.OPERATION_PUT_OFFICE, office.getIdentifier());

    final Office changedOffice = this.organizationManager.findOfficeByIdentifier(office.getIdentifier());
    Assert.assertEquals(modifiedOfficeName, changedOffice.getName());

    this.organizationManager.deleteOffice(office.getIdentifier());
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_OFFICE, office.getIdentifier());
  }

  @Test
  public void shouldNotUpdateOfficeIdentifierMismatch() throws Exception {
    final Office office = OfficeFactory.createRandomOffice();
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    final String originalIdentifier = office.getIdentifier();
    office.setIdentifier(RandomStringUtils.randomAlphanumeric(32));

    try {
      this.organizationManager.updateOffice(originalIdentifier, office);
      Assert.fail();
    } catch (final BadRequestException ex) {
      // do nothing, expected
    }
    this.organizationManager.deleteOffice(originalIdentifier);
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_OFFICE, office.getIdentifier());
  }

  @Test
  public void shouldAddBranch() throws Exception {
    final Office office = OfficeFactory.createRandomOffice();
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    final Office branch = OfficeFactory.createRandomOffice();
    this.organizationManager.addBranch(office.getIdentifier(), branch);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, branch.getIdentifier());

    final OfficePage officePage = this.organizationManager.getBranches(office.getIdentifier(), 0, 10, null, null);
    Assert.assertEquals(Long.valueOf(1L), officePage.getTotalElements());

    final Office savedBranch = officePage.getOffices().get(0);
    Assert.assertEquals(branch.getIdentifier(), savedBranch.getIdentifier());

    this.organizationManager.deleteOffice(branch.getIdentifier());
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_OFFICE, branch.getIdentifier());
    this.organizationManager.deleteOffice(office.getIdentifier());
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_OFFICE, office.getIdentifier());
  }

  @Test
  public void shouldNotAddBranchParentNotFound() throws Exception {
    try {
      final Office branch = OfficeFactory.createRandomOffice();
      this.organizationManager.addBranch(RandomStringUtils.randomAlphanumeric(32), branch);
      Assert.fail();
    } catch (final NotFoundException ex) {
      // do nothing, expected
    }
  }

  @Test
  public void shouldNotAddBranchDuplicate() throws Exception {
    final Office office = OfficeFactory.createRandomOffice();
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());
    try {
      this.organizationManager.addBranch(office.getIdentifier(), office);
      Assert.fail();
    } catch (final AlreadyExistsException ex) {
      // do nothing, expected
    }

    this.organizationManager.deleteOffice(office.getIdentifier());
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_OFFICE, office.getIdentifier());
  }

  @Test
  public void shouldSetAddressOfOffice() throws Exception {
    final Office office = OfficeFactory.createRandomOffice();
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    final Address address = AddressFactory.createRandomAddress();
    this.organizationManager.setAddressForOffice(office.getIdentifier(), address);
    this.eventRecorder.wait(EventConstants.OPERATION_PUT_ADDRESS, office.getIdentifier());

    final Address savedAddress = this.organizationManager.getAddressOfOffice(office.getIdentifier());
    Assert.assertNotNull(savedAddress);
    Assert.assertEquals(address.getStreet(), savedAddress.getStreet());
    Assert.assertEquals(address.getCity(), savedAddress.getCity());
    Assert.assertEquals(address.getRegion(), savedAddress.getRegion());
    Assert.assertEquals(address.getPostalCode(), savedAddress.getPostalCode());
    Assert.assertEquals(address.getCountryCode(), savedAddress.getCountryCode());
    Assert.assertEquals(address.getCountry(), savedAddress.getCountry());

    this.organizationManager.deleteOffice(office.getIdentifier());
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_OFFICE, office.getIdentifier());
  }

  @Test
  public void shouldGetAddressOfOffice() throws Exception {
    final Office office = OfficeFactory.createRandomOffice();
    final Address address = AddressFactory.createRandomAddress();
    office.setAddress(address);
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    final Address savedAddress = this.organizationManager.getAddressOfOffice(office.getIdentifier());
    Assert.assertNotNull(savedAddress);
    Assert.assertEquals(address.getStreet(), savedAddress.getStreet());
    Assert.assertEquals(address.getCity(), savedAddress.getCity());
    Assert.assertEquals(address.getRegion(), savedAddress.getRegion());
    Assert.assertEquals(address.getPostalCode(), savedAddress.getPostalCode());
    Assert.assertEquals(address.getCountryCode(), savedAddress.getCountryCode());
    Assert.assertEquals(address.getCountry(), savedAddress.getCountry());

    this.organizationManager.deleteOffice(office.getIdentifier());
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_OFFICE, office.getIdentifier());
  }

  @Test
  public void shouldDeleteAddressOfOffice() throws Exception {
    final Office office = OfficeFactory.createRandomOffice();
    final Address address = AddressFactory.createRandomAddress();
    office.setAddress(address);
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    final Address savedAddress = this.organizationManager.getAddressOfOffice(office.getIdentifier());
    Assert.assertNotNull(savedAddress);

    this.organizationManager.deleteAddressOfOffice(office.getIdentifier());
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_ADDRESS, office.getIdentifier());

    Assert.assertNull(this.organizationManager.getAddressOfOffice(office.getIdentifier()));

    this.organizationManager.deleteOffice(office.getIdentifier());
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_OFFICE, office.getIdentifier());
  }

  @Test
  public void shouldReturnParentOfBranch() throws Exception {
    final Office parent = OfficeFactory.createRandomOffice();
    this.organizationManager.createOffice(parent);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, parent.getIdentifier());

    final Office branch = OfficeFactory.createRandomOffice();
    this.organizationManager.addBranch(parent.getIdentifier(), branch);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, branch.getIdentifier());

    final Office savedBranch = this.organizationManager.findOfficeByIdentifier(branch.getIdentifier());

    Assert.assertEquals(parent.getIdentifier(), savedBranch.getParentIdentifier());
  }

  @Test(expected = NotFoundException.class)
  public void shouldDeleteOffice() throws Exception{
    final Office office = OfficeFactory.createRandomOffice();
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    this.organizationManager.deleteOffice(office.getIdentifier());

    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_OFFICE, office.getIdentifier());

    this.organizationManager.findOfficeByIdentifier(office.getIdentifier());
  }

  @Test(expected = ChildrenExistException.class)
  public void shouldNotDeleteOfficeWithBranches() throws Exception {
    final Office parent = OfficeFactory.createRandomOffice();
    this.organizationManager.createOffice(parent);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, parent.getIdentifier());

    final Office branch = OfficeFactory.createRandomOffice();
    this.organizationManager.addBranch(parent.getIdentifier(), branch);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, branch.getIdentifier());

    final Office fetchedParent = this.organizationManager.findOfficeByIdentifier(parent.getIdentifier());
    Assert.assertTrue(fetchedParent.getExternalReferences());

    this.organizationManager.deleteOffice(parent.getIdentifier());
  }

  @Test(expected = ChildrenExistException.class)
  public void shouldNotDeleteOfficeWithEmployees() throws Exception {
    final Office office = OfficeFactory.createRandomOffice();
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    final Employee employee = EmployeeFactory.createRandomEmployee();
    employee.setAssignedOffice(office.getIdentifier());
    this.organizationManager.createEmployee(employee);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, employee.getIdentifier());

    final Office fetchedOffice = this.organizationManager.findOfficeByIdentifier(office.getIdentifier());
    Assert.assertTrue(fetchedOffice.getExternalReferences());

    this.organizationManager.deleteOffice(office.getIdentifier());
  }

  @Test(expected = ChildrenExistException.class)
  public void shouldNotDeleteOfficeWithActiveExternalReference() throws Exception {
    final Office randomOffice = OfficeFactory.createRandomOffice();
    this.organizationManager.createOffice(randomOffice);
    Assert.assertTrue(this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, randomOffice.getIdentifier()));

    final ExternalReference externalReference = new ExternalReference();
    externalReference.setType("anytype");
    externalReference.setState(ExternalReference.State.ACTIVE.name());

    this.organizationManager.addExternalReference(randomOffice.getIdentifier(), externalReference);
    Assert.assertTrue(this.eventRecorder.wait(EventConstants.OPERATION_PUT_REFERENCE, randomOffice.getIdentifier()));

    this.organizationManager.deleteOffice(randomOffice.getIdentifier());
  }

  @Test
  public void shouldDeleteOfficeWithInactiveExternalReference() throws Exception {
    final Office randomOffice = OfficeFactory.createRandomOffice();
    this.organizationManager.createOffice(randomOffice);
    Assert.assertTrue(this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, randomOffice.getIdentifier()));

    final ExternalReference externalReference = new ExternalReference();
    externalReference.setType("anytype");
    externalReference.setState(ExternalReference.State.INACTIVE.name());

    this.organizationManager.addExternalReference(randomOffice.getIdentifier(), externalReference);
    Assert.assertTrue(this.eventRecorder.wait(EventConstants.OPERATION_PUT_REFERENCE, randomOffice.getIdentifier()));

    final Office office = this.organizationManager.findOfficeByIdentifier(randomOffice.getIdentifier());
    Assert.assertFalse(office.getExternalReferences());

    this.organizationManager.deleteOffice(randomOffice.getIdentifier());
    Assert.assertTrue(this.eventRecorder.wait(EventConstants.OPERATION_DELETE_OFFICE, randomOffice.getIdentifier()));
  }

  @Test
  public void shouldIndicateOfficeHasExternalReferences() throws Exception {
    final Office randomOffice = OfficeFactory.createRandomOffice();
    this.organizationManager.createOffice(randomOffice);
    Assert.assertTrue(this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, randomOffice.getIdentifier()));

    final ExternalReference externalReference = new ExternalReference();
    externalReference.setType("anytype");
    externalReference.setState(ExternalReference.State.ACTIVE.name());

    this.organizationManager.addExternalReference(randomOffice.getIdentifier(), externalReference);
    Assert.assertTrue(this.eventRecorder.wait(EventConstants.OPERATION_PUT_REFERENCE, randomOffice.getIdentifier()));

    final Office office = this.organizationManager.findOfficeByIdentifier(randomOffice.getIdentifier());
    Assert.assertTrue(office.getExternalReferences());
  }
}
