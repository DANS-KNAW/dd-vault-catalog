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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import nl.knaw.dans.convert.jpa.SwordTokenConverter;
import nl.knaw.dans.validation.SwordToken;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dataset", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "nbn" }),
    @UniqueConstraint(columnNames = { "dataverse_pid" }),
    @UniqueConstraint(columnNames = { "sword_token" })
})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Dataset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nbn", nullable = false)
    private String nbn;

    @Column(name = "dataverse_pid")
    private String dataversePid;

    @Column(name = "sword_token")
    @Convert(converter = SwordTokenConverter.class)
    @SwordToken
    private String swordToken;

    @Column(name = "data_supplier")
    private String dataSupplier;

    @Column(name = "ocfl_storage_root", nullable = false)
    private String ocflStorageRoot;

    @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<DatasetVersionExport> datasetVersionExports = new ArrayList<>();

    public void addDatasetVersionExport(DatasetVersionExport dve) {
        dve.setDataset(this);
        datasetVersionExports.add(dve);
    }
}
