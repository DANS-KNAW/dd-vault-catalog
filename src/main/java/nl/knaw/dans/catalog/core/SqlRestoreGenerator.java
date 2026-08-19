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

        boolean hasFileMetas = export.getFileMetas() != null && !export.getFileMetas().isEmpty();

        if (includeDataset || hasFileMetas) {
            sql.append("WITH ");
        }

        if (includeDataset) {
            sql.append("new_dataset AS (\n");
            sql.append("  INSERT INTO dataset (nbn, dataverse_pid, sword_token, data_supplier, ocfl_storage_root) VALUES (\n");
            sql.append("    ").append(escape(dataset.getNbn())).append(",\n");
            sql.append("    ").append(escape(dataset.getDataversePid())).append(",\n");
            sql.append("    ").append(escape(dataset.getSwordToken())).append(",\n");
            sql.append("    ").append(escape(dataset.getDataSupplier())).append(",\n");
            sql.append("    ").append(escape(dataset.getOcflStorageRoot())).append("\n");
            sql.append("  ) RETURNING id\n");
            sql.append(")");
            
            if (hasFileMetas) {
                sql.append(",\n");
            } else {
                sql.append("\n");
            }
        }

        if (hasFileMetas) {
            sql.append("new_export AS (\n");
        }

        String indent = hasFileMetas ? "  " : "";

        sql.append(indent).append("INSERT INTO dataset_version_export (dataset_id, bag_id, ocfl_object_version_number, created_timestamp, archived_timestamp, title, dataverse_pid_version, other_id, other_id_version, metadata, deaccessioned, exporter, exporter_version, skeleton_record) VALUES (\n");
        
        if (includeDataset) {
            sql.append(indent).append("  (SELECT id FROM new_dataset),\n");
        } else {
            sql.append(indent).append("  ").append(dataset.getId()).append(",\n");
        }

        sql.append(indent).append("  ").append(export.getBagId() != null ? escape(export.getBagId().toString()) : "NULL").append(",\n");
        sql.append(indent).append("  ").append(format(export.getOcflObjectVersionNumber())).append(",\n");
        sql.append(indent).append("  ").append(export.getCreatedTimestamp() != null ? escape(export.getCreatedTimestamp().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)) : "NULL").append(",\n");
        sql.append(indent).append("  ").append(export.getArchivedTimestamp() != null ? escape(export.getArchivedTimestamp().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)) : "NULL").append(",\n");
        sql.append(indent).append("  ").append(escape(export.getTitle())).append(",\n");
        sql.append(indent).append("  ").append(escape(export.getDataversePidVersion())).append(",\n");
        sql.append(indent).append("  ").append(escape(export.getOtherId())).append(",\n");
        sql.append(indent).append("  ").append(escape(export.getOtherIdVersion())).append(",\n");
        
        if (export.getMetadata() == null) {
            sql.append(indent).append("  NULL,\n");
        } else {
            sql.append(indent).append("  lo_from_bytea(0, convert_to(").append(escape(export.getMetadata())).append(", 'UTF8')),\n");
        }
        
        sql.append(indent).append("  ").append(format(export.getDeaccessioned())).append(",\n");
        sql.append(indent).append("  ").append(escape(export.getExporter())).append(",\n");
        sql.append(indent).append("  ").append(escape(export.getExporterVersion())).append(",\n");
        sql.append(indent).append("  ").append(format(export.getSkeletonRecord())).append("\n");

        if (hasFileMetas) {
            sql.append(indent).append(") RETURNING id\n");
            sql.append(")\n");
            
            sql.append("INSERT INTO file_meta (version_export_id, filepath, file_uri, byte_size, sha1sum) VALUES \n");
            
            java.util.List<FileMeta> metas = new java.util.ArrayList<>(export.getFileMetas());
            for (int i = 0; i < metas.size(); i++) {
                FileMeta fm = metas.get(i);
                sql.append("  ((SELECT id FROM new_export), ");
                sql.append(escape(fm.getFilepath())).append(", ");
                sql.append(fm.getFileUri() != null ? escape(fm.getFileUri().toString()) : "NULL").append(", ");
                sql.append(format(fm.getByteSize())).append(", ");
                sql.append(escape(fm.getSha1sum())).append(")");
                if (i < metas.size() - 1) {
                    sql.append(",\n");
                } else {
                    sql.append(";\n\n");
                }
            }
        } else {
            sql.append(indent).append(");\n\n");
        }

        sql.append("COMMIT;\n");

        return sql.toString();
    }
}