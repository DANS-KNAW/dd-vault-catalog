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

import io.dropwizard.hibernate.UnitOfWork;
import nl.knaw.dans.catalog.db.OcflObjectVersion;
import nl.knaw.dans.catalog.db.OcflObjectVersionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OcflObjectVersionServiceImpl implements OcflObjectVersionService {
    private static final Logger log = LoggerFactory.getLogger(OcflObjectVersionServiceImpl.class);

    private final OcflObjectVersionDao ocflObjectVersionDao;

    public OcflObjectVersionServiceImpl(OcflObjectVersionDao ocflObjectVersionDao) {
        this.ocflObjectVersionDao = ocflObjectVersionDao;
    }

    @Override
    @UnitOfWork
    public List<OcflObjectVersion> findByNbn(String id) {
        log.trace("Getting OcflObjectVersion by NBN {}", id);
        return ocflObjectVersionDao.findByNbn(id);
    }

}
