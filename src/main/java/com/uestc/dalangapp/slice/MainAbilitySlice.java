package com.uestc.dalangapp.slice;

import com.uestc.dalangapp.model.CSaveDataInoFIle;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.data.resultset.ResultSet;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.Size;
import ohos.media.photokit.metadata.AVStorage;
import ohos.utils.Pair;
import ohos.utils.net.Uri;
import org.askerov.dynamicgrid.*;
import com.uestc.dalangapp.DataBaseAbility;
import com.uestc.dalangapp.PicShowPageAbility;
import com.uestc.dalangapp.ResourceTable;
import com.uestc.dalangapp.model.CNetImgDownByGlide;
import com.uestc.dalangapp.model.Page;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class MainAbilitySlice extends AbilitySlice implements Component.ClickedListener {
    Button btnAnFang, btnTianShou,btnXianHua,btnLiyi,btnFaShi,btnFengShui,btnRenXiang,btnFuWu,btnMuXing,btnPlay,btnSetting;
    static DataBaseAbility dbOper=null;
    int imgRequestCode=1123;
    private Object IOException;
    MainAbilitySlice _this=this;
    //picture data
    Map<Integer, List<String>> m_mpCategoryFileName;
    ShowPicsSlice m_showPicSlice=new ShowPicsSlice();
    //
    Map<Integer, List<Page>> m_mpCategoryPages=new HashMap<>();
    //
    Map<Integer, List<GridItemInfo> > m_mpAllData=null;
    //thread pools
    private List<Pair<String,String>> listNetDownInfo=null;
    //
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        //hide the show file name text, now not be used
        Text txt=(Text)findComponentById(ResourceTable.Id_txt_show_down_filename);
        txt.setVisibility(Component.HIDE);
        //back icon to file name
        String fileName = new File(getFilesDir(), "icon_back_image.png").getPath();
        Image bkImg=(Image) findComponentById(ResourceTable.Id_back_icon_img);
        OperToolImagFile.SaveImageToFile(bkImg,fileName,96,96);
        //
        btnAnFang=(Button)findComponentById(ResourceTable.Id_anFang);
        btnTianShou=(Button)findComponentById(ResourceTable.Id_tianShou);
        btnXianHua=(Button)findComponentById(ResourceTable.Id_xianhua);
        btnLiyi=(Button)findComponentById(ResourceTable.Id_liyi);
        btnFaShi=(Button)findComponentById(ResourceTable.Id_fashi);
        btnFengShui=(Button)findComponentById(ResourceTable.Id_fengshui);
        btnRenXiang=(Button)findComponentById(ResourceTable.Id_renxiang);
        btnFuWu=(Button)findComponentById(ResourceTable.Id_fuwu);
        btnMuXing=(Button)findComponentById(ResourceTable.Id_muxing);
       // btnPlay=(Button)findComponentById(ResourceTable.Id_play);
        btnSetting=(Button)findComponentById(ResourceTable.Id_setting);

        btnAnFang.setClickedListener(this);
        btnTianShou.setClickedListener(this);
        btnXianHua.setClickedListener(this);
        btnLiyi.setClickedListener( this);
        btnFaShi.setClickedListener(this);
        btnFengShui.setClickedListener( this);
        btnRenXiang.setClickedListener( this);
        btnFuWu.setClickedListener( this);
        btnMuXing.setClickedListener( this);
      //  btnPlay.setClickedListener( this);
        btnSetting.setClickedListener(this);
        //read category file name
        m_mpAllData=CSaveDataInoFIle.ReadCategoryFileName(this);
        if (m_mpAllData==null) //first run
        {
            try {
                m_mpAllData=CSaveDataInoFIle.FirstReadCategoryFileName(this);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onClick(Component component) {
        int idx=-1;
        if (component == btnAnFang) {
            idx=0;
        }
        else if (component ==btnTianShou ) {
            idx=1;;
        }
        else if (component ==btnXianHua ) {
            idx=2;
        }
        else if (component ==btnLiyi ) {
            idx=3;
        }
        else if (component ==btnFaShi ) {
            idx=4;
        }
        else if (component ==btnFengShui ) {
            idx=5;
        }
        else if (component == btnRenXiang) {
            idx=6;
        }
        else if (component ==btnFuWu ) {
            idx=7;
        }
        else if (component ==btnMuXing ) {
            idx=8;

        }
        else if (component == btnPlay) {
        }
        else if (component == btnSetting) {
            Intent intent = new Intent();
            _this.present(new SettingSlice(), intent);
            return;
        }
        if(idx<0) return;;
        List<GridItemInfo> data=m_mpAllData.get(idx);
        if(data!=null && data.size()>0)
        {
            Intent intent= new Intent();
            PicShowPageAbility slice=new PicShowPageAbility();
            slice.SetListPicInfo(data);
            _this.present(slice, intent);
        }
    }
    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    /**
     * 选择系统相册图片
     */
    private void PickFromGallery() {
        DataAbilityHelper helper = DataAbilityHelper.creator(this);
        try {
            // columns为null则查询记录所有字段，当前例子表示查询id字段
            ResultSet resultSet = helper.query(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI, new String[]{AVStorage.Images.Media.ID},
                    null);
            while (resultSet != null && resultSet.goToNextRow()) {
                PixelMap pixelMap = null;
                ImageSource imageSource = null;
                Image image = new Image(this);
                image.setWidth(2560);
                image.setHeight(1600);
                image.setScaleMode(Image.ScaleMode.CLIP_CENTER);
                // 获取id字段的值
                int id = resultSet.getInt(resultSet.getColumnIndexForName(AVStorage.Images.Media.ID));
                Uri uri = Uri.appendEncodedPathToUri(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI, String.valueOf(id));
                FileDescriptor fd = helper.openFile(uri, "r");
                ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
                try {
                    imageSource = ImageSource.create(fd, null);
                    pixelMap = imageSource.createPixelmap(null);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (imageSource != null) {
                        imageSource.release();
                    }
                }
                image.setPixelMap(pixelMap);
                //save image file
            }
        } catch (FileNotFoundException | DataAbilityRemoteException e) {
            e.printStackTrace();
        }
    }

    //
    private boolean NetDownPicData() throws Exception {
        String keyFileName,valNetUrl,fileName;
        //judge icon file
        keyFileName="judge_down_pic.png";
        fileName=new File(getFilesDir(),keyFileName).getPath();
        File file=new File(fileName);
        if(file.exists()) return false;
        //get judge_down_pic picture
        valNetUrl="https://images.gitee.com/uploads/images/2022/0717/223611_9737fd9f_7752931.png";
        //if(new CNetImgDownByGlide().saveImgToLocal(this,valNetUrl,fileName)) return true;
        new CNetImgDownByGlide().saveImgToLocal(this,valNetUrl,fileName);
        /////down all picture
        int idc,n;
        Map<String,String> mpNameNetImg;
        List<GridItemInfo> dataAnfang,dataTianshou,dataXianhua,dataLiyi,dataFashi,dataFengshui,dataRenxiang,dataFuwu,dataMuxing;
        //
        idc=-1;
        listNetDownInfo=new ArrayList<>();
        mpNameNetImg =new TreeMap<>();
        m_mpAllData=new TreeMap<>();
        dataAnfang=new ArrayList<>();   dataTianshou=new ArrayList<>(); dataXianhua=new ArrayList<>();
        dataLiyi=new ArrayList<>();     dataFashi=new ArrayList<>();    dataFengshui=new ArrayList<>();
        dataRenxiang=new ArrayList<>(); dataFuwu=new ArrayList<>();     dataMuxing=new ArrayList<>();
        //anfang pics
        n=0;
        idc=idc+1;
        mpNameNetImg.clear();
        mpNameNetImg.put("0_01.png","https://images.gitee.com/uploads/images/2022/0717/225805_e1347cee_7752931.png");
        mpNameNetImg.put("0_02.png","https://images.gitee.com/uploads/images/2022/0717/225819_6204f149_7752931.png");
        mpNameNetImg.put("0_03.png","https://images.gitee.com/uploads/images/2022/0717/225852_9bcd41bc_7752931.png");
        mpNameNetImg.put("0_04.png","https://images.gitee.com/uploads/images/2022/0717/225902_a64bf7d3_7752931.png");
        mpNameNetImg.put("0_05.png","https://images.gitee.com/uploads/images/2022/0717/225913_2a3fd798_7752931.png");
        mpNameNetImg.put("0_06.png","https://images.gitee.com/uploads/images/2022/0717/225924_63c7cd9c_7752931.png");
        mpNameNetImg.put("0_07.png","https://images.gitee.com/uploads/images/2022/0717/225935_ce882c67_7752931.png");
        mpNameNetImg.put("0_08.png","https://images.gitee.com/uploads/images/2022/0717/225946_7da9ddfb_7752931.png");
        mpNameNetImg.put("0_09.png","https://images.gitee.com/uploads/images/2022/0717/225958_5141ab52_7752931.png");
        mpNameNetImg.put("0_10.png","https://images.gitee.com/uploads/images/2022/0717/230129_f495bc66_7752931.png");
        for(Map.Entry<String,String> entry:mpNameNetImg.entrySet())
        {
            keyFileName=(String) entry.getKey();
            valNetUrl=(String) entry.getValue();
            fileName=new File(getFilesDir(),keyFileName).getPath();
            GridItemInfo data=new GridItemInfo(fileName,n,n);
            dataAnfang.add(data);
            ++n;
            Pair<String,String> pairData=new Pair<>(fileName,valNetUrl);
            listNetDownInfo.add(pairData);
            Thread.sleep(100);
            new CNetImgDownByGlide().saveImgToLocal(this,valNetUrl,fileName);
        }
        m_mpAllData.put(idc,dataAnfang);
        //tianshou pics
        idc=idc+1;
        n=0;
        mpNameNetImg.clear();
        mpNameNetImg.put("1_01.png","https://images.gitee.com/uploads/images/2022/0718/014548_c7e967f2_7752931.png");
        mpNameNetImg.put("1_02.png","https://images.gitee.com/uploads/images/2022/0718/014721_7568dfd5_7752931.png");
        mpNameNetImg.put("1_03.png","https://images.gitee.com/uploads/images/2022/0718/014738_3a86238f_7752931.png");
        mpNameNetImg.put("1_04.png","https://images.gitee.com/uploads/images/2022/0718/014748_05f1d0f5_7752931.png");
        mpNameNetImg.put("1_05.png","https://images.gitee.com/uploads/images/2022/0718/014759_04862a9b_7752931.png");
        mpNameNetImg.put("1_06.png","https://images.gitee.com/uploads/images/2022/0718/014811_627b2759_7752931.png");
        for(Map.Entry<String,String> entry:mpNameNetImg.entrySet())
        {
            keyFileName=(String) entry.getKey();
            valNetUrl=(String) entry.getValue();
            fileName=new File(getFilesDir(),keyFileName).getPath();
            GridItemInfo data=new GridItemInfo(fileName,n,n);
            dataTianshou.add(data);
            ++n;
            Pair<String,String> pairData=new Pair<>(fileName,valNetUrl);
            listNetDownInfo.add(pairData);
            Thread.sleep(100);
            new CNetImgDownByGlide().saveImgToLocal(this,valNetUrl,fileName);
        }
        m_mpAllData.put(idc,dataTianshou);
        //xianhua pics
        idc=idc+1;
        n=0;
        mpNameNetImg.clear();
        mpNameNetImg.put("2_01.png","https://images.gitee.com/uploads/images/2022/0718/014948_42c7d08a_7752931.png");
        mpNameNetImg.put("2_02.png","https://images.gitee.com/uploads/images/2022/0718/014959_59400be5_7752931.png");
        mpNameNetImg.put("2_03.png","https://images.gitee.com/uploads/images/2022/0718/015020_2bc493c3_7752931.png");
        mpNameNetImg.put("2_04.png","https://images.gitee.com/uploads/images/2022/0718/015030_040a62b3_7752931.png");
        mpNameNetImg.put("2_05.png","(https://images.gitee.com/uploads/images/2022/0718/015051_e1111ce8_7752931.png");
        mpNameNetImg.put("2_06.png","https://images.gitee.com/uploads/images/2022/0718/015103_1fb4673e_7752931.png");
        mpNameNetImg.put("2_07.png","https://images.gitee.com/uploads/images/2022/0718/015120_9747814b_7752931.png");
        mpNameNetImg.put("2_08.png","https://images.gitee.com/uploads/images/2022/0718/015135_fde8707c_7752931.png");
        mpNameNetImg.put("2_09.png","(https://images.gitee.com/uploads/images/2022/0718/015213_7930d2d4_7752931.png");
        mpNameNetImg.put("2_10.png","https://images.gitee.com/uploads/images/2022/0718/015227_4e64f596_7752931.png");
        mpNameNetImg.put("2_11.png","https://images.gitee.com/uploads/images/2022/0718/015243_91da3745_7752931.png");
        mpNameNetImg.put("2_12.png","https://images.gitee.com/uploads/images/2022/0718/015256_b8e69614_7752931.png");
        mpNameNetImg.put("2_13.png","https://images.gitee.com/uploads/images/2022/0718/015312_9613ecd6_7752931.png");
        mpNameNetImg.put("2_14.png","https://images.gitee.com/uploads/images/2022/0718/015324_595a8644_7752931.png");
        mpNameNetImg.put("2_15.png","https://images.gitee.com/uploads/images/2022/0718/015337_715d5f7f_7752931.png");
        mpNameNetImg.put("2_16.png","https://images.gitee.com/uploads/images/2022/0718/015348_511502ed_7752931.png");
        mpNameNetImg.put("2_17.png","https://images.gitee.com/uploads/images/2022/0718/015359_67e2ce66_7752931.png");
        for(Map.Entry<String,String> entry:mpNameNetImg.entrySet())
        {
            keyFileName=(String) entry.getKey();
            valNetUrl=(String) entry.getValue();
            fileName=new File(getFilesDir(),keyFileName).getPath();
            GridItemInfo data=new GridItemInfo(fileName,n,n);
            dataXianhua.add(data);
            ++n;
            Pair<String,String> pairData=new Pair<>(fileName,valNetUrl);
            listNetDownInfo.add(pairData);
            Thread.sleep(100);
            new CNetImgDownByGlide().saveImgToLocal(this,valNetUrl,fileName);
        }
        m_mpAllData.put(idc,dataXianhua);
        //liyi pics
        idc=idc+1;
        n=0;
        mpNameNetImg.clear();
        mpNameNetImg.put("3_01.png","https://i.postimg.cc/2yYzyzC8/liyilinian.png");
        mpNameNetImg.put("3_02.png","https://i.postimg.cc/4dkG2J7b/liyiliucheng1.png");
        mpNameNetImg.put("3_03.png","https://i.postimg.cc/Bv1sC86n/liyiliucheng2.png");
        mpNameNetImg.put("3_04.png","https://i.postimg.cc/PfbXc8ML/liyiliucheng3.png");
        mpNameNetImg.put("3_05.png","https://i.postimg.cc/HsBpWDRd/liyiliucheng4.png");
        mpNameNetImg.put("3_06.png","https://i.postimg.cc/DwNnsQ7N/liyiliucheng5.png");
        mpNameNetImg.put("3_07.png","https://i.postimg.cc/YC0thDBg/liyiliucheng6.png");
        mpNameNetImg.put("3_08.png","https://i.postimg.cc/hvMqGTCp/liyiliucheng7.png");
        mpNameNetImg.put("3_09.png","https://i.postimg.cc/3JJTcfR6/liyiliucheng8.png");
        mpNameNetImg.put("3_10.png","https://i.postimg.cc/3JST5j0w/liyimulu2.png");
        mpNameNetImg.put("3_21.png","https://i.postimg.cc/yN94ZwQJ/liyixiaogou2.png");
        mpNameNetImg.put("3_27.png","https://i.postimg.cc/8PC2HFXg/liyixiaogou4.png");
        mpNameNetImg.put("3_28.png","https://i.postimg.cc/Kj0dbFzW/liyixiaogou5.png");
        mpNameNetImg.put("3_29.png","https://i.postimg.cc/sD0t0VbQ/liyixiaogou6.png");
        mpNameNetImg.put("3_30.png","https://i.postimg.cc/FHMwNTrh/liyixiaogou7.png");
        mpNameNetImg.put("3_31.png","https://i.postimg.cc/85HYdDhC/liyixiaogou8.png");
        mpNameNetImg.put("3_32.png","https://i.postimg.cc/L5Q07TQK/liyixiaogou9.png");
        mpNameNetImg.put("3_11.png","https://i.postimg.cc/X7XP5RXV/liyixiaogou10.png");
        mpNameNetImg.put("3_12.png","https://i.postimg.cc/0ywHW7pF/liyixiaogou11.png");
        mpNameNetImg.put("3_13.png","https://i.postimg.cc/qqFjQ7gp/liyixiaogou12.png");
        mpNameNetImg.put("3_14.png","https://i.postimg.cc/k4n1gZkP/liyixiaogou13.png");
        mpNameNetImg.put("3_15.png","https://i.postimg.cc/nz0dj2Jj/liyixiaogou14.png");
        mpNameNetImg.put("3_16.png","https://i.postimg.cc/tRvS9Bk8/liyixiaogou15.png");
        mpNameNetImg.put("3_17.png","https://i.postimg.cc/htTpbcnq/liyixiaogou16.png");
        mpNameNetImg.put("3_18.png","https://i.postimg.cc/N0xDSDLJ/liyixiaogou17.png");
        mpNameNetImg.put("3_19.png","https://i.postimg.cc/xdQg6hjN/liyixiaogou18.png");
        mpNameNetImg.put("3_20.png","https://i.postimg.cc/rsD9dLj3/liyixiaogou19.png");
        mpNameNetImg.put("3_22.png","https://i.postimg.cc/kMHc0BXm/liyixiaogou20.png");
        mpNameNetImg.put("3_23.png","https://i.postimg.cc/tg9t36Dh/liyixiaogou21.png");
        mpNameNetImg.put("3_24.png","https://i.postimg.cc/BnTBPp5S/liyixiaogou22.png");
        mpNameNetImg.put("3_25.png","https://i.postimg.cc/wBF50Bbn/liyixiaogou23.png");
        mpNameNetImg.put("3_26.png","https://i.postimg.cc/C5njhsLV/liyixiaogou24.png");
        mpNameNetImg.put("3_34.png","https://i.postimg.cc/NMmsQyqz/liyifeiyong.png");
        for(Map.Entry<String,String> entry:mpNameNetImg.entrySet())
        {
            keyFileName=(String) entry.getKey();
            valNetUrl=(String) entry.getValue();
            fileName=new File(getFilesDir(),keyFileName).getPath();
            //
            GridItemInfo data=new GridItemInfo(fileName,n,n);
            dataLiyi.add(data);
            ++n;
            Pair<String,String> pairData=new Pair<>(fileName,valNetUrl);
            listNetDownInfo.add(pairData);
            Thread.sleep(100);
            new CNetImgDownByGlide().saveImgToLocal(this,valNetUrl,fileName);
        }
        m_mpAllData.put(idc,dataLiyi);
        //fashi pics
        idc=idc+1;
        n=0;
        mpNameNetImg.clear();
        mpNameNetImg.put("4_01.png","https://i.postimg.cc/L8Kj4058/01-1-1.png");
        mpNameNetImg.put("4_02.png","https://i.postimg.cc/5tpz343X/02-1-2.png");
        mpNameNetImg.put("4_03.png","https://i.postimg.cc/mkP7kv7t/11-1.png");
        mpNameNetImg.put("4_04.png","https://i.postimg.cc/Znnx3BzY/12-2.png");
        mpNameNetImg.put("4_05.png","https://i.postimg.cc/QCzkq8jQ/13-3.png");
        mpNameNetImg.put("4_06.png","https://i.postimg.cc/15Lrn3CP/14-4.png");
        mpNameNetImg.put("4_07.png","https://i.postimg.cc/rmnS8V0B/15-5.png");
        mpNameNetImg.put("4_08.png","https://i.postimg.cc/WbKXGn4B/16-6.png");
        mpNameNetImg.put("4_09.png","https://i.postimg.cc/nVSkxkqX/17-6-1.png");
        mpNameNetImg.put("4_10.png","https://i.postimg.cc/QCd0bY13/18-6-2.png");
        mpNameNetImg.put("4_11.png","https://i.postimg.cc/SKG6v7yN/19-1-7-1.png");
        mpNameNetImg.put("4_12.png","https://i.postimg.cc/28qxSZx2/19-2-7-2.png");
        mpNameNetImg.put("4_13.png","https://i.postimg.cc/sgQHqL8f/19-3-7-3.png");
        mpNameNetImg.put("4_14.png","https://i.postimg.cc/KjpQs7Dg/19-4-9.png");
        mpNameNetImg.put("4_15.png","https://i.postimg.cc/13qMJqnf/21-1.png");
        mpNameNetImg.put("4_16.png","https://i.postimg.cc/Y93dQB4R/31-1.png");
        mpNameNetImg.put("4_17.png","https://i.postimg.cc/50x34KPC/32-2.png");
        mpNameNetImg.put("4_18.png","https://i.postimg.cc/gJNKny5B/41-1.png");
        mpNameNetImg.put("4_19.png","https://i.postimg.cc/RVqgJ8v1/51-1.png");
        mpNameNetImg.put("4_20.png","https://i.postimg.cc/pTXfbhTQ/61-1.png");
        mpNameNetImg.put("4_21.png","https://i.postimg.cc/jSp3bX0H/71-1.png");
        mpNameNetImg.put("4_22.png","https://i.postimg.cc/Z5pFrz2N/72-2.png");
        mpNameNetImg.put("4_23.png","https://i.postimg.cc/Gpv7hydZ/81.png");
        mpNameNetImg.put("4_24.png","https://i.postimg.cc/1XbwqvKM/91-1.png");
        mpNameNetImg.put("4_25.png","https://i.postimg.cc/W4srgFMR/fashimulu3.png");
        for(Map.Entry<String,String> entry:mpNameNetImg.entrySet())
        {
            keyFileName=(String) entry.getKey();
            valNetUrl=(String) entry.getValue();
            fileName=new File(getFilesDir(),keyFileName).getPath();
            GridItemInfo data=new GridItemInfo(fileName,n,n);
            dataFashi.add(data);
            ++n;
            Pair<String,String> pairData=new Pair<>(fileName,valNetUrl);
            listNetDownInfo.add(pairData);
            Thread.sleep(100);
            new CNetImgDownByGlide().saveImgToLocal(this,valNetUrl,fileName);
        }
        m_mpAllData.put(idc,dataFashi);
        //fengshui pics
        idc=idc+1;
        n=0;
        mpNameNetImg.clear();
        mpNameNetImg.put("5_01.png","https://i.postimg.cc/gkB95Qzr/fengshui1.png");
        mpNameNetImg.put("5_02.png","https://i.postimg.cc/g08fx0v6/fengshui2.png");
        mpNameNetImg.put("5_03.png","https://i.postimg.cc/T3Zz2bW6/fengshui3.png");
        mpNameNetImg.put("5_04.png","https://i.postimg.cc/2SbpfGyG/fengshui4.png");
        for(Map.Entry<String,String> entry:mpNameNetImg.entrySet())
        {
            keyFileName=(String) entry.getKey();
            valNetUrl=(String) entry.getValue();
            fileName=new File(getFilesDir(),keyFileName).getPath();
            GridItemInfo data=new GridItemInfo(fileName,n,n);
            dataFengshui.add(data);
            ++n;
            Pair<String,String> pairData=new Pair<>(fileName,valNetUrl);
            listNetDownInfo.add(pairData);
            Thread.sleep(100);
            new CNetImgDownByGlide().saveImgToLocal(this,valNetUrl,fileName);
        }
        m_mpAllData.put(idc,dataFengshui);
        //renxiang pics
        idc=idc+1;
        n=0;
        mpNameNetImg.clear();
        mpNameNetImg.put("6_00.png","https://i.postimg.cc/t4td88PW/renxiangmulu.png");
        mpNameNetImg.put("6_01.png","https://i.postimg.cc/mkHMs0xR/renxiang1.png");
        mpNameNetImg.put("6_02.png","https://i.postimg.cc/BtfFQMsF/renxiang2.png");
        mpNameNetImg.put("6_03.png","https://i.postimg.cc/qBzLgbVH/renxiang3.png");
        mpNameNetImg.put("6_04.png","https://i.postimg.cc/qRYnWp5j/renxiang4.png");
        mpNameNetImg.put("6_05.png","https://i.postimg.cc/9fmG789t/renxiang5.png");
        mpNameNetImg.put("6_06.png","https://i.postimg.cc/Sx76jFnx/renxiang6.png");
        mpNameNetImg.put("6_07.png","https://i.postimg.cc/6QMCLrLs/renxiang7.png");
        mpNameNetImg.put("6_08.png","https://i.postimg.cc/JntZTsPs/renxiang8.png");
        mpNameNetImg.put("6_09.png","https://i.postimg.cc/qMKxxNvm/renxiang9.png");
        mpNameNetImg.put("6_10.png","https://i.postimg.cc/13ZcCygy/renxiang10.png");
        mpNameNetImg.put("6_11.png","https://i.postimg.cc/d0sRFnmX/renxiang11.png");
        for(Map.Entry<String,String> entry:mpNameNetImg.entrySet())
        {
            keyFileName=(String) entry.getKey();
            valNetUrl=(String) entry.getValue();
            fileName=new File(getFilesDir(),keyFileName).getPath();
            GridItemInfo data=new GridItemInfo(fileName,n,n);
            dataRenxiang.add(data);
            ++n;
            Pair<String,String> pairData=new Pair<>(fileName,valNetUrl);
            listNetDownInfo.add(pairData);
            Thread.sleep(100);
            new CNetImgDownByGlide().saveImgToLocal(this,valNetUrl,fileName);
        }
        m_mpAllData.put(idc,dataRenxiang);
        //fuwu pics
        idc=idc+1;
        n=0;
        mpNameNetImg.clear();
        mpNameNetImg.put("7_28.png","https://i.postimg.cc/x1D7YrZd/fuwucontent.png");
        mpNameNetImg.put("7_27.png","https://i.postimg.cc/vBFLxMW1/fuwu-muzhi1.png");
        mpNameNetImg.put("7_01.png","https://i.postimg.cc/RFFpGvWg/fuwu-daike0.png");
        mpNameNetImg.put("7_02.png","https://i.postimg.cc/Jz8LxBMK/fuwu-daike1.png");
        mpNameNetImg.put("7_03.png","https://i.postimg.cc/BZds8Vzr/fuwu-daike2.png");
        mpNameNetImg.put("7_04.png","https://i.postimg.cc/FHxQBsKv/fuwu-daike3.png");
        mpNameNetImg.put("7_05.png","https://i.postimg.cc/LXJK5t34/fuwu-fanxin1.png");
        mpNameNetImg.put("7_06.png","https://i.postimg.cc/RC34ZBg1/fuwu-lingtang1.png");
        mpNameNetImg.put("7_07.png","https://i.postimg.cc/bNkP1Rcs/fuwu-liyi1.png");
        mpNameNetImg.put("7_08.png","https://i.postimg.cc/6qZkLBPD/fuwu-liyi2.png");
        mpNameNetImg.put("7_09.png","https://i.postimg.cc/vHrR4Hfn/fuwu-liyi3.png");
        mpNameNetImg.put("7_10.png","https://i.postimg.cc/k55LYQZN/fuwu-liyi4.png");
        mpNameNetImg.put("7_11.png","https://i.postimg.cc/MH1LfdMw/fuwu-liyi5.png");
        mpNameNetImg.put("7_12.png","https://i.postimg.cc/XN5mP72P/fuwu-liyijibai0.png");
        mpNameNetImg.put("7_13.png","https://i.postimg.cc/J4WvCVvz/fuwu-liyijibai1.png");
        mpNameNetImg.put("7_14.png","https://i.postimg.cc/HLjP9pcB/fuwu-liyijibai2.png");
        mpNameNetImg.put("7_15.png","https://i.postimg.cc/QtT4v0q7/fuwu-liyijibai3.png");
        mpNameNetImg.put("7_16.png","https://i.postimg.cc/vT7PZTkH/fuwu-liyijibai4.png");
        mpNameNetImg.put("7_17.png","https://i.postimg.cc/KvFqs73K/fuwu-liyijibai5.png");
        mpNameNetImg.put("7_18.png","https://i.postimg.cc/pVKcZcrb/fuwu-liyijibai6.png");
        mpNameNetImg.put("7_19.png","https://i.postimg.cc/6ppHB90t/fuwu-mu1.png");
        mpNameNetImg.put("7_20.png","https://i.postimg.cc/DzWBbBHN/fuwu-mu2.png");
        mpNameNetImg.put("7_21.png","https://i.postimg.cc/C50NXY5y/fuwu-mu3.png");
        mpNameNetImg.put("7_22.png","https://i.postimg.cc/D0cgKD4S/fuwu-mu4.png");
        mpNameNetImg.put("7_23.png","https://i.postimg.cc/Qx0b42rP/fuwu-mu5.png");
        mpNameNetImg.put("7_24.png","https://i.postimg.cc/Fs9px9w3/fuwu-mu6.png");
        mpNameNetImg.put("7_25.png","https://i.postimg.cc/7ZhNpKHL/fuwu-mu7.png");
        mpNameNetImg.put("7_26.png","https://i.postimg.cc/rptNpztX/fuwu-mu8.png");
        for(Map.Entry<String,String> entry:mpNameNetImg.entrySet())
        {
            keyFileName=(String) entry.getKey();
            valNetUrl=(String) entry.getValue();
            fileName=new File(getFilesDir(),keyFileName).getPath();
            GridItemInfo data=new GridItemInfo(fileName,n,n);
            dataFuwu.add(data);
            ++n;
            Pair<String,String> pairData=new Pair<>(fileName,valNetUrl);
            listNetDownInfo.add(pairData);
            Thread.sleep(100);
            new CNetImgDownByGlide().saveImgToLocal(this,valNetUrl,fileName);
        }
        m_mpAllData.put(idc,dataFuwu);
        //muxing pics
        idc=idc+1;
        n=0;
        mpNameNetImg.clear();
        mpNameNetImg.put("8_34.png","https://i.postimg.cc/bJHKrqZg/muxingmulu.png");
        mpNameNetImg.put("8_01.png","https://i.postimg.cc/5tmHmFTM/muxing1.png");
        mpNameNetImg.put("8_12.png","https://i.postimg.cc/YCKhK3wH/muxing2.png");
        mpNameNetImg.put("8_23.png","https://i.postimg.cc/SsTJBbmT/muxing3.png");
        mpNameNetImg.put("8_28.png","https://i.postimg.cc/SRnJq1sy/muxing4.png");
        mpNameNetImg.put("8_29.png","https://i.postimg.cc/TYWp4sF0/muxing5.png");
        mpNameNetImg.put("8_30.png","https://i.postimg.cc/J7FsK86t/muxing6.png");
        mpNameNetImg.put("8_31.png","https://i.postimg.cc/Kzv1p8h3/muxing7.png");
        mpNameNetImg.put("8_32.png","https://i.postimg.cc/x8BqdGJL/muxing8.png");
        mpNameNetImg.put("8_33.png","https://i.postimg.cc/65RCvTWB/muxing9.png");
        mpNameNetImg.put("8_02.png","https://i.postimg.cc/6Q6Zg0dQ/muxing10.png");
        mpNameNetImg.put("8_03.png","https://i.postimg.cc/P5QZScvc/muxing11.png");
        mpNameNetImg.put("8_04.png","https://i.postimg.cc/h4Qdkh7V/muxing12.png");
        mpNameNetImg.put("8_05.png","https://i.postimg.cc/kXxt5Zs4/muxing13.png");
        mpNameNetImg.put("8_06.png","https://i.postimg.cc/3wwynwT4/muxing14.png");
        mpNameNetImg.put("8_07.png","https://i.postimg.cc/0NHz3qkt/muxing15.png");
        mpNameNetImg.put("8_08.png","https://i.postimg.cc/nzjXfwMn/muxing16.png");
        mpNameNetImg.put("8_09.png","https://i.postimg.cc/4NTmjPGT/muxing17.png");
        mpNameNetImg.put("8_10.png","https://i.postimg.cc/gkqnphcL/muxing18.png");
        mpNameNetImg.put("8_11.png","https://i.postimg.cc/K8G48Y4q/muxing19.png");
        mpNameNetImg.put("8_13.png","https://i.postimg.cc/rpzm5Y15/muxing20.png");
        mpNameNetImg.put("8_14.png","https://i.postimg.cc/FRLHBdnR/muxing21.png");
        mpNameNetImg.put("8_15.png","https://i.postimg.cc/jdXdmrvz/muxing22.png");
        mpNameNetImg.put("8_16.png","https://i.postimg.cc/FHHsQnnw/muxing23.png");
        mpNameNetImg.put("8_17.png","https://i.postimg.cc/4x244bDy/muxing24.png");
        mpNameNetImg.put("8_18.png","https://i.postimg.cc/qBck6dP9/muxing25.png");
        mpNameNetImg.put("8_19.png","https://i.postimg.cc/L85RWgJp/muxing26.png");
        mpNameNetImg.put("8_20.png","https://i.postimg.cc/J41Rb2mt/muxing27.png");
        mpNameNetImg.put("8_21.png","https://i.postimg.cc/25BYkpd9/muxing28.png");
        mpNameNetImg.put("8_22.png","https://i.postimg.cc/HxwCMQhQ/muxing29.png");
        mpNameNetImg.put("8_24.png","https://i.postimg.cc/5NNMHQqC/muxing30.png");
        mpNameNetImg.put("8_25.png","https://i.postimg.cc/x1XD5rZj/muxing31.png");
        mpNameNetImg.put("8_26.png","https://i.postimg.cc/TY7vn2CH/muxing32.png");
        mpNameNetImg.put("8_27.png","https://i.postimg.cc/wMwZ5Fcs/muxing33.png");

        for(Map.Entry<String,String> entry:mpNameNetImg.entrySet())
        {
            keyFileName=(String) entry.getKey();
            valNetUrl=(String) entry.getValue();
            fileName=new File(getFilesDir(),keyFileName).getPath();
            GridItemInfo data=new GridItemInfo(fileName,n,n);
            dataMuxing.add(data);
            ++n;
            Pair<String,String> pairData=new Pair<>(fileName,valNetUrl);
            listNetDownInfo.add(pairData);
            Thread.sleep(100);
            new CNetImgDownByGlide().saveImgToLocal(this,valNetUrl,fileName);
        }
        m_mpAllData.put(idc,dataMuxing);

        //save the data file
        CSaveDataInoFIle.WriteFile(this,m_mpAllData);
        return true;
    }
}
