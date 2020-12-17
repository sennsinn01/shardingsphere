/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.proxy.backend.text.distsql.rdl.detail;

import org.apache.shardingsphere.governance.core.event.model.schema.SchemaNamePersistEvent;
import org.apache.shardingsphere.infra.database.type.DatabaseType;
import org.apache.shardingsphere.infra.eventbus.ShardingSphereEventBus;
import org.apache.shardingsphere.proxy.backend.communication.jdbc.connection.BackendConnection;
import org.apache.shardingsphere.proxy.backend.context.ProxyContext;
import org.apache.shardingsphere.proxy.backend.exception.DBCreateExistsException;
import org.apache.shardingsphere.proxy.backend.response.header.ResponseHeader;
import org.apache.shardingsphere.proxy.backend.response.header.update.UpdateResponseHeader;
import org.apache.shardingsphere.sql.parser.sql.common.statement.ddl.CreateDatabaseStatement;

/**
 * Create database backend handler.
 */
public final class CreateDatabaseBackendHandler implements RDLBackendDetailHandler<CreateDatabaseStatement> {
    
    @Override
    public ResponseHeader execute(final DatabaseType databaseType, final BackendConnection backendConnection, final CreateDatabaseStatement sqlStatement) {
        check(sqlStatement);
        post(sqlStatement);
        return new UpdateResponseHeader(sqlStatement);
    }
    
    private void check(final CreateDatabaseStatement sqlStatement) {
        if (ProxyContext.getInstance().getAllSchemaNames().contains(sqlStatement.getDatabaseName())) {
            throw new DBCreateExistsException(sqlStatement.getDatabaseName());
        }
    }
    
    private void post(final CreateDatabaseStatement sqlStatement) {
        // TODO Need to get the executed feedback from registry center for returning.
        ShardingSphereEventBus.getInstance().post(new SchemaNamePersistEvent(sqlStatement.getDatabaseName(), false));
    }
}