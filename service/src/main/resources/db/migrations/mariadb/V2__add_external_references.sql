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

CREATE TABLE horus_external_references (
  id                BIGINT       NOT NULL AUTO_INCREMENT,
  office_identifier VARCHAR(32)  NOT NULL,
  a_type            VARCHAR(32)  NULL,
  a_state           VARCHAR(256) NULL,
  CONSTRAINT external_references_pk PRIMARY KEY (id),
  CONSTRAINT external_references_uq UNIQUE (office_identifier, a_type)
);