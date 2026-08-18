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

import java.time.format.DateTimeFormatter;

public class SqlRestoreGenerator {

    private static String escape(String value) {
        if (value == null) {
            return "NULL";
        }
        return "'" + value.replace("'", "''") + "'";
    }

    private static String format(Boolean value) {
        if (value == null) {
            return "NULL";
        }
        return value ? "TRUE" : "FALSE";
    }

    private static String format(Number value) {
        if (value == null) {
            return "NULL";
        }
        return value.toString();
    }

    public static String generateRestoreSql(Dataset dataset, DatasetVersionExport export, boolean includeDataset) {
        StringBuilder sql = new StringBuilder();
        sql.append("BEGIN;\n\n");

        if (includeDataset) {
            sql.append("INSERT INTO dataset (id, nbn, dataverse_pid, sword_token, data_supplier, ocfl_storage_root) VALUES (\n");
            sql.append("  ").append(dataset.getId()).append(",\n");
            sql.append("  ").append(escape(dataset.getNbn())).append(",\n");
            sql.append("  ").append(escape(dataset.getDataversePid())).append(",\n");
            sql.append("  ").append(escape(dataset.getSwordToken())).append(",\n");
            sql.append("  ").append(escape(dataset.getDataSupplier())).append(",\n");
            sql.append("  ").append(escape(dataset.getOcflStorageRoot())).append("\n");
            sql.append(");\n\n");
        }

        sql.append("INSERT INTO dataset_version_export (id, dataset_id, bag_id, ocfl_object_version_number, created_timestamp, archived_timestamp, title, dataverse_pid_version, other_id, other_id_version, metadata, deaccessioned, exporter, exporter_version, skeleton_record) VALUES (\n");
        sql.append("  ").append(export.getId()).append(",\n");
        sql.append("  ").append(dataset.getId()).append(",\n");
        sql.append("  ").append(export.getBagId() != null ? escape(export.getBagId().toString()) : "NULL").append(",\n");
        sql.append("  ").append(format(export.getOcflObjectVersionNumber())).append(",\n");
        sql.append("  ").append(export.getCreatedTimestamp() != null ? escape(export.getCreatedTimestamp().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)) : "NULL").append(",\n");
        sql.append("  ").append(export.getArchivedTimestamp() != null ? escape(export.getArchivedTimestamp().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)) : "NULL").append(",\n");
        sql.append("  ").append(escape(export.getTitle())).append(",\n");
        sql.append("  ").append(escape(export.getDataversePidVersion())).append(",\n");
        sql.append("  ").append(escape(export.getOtherId())).append(",\n");
        sql.append("  ").append(escape(export.getOtherIdVersion())).append(",\n");
        sql.append("  ").append(escape(export.getMetadata())).append(",\n");
        sql.append("  ").append(format(export.getDeaccessioned())).append(",\n");
        sql.append("  ").append(escape(export.getExporter())).append(",\n");
        sql.append("  ").append(escape(export.getExporterVersion())).append(",\n");
        sql.append("  ").append(format(export.getSkeletonRecord())).append("\n");
        sql.append(");\n\n");

        if (export.getFileMetas() != null) {
            for (FileMeta fm : export.getFileMetas()) {
                sql.append("INSERT INTO file_meta (id, version_export_id, filepath, file_uri, byte_size, sha1sum) VALUES (\n");
                sql.append("  ").append(fm.getId()).append(",\n");
                sql.append("  ").append(export.getId()).append(",\n");
                sql.append("  ").append(escape(fm.getFilepath())).append(",\n");
                sql.append("  ").append(fm.getFileUri() != null ? escape(fm.getFileUri().toString()) : "NULL").append(",\n");
                sql.append("  ").append(format(fm.getByteSize())).append(",\n");
                sql.append("  ").append(escape(fm.getSha1sum())).append("\n");
                sql.append(");\n\n");
            }
        }

        sql.append("COMMIT;\n");

        return sql.toString();
    }
}