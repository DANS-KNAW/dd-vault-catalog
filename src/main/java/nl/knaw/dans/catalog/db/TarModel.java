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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tars")
public class TarModel {
    @Id
    @Column(name = "tar_uuid", nullable = false)
    private String tarUuid;
    @Column(name = "vault_path")
    private String vaultPath;
    @Column(name = "archival_date")
    private LocalDateTime archivalDate;
    @OneToMany(mappedBy = "tar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TarPartModel> tarParts = new ArrayList<>();
    @OneToMany(mappedBy = "tar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferItemModel> transferItems = new ArrayList<>();

    public String getTarUuid() {
        return tarUuid;
    }

    public void setTarUuid(String tarUuid) {
        this.tarUuid = tarUuid;
    }

    public String getVaultPath() {
        return vaultPath;
    }

    public void setVaultPath(String vaultPath) {
        this.vaultPath = vaultPath;
    }

    public LocalDateTime getArchivalDate() {
        return archivalDate;
    }

    public void setArchivalDate(LocalDateTime archivalDate) {
        this.archivalDate = archivalDate;
    }

    public List<TarPartModel> getTarParts() {
        return tarParts;
    }

    public void setTarParts(List<TarPartModel> tarParts) {
        this.tarParts = tarParts;
    }

    public List<TransferItemModel> getTransferItems() {
        return transferItems;
    }

    public void setTransferItems(List<TransferItemModel> transferItems) {
        this.transferItems = transferItems;
    }

}
