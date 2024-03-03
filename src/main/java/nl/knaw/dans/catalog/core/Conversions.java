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
package nl.knaw.dans.catalog.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.dans.catalog.api.DatasetDto;
import nl.knaw.dans.catalog.api.VersionExportDto;
import nl.knaw.dans.catalog.core.Dataset;
import nl.knaw.dans.catalog.core.DatasetVersionExport;
import nl.knaw.dans.catalog.core.UrnUuid;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Conversion between DTOs and domain objects.
 */
@Mapper
public interface Conversions {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Mapping(target = "datasetNbn", source = "dataset.nbn")
    VersionExportDto convert(DatasetVersionExport datasetVersionExport);

    DatasetVersionExport convert(VersionExportDto versionExportDto);

    @Named("mapVersionExportDtoListToDatasetVersionExportList")
    default List<DatasetVersionExport> mapVersionExportDtoListToDatasetVersionExportList(List<VersionExportDto> versionExportDtoList) {
        List<DatasetVersionExport> datasetVersionExports = new ArrayList<>();
        for (VersionExportDto versionExportDto : versionExportDtoList) {
            DatasetVersionExport datasetVersionExport = convert(versionExportDto);
            datasetVersionExports.add(datasetVersionExport);
        }
        return datasetVersionExports;
    }

    @AfterMapping
    default void setDataset(Object source, @MappingTarget Dataset dataset) {
        for (DatasetVersionExport datasetVersionExport : dataset.getDatasetVersionExports()) {
            datasetVersionExport.setDataset(dataset);
        }
    }

    @Mapping(target = "datasetVersionExports", source = "datasetDto.versionExports", qualifiedByName = "mapVersionExportDtoListToDatasetVersionExportList")
    Dataset convert(DatasetDto datasetDto);

    default UUID stringToUuid(String value) {
        if (value == null) {
            return null;
        }
        return UrnUuid.fromString(value).getUuid();
    }

    default String uuidToString(UUID value) {
        if (value == null) {
            return null;
        }
        var uri = URI.create("urn:uuid:" + value);
        return uri.toString();
    }

    default String objectToString(Object value) {
        if (value == null) {
            return null;
        }

        return value.toString();
    }

    default String mapToJsonString(Map<String, Object> value) throws JsonProcessingException {
        if (value == null) {
            return null;
        }

        return OBJECT_MAPPER.writeValueAsString(value);
    }

    default Map<String, Object> jsonStringToMap(String value) {
        if (value == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readValue(value, new TypeReference<>() {

            });
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Unable to parse JSON: %s", value), e);
        }
    }
}
