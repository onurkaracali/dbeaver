/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2016 Serge Rieder (serge@jkiss.org)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (version 2)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jkiss.dbeaver.ext.mysql.model;

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBPEvaluationContext;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.impl.jdbc.struct.JDBCTableForeignKey;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.rdb.DBSForeignKeyModifyRule;

import java.util.ArrayList;
import java.util.List;

/**
 * GenericForeignKey
 */
public class MySQLTableForeignKey extends JDBCTableForeignKey<MySQLTable, MySQLTableConstraint>
{
    private List<MySQLTableForeignKeyColumn> columns;

    public MySQLTableForeignKey(
        MySQLTable table,
        String name,
        String remarks,
        MySQLTableConstraint referencedKey,
        DBSForeignKeyModifyRule deleteRule,
        DBSForeignKeyModifyRule updateRule,
        boolean persisted)
    {
        super(table, name, remarks, referencedKey, deleteRule, updateRule, persisted);
    }

    // Copy constructor
    public MySQLTableForeignKey(DBRProgressMonitor monitor, MySQLTable table, MySQLTableForeignKey source) throws DBException {
        super(
            table,
            source.getName(),
            source.getDescription(),
            source.getReferencedConstraint(),
            source.deleteRule,
            source.updateRule,
            false);
        if (source.columns != null) {
            this.columns = new ArrayList<>(source.columns.size());
            for (MySQLTableForeignKeyColumn srcCol : source.columns) {
                this.columns.add(new MySQLTableForeignKeyColumn(
                    this,
                    table.getAttribute(monitor, srcCol.getName()),
                    srcCol.getOrdinalPosition(),
                    table.getAttribute(monitor, srcCol.getReferencedColumn().getName())));
            }
        }
    }

    @Override
    public List<MySQLTableForeignKeyColumn> getAttributeReferences(DBRProgressMonitor monitor)
    {
        return columns;
    }

    public void addColumn(MySQLTableForeignKeyColumn column)
    {
        if (columns == null) {
            columns = new ArrayList<>();
        }
        columns.add(column);
    }

    @NotNull
    @Override
    public String getFullyQualifiedName(DBPEvaluationContext context)
    {
        return DBUtils.getFullQualifiedName(getDataSource(),
            getTable().getContainer(),
            getTable(),
            this);
    }

    @NotNull
    @Override
    public MySQLDataSource getDataSource()
    {
        return getTable().getDataSource();
    }
}
