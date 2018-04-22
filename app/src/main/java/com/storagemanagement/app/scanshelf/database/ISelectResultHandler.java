package com.storagemanagement.app.scanshelf.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ISelectResultHandler<R> {
     R process(ResultSet resultSet) throws SQLException;
}
