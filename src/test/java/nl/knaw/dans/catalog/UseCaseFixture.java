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
package nl.knaw.dans.catalog;

import nl.knaw.dans.catalog.core.OcflObjectVersionRepository;
import nl.knaw.dans.catalog.core.SearchIndex;
import nl.knaw.dans.catalog.core.TarRepository;
import nl.knaw.dans.catalog.core.UseCases;
import org.mockito.Mockito;

public class UseCaseFixture {
    public static final OcflObjectVersionRepository ocflObjectVersionRepository = Mockito.mock(OcflObjectVersionRepository.class);
    public static final TarRepository tarRepository = Mockito.mock(TarRepository.class);
    public static final SearchIndex searchIndex = Mockito.mock(SearchIndex.class);
    public static final UseCases useCases = new UseCases(
        ocflObjectVersionRepository,
        tarRepository,
        searchIndex
    );

    public static void reset() {
        Mockito.reset(ocflObjectVersionRepository, tarRepository, searchIndex);
    }
}
