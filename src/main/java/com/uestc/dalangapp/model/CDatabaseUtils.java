package com.uestc.dalangapp.model;


import ohos.data.orm.Blob;
import ohos.data.orm.OrmContext;
import ohos.data.orm.OrmPredicates;
import ohos.data.rdb.ValuesBucket;

import java.io.IOException;
import java.io.InputStream;
import  java.util.List;
/**
 * Database Operations
 *
 */
public class CDatabaseUtils {
    /**
     * add card info
     *
     * @param pic card object
     * @param connect data connection
     */
    public static void insertPic(CDataPics pic, OrmContext connect) {
        connect.insert(pic);
        connect.flush();

    }

    /**
     * query database
     *
     * @param connect data connection
     * @return database
     */
    public static List<CDataPics> queryPic(OrmContext connect,int idCategory){
// 从数据库中获取信息
        OrmPredicates ormPredicates = new OrmPredicates(CDataPics.class);// 搜索实例
        ormPredicates.equalTo("idcategory",idCategory);
        List<CDataPics> formList = connect.query(ormPredicates);
        return formList;
    }
    /**
     * update database
     *
     * @param connect 数据库实体
     * @param pic user
     */
    public static void updatePic(OrmContext connect,CDataPics pic) throws IOException {
// 从数据库中获取信息
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putInteger("idpic", pic.getIdpic());
        valuesBucket.putString("name", pic.getName());
        valuesBucket.putInteger("idcategory", pic.getIdcategory());
        valuesBucket.putInteger("size", pic.getSize());
        Blob data=pic.getData();
        InputStream iStream = null;
        iStream=data.getBinaryStream();
        byte[] byData =new byte[(int)data.length()];
        iStream.read(byData);
        valuesBucket.putByteArray("data", byData);
        OrmPredicates update = connect.where(CDataPics.class).equalTo("idpic", pic.getIdpic());
        connect.update(update, valuesBucket);
    }


    /**
     * delete data
     *
     * @param id pic id
     * @param connect data connection
     */
    public static void deletePic(long id, OrmContext connect) {
        OrmPredicates where = connect.where(CDataPics.class);
        where.equalTo("idpic", id); // 在数据库的“user”表中查询formId为“”的User对象列表
        List<CDataPics> query = connect.query(where);
        if (!query.isEmpty()) {
            connect.delete(query.get(0));
            connect.flush();
        }
    }

    /**
     * add card info
     *
     * @param data card object
     * @param connect data connection
     */
    public static void insertCategory(CDataCategory data, OrmContext connect) {
        connect.insert(data);
        connect.flush();
    }
    /**
     * query database
     *
     * @param connect data connection
     * @return database
     */
    public static List<CDataCategory> queryCategory(OrmContext connect){
// 从数据库中获取信息
        OrmPredicates ormPredicates = new OrmPredicates(CDataCategory.class);// 搜索实例
        List<CDataCategory> formList = connect.query(ormPredicates);
        return formList;
    }
    /**
     * update database
     *
     * @param connect 数据库实体
     * @param data user
     */
    public static void updateCategory(OrmContext connect,CDataCategory data) throws IOException {
// 从数据库中获取信息
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putInteger("idcategory", data.getId());
        valuesBucket.putString("name", data.getName());
        OrmPredicates update = connect.where(CDataPics.class).equalTo("idcategory",data.getId());
        connect.update(update, valuesBucket);
    }


    /**
     * delete data
     *
     * @param id pic id
     * @param connect data connection
     */
    public static void deleteCategory(long id, OrmContext connect) {
        OrmPredicates where = connect.where(CDataCategory.class);
        where.equalTo("idcategory", id); // 在数据库的“user”表中查询formId为“”的User对象列表
        List<CDataPics> query = connect.query(where);
        if (!query.isEmpty()) {
            connect.delete(query.get(0));
            connect.flush();
        }
    }
}