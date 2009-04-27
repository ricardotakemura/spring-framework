/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.jdbc.datasource.embedded;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ClassUtils;

public class HsqlEmbeddedDatabaseConfigurer implements EmbeddedDatabaseConfigurer {
	
	private static HsqlEmbeddedDatabaseConfigurer INSTANCE;
	
	public static synchronized HsqlEmbeddedDatabaseConfigurer getInstance() throws ClassNotFoundException {
		if (INSTANCE == null) {
			ClassUtils.forName("org.hsqldb.jdbcDriver", HsqlEmbeddedDatabaseConfigurer.class.getClassLoader());
			INSTANCE = new HsqlEmbeddedDatabaseConfigurer();
		}
		return INSTANCE;
	}
	
	public void configureConnectionProperties(ConnectionProperties properties, String databaseName) {
		properties.setDriverClass(org.hsqldb.jdbcDriver.class);
		properties.setUrl("jdbc:hsqldb:mem:" + databaseName);
		properties.setUsername("sa");
		properties.setPassword("");		
	}

	public void shutdown(DataSource dataSource) {
		new JdbcTemplate(dataSource).execute("SHUTDOWN");
	}

}