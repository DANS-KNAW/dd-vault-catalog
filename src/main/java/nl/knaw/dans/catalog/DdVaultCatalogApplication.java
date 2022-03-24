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

import com.fasterxml.jackson.databind.SerializationFeature;
import io.dropwizard.Application;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.jersey.errors.ErrorEntityWriter;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.View;
import io.dropwizard.views.ViewBundle;
import nl.knaw.dans.catalog.core.OcflObjectVersionServiceImpl;
import nl.knaw.dans.catalog.core.SolrServiceImpl;
import nl.knaw.dans.catalog.core.TarServiceImpl;
import nl.knaw.dans.catalog.db.OcflObjectVersion;
import nl.knaw.dans.catalog.db.OcflObjectVersionDao;
import nl.knaw.dans.catalog.db.Tar;
import nl.knaw.dans.catalog.db.TarDAO;
import nl.knaw.dans.catalog.db.TarPart;
import nl.knaw.dans.catalog.resource.api.TarAPIResource;
import nl.knaw.dans.catalog.resource.view.ErrorView;
import nl.knaw.dans.catalog.resource.web.ArchiveDetailResource;

import javax.ws.rs.core.MediaType;

public class DdVaultCatalogApplication extends Application<DdVaultCatalogConfiguration> {
    private final HibernateBundle<DdVaultCatalogConfiguration> hibernateBundle = new HibernateBundle<>(OcflObjectVersion.class, Tar.class, TarPart.class) {

        @Override
        public PooledDataSourceFactory getDataSourceFactory(DdVaultCatalogConfiguration configuration) {
            return configuration.getDatabase();
        }
    };

    public static void main(final String[] args) throws Exception {
        new DdVaultCatalogApplication().run(args);
    }

    @Override
    public String getName() {
        return "Dd Vault Catalog";
    }

    @Override
    public void initialize(final Bootstrap<DdVaultCatalogConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new ViewBundle<>());
        bootstrap.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void run(final DdVaultCatalogConfiguration configuration, final Environment environment) {
        var tarDao = new TarDAO(hibernateBundle.getSessionFactory());
        var ocflObjectVersionDao = new OcflObjectVersionDao(hibernateBundle.getSessionFactory());
        var tarService = new UnitOfWorkAwareProxyFactory(hibernateBundle).create(TarServiceImpl.class, TarDAO.class, tarDao);

        var ocflObjectVersionService = new UnitOfWorkAwareProxyFactory(hibernateBundle)
            .create(OcflObjectVersionServiceImpl.class, OcflObjectVersionDao.class, ocflObjectVersionDao);

        var solrService = new SolrServiceImpl(configuration.getSolr());

        environment.jersey().register(new TarAPIResource(tarService, solrService, ocflObjectVersionService));
        environment.jersey().register(new ArchiveDetailResource(ocflObjectVersionService));
        environment.jersey().register(new ErrorEntityWriter<ErrorMessage, View>(MediaType.TEXT_HTML_TYPE, View.class) {

            @Override
            protected View getRepresentation(ErrorMessage errorMessage) {
                return new ErrorView(errorMessage);
            }
        });
    }
}
