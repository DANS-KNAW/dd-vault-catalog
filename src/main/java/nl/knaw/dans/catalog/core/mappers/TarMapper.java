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
package nl.knaw.dans.catalog.core.mappers;

import nl.knaw.dans.catalog.api.TarParameterDto;
import nl.knaw.dans.catalog.api.TarPartParameterDto;
import nl.knaw.dans.catalog.core.Tar;
import nl.knaw.dans.catalog.core.TarPart;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper
public interface TarMapper {

    TarMapper INSTANCE = Mappers.getMapper(TarMapper.class);

    Tar convert(TarParameterDto parameters);

    TarPart convert(TarPartParameterDto parameters);

    default String mapTarUuid(UUID uuid) {
        return uuid.toString();
    }

}
