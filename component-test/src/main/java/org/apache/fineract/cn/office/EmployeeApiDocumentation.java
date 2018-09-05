package org.apache.fineract.cn.office;

import com.google.gson.Gson;
import org.apache.fineract.cn.office.api.v1.EventConstants;
import org.apache.fineract.cn.office.api.v1.domain.*;
import org.apache.fineract.cn.office.util.EmployeeFactory;
import org.apache.fineract.cn.office.util.OfficeFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;

public class EmployeeApiDocumentation extends TestEmployee {

  @Rule
  public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/doc/generated-snippets/test-employee");

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @Before
  public void setUp ( ) {

    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
            .apply(documentationConfiguration(this.restDocumentation))
            .alwaysDo(document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
            .build();
  }

  @Test
  public void documentCreateEmployee ( ) throws Exception {

    final Employee employee = EmployeeFactory.createRandomEmployee();

    final Office office = OfficeFactory.createRandomOffice();
    office.setIdentifier("Accra");

    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    ContactDetail firstContact = new ContactDetail();
    firstContact.setType(ContactDetail.Type.MOBILE.name());
    firstContact.setGroup(ContactDetail.Group.PRIVATE.name());
    firstContact.setValue("677889900");
    firstContact.setPreferenceLevel(new Integer(1));

    List <ContactDetail> contacts = new ArrayList <>();
    contacts.add(firstContact);

    employee.setIdentifier("employ0010");
    employee.setGivenName("Chale");
    employee.setMiddleName("Asamoah");
    employee.setSurname("Yamoah");
    employee.setAssignedOffice("Accra");
    employee.setContactDetails(contacts);

    Gson gson = new Gson();
    this.mockMvc.perform(post("/employees")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(gson.toJson(employee)))
            .andExpect(status().isAccepted())
            .andDo(document("document-create-employee", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    requestFields(
                            fieldWithPath("identifier").description("Employee's identifier"),
                            fieldWithPath("givenName").description(" Employee given name"),
                            fieldWithPath("middleName").description("Employee's middle name"),
                            fieldWithPath("surname").description("Employee's surname"),
                            fieldWithPath("assignedOffice").description("Employee's assigned office"),
                            fieldWithPath("contactDetails").type("List<ContactDetail>").description("Employee's contact details"))));
  }

  @Test
  public void documentFindEmployee ( ) throws Exception {

    final Office office = OfficeFactory.createRandomOffice();
    office.setIdentifier("myOffice");
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    ContactDetail firstContact = new ContactDetail();
    firstContact.setType(ContactDetail.Type.MOBILE.name());
    firstContact.setGroup(ContactDetail.Group.PRIVATE.name());
    firstContact.setValue("699009900");
    firstContact.setPreferenceLevel(new Integer(1));

    List <ContactDetail> contactsOne = new ArrayList <>();
    contactsOne.add(firstContact);

    final Employee employee = EmployeeFactory.createRandomEmployee();
    employee.setIdentifier("emNo1");
    employee.setGivenName("Ojong");
    employee.setMiddleName("Cho");
    employee.setSurname("Tah");
    employee.setAssignedOffice(office.getIdentifier());
    employee.setContactDetails(contactsOne);
    this.organizationManager.createEmployee(employee);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, employee.getIdentifier());

    this.mockMvc.perform(get("/employees/" + employee.getIdentifier())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isOk())
            .andDo(document("document-find-employee", preprocessResponse(prettyPrint()),
                    responseFields(
                            fieldWithPath("identifier").description("first employee's identifier"),
                            fieldWithPath("givenName").description(" first employee given name"),
                            fieldWithPath("middleName").description("first employee's middle name"),
                            fieldWithPath("surname").description("first employee's surname"),
                            fieldWithPath("assignedOffice").description("first employee's assigned office"),
                            fieldWithPath("contactDetails").type("List<ContactDetail>").description("first employee's contact details"))));
  }

  @Test
  public void documentFindAllEmployees ( ) throws Exception {

    final Office office = OfficeFactory.createRandomOffice();
    office.setIdentifier("office001");
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    ContactDetail firstContact = new ContactDetail();
    firstContact.setType(ContactDetail.Type.MOBILE.name());
    firstContact.setGroup(ContactDetail.Group.PRIVATE.name());
    firstContact.setValue("677889900");
    firstContact.setPreferenceLevel(new Integer(1));

    List <ContactDetail> contactsOne = new ArrayList <>();
    contactsOne.add(firstContact);

    final Employee firstEmployee = EmployeeFactory.createRandomEmployee();
    firstEmployee.setIdentifier("employ001");
    firstEmployee.setGivenName("Oru");
    firstEmployee.setMiddleName("Asam");
    firstEmployee.setSurname("Yoah");
    firstEmployee.setAssignedOffice(office.getIdentifier());
    firstEmployee.setContactDetails(contactsOne);
    this.organizationManager.createEmployee(firstEmployee);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, firstEmployee.getIdentifier());


    ContactDetail secondContact = new ContactDetail();
    secondContact.setType(ContactDetail.Type.MOBILE.name());
    secondContact.setGroup(ContactDetail.Group.PRIVATE.name());
    secondContact.setValue("675859565");
    secondContact.setPreferenceLevel(new Integer(2));

    List <ContactDetail> contactsTwo = new ArrayList <>();
    contactsTwo.add(secondContact);

    final Employee secondEmployee = EmployeeFactory.createRandomEmployee();
    secondEmployee.setIdentifier("employ002");
    secondEmployee.setGivenName("Oyadipo");
    secondEmployee.setMiddleName("Okah");
    secondEmployee.setSurname("Omo");
    secondEmployee.setAssignedOffice(office.getIdentifier());
    secondEmployee.setContactDetails(contactsTwo);
    this.organizationManager.createEmployee(secondEmployee);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, secondEmployee.getIdentifier());

    this.mockMvc.perform(get("/employees")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isOk())
            .andDo(document("document-find-all-employees", preprocessResponse(prettyPrint()),
                    responseFields(
                            fieldWithPath("employees").description("Page of employees"),
                            fieldWithPath("employees[].identifier").description("first employee's identifier"),
                            fieldWithPath("employees[].givenName").description(" first employee given name"),
                            fieldWithPath("employees[].middleName").description("first employee's middle name"),
                            fieldWithPath("employees[].surname").description("first employee's surname"),
                            fieldWithPath("employees[].assignedOffice").description("first employee's assigned office"),
                            fieldWithPath("employees[].contactDetails").type("List<ContactDetail>").description("first employee's contact details"),
                            fieldWithPath("employees[1].identifier").description("second employee's identifier"),
                            fieldWithPath("employees[1].givenName").description(" second employee given name"),
                            fieldWithPath("employees[1].middleName").description("second employee's middle name"),
                            fieldWithPath("employees[1].surname").description("second employee's surname"),
                            fieldWithPath("employees[1].assignedOffice").description("second employee's assigned office"),
                            fieldWithPath("employees[1].contactDetails").type("List<ContactDetail>").description("second employee's contact details"),
                            fieldWithPath("totalPages").type("Integer").description("Page of Employees"),
                            fieldWithPath("totalElements").type("Integer").description("Page of Employees"))));
  }

  @Test
  public void documentUpdateEmployee ( ) throws Exception {

    final Office office = OfficeFactory.createRandomOffice();
    office.setIdentifier("myOfficeOne");
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    ContactDetail orangeContact = new ContactDetail();
    orangeContact.setType(ContactDetail.Type.MOBILE.name());
    orangeContact.setGroup(ContactDetail.Group.PRIVATE.name());
    orangeContact.setValue("699009900");
    orangeContact.setPreferenceLevel(new Integer(1));

    List <ContactDetail> contacts = new ArrayList <>();
    contacts.add(orangeContact);

    final Employee employed = EmployeeFactory.createRandomEmployee();
    employed.setIdentifier("emNo10");
    employed.setGivenName("Ojong");
    employed.setMiddleName("Cho");
    employed.setSurname("Tah");
    employed.setAssignedOffice(office.getIdentifier());
    employed.setContactDetails(contacts);
    this.organizationManager.createEmployee(employed);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, employed.getIdentifier());

    ContactDetail updatedContact = new ContactDetail();
    updatedContact.setType(ContactDetail.Type.PHONE.name());
    updatedContact.setGroup(ContactDetail.Group.BUSINESS.name());
    updatedContact.setValue("677557755");
    updatedContact.setPreferenceLevel(new Integer(1));

    Assert.assertTrue(contacts.size() == 1);
    contacts.remove(orangeContact);
    Assert.assertTrue(contacts.size() == 0);
    contacts.add(updatedContact);
    Assert.assertTrue(contacts.size() == 1);
    employed.setContactDetails(contacts);
    Assert.assertTrue(employed.getContactDetails().size() == contacts.size());

    Gson gson = new Gson();
    this.mockMvc.perform(put("/employees/" + employed.getIdentifier())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(gson.toJson(employed))
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isAccepted())
            .andDo(document("document-update-employee", preprocessResponse(prettyPrint()),
                    requestFields(
                            fieldWithPath("identifier").description("first employee's identifier"),
                            fieldWithPath("givenName").description(" first employee given name"),
                            fieldWithPath("middleName").description("first employee's middle name"),
                            fieldWithPath("surname").description("first employee's surname"),
                            fieldWithPath("assignedOffice").description("first employee's assigned office"),
                            fieldWithPath("contactDetails").type("List<ContactDetail>").description("first employee's contact details"))));
  }

  @Test
  public void documentDeleteEmployee ( ) throws Exception {

    final Office office = OfficeFactory.createRandomOffice();
    office.setIdentifier("myOfficeTwo");
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    ContactDetail orangeContact = new ContactDetail();
    orangeContact.setType(ContactDetail.Type.MOBILE.name());
    orangeContact.setGroup(ContactDetail.Group.PRIVATE.name());
    orangeContact.setValue("699669966");
    orangeContact.setPreferenceLevel(new Integer(1));

    ContactDetail mtnContact = new ContactDetail();
    mtnContact.setType(ContactDetail.Type.PHONE.name());
    mtnContact.setGroup(ContactDetail.Group.BUSINESS.name());
    mtnContact.setValue("677667766");
    mtnContact.setPreferenceLevel(new Integer(1));

    List <ContactDetail> contacts = new ArrayList <>();
    contacts.add(orangeContact);
    contacts.add(mtnContact);

    final Employee employeeToDelete = EmployeeFactory.createRandomEmployee();
    employeeToDelete.setIdentifier("employeeNo2");
    employeeToDelete.setGivenName("Manu");
    employeeToDelete.setMiddleName("Ngoh");
    employeeToDelete.setSurname("Haba");
    employeeToDelete.setAssignedOffice(office.getIdentifier());
    employeeToDelete.setContactDetails(contacts);
    this.organizationManager.createEmployee(employeeToDelete);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, employeeToDelete.getIdentifier());

    Gson gson = new Gson();
    this.mockMvc.perform(delete("/employees/" + employeeToDelete.getIdentifier())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isAccepted())
            .andDo(document("document-delete-employee", preprocessResponse(prettyPrint())));
  }

  @Test
  public void documentSetContactDetail ( ) throws Exception {

    final Office office = OfficeFactory.createRandomOffice();
    office.setIdentifier("ourOffice");
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    ContactDetail orangeContact = new ContactDetail();
    orangeContact.setType(ContactDetail.Type.MOBILE.name());
    orangeContact.setGroup(ContactDetail.Group.PRIVATE.name());
    orangeContact.setValue("699889988");
    orangeContact.setPreferenceLevel(new Integer(1));

    ContactDetail mtnContact = new ContactDetail();
    mtnContact.setType(ContactDetail.Type.PHONE.name());
    mtnContact.setGroup(ContactDetail.Group.BUSINESS.name());
    mtnContact.setValue("677557700");
    mtnContact.setPreferenceLevel(new Integer(1));

    List <ContactDetail> contacts = new ArrayList <>();
    contacts.add(orangeContact);

    final Employee employee = EmployeeFactory.createRandomEmployee();
    employee.setIdentifier("employeeNo20");
    employee.setGivenName("Manu");
    employee.setMiddleName("Ngoh");
    employee.setSurname("Haba");
    employee.setAssignedOffice(office.getIdentifier());
    employee.setContactDetails(contacts);
    this.organizationManager.createEmployee(employee);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, employee.getIdentifier());

    contacts.add(mtnContact);

    Gson gson = new Gson();
    this.mockMvc.perform(put("/employees/" + employee.getIdentifier() + "/contacts")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(gson.toJson(contacts))
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isAccepted())
            .andDo(document("document-set-contact-detail", preprocessResponse(prettyPrint())));
  }

  @Test
  public void documentFetchContactDetails ( ) throws Exception {

    final Office office = OfficeFactory.createRandomOffice();
    office.setIdentifier("officeZero");
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    final Employee employee = EmployeeFactory.createRandomEmployee();

    ContactDetail emailContact = new ContactDetail();
    emailContact.setType(ContactDetail.Type.EMAIL.toString());
    emailContact.setGroup(ContactDetail.Group.PRIVATE.toString());
    emailContact.setValue("me@example.com");
    emailContact.setPreferenceLevel(new Integer(2));

    ContactDetail phoneContact = new ContactDetail();
    phoneContact.setType(ContactDetail.Type.MOBILE.name());
    phoneContact.setGroup(ContactDetail.Group.BUSINESS.name());
    phoneContact.setValue("6667667667");
    phoneContact.setPreferenceLevel(new Integer(1));

    List <ContactDetail> contacts = new ArrayList <>();
    contacts.add(emailContact);
    contacts.add(phoneContact);

    employee.setIdentifier("empNoThree");
    employee.setGivenName("Mendi");
    employee.setMiddleName("Ngong");
    employee.setSurname("Ngang");
    employee.setAssignedOffice(office.getIdentifier());
    employee.setContactDetails(contacts);
    this.organizationManager.createEmployee(employee);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, employee.getIdentifier());

    this.mockMvc.perform(get("/employees/" + employee.getIdentifier() + "/contacts")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isOk())
            .andDo(document("document-fetch-contact-details", preprocessResponse(prettyPrint()),
                    responseFields(
                            fieldWithPath("[].type").description("Type of first contact"),
                            fieldWithPath("[].group").description("Group of first contact"),
                            fieldWithPath("[].value").description("Value of first contact"),
                            fieldWithPath("[].preferenceLevel").type("Integer").description("Preference level of first contact"),
                            fieldWithPath("[1].type").description("Type of second contact"),
                            fieldWithPath("[1].group").description("Group of second contact"),
                            fieldWithPath("[1].value").description("Value of second contact"),
                            fieldWithPath("[1].preferenceLevel").type("Integer").description("Preference level of second contact")
                    )));
  }

  @Test
  public void documentDeleteContactDetails ( ) throws Exception {

    final Office office = OfficeFactory.createRandomOffice();
    office.setIdentifier("Office24");
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    ContactDetail orangeContact = new ContactDetail();
    orangeContact.setType(ContactDetail.Type.MOBILE.name());
    orangeContact.setGroup(ContactDetail.Group.PRIVATE.name());
    orangeContact.setValue("699699966");
    orangeContact.setPreferenceLevel(new Integer(1));

    ContactDetail mtnContact = new ContactDetail();
    mtnContact.setType(ContactDetail.Type.PHONE.name());
    mtnContact.setGroup(ContactDetail.Group.BUSINESS.name());
    mtnContact.setValue("677688766");
    mtnContact.setPreferenceLevel(new Integer(1));

    List <ContactDetail> contacts = new ArrayList <>();
    contacts.add(orangeContact);

    final Employee employeeToDelete = EmployeeFactory.createRandomEmployee();
    employeeToDelete.setIdentifier("employeeNo4");
    employeeToDelete.setGivenName("Maimuna");
    employeeToDelete.setMiddleName("Obale");
    employeeToDelete.setSurname("Sehu");
    employeeToDelete.setAssignedOffice(office.getIdentifier());
    employeeToDelete.setContactDetails(contacts);
    this.organizationManager.createEmployee(employeeToDelete);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_EMPLOYEE, employeeToDelete.getIdentifier());

    contacts.add(mtnContact);
    this.organizationManager.setContactDetails(employeeToDelete.getIdentifier(), contacts);
    this.eventRecorder.wait(EventConstants.SELECTOR_PUT_CONTACT_DETAIL, employeeToDelete.getIdentifier());

    Gson gson = new Gson();
    this.mockMvc.perform(delete("/employees/" + employeeToDelete.getIdentifier() + "/contacts")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isAccepted())
            .andDo(document("document-delete-contact-details", preprocessResponse(prettyPrint())));
  }
}