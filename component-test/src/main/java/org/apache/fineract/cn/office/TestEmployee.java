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
import org.apache.fineract.cn.office.api.v1.client.NotFoundException;
import org.apache.fineract.cn.office.api.v1.domain.ContactDetail;
import org.apache.fineract.cn.office.api.v1.domain.Employee;
import org.apache.fineract.cn.office.api.v1.domain.EmployeePage;
import org.apache.fineract.cn.office.api.v1.domain.Office;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.fineract.cn.office.util.EmployeeFactory;
import org.apache.fineract.cn.office.util.OfficeFactory;
import org.junit.Assert;
import org.junit.Test;

public class TestEmployee extends AbstractOfficeTest {

  @Test
  public void shouldCreateEmployee() throws Exception {
    final Employee employee = EmployeeFactory.createRandomEmployee();
    {
      super.organizationManager.createEmployee(employee);
      final boolean found = this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, employee.getIdentifier());
      Assert.assertTrue(found);
    }

    final Employee savedEmployee = this.organizationManager.findEmployee(employee.getIdentifier());

    Assert.assertNotNull(savedEmployee);
    Assert.assertEquals(employee.getIdentifier(), savedEmployee.getIdentifier());
    Assert.assertEquals(employee.getGivenName(), savedEmployee.getGivenName());
    Assert.assertEquals(employee.getMiddleName(), savedEmployee.getMiddleName());
    Assert.assertEquals(employee.getSurname(), savedEmployee.getSurname());

    this.organizationManager.deleteEmployee(employee.getIdentifier());
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_EMPLOYEE, employee.getIdentifier());
  }

  @Test
  public void shouldNotCreateEmployeeAlreadyExists() throws Exception {
    final Employee employee = EmployeeFactory.createRandomEmployee();
    this.organizationManager.createEmployee(employee);

    this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, employee.getIdentifier());

    try {
      this.organizationManager.createEmployee(employee);
      Assert.fail();
    } catch (final AlreadyExistsException ex) {
      // do nothing, expected
    }

    this.organizationManager.deleteEmployee(employee.getIdentifier());
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_EMPLOYEE, employee.getIdentifier());
  }

  @Test
  public void shouldFindEmployeesByOffice() throws Exception {
    final Office office = OfficeFactory.createRandomOffice();
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    final Employee employee = EmployeeFactory.createRandomEmployee();
    employee.setAssignedOffice(office.getIdentifier());
    this.organizationManager.createEmployee(employee);

    this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, employee.getIdentifier());

    final EmployeePage employeePage = this.organizationManager.fetchEmployees(null, office.getIdentifier(), 0, 20, null, null);
    Assert.assertEquals(Long.valueOf(1L), employeePage.getTotalElements());
    final Employee savedEmployee = employeePage.getEmployees().get(0);
    Assert.assertEquals(employee.getIdentifier(), savedEmployee.getIdentifier());
    Assert.assertEquals(employee.getGivenName(), savedEmployee.getGivenName());
    Assert.assertEquals(employee.getMiddleName(), savedEmployee.getMiddleName());
    Assert.assertEquals(employee.getSurname(), savedEmployee.getSurname());

    this.organizationManager.deleteEmployee(employee.getIdentifier());
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_EMPLOYEE, employee.getIdentifier());
    this.organizationManager.deleteOffice(office.getIdentifier());
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_OFFICE, office.getIdentifier());
  }

  @Test
  public void shouldFindAllEmployees() throws Exception {
    final Employee firstEmployee = EmployeeFactory.createRandomEmployee();
    this.organizationManager.createEmployee(firstEmployee);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, firstEmployee.getIdentifier());
    final Employee secondEmployee = EmployeeFactory.createRandomEmployee();
    this.organizationManager.createEmployee(secondEmployee);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, secondEmployee.getIdentifier());

    final EmployeePage employeePage = this.organizationManager.fetchEmployees(null, null, 0, 20, null, null);
    Assert.assertEquals(Long.valueOf(2L), employeePage.getTotalElements());

    this.organizationManager.deleteEmployee(firstEmployee.getIdentifier());
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_EMPLOYEE, firstEmployee.getIdentifier());
    this.organizationManager.deleteEmployee(secondEmployee.getIdentifier());
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_EMPLOYEE, secondEmployee.getIdentifier());
  }

  @Test
  public void shouldDeleteEmployee() throws Exception {
    final Employee employee = EmployeeFactory.createRandomEmployee();
    this.organizationManager.createEmployee(employee);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, employee.getIdentifier());
    Assert.assertNotNull(this.organizationManager.findEmployee(employee.getIdentifier()));

    {
      this.organizationManager.deleteEmployee(employee.getIdentifier());
      final boolean found = this.eventRecorder.wait(EventConstants.OPERATION_DELETE_EMPLOYEE, employee.getIdentifier());
      Assert.assertTrue(found);
    }

    try {
      this.organizationManager.findEmployee(employee.getIdentifier());
      Assert.fail();
    } catch (final NotFoundException ex) {
      // do nothing, expected
    }
  }

  @Test
  public void shouldUpdateEmployee() throws Exception {
    final Employee employee = EmployeeFactory.createRandomEmployee();
    this.organizationManager.createEmployee(employee);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, employee.getIdentifier());

    final Office office = OfficeFactory.createRandomOffice();
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    final Employee updatedEmployee = EmployeeFactory.createRandomEmployee();
    updatedEmployee.setIdentifier(employee.getIdentifier());
    updatedEmployee.setAssignedOffice(office.getIdentifier());

    {
      this.organizationManager.updateEmployee(employee.getIdentifier(), updatedEmployee);
      final boolean found = this.eventRecorder.wait(EventConstants.OPERATION_PUT_EMPLOYEE, employee.getIdentifier());
      Assert.assertTrue(found);
    }

    final Employee savedEmployee = this.organizationManager.findEmployee(employee.getIdentifier());
    Assert.assertNotNull(savedEmployee);
    Assert.assertEquals(updatedEmployee.getIdentifier(), savedEmployee.getIdentifier());
    Assert.assertEquals(updatedEmployee.getGivenName(), savedEmployee.getGivenName());
    Assert.assertEquals(updatedEmployee.getMiddleName(), savedEmployee.getMiddleName());
    Assert.assertEquals(updatedEmployee.getSurname(), savedEmployee.getSurname());
    Assert.assertEquals(updatedEmployee.getAssignedOffice(), savedEmployee.getAssignedOffice());

    {
      this.organizationManager.deleteEmployee(employee.getIdentifier());
      final boolean found = this.eventRecorder.wait(EventConstants.OPERATION_DELETE_EMPLOYEE, employee.getIdentifier());
      Assert.assertTrue(found);
    }
  }

  @Test
  public void shouldNotUpdateEmployeeCodeMismatch() throws Exception {
    final Employee employee = EmployeeFactory.createRandomEmployee();
    this.organizationManager.createEmployee(employee);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, employee.getIdentifier());

    final String originalCode = employee.getIdentifier();
    employee.setIdentifier(RandomStringUtils.randomAlphanumeric(8));

    try {
      this.organizationManager.updateEmployee(originalCode, employee);
      Assert.fail();
    } catch (final BadRequestException ex) {
      // do nothing, expected
    }

    this.organizationManager.deleteEmployee(originalCode);
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_EMPLOYEE, originalCode);
  }

  @Test
  public void shouldNotUpdateEmployeeNotFound() throws Exception {
    final Employee employee = EmployeeFactory.createRandomEmployee();
    this.organizationManager.createEmployee(employee);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, employee.getIdentifier());

    try {
      this.organizationManager.updateEmployee(RandomStringUtils.randomAlphanumeric(8), employee);
      Assert.fail();
    } catch (final NotFoundException ex) {
      // do nothing, expected
    }

    this.organizationManager.deleteEmployee(employee.getIdentifier());
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_EMPLOYEE, employee.getIdentifier());
  }

  @Test
  public void shouldSetContactDetailOfEmployee() throws Exception {
    final Employee employee = EmployeeFactory.createRandomEmployee();
    this.organizationManager.createEmployee(employee);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, employee.getIdentifier());

    final ContactDetail email = new ContactDetail();
    email.setType(ContactDetail.Type.EMAIL.name());
    email.setGroup(ContactDetail.Group.PRIVATE.name());
    email.setValue("test@example.org");
    email.setPreferenceLevel(1);

    final ContactDetail phone = new ContactDetail();
    phone.setType(ContactDetail.Type.PHONE.name());
    phone.setGroup(ContactDetail.Group.PRIVATE.name());
    phone.setValue("123456789");
    phone.setPreferenceLevel(2);

    {
      this.organizationManager.setContactDetails(employee.getIdentifier(), Arrays.asList(email, phone));
      final boolean found = this.eventRecorder.wait(EventConstants.OPERATION_PUT_CONTACT_DETAIL, employee.getIdentifier());
      Assert.assertTrue(found);
    }

    final List<ContactDetail> savedContactDetails = this.organizationManager.fetchContactDetails(employee.getIdentifier());
    Assert.assertNotNull(savedContactDetails);
    Assert.assertEquals(2, savedContactDetails.size());

    final ContactDetail savedEmail = savedContactDetails.get(0);
    Assert.assertEquals(email.getType(), savedEmail.getType());
    Assert.assertEquals(email.getGroup(), savedEmail.getGroup());
    Assert.assertEquals(email.getValue(), savedEmail.getValue());
    Assert.assertEquals(email.getPreferenceLevel(), savedEmail.getPreferenceLevel());

    final ContactDetail savedPhone = savedContactDetails.get(1);
    Assert.assertEquals(phone.getType(), savedPhone.getType());
    Assert.assertEquals(phone.getGroup(), savedPhone.getGroup());
    Assert.assertEquals(phone.getValue(), savedPhone.getValue());
    Assert.assertEquals(phone.getPreferenceLevel(), savedPhone.getPreferenceLevel());

    {
      this.organizationManager.deleteEmployee(employee.getIdentifier());
      final boolean found = this.eventRecorder.wait(EventConstants.OPERATION_DELETE_EMPLOYEE, employee.getIdentifier());
      Assert.assertTrue(found);
    }
  }

  @Test
  public void shouldNotSetContactDetailEmployeeNotFound() throws Exception {
    final ContactDetail contactDetail = new ContactDetail();
    contactDetail.setType(ContactDetail.Type.EMAIL.name());
    contactDetail.setGroup(ContactDetail.Group.BUSINESS.name());
    contactDetail.setValue("employee@example.com");

    try {
      this.organizationManager.setContactDetails(RandomStringUtils.randomAlphanumeric(8), Collections.singletonList(contactDetail));
      Assert.fail();
    } catch (final NotFoundException ex) {
      // do nothing, expected
    }
  }

  @Test
  public void shouldDeleteContactDetailOfEmployee() throws Exception {
    final Employee employee = EmployeeFactory.createRandomEmployee();
    this.organizationManager.createEmployee(employee);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, employee.getIdentifier());

    final ContactDetail email = new ContactDetail();
    email.setType(ContactDetail.Type.EMAIL.name());
    email.setGroup(ContactDetail.Group.PRIVATE.name());
    email.setValue("test@example.org");
    email.setPreferenceLevel(1);

    final ContactDetail phone = new ContactDetail();
    phone.setType(ContactDetail.Type.PHONE.name());
    phone.setGroup(ContactDetail.Group.PRIVATE.name());
    phone.setValue("123456789");
    phone.setPreferenceLevel(2);

    {
      this.organizationManager.setContactDetails(employee.getIdentifier(), Arrays.asList(email, phone));
      final boolean found = this.eventRecorder.wait(EventConstants.OPERATION_PUT_CONTACT_DETAIL, employee.getIdentifier());
      Assert.assertTrue(found);
    }

    Assert.assertNotNull(this.organizationManager.fetchContactDetails(employee.getIdentifier()));

    {
      this.organizationManager.deleteContactDetails(employee.getIdentifier());
      final boolean found = this.eventRecorder.wait(EventConstants.OPERATION_DELETE_CONTACT_DETAIL, employee.getIdentifier());
      Assert.assertTrue(found);
    }

    Assert.assertTrue(this.organizationManager.fetchContactDetails(employee.getIdentifier()).isEmpty());

    this.organizationManager.deleteEmployee(employee.getIdentifier());
    this.eventRecorder.wait(EventConstants.OPERATION_DELETE_EMPLOYEE, employee.getIdentifier());
  }
}
