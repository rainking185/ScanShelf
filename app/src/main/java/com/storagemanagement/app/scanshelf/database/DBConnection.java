package com.storagemanagement.app.scanshelf.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    public final static String defaultDBFileName = "storage.db";

    private final Connection connection;
    public final Shelf.Manager shelfs;
    public final ItemStack.Manager itemStacks;
    public final ItemClass.Manager itemClasses;
    public final Category.Manager categories;

    private DBConnection(Connection connection) {
        this.connection = connection;
        this.shelfs = new Shelf.Manager(this);
        this.itemStacks = new ItemStack.Manager(this);
        this.itemClasses = new ItemClass.Manager(this);
        this.categories = new Category.Manager(this);
    }

    public static DBConnection local() {
        return DBConnection.connect(defaultDBFileName);
    }

    public static DBConnection connect(String dbFileName) {
        String url = "jdbc:sqlite:" + dbFileName;
        Connection connection = null;
        try {
            connection =  DriverManager.getConnection(url);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            return connection==null ? null : new DBConnection(connection);
        }
    }

    public void close() {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void createTables() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(Shelf.tableSQL);
            statement.execute(ItemStack.tableSQL);
            statement.execute(Category.tableSQL);
            statement.execute(ItemClass.tableSQL);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public <T extends AbstractManager> T getManager(String type) {
        if (type.startsWith(DBContract.SHELF)) {
            return (T)shelfs;
        } else if (type.startsWith(DBContract.CATEGORY)) {
            return (T)categories;
        } else if (type.startsWith(DBContract.ITEM_CLASS)) {
            return (T)itemClasses;
        } else if (type.startsWith(DBContract.ITEM_STACK)) {
            return (T)itemStacks;
        } else {
            throw new RuntimeException("Unknown type: " + type);
        }
    }

    public <T extends AbstractRecord> AbstractManager<T, AbstractRecord> getManager(T obj) {
        if (obj instanceof Shelf) {
            return (AbstractManager)shelfs;
        } else if (obj instanceof Category){
            return (AbstractManager)categories;
        } else if (obj instanceof ItemClass) {
            return (AbstractManager) itemClasses;
        } else if (obj instanceof ItemStack) {
            return (AbstractManager) itemStacks;
        } else {
            throw new RuntimeException("Unknown type: " + obj.toString());
        }
    }

    protected boolean insert(String table, String columns, String values) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO " + table + "(" + columns +") VALUES (" + values + ");");
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected boolean delete(String table, String uuid) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM " + table + " WHERE uuid LIKE '" + uuid + "';");
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected <R> R select(String table, String condition, ISelectResultHandler<R> handler) {
        Statement statement = null;
        R result = null;
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM " + table + " WHERE " + condition + ";"
            );

            result = handler.process(resultSet);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    protected boolean update(String table, String uuid, String fieldAndValue) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE " + table + " SET " + fieldAndValue + " WHERE uuid='" + uuid + "';");
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
