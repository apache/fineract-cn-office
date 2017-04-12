--
-- Copyright 2017 The Mifos Initiative
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--    http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

CREATE TABLE horus_offices (
  id               BIGINT        NOT NULL AUTO_INCREMENT,
  parent_office_id BIGINT        NULL,
  identifier       VARCHAR(32)   NOT NULL,
  a_name           VARCHAR(256)  NOT NULL,
  description      VARCHAR(2048) NULL,
  created_by       VARCHAR(32)   NOT NULL,
  created_on       TIMESTAMP(3)  NOT NULL,
  last_modified_by VARCHAR(32)   NULL,
  last_modified_on TIMESTAMP(3)  NULL,
  CONSTRAINT office_pk
  PRIMARY KEY (id),
  CONSTRAINT office_identifier_uq
  UNIQUE (identifier),
  CONSTRAINT office_parent_fk
  FOREIGN KEY (parent_office_id) REFERENCES horus_offices (id)
    ON UPDATE RESTRICT
);

CREATE TABLE horus_addresses (
  id           BIGINT       NOT NULL AUTO_INCREMENT,
  office_id    BIGINT       NOT NULL,
  street       VARCHAR(256) NOT NULL,
  city         VARCHAR(256) NOT NULL,
  region       VARCHAR(256) NULL,
  postal_code  VARCHAR(32)  NULL,
  country_code VARCHAR(2)   NOT NULL,
  country      VARCHAR(256) NOT NULL,
  CONSTRAINT address_pk
  PRIMARY KEY (id),
  CONSTRAINT address_office_fk
  FOREIGN KEY (office_id) REFERENCES horus_offices (id)
    ON UPDATE RESTRICT
);

CREATE TABLE horus_employees (
  id                 BIGINT       NOT NULL AUTO_INCREMENT,
  identifier         VARCHAR(32)  NOT NULL,
  given_name         VARCHAR(256) NULL,
  middle_name        VARCHAR(256) NULL,
  surname            VARCHAR(256) NULL,
  assigned_office_id BIGINT       NULL,
  created_by         VARCHAR(32)  NOT NULL,
  created_on         TIMESTAMP(3) NOT NULL,
  last_modified_by   VARCHAR(32)   NULL,
  last_modified_on   TIMESTAMP(3) NULL,
  CONSTRAINT employee_pk
  PRIMARY KEY (id),
  CONSTRAINT employee_identifier_uq
  UNIQUE (identifier),
  CONSTRAINT employee_office_fk
  FOREIGN KEY (assigned_office_id) REFERENCES horus_offices (id)
    ON UPDATE RESTRICT
);

CREATE TABLE horus_contact_details (
  id               BIGINT       NOT NULL AUTO_INCREMENT,
  employee_id      BIGINT       NOT NULL,
  a_type           VARCHAR(256) NOT NULL,
  a_group          VARCHAR(256) NOT NULL,
  a_value          VARCHAR(256) NOT NULL,
  preference_level TINYINT      NULL,
  CONSTRAINT contact_detail_pk
  PRIMARY KEY (id),
  CONSTRAINT contact_detail_employee_fk
  FOREIGN KEY (employee_id) REFERENCES horus_employees (id)
    ON UPDATE RESTRICT
);