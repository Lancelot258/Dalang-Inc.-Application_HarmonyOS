package com.uestc.dalangapp;

import com.uestc.dalangapp.model.CDataBaseDalang;
import com.uestc.dalangapp.model.CDataPics;
import com.uestc.dalangapp.model.CDatabaseUtils;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Image;
import ohos.data.DatabaseHelper;
import ohos.data.orm.OrmContext;
import ohos.data.orm.OrmPredicates;
import ohos.data.rdb.RdbOpenCallback;
import ohos.data.rdb.RdbStore;
import ohos.data.rdb.StoreConfig;
import ohos.data.resultset.ResultSet;
import ohos.data.rdb.ValuesBucket;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.Size;
import ohos.media.photokit.metadata.AVStorage;
import ohos.rpc.MessageParcel;
import ohos.utils.net.Uri;
import ohos.utils.PacMap;

import java.io.*;
import java.util.List;

public class DataBaseAbility extends  Ability {
    private  int wScreen,hScreen;
    private int imgRequestCode=1123;
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0xD00201, "Data_Log");

    private static final String DATABASE_NAME = "dalangDatabase";
    private static final String DATABASE_NAME_FILE = "dalangDatabase.db";

    private DatabaseHelper helper = new DatabaseHelper(this);
    public OrmContext connect = null;
    private CDatabaseUtils dbTools=new CDatabaseUtils();
    //pic file name
    private  static String openFileName;
    byte dataFile[]=null;
    int nFileSize;


    public void InsertPic(CDataPics pic)
    {
        connect = helper.getOrmContext(DATABASE_NAME,DATABASE_NAME_FILE, CDataBaseDalang.class);
        dbTools.insertPic(pic,connect);
    }
    public  List<CDataPics> GetAllPics(int idCategory)
    {
        connect = helper.getOrmContext(DATABASE_NAME,DATABASE_NAME_FILE, CDataBaseDalang.class);
        List<CDataPics> listData=dbTools.queryPic(connect,idCategory);
        return  listData;
    }
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        ////database
        connect = helper.getOrmContext(DATABASE_NAME,DATABASE_NAME_FILE, CDataBaseDalang.class);
        if (connect==null)
        {
            //create the DB
            StoreConfig dbConfig=StoreConfig.newDefaultConfig(DATABASE_NAME_FILE);
            RdbOpenCallback dbCallBack=new RdbOpenCallback() {
                @Override
                public void onCreate(RdbStore rdbStore) {
                    rdbStore.executeSql("CREATE TABLE IF NOT EXISTS picsTbl (idpic INTEGER PRIMARY KEY AUTOINCREMENT, idcategory INTEGER, name TEXT, size INTEGER, data BLOB) ");
                    rdbStore.executeSql("CREATE TABLE IF NOT EXISTS categoryTbl (id INTEGER PRIMARY KEY AUTOINCREMENT,  name TEXT) ");
                }
                @Override
                public void onUpgrade(RdbStore rdbStore, int iOld, int iNew) {
                }
            };

        }


//        DatabaseHelper dbHelper=new DatabaseHelper(getContext());
//        RdbStore rdbStore=dbHelper.getRdbStore(dbConfig,1,dbCallBack);
//        ValuesBucket dbRecord=new ValuesBucket();
//        dbRecord.putInteger("idClass",1);
//        dbRecord.putString("filename","img.png");
//        rdbStore.insert("dalang",dbRecord);
        //
       // DatabaseHelper manager = new DatabaseHelper(this);
       // ormContext = manager.getOrmContext(DATABASE_NAME_ALIAS, DATABASE_NAME, ValuesBucket.class);

    }


    @Override
    public FileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        AddAlbumPic(1);
        if(false)
        {
            // 创建messageParcel
            MessageParcel messageParcel = MessageParcel.obtain();
            File file = new File(uri.getDecodedPathList().get(0)); // get(0)是获取URI完整字段中查询参数字段。
            if (mode == null || !"rw".equals(mode)) {
                file.setReadOnly();
            }
            FileInputStream fileIs = new FileInputStream(file);
            FileDescriptor fd = null;
            try {
                fd = fileIs.getFD();
            } catch (IOException e) {
                HiLog.info(LABEL_LOG, "failed to getFD");
            }
            int len= (int) file.length();
            // 绑定文件描述符
            return messageParcel.dupFileDescriptor(fd);
        }
        return  null;
     }

    public void  ReadFileData(Uri uri) throws IOException {
        File file = new File(uri.getDecodedPathList().get(0)); // get(0)是获取URI完整字段中查询参数字段。
         file.setReadOnly();
        openFileName=file.getName();
        nFileSize=(int)file.length();
        if(nFileSize>0) {
            FileInputStream fileIs = new FileInputStream(file);
            dataFile = new byte[nFileSize];
            fileIs.read(dataFile,0,nFileSize);
        }
    }

    public  void  SetParamData(int w,int h)
    {
       wScreen=w;
        hScreen=h;
    }

    public void AddAlbumPic(int idCategory) {
        //read picture of album
        openFileName="";
        //int imgRequestCode=1123;
        Intent intent = new Intent();
        Operation opt=new Intent.OperationBuilder().withAction("android.intent.action.GET_CONTENT").build();
        intent.setOperation(opt);
        intent.addFlags(Intent.FLAG_NOT_OHOS_COMPONENT);
        intent.setType("image/*");
        startAbilityForResult(intent, imgRequestCode);
        //
        if (openFileName.length()>0)
        {

        }
    }

    /**
     * 选择系统相册图片
     */
    public void pickFromGallery() {
        DataAbilityHelper helper = DataAbilityHelper.creator(this);
        try {
            // columns为null则查询记录所有字段，当前例子表示查询id字段
            ResultSet resultSet = helper.query(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI, new String[]{AVStorage.Images.Media.ID},
                    null);
            while (resultSet != null && resultSet.goToNextRow()) {
                PixelMap pixelMap = null;
                ImageSource imageSource = null;
                Image image = new Image(this);
                image.setWidth(wScreen);
                image.setHeight(hScreen);
                image.setScaleMode(Image.ScaleMode.CLIP_CENTER);
                // 获取id字段的值
                int id = resultSet.getInt(resultSet.getColumnIndexForName(AVStorage.Images.Media.ID));
                Uri uri = Uri.appendEncodedPathToUri(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI, String.valueOf(id));
                FileDescriptor fd = helper.openFile(uri, "r");
                ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
                try {
                    imageSource = ImageSource.create(fd, null);
                    pixelMap = imageSource.createPixelmap(null);
                    int height = pixelMap.getImageInfo().size.height;
                    int width = pixelMap.getImageInfo().size.width;
                    float sampleFactor = Math.max(height /250f, width/250f);
                    decodingOptions.desiredSize = new Size((int) (width/sampleFactor), (int)(height/sampleFactor));
                    pixelMap = imageSource.createPixelmap(decodingOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (imageSource != null) {
                        imageSource.release();
                    }
                }
            }
        } catch (DataAbilityRemoteException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onAbilityResult(int requestCode, int resultCode, Intent resultData) {
        if(requestCode==imgRequestCode)
        {
            //选择的Img对应的Uri
            String chooseImgUri=resultData.getUriString();
            //定义数据能力帮助对象
            DataAbilityHelper helper=DataAbilityHelper.creator(getContext());
            //定义图片来源对象
            ImageSource imageSource = null;
            //获取选择的Img对应的Id
            String chooseImgId=null;
            //如果是选择文件则getUriString结果为content://com.android.providers.media.documents/document/image%3A30，其中%3A是":"的URL编码结果，后面的数字就是image对应的Id
            //如果选择的是图库则getUriString结果为content://media/external/images/media/30，最后就是image对应的Id
            //这里需要判断是选择了文件还是图库
            if(chooseImgUri.lastIndexOf("%3A")!=-1){
                chooseImgId = chooseImgUri.substring(chooseImgUri.lastIndexOf("%3A")+3);
            }
            else {
                chooseImgId = chooseImgUri.substring(chooseImgUri.lastIndexOf('/')+1);
            }
            //获取图片对应的uri，由于获取到的前缀是content，我们替换成对应的dataability前缀
            Uri uri=Uri.appendEncodedPathToUri(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI,chooseImgId);
            int nsize;
            try {
                ReadFileData(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

//            try {
//                //读取图片
//                FileDescriptor fd = helper.openFile(uri, "r");
//                imageSource = ImageSource.create(fd, null);
//                //创建位图
//                PixelMap pixelMap = imageSource.createPixelmap(null);
//                //设置图片控件对应的位图
//                pixelMap.get
//               // image.setPixelMap(pixelMap);
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (imageSource != null) {
//                    imageSource.release();
//                }
//            }
        }
    }

    @Override
    public ResultSet query(Uri uri, String[] columns, DataAbilityPredicates predicates) {
        return null;
    }

    @Override
    public int insert(Uri uri, ValuesBucket value) {
        HiLog.info(LABEL_LOG, "DataBaseAbility insert");
        return 999;
    }

    @Override
    public int delete(Uri uri, DataAbilityPredicates predicates) {
        return 0;
    }

    @Override
    public int update(Uri uri, ValuesBucket value, DataAbilityPredicates predicates) {
        return 0;
    }

    @Override
    public String[] getFileTypes(Uri uri, String mimeTypeFilter) {
        return new String[0];
    }

    @Override
    public PacMap call(String method, String arg, PacMap extras) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}