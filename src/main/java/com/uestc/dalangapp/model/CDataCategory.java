package com.uestc.dalangapp.model;

import ohos.data.orm.OrmObject;
import ohos.data.orm.annotation.Entity;
import ohos.data.orm.annotation.PrimaryKey;

@Entity(tableName = "categoryTbl")
public class CDataCategory  extends OrmObject {
    @PrimaryKey()
    Integer id;
    String name;

    public Integer getId()
    {
        return  id;
    }
    public  void setId(Integer id)
    {
        this.id=id;
    }
    public  String getName()
    {
        return  name;
    }
    public  void setName(String nm)
    {
        this.name=nm;
    }
}
