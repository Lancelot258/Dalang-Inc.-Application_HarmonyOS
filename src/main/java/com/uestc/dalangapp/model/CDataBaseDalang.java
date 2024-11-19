package com.uestc.dalangapp.model;

import ohos.data.orm.OrmDatabase;
import ohos.data.orm.annotation.Database;
import ohos.data.rdb.RdbOpenCallback;

@Database(entities = {CDataCategory.class, CDataPics.class}, version = 1)
public abstract  class CDataBaseDalang  extends OrmDatabase {
    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public RdbOpenCallback getHelper() {
        return null;
    }
}
