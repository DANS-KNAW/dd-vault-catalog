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

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.net.URI;
import java.util.UUID;

@Converter(autoApply = true)
public class UrnUuidConverter implements AttributeConverter<URI, UUID> {

    @Override
    public UUID convertToDatabaseColumn(URI urn) {
        return UUID.fromString(urn.getSchemeSpecificPart().substring("uuid:".length()));
    }

    @Override
    public URI convertToEntityAttribute(UUID uuid) {
        return URI.create("urn:uuid:" + uuid.toString());
    }
}
