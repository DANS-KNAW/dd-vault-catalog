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
package nl.knaw.dans.catalog.resources;

import lombok.extern.slf4j.Slf4j;
import nl.knaw.dans.lib.util.VersionProvider;

import javax.ws.rs.core.Response;

@Slf4j
public class DefaultApiResource implements DefaultApi {

    @Override
    public Response getInfo() {
        try {
            return Response.ok(String.format("DD Vault Catalog Service v%s running", new VersionProvider().getVersion())).build();
        }
        catch (Exception e) {
            log.error("Error while getting version", e);
            return Response.serverError().entity("Error while getting version").build();
        }
    }
}
