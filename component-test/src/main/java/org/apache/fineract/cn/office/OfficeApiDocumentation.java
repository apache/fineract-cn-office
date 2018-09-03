package org.apache.fineract.cn.office;

import com.google.gson.Gson;
import org.apache.fineract.cn.office.api.v1.EventConstants;
import org.apache.fineract.cn.office.api.v1.domain.*;
import org.apache.fineract.cn.office.util.AddressFactory;
import org.apache.fineract.cn.office.util.OfficeFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OfficeApiDocumentation extends TestOffice {

  @Rule
  public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/doc/generated-snippets/test-office");

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
  public void documentCreateOffice ( ) throws Exception {

    final Office office = OfficeFactory.createRandomOffice();
    office.setIdentifier("Kigali");
    office.setName("Kigali And Sons MFI");
    office.setDescription("Sons Of Kigali MFI");
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    Gson gson = new Gson();
    this.mockMvc.perform(post("/employees")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(gson.toJson(office)))
            .andExpect(status().isAccepted())
            .andDo(document("document-create-office", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    requestFields(
                            fieldWithPath("identifier").description("Employee's identifier"),
                            fieldWithPath("name").description(" Employee given name"),
                            fieldWithPath("description").description("Employee's middle name"))));
  }

  @Test
  public void documentFindOffice ( ) throws Exception {

    final Office office = OfficeFactory.createRandomOffice();
    office.setIdentifier("MyOffice");
    office.setName("My Office");
    office.setDescription("My Own Office");
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    this.mockMvc.perform(get("/offices/" + office.getIdentifier())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isOk())
            .andDo(document("document-find-office", preprocessResponse(prettyPrint()),
                    responseFields(
                            fieldWithPath("identifier").description("office's identifier"),
                            fieldWithPath("name").description("office name"),
                            fieldWithPath("description").description("office description"),
                            fieldWithPath("parentIdentifier").type("String").description("Parent's branch"),
                            fieldWithPath("address").type("Address").description("Office's address + \n " +
                                    " *class* _Address_ { + \n" +
                                    "       private String street + \n" +
                                    "       private String city + \n" +
                                    "       private String region + \n" +
                                    "       private String postalCode + \n" +
                                    "       private String countryCode + \n" +
                                    "       private String country + \n" +
                                    "}"),
                            fieldWithPath("externalReferences").description("External references"))));
  }

  @Test
  public void documentUpdateOffice ( ) throws Exception {

    final Office office = OfficeFactory.createRandomOffice();
    office.setIdentifier("originalOffice");
    office.setName("Original Office");
    office.setDescription("My Original Office");
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    office.setName("Updated Office");
    office.setDescription("My Updated Office");

    Gson gson = new Gson();
    this.mockMvc.perform(put("/offices/" + office.getIdentifier())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(gson.toJson(office))
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isAccepted())
            .andDo(document("document-update-office", preprocessRequest(prettyPrint()),
                    requestFields(
                            fieldWithPath("identifier").description("office's identifier"),
                            fieldWithPath("name").description("office name"),
                            fieldWithPath("description").description("office description"))));
  }

  @Test
  public void documentAddBranch ( ) throws Exception {

    final Office parentOffice = OfficeFactory.createRandomOffice();
    parentOffice.setIdentifier("parentOffice");
    parentOffice.setName("Parent Office");
    parentOffice.setDescription("My Parent Office");
    this.organizationManager.createOffice(parentOffice);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, parentOffice.getIdentifier());

    final Office branch = OfficeFactory.createRandomOffice();
    branch.setIdentifier("Branch");
    branch.setName("Branch To Add");
    branch.setDescription("Branch To Be Added");
    branch.setParentIdentifier(parentOffice.getIdentifier());

    Gson gson = new Gson();
    this.mockMvc.perform(post("/offices/" + parentOffice.getIdentifier())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(gson.toJson(branch))
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isAccepted())
            .andDo(document("document-add-branch", preprocessRequest(prettyPrint()),
                    requestFields(
                            fieldWithPath("identifier").description("office's identifier"),
                            fieldWithPath("name").description("office name"),
                            fieldWithPath("description").description("office description"),
                            fieldWithPath("parentIdentifier").description("Parent Office"))));
  }

  @Test
  public void documentGetBranches ( ) throws Exception {

    final Office office = OfficeFactory.createRandomOffice();
    office.setIdentifier("office001");
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    final Address firstAddress = AddressFactory.createRandomAddress();
    firstAddress.setStreet("Rue De Vie");
    firstAddress.setCity("Bandjoun");
    firstAddress.setRegion("West Region");
    firstAddress.setPostalCode("8050");
    firstAddress.setCountry("Cameroon");

    final Office firstBranch = OfficeFactory.createRandomOffice();
    firstBranch.setIdentifier("firstBranch");
    firstBranch.setName("First Branch");
    firstBranch.setDescription("First Branch Of MFI");
    firstBranch.setAddress(firstAddress);
    this.organizationManager.addBranch(office.getIdentifier(), firstBranch);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, firstBranch.getIdentifier());

    final Address secondAddress = AddressFactory.createRandomAddress();
    secondAddress.setStreet("Rue Du Bon");
    secondAddress.setCity("Baham");
    secondAddress.setRegion("West Region");
    secondAddress.setPostalCode("8050");
    secondAddress.setCountry("Cameroon");

    final Office secondBranch = OfficeFactory.createRandomOffice();
    secondBranch.setIdentifier("secondBranch");
    secondBranch.setName("Second Branch");
    secondBranch.setDescription("Second Branch Of MFI");
    secondBranch.setAddress(secondAddress);
    this.organizationManager.addBranch(office.getIdentifier(), secondBranch);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, secondBranch.getIdentifier());

    final OfficePage officePage = this.organizationManager.getBranches(office.getIdentifier(), 0, 20, null, null);
    Assert.assertEquals(Long.valueOf(2L), officePage.getTotalElements());

    this.mockMvc.perform(get("/offices/" + office.getIdentifier() + "/branches")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isOk())
            .andDo(document("document-get-branches", preprocessResponse(prettyPrint()),
                    responseFields(
                            fieldWithPath("offices[].identifier").description("first employee's identifier"),
                            fieldWithPath("offices[].parentIdentifier").description("Parent office"),
                            fieldWithPath("offices[].name").description("first office's name"),
                            fieldWithPath("offices[].description").description("first branch's description"),
                            fieldWithPath("offices[].address").type("Address").description("first branch's address + \n " +
                                    " *class* _Address_ { + \n" +
                                    "       private String street + \n" +
                                    "       private String city + \n" +
                                    "       private String region + \n" +
                                    "       private String postalCode + \n" +
                                    "       private String countryCode + \n" +
                                    "       private String country + \n" +
                                    "}"),
                            fieldWithPath("offices[].externalReferences").description("first branch's external reference"),
                            fieldWithPath("offices[1].identifier").description("second employee's identifier"),
                            fieldWithPath("offices[1].parentIdentifier").description("Parent office"),
                            fieldWithPath("offices[1].name").description("second office's name"),
                            fieldWithPath("offices[1].description").description("second branch's description"),
                            fieldWithPath("offices[1].address").type("Address").description("second branch's address + \n " +
                                    " *class* _Address_ { + \n" +
                                    "       private String street + \n" +
                                    "       private String city + \n" +
                                    "       private String region + \n" +
                                    "       private String postalCode + \n" +
                                    "       private String countryCode + \n" +
                                    "       private String country + \n" +
                                    "}"),
                            fieldWithPath("offices[1].externalReferences").description("second branch's external reference"),
                            fieldWithPath("totalPages").type("Integer").description("Page of offices"),
                            fieldWithPath("totalElements").type("Integer").description("Page of offices"))));
  }

  @Test
  public void documentDeleteOffice ( ) throws Exception {

    final Address address = AddressFactory.createRandomAddress();
    address.setStreet("Commercial Avenue");
    address.setCity("Bamenda");
    address.setRegion("North West Region");
    address.setPostalCode("8050");
    address.setCountry("Cameroon");

    final Office office = OfficeFactory.createRandomOffice();
    office.setIdentifier("office007");
    office.setName("Seventh Office ");
    office.setDescription("The Seventh Office Of MFI");
    office.setAddress(address);
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    this.mockMvc.perform(delete("/offices/" + office.getIdentifier())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isAccepted())
            .andDo(document("document-delete-office", preprocessResponse(prettyPrint())));
  }

  @Test
  public void documentSetOfficeAddress ( ) throws Exception {

    final Office office = OfficeFactory.createRandomOffice();
    office.setIdentifier("originalOffice");
    office.setName("Original Office");
    office.setDescription("My Original Office");
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    final Address newAddress = AddressFactory.createRandomAddress();
    newAddress.setStreet("New Street");
    newAddress.setCity("New City");
    newAddress.setRegion("New region");
    newAddress.setPostalCode("8050");
    newAddress.setCountry("Cameroon");

    Gson gson = new Gson();
    this.mockMvc.perform(put("/offices/" + office.getIdentifier() + "/address")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(gson.toJson(newAddress))
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isAccepted())
            .andDo(document("document-set-office-address", preprocessRequest(prettyPrint()),
                    requestFields(
                            fieldWithPath("street").description("Street"),
                            fieldWithPath("city").description("City"),
                            fieldWithPath("region").description("Region"),
                            fieldWithPath("postalCode").description("Postal Code"),
                            fieldWithPath("countryCode").description("Country Code"),
                            fieldWithPath("country").description("Country"))));
  }

  @Test
  public void documentGetOfficeAddress ( ) throws Exception {

    final Address address = AddressFactory.createRandomAddress();
    address.setStreet("The Street");
    address.setCity("The City");
    address.setRegion("The Region");
    address.setPostalCode("8085");
    address.setCountry("Gambia");

    final Office office = OfficeFactory.createRandomOffice();
    office.setIdentifier("theOffice");
    office.setName("The Office");
    office.setDescription("The Office");
    office.setAddress(address);
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    this.mockMvc.perform(get("/offices/" + office.getIdentifier() + "/address")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isOk())
            .andDo(document("document-get-office-address", preprocessResponse(prettyPrint()),
                    responseFields(
                            fieldWithPath("street").description("Street"),
                            fieldWithPath("city").description("City"),
                            fieldWithPath("region").description("Region"),
                            fieldWithPath("postalCode").description("Postal Code"),
                            fieldWithPath("countryCode").description("Country Code"),
                            fieldWithPath("country").description("Country"))));
  }

  @Test
  public void documentDeleteOfficeAddress ( ) throws Exception {

    final Address address = AddressFactory.createRandomAddress();
    address.setStreet("Delete Street");
    address.setCity("Delete City");
    address.setRegion("Delete Region");
    address.setPostalCode("8040");
    address.setCountry("Niger");

    final Office office = OfficeFactory.createRandomOffice();
    office.setIdentifier("officeToDelete");
    office.setName("Office To Delete");
    office.setDescription("The Office To Delete");
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    this.organizationManager.setAddressForOffice(office.getIdentifier(), address);
    this.eventRecorder.wait(EventConstants.OPERATION_PUT_ADDRESS, office.getIdentifier());

    this.mockMvc.perform(delete("/offices/" + office.getIdentifier() + "/address")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isAccepted())
            .andDo(document("document-delete-office-address", preprocessResponse(prettyPrint())));
  }

  @Test
  public void documentAddExternalReference ( ) throws Exception {

    final Office office = OfficeFactory.createRandomOffice();
    office.setIdentifier("ourOffice");
    this.organizationManager.createOffice(office);
    this.eventRecorder.wait(EventConstants.OPERATION_POST_OFFICE, office.getIdentifier());

    final ExternalReference reference = new ExternalReference();
    reference.setType("Type One");
    reference.setState(ExternalReference.State.ACTIVE.toString());

    Gson gson = new Gson();
    this.mockMvc.perform(put("/offices/" + office.getIdentifier() + "/references")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(gson.toJson(reference))
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isAccepted())
            .andDo(document("document-add-external-reference", preprocessRequest(prettyPrint()),
                    requestFields(
                            fieldWithPath("type").description("Type of reference"),
                            fieldWithPath("state").description("State of reference ")
                    )));
  }
}
