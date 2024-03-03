/*
 * Copyright (C) 2022 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.catalog.resources;

import io.dropwizard.hibernate.UnitOfWork;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nl.knaw.dans.catalog.core.Conversions;
import nl.knaw.dans.catalog.core.UrnUuid;
import nl.knaw.dans.catalog.db.DatasetVersionExportDao;
import org.mapstruct.factory.Mappers;

import javax.ws.rs.core.Response;

@RequiredArgsConstructor
public class DatasetVersionExportApiResource implements DatasetVersionExportApi {
    private static final Conversions conversions = Mappers.getMapper(Conversions.class);

    @NonNull
    private final DatasetVersionExportDao dao;

    @Override
    @UnitOfWork
    public Response getDatasetVersionExportByBagId(String bagId) {
        return Response.ok(conversions.convert(dao.findByBagId(UrnUuid.fromString(bagId).getUuid()))).build();
    }
}
