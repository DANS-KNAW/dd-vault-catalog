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

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class SqlRestoreGeneratorTest {

    @Test
    void generateRestoreSql_should_include_dataset_insert_if_requested() throws Exception {
        var dataset = new Dataset();
        dataset.setId(1L);
        dataset.setNbn("urn:nbn:nl:ui:13-1234");
        dataset.setDataversePid("doi:10.1234/5678");
        dataset.setSwordToken("sword:123");
        dataset.setDataSupplier("DataSupplier");
        dataset.setOcflStorageRoot("/path/to/root");

        var export = new DatasetVersionExport();
        export.setId(10L);
        export.setDataset(dataset);
        export.setBagId(new URI("urn:uuid:12345678-1234-1234-1234-123456789012"));
        export.setOcflObjectVersionNumber(1);
        export.setCreatedTimestamp(OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC));
        export.setArchivedTimestamp(OffsetDateTime.of(2023, 1, 2, 12, 0, 0, 0, ZoneOffset.UTC));
        export.setTitle("My Title");
        export.setDataversePidVersion("1.0");
        export.setOtherId("other-1");
        export.setOtherIdVersion("1.1");
        export.setMetadata("some metadata");
        export.setDeaccessioned(false);
        export.setExporter("Exporter");
        export.setExporterVersion("1.0");
        export.setSkeletonRecord(false);

        var fileMeta = new FileMeta();
        fileMeta.setId(100L);
        fileMeta.setVersionExport(export);
        fileMeta.setFilepath("data/file.txt");
        fileMeta.setFileUri(new URI("http://example.com/file.txt"));
        fileMeta.setByteSize(1024L);
        fileMeta.setSha1sum("abcdef123456");

        export.addFileMeta(fileMeta);

        String sql = SqlRestoreGenerator.generateRestoreSql(dataset, export, true);

        assertThat(sql).contains("BEGIN;");
        assertThat(sql).contains("INSERT INTO dataset");
        assertThat(sql).contains("VALUES (");
        assertThat(sql).contains("1,");
        assertThat(sql).contains("'urn:nbn:nl:ui:13-1234',");
        assertThat(sql).contains("'doi:10.1234/5678',");
        assertThat(sql).contains("'sword:123',");
        assertThat(sql).contains("'DataSupplier',");
        assertThat(sql).contains("'/path/to/root'");

        assertThat(sql).contains("INSERT INTO dataset_version_export");
        assertThat(sql).contains("10,");
        assertThat(sql).contains("'urn:uuid:12345678-1234-1234-1234-123456789012',");
        assertThat(sql).contains("1,");
        assertThat(sql).contains("'2023-01-01T12:00:00Z',");
        assertThat(sql).contains("'2023-01-02T12:00:00Z',");
        assertThat(sql).contains("'My Title',");
        assertThat(sql).contains("FALSE,");
        assertThat(sql).contains("'Exporter',");

        assertThat(sql).contains("INSERT INTO file_meta");
        assertThat(sql).contains("100,");
        assertThat(sql).contains("'data/file.txt',");
        assertThat(sql).contains("'http://example.com/file.txt',");
        assertThat(sql).contains("1024,");
        assertThat(sql).contains("'abcdef123456'");
        
        assertThat(sql).contains("COMMIT;");
    }

    @Test
    void generateRestoreSql_should_not_include_dataset_insert_if_not_requested() {
        var dataset = new Dataset();
        dataset.setId(1L);
        
        var export = new DatasetVersionExport();
        export.setId(10L);
        export.setDataset(dataset);
        export.setOcflObjectVersionNumber(1);

        String sql = SqlRestoreGenerator.generateRestoreSql(dataset, export, false);

        assertThat(sql).doesNotContain("INSERT INTO dataset (");
        assertThat(sql).contains("INSERT INTO dataset_version_export");
    }

    @Test
    void generateRestoreSql_should_escape_single_quotes() {
        var dataset = new Dataset();
        dataset.setId(1L);
        dataset.setNbn("urn:nbn:nl:ui:13-1234");
        dataset.setDataversePid("doi:10.1234/5678");
        dataset.setOcflStorageRoot("/path/to/root");
        
        var export = new DatasetVersionExport();
        export.setId(10L);
        export.setDataset(dataset);
        export.setOcflObjectVersionNumber(1);
        export.setTitle("Title with 'single quotes'");

        String sql = SqlRestoreGenerator.generateRestoreSql(dataset, export, false);

        assertThat(sql).contains("'Title with ''single quotes'''");
    }

    @Test
    void generateRestoreSql_should_handle_null_values() {
        var dataset = new Dataset();
        dataset.setId(1L);
        
        var export = new DatasetVersionExport();
        export.setId(10L);
        export.setDataset(dataset);
        export.setOcflObjectVersionNumber(1);
        // leaving many fields null, including boolean, string, numbers, dates

        String sql = SqlRestoreGenerator.generateRestoreSql(dataset, export, true);

        // check that "NULL" without quotes is printed for null values instead of "null" string
        assertThat(sql).contains("  1,\n  NULL,\n  NULL,\n  NULL,\n  NULL,\n  NULL\n);"); // for dataset (id 1, rest nulls)
    }
}
