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
package org.apache.fineract.cn.office.internal.mapper;

import org.apache.fineract.cn.office.api.v1.domain.Office;
import org.apache.fineract.cn.office.internal.repository.OfficeEntity;

public final class OfficeMapper {

  private OfficeMapper() {
    super();
  }

  public static OfficeEntity map(final Office office) {
    final OfficeEntity officeEntity = new OfficeEntity();
    officeEntity.setIdentifier(office.getIdentifier());
    officeEntity.setName(office.getName());
    officeEntity.setDescription(office.getDescription());
    return officeEntity;
  }

  public static Office map(final OfficeEntity officeEntity) {
    final Office office = new Office();
    office.setIdentifier(officeEntity.getIdentifier());
    office.setName(officeEntity.getName());
    office.setDescription(officeEntity.getDescription());
    return office;
  }
}
