package com.uestc.dalangapp.model;


import ohos.data.orm.Blob;
import ohos.data.orm.OrmObject;
import ohos.data.orm.annotation.Entity;
import ohos.data.orm.annotation.PrimaryKey;

@Entity(tableName = "picsTbl")
public class CDataPics  extends OrmObject {
    @PrimaryKey(autoGenerate = true)
    int idpic;
    int idcategory;
    String name; //filename
    int size; //file size
    Blob data; //file data

    public int getIdpic()
    {
        return  idpic;
    }
    public  void setIdpic(int idd)
    {
        this.idpic=idd;
    }

    public  String getName()
    {
        return  name;
    }
    public  void setName(String nm)
    {
        this.name=nm;
    }
    public  int getSize(){
        return  size;
    }
    public  void setSize(int ns)
    {
        size=ns;
    }
    public  Blob getData()
    {
        return  data;
    }
    public  void setData(Blob vd)
    {
        data=vd;
    }
    public  int getIdcategory()
    {
        return idcategory;
    }
    public  void setIdcategory(int idcategory)
    {
        this.idcategory=idcategory;
    }
}
