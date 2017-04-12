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
package io.mifos.office.internal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

  EmployeeEntity findByIdentifier(final String identifier);

  @Query("SELECT CASE WHEN COUNT(e) > 0 THEN 'true' ELSE 'false' END FROM EmployeeEntity e WHERE e.identifier = :identifier")
  Boolean existsByIdentifier(@Param("identifier") final String identifier);

  Page<EmployeeEntity> findByAssignedOffice(final OfficeEntity assignedOffice, final Pageable pageable);

  Page<EmployeeEntity> findByIdentifierContaining(String term, Pageable pageRequest);

  @Query("SELECT CASE WHEN COUNT(e) > 0 THEN 'true' ELSE 'false' END FROM EmployeeEntity e WHERE e.assignedOffice = :office")
  Boolean existsByAssignedOffice(@Param("office") final OfficeEntity assignedOffice);
}
