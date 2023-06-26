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
package nl.knaw.dans.catalog.db;

import io.dropwizard.hibernate.AbstractDAO;
import nl.knaw.dans.catalog.core.TarRepository;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TarEntityRepository extends AbstractDAO<TarEntity> implements TarRepository {
    public TarEntityRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Optional<TarEntity> getTarById(String id) {
        return query("from TarEntity where tarUuid = :id")
            .setParameter("id", id)
            .uniqueResultOptional();
    }

    @Override
    public TarEntity save(TarEntity tar) {
        for (var version : tar.getOcflObjectVersions()) {
            version.setTar(tar);
        }

        for (var part : tar.getTarParts()) {
            part.setTar(tar);
        }

        return persist(tar);
    }

    @Override
    public List<TarEntity> findAll() {
        return new ArrayList<>(list(query("from TarEntity")));
    }
}
