package com.uestc.dalangapp.slice;


import com.uestc.dalangapp.ResourceTable;
import com.uestc.dalangapp.model.Item;
import com.uestc.dalangapp.provider.ImagePageSliderProvider;
import com.uestc.dalangapp.provider.TablePageSliderProvider;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.*;
import ohos.agp.components.element.Element;
import ohos.agp.database.DataSetSubscriber;
import ohos.agp.utils.DimensFloat;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.utils.Point;
import ohos.agp.window.dialog.CommonDialog;
import ohos.agp.window.dialog.ToastDialog;
import ohos.agp.window.service.DisplayAttributes;
import ohos.agp.window.service.DisplayManager;
import ohos.data.rdb.ValuesBucket;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.ImagePacker;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.photokit.metadata.AVStorage;
import ohos.multimodalinput.event.MmiPoint;
import ohos.multimodalinput.event.TouchEvent;
import ohos.utils.net.Uri;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public  class PageCategoryManageAbilitySlice extends AbilitySlice {
    //drag
    private Point point;
    private final ThreadLocal<Point> componentPo = new ThreadLocal<Point>();
    private int dragStartIndex=-1;
    private int dragEndIndex=-1;
    //end drag

    private final int imgRequestCode=1123;
    TabList m_tabList=null;
    List<Integer> layoutFileIds=null;
    int nFileSize;
    int idCategory_pics,index_pic;
    //data
    Map<Integer,List<Image> > listArr=new HashMap<Integer, List<Image>>();
    PageSlider m_pageslider=null;
    TableLayout m_tblLayout=null;
    Map<Integer,List<String>> m_mpCategoryFileName=new HashMap<Integer,List<String>>();

    private void ReadCategoryFileName()
    {
        //save file
        String name="categoryDataConfig.txt";
        String fileName=new File(getFilesDir(),name).getPath();
        m_mpCategoryFileName.clear();
        StringBuilder result = new StringBuilder();
        try {
            File file=new File(fileName);
            if(!file.exists()) return;
            BufferedReader br=new BufferedReader(new FileReader(file));
            String str=null;
            while ((str=br.readLine())!=null)
            {
                if(str.isEmpty()) continue;
                String strUnit[]=str.split(":");
                int idCatory=Integer.parseInt(strUnit[0]);
                String nameArr[]=strUnit[1].split(";");
                List<String> listData=new ArrayList<>();
                for (int i=0;i<nameArr.length;i++)
                {
                    if(nameArr[i].isEmpty()) continue;
                    listData.add(i,nameArr[i]);
                }
                m_mpCategoryFileName.put(idCatory,listData);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void WriteCategoryFileName()
    {
        //save file
        String name="categoryDataConfig.txt";
        String fileName=new File(getFilesDir(),name).getPath();
        StringBuffer str = new StringBuffer();
        try {
            FileWriter fw=new FileWriter(fileName,false);
            Set set=m_mpCategoryFileName.entrySet();
            Iterator iter=set.iterator();
            while (iter.hasNext())
            {
                Map.Entry entry=(Map.Entry)iter.next();
                List<String> list= (List<String>) entry.getValue();
                String nameStr="";
                for (int i=0;i<list.size();i++)
                {
                    nameStr=nameStr+list.get(i)+";";
                }
                str.append(entry.getKey()+":"+nameStr).append('\n');
            }
            fw.write(str.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onStop() {
        listArr.clear();
    }

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_control);
        //
        ReadCategoryFileName();
        //
        m_tabList = (TabList) findComponentById(ResourceTable.Id_tab_list);
        m_pageslider = (PageSlider) findComponentById(ResourceTable.Id_page_slider);
        PageSliderProvider provider =m_pageslider.getProvider();
        if (provider==null) //only one provider
        {
            String[] tabListTags={"安放上品","添寿上品","鲜花装饰","礼仪安放","法事项目","大朗风水","人像雕塑","相关服务","模型展示","exit"};
            for(int i=0;i<tabListTags.length;i++)
            {
                TabList.Tab tab= m_tabList.new Tab(this);
                tab.setText(tabListTags[i]);
                m_tabList.addTab(tab);
            }

            layoutFileIds = new ArrayList<>();
            layoutFileIds.add(ResourceTable.Layout_ability_control_1);
            m_pageslider.setProvider(new TablePageSliderProvider(layoutFileIds,this));
            //
            m_tabList.addTabSelectedListener(new TabList.TabSelectedListener() {
                                                 @Override
                                                 public void onSelected(TabList.Tab tab) {
                                                     idCategory_pics = tab.getPosition();//获取点击索引
                                                     //pageslider.setCurrentPage(idCategory_pics);
                                                     if(idCategory_pics==9) {
                                                         listArr.clear();
                                                         Intent i = new Intent();
                                                         present(new MainAbilitySlice(),i);
                                                     }
                                                     else {
                                                         try {
                                                             ClickedCategoryPics(idCategory_pics);
                                                         } catch (IOException e) {
                                                             e.printStackTrace();

                                                         }
                                                     }
                                                 }
                                                 @Override
                                                 public void onUnselected(TabList.Tab tab) {
                                                 }
                                                 @Override
                                                 public void onReselected(TabList.Tab tab) {

                                                 }
                                             }
            );
            m_tblLayout = (TableLayout) findComponentById(ResourceTable.Id_con1_tablayout);
//            //drag table element
//            m_tblLayout.setDraggedListener(Component.DRAG_HORIZONTAL_VERTICAL, new Component.DraggedListener() {
//                @Override
//                public boolean onDragPreAccept(Component component, int dragDirection) {
//                    return true;
//                }
//                @Override
//                public void onDragDown(Component component, DragInfo dragInfo) {
//
//                }
//
//                @Override
//                public void onDragStart(Component component, DragInfo dragInfo) {
//                    TableLayout tbl=(TableLayout) component;
//                    int idx=tbl.getChildIndex(component);
//
//
//                    //获取拖拽前触摸点位置
//                    point = dragInfo.startPoint;
//                    //获取拖拽前组件在父组件中的左上角起始位置
//                    componentPo.set(new Point(component.getContentPositionX(), component.getContentPositionY()));
//                }
//
//                @Override
//                public void onDragUpdate(Component component, DragInfo dragInfo) {
//                    float xOffset = dragInfo.updatePoint.getPointX() - point.getPointX();
//                    float yOffset = dragInfo.updatePoint.getPointY() - point.getPointY();
//                    //setContentPosition为组件在父组件中的位置
//                    component.setContentPosition(componentPo.get().getPointX() + xOffset, componentPo.get().getPointY() + yOffset);
//                    componentPo.set(new Point(component.getContentPositionX(), component.getContentPositionY()));
//
//                }
//
//                @Override
//                public void onDragEnd(Component component, DragInfo dragInfo) {
//                    dragEndIndex=m_tblLayout.getChildIndex(component);
//                    int nn=0;
//                }
//
//                @Override
//                public void onDragCancel(Component component, DragInfo dragInfo) {
//
//                }
//            });


        }
        m_tabList.selectTabAt(0);
    }
    private void ReadCategoryPics(int idCategory) throws IOException {
        List<Image> listData;
        listData=listArr.get(idCategory);
        if(listData==null){
            List<Image> list=new ArrayList<Image>();
            listArr.put(idCategory,list);
        }
        listData=listArr.get(idCategory);
        //read the idCatgory_idx.png
        List<String> listFileName=m_mpCategoryFileName.get(idCategory);
        if (listFileName==null) return;
        for (int n=0;n<listFileName.size();n++)
        {
            String fileName=listFileName.get(n);
            File file=new File(fileName);
            if(!file.exists()) continue;
            Image image=ReadImageToFile(fileName);
            listData.add(n,image);
        }
    }
    private  void AddNewPicStackLayout(int idx,Image image)
    {
        StackLayout newStack=new StackLayout(m_tblLayout.getContext());
        //add data image
        StackLayout.LayoutConfig config0 = new StackLayout.LayoutConfig(newStack.getLayoutConfig());
        config0.alignment= LayoutAlignment.CENTER|LayoutAlignment.CENTER;
        config0.height=(int)(280*2.5);            config0.width=(int)(448*2.5);
        config0.setMarginRight(20);
        config0.setMarginTop(20);
        newStack.addComponent(image,0,config0);
        //add close image
        Image closeImage=new Image(newStack.getContext());
        closeImage.setPixelMap(ResourceTable.Media_delete);
        closeImage.setHeight(80);
        closeImage.setWidth(80);
        closeImage.setScaleMode(Image.ScaleMode.STRETCH);
        //set layout of close image
        StackLayout.LayoutConfig config1 = new StackLayout.LayoutConfig(newStack.getLayoutConfig());
        config1.alignment= LayoutAlignment.RIGHT|LayoutAlignment.TOP;
        config1.height=80;            config1.width=80;
        config1.setMarginRight(0);
        config1.setMarginTop(10);
        newStack.addComponent(closeImage,1,config1);
        //add listener of close button of data picture
        newStack.setClickedListener(component -> {
            closeImage.setClickedListener(component1 ->
            {
                int idxSel=m_tblLayout.getChildIndex(component);
                //delete file of data
                String name=String.format("%d_%d.png",idCategory_pics,idxSel);
                String fileName=new File(getFilesDir(),name).getPath();
                try {
                    Files.deleteIfExists(Paths.get(fileName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //delete index data of list
                listArr.get(idCategory_pics).remove(idx);
                //delete stack layout of data
                //m_tblLayout.removeComponent((Component)cParent);
                m_tblLayout.removeComponent(component);
                //delete the data of, and save idcatoryConfig.txt file
                List<String> listName=m_mpCategoryFileName.get(idCategory_pics);
                if(listName!=null){
                    listName.remove(idx);
                }
                //save dataconfig file
                WriteCategoryFileName();
            });
            //
        });
        //drag the stack layout
        newStack.setDraggedListener(Component.DRAG_HORIZONTAL, new Component.DraggedListener() {
            @Override
            public boolean onDragPreAccept(Component component, int dragDirection) {
                return true;
            }
            @Override
            public void onDragDown(Component component, DragInfo dragInfo) {

            }

            @Override
            public void onDragStart(Component component, DragInfo dragInfo) {
                //获取拖拽前触摸点位置
                point = dragInfo.startPoint;
                //获取拖拽前组件在父组件中的左上角起始位置
                componentPo.set(new Point(component.getContentPositionX(), component.getContentPositionY()));
                //current index
                dragStartIndex=m_tblLayout.getChildIndex(component);
            }

            @Override
            public void onDragUpdate(Component component, DragInfo dragInfo) {
                float xOffset = dragInfo.updatePoint.getPointX() - point.getPointX();
                float yOffset = dragInfo.updatePoint.getPointY() - point.getPointY();
                //setContentPosition为组件在父组件中的位置
                component.setContentPosition(componentPo.get().getPointX() + xOffset, componentPo.get().getPointY() + yOffset);
                componentPo.set(new Point(component.getContentPositionX(), component.getContentPositionY()));

            }

            @Override
            public void onDragEnd(Component component, DragInfo dragInfo) {
                //move index postion
                Point endPt=dragInfo.downPoint;
                dragEndIndex=m_tblLayout.getChildIndex(component);
                if (dragStartIndex!=dragEndIndex)
                {
                    //refresh
                    int kk=0;
                }
            }

            @Override
            public void onDragCancel(Component component, DragInfo dragInfo) {
//current index
                dragEndIndex=m_tblLayout.getChildIndex(component);
                if (dragStartIndex!=dragEndIndex)
                {
                    //refresh
                    int kk=0;
                    int nn=kk;
                }
            }
        });
        m_tblLayout.addComponent(newStack,idx);
    }
    private  void AddLastAddPicStackLayout(int idx)
    {
        StackLayout newStack=new StackLayout(m_tblLayout.getContext());
        //add back image
        Image bkImage=new Image(newStack.getContext());
        bkImage.setPixelMap(ResourceTable.Media_addBackgroud);
        bkImage.setHeight((int)(280*2.5));
        bkImage.setWidth((int)(448*2.5));
        bkImage.setMarginTop(20);
        bkImage.setScaleMode(Image.ScaleMode.STRETCH);
        //set layout of close image
        StackLayout.LayoutConfig config0 = new StackLayout.LayoutConfig(newStack.getLayoutConfig());
        config0.alignment= LayoutAlignment.CENTER|LayoutAlignment.CENTER;
        config0.height=(int)(280*2.5);            config0.width=(int)(448*2.5);
        config0.setMarginRight(20);
        config0.setMarginTop(20);
        newStack.addComponent(bkImage,0,config0);
        //add add image
        Image addImage=new Image(newStack.getContext());
        addImage.setPixelMap(ResourceTable.Media_add);
        addImage.setHeight(100*3);
        addImage.setWidth(100*3);
        bkImage.setMarginTop(20);
        addImage.setScaleMode(Image.ScaleMode.STRETCH);
        //set layout of close image
        StackLayout.LayoutConfig config1 = new StackLayout.LayoutConfig(newStack.getLayoutConfig());
        config1.alignment= LayoutAlignment.CENTER|LayoutAlignment.CENTER;
        config1.height=80*3;            config1.width=80*3;
        config1.setMarginRight(0);
        config1.setMarginTop(20);
        newStack.addComponent(addImage,1,config1);
        //add listener of close button of data picture
        newStack.setClickedListener(component -> {
            selectPic();
        });
        m_tblLayout.addComponent(newStack,idx);
    }
    private void ClickedCategoryPics(int idCategory) throws IOException {
        List<Image> listData=listArr.get(idCategory);
        if(listData!=null) listData.clear();
        //read the category picture file data
        ReadCategoryPics(idCategory);
        //if(!bTrue) return;
        //table layout
        //m_tblLayout = (TableLayout) findComponentById(ResourceTable.Id_con1_tablayout);
        //remove all data
        while(true)
        {
            Component component=m_tblLayout.getComponentAt(0);
            if(component==null) break;
            component.release();
            m_tblLayout.removeComponentAt(0);
        }
        //m_tblLayout.removeAllComponents();
        //add category data
        int nLast;
        nLast=0;
        listData=listArr.get(idCategory);
        if(listData!=null)
        {
            nLast=listData.size();
            for (int n=0;n<nLast;n++)
            {
                AddNewPicStackLayout(n,listData.get(n));
            }
        }
        //add the addition picture of the table end
        AddLastAddPicStackLayout(nLast);
    }
    private void selectPic() {
        Intent intent = new Intent();
        Operation opt=new Intent.OperationBuilder().withAction("android.intent.action.GET_CONTENT").build();
        intent.setOperation(opt);
        intent.addFlags(Intent.FLAG_NOT_OHOS_COMPONENT);
        intent.setType("image/*");
        startAbilityForResult(intent, imgRequestCode);
    }
    @Override
    protected void onAbilityResult(int requestCode, int resultCode, Intent resultData) {
        if(requestCode==imgRequestCode && resultCode!=0)
        {
            // HiLog.info(label,"选择图片getUriString:"+resultData.getUriString());
//选择的Img对应的Uri
            String chooseImgUri=resultData.getUriString();
            String chosseName=chooseImgUri.substring(chooseImgUri.lastIndexOf('/'));
            // HiLog.info(label,"选择图片getScheme:"+chooseImgUri.substring(chooseImgUri.lastIndexOf('/')));

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
            //HiLog.info(label,"选择图片dataability路径:"+uri.toString());
            try {
//读取图片
                FileDescriptor fd = helper.openFile(uri, "r");
                imageSource = ImageSource.create(fd, null);
//创建位图
                PixelMap pixelMap = imageSource.createPixelmap(null);
                //save image file
                Image image=new Image(getContext());
                image.setPixelMap(pixelMap);
                image.setHeight((int)(280*2.5));
                image.setWidth((int)(448*2.5));
                image.setMarginTop(20);
                image.setScaleMode(Image.ScaleMode.STRETCH);
                image.setCornerRadius(32);
                //add stack table data
                List<Image> listData;
                listData=listArr.get(idCategory_pics);
                int idx=listData.size();
                //delete last stack layout
                m_tblLayout.removeComponentAt(idx);
                AddNewPicStackLayout(idx,image);
                //add image data to list
                Image cloneImage=new Image(getContext());
                cloneImage.setPixelMap(pixelMap);
                cloneImage.setHeight((int)(280*2.5));
                cloneImage.setWidth((int)(448*2.5));
                cloneImage.setMarginTop(20);
                cloneImage.setMarginLeft(20);
                cloneImage.setScaleMode(Image.ScaleMode.STRETCH);
                cloneImage.setCornerRadius(32);
                listData.add(idx,cloneImage);
                //add last add icon
                AddLastAddPicStackLayout(idx+1);
                //save file
                String name=String.format("%d_%d.png",idCategory_pics,idx);
                String fileName=new File(getFilesDir(),name).getPath();
                SaveImageToFile(image,fileName);
                //save idcatoryConfig.txt file
                List<String> listName=m_mpCategoryFileName.get(idCategory_pics);
                if(listName==null){
                    listName=new ArrayList<>();
                    listName.add(idx,fileName);
                    m_mpCategoryFileName.put(idCategory_pics,listName);
                }else{
                    listName.add(idx,fileName);
                }
                WriteCategoryFileName();
                //
            } catch (Exception e) {
//                CommonDialog cd =new CommonDialog(this);
//                cd.setTitleText("读取文件类型错误！！");
//                cd.setContentText("请选择.png,.jpg,.bmp格式的图片文件，图片长宽为16:10.");
//                cd.setAutoClosable(true);

                new ToastDialog(getContext())
                        .setText("请选择.png,.jpg,.bmp格式的图片文件，建议图片长宽为16:10.")
                        .setAlignment(LayoutAlignment.CENTER)
                        .setTransparent(false)
                        .setSize(600,300)
                        .setDuration(8000)
                        .show();

                e.printStackTrace();
            } finally {
                if (imageSource != null) {
                    imageSource.release();
                }
            }
        }
    }
    public Image ByteToPixelMap(byte[] bytes){
        // 用于 ImageSource的 create(bytes,srcOpts)方法
        ImageSource.SourceOptions srcOpts=new ImageSource.SourceOptions();
        //设置图片原格式也可以用 null
        srcOpts.formatHint="image/jpg";
        ImageSource imageSource=ImageSource.create(bytes,srcOpts);
        //通过ImageSource创建 PixelMap文件
        PixelMap pixelMap=imageSource.createPixelmap(null);
        Image image=new Image(getContext());
        image.setPixelMap(pixelMap);
        image.setHeight((int)(280*2.5));
        image.setWidth((int)(448*2.5));
        image.setScaleMode(Image.ScaleMode.STRETCH);
        image.setCornerRadius(64);
        return image;
    }
    //将图片转换成byte[]
    public byte[] GetBytesByImage(Image image) {
        PixelMap pm = image.getPixelMap();
        ImagePacker imagePacker = ImagePacker.create();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImagePacker.PackingOptions packingOptions = new ImagePacker.PackingOptions();
        imagePacker.initializePacking(byteArrayOutputStream, packingOptions);
        imagePacker.addImage(pm);
        imagePacker.finalizePacking();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }
    public void SaveImageToFile(Image image,String fileName)
    {
        byte[] byteData = GetBytesByImage(image);
        try {
            int nFileSize=byteData.length;
            FileOutputStream fileOutputStream=new FileOutputStream(fileName);
            fileOutputStream.write(byteData,0,nFileSize);
            fileOutputStream.close();
            //
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    public Image ReadImageToFile(String fileName) throws IOException {
        File file=new File(fileName);
        if(!file.exists()) return null;
        int nFileSize= (int) file.length();
        FileInputStream fileInputStream=new FileInputStream(fileName);
        if(fileInputStream!=null)
        {
            byte rdata[]=new byte[nFileSize];
            fileInputStream.read(rdata,0,nFileSize);
            fileInputStream.close();
            Image image=ByteToPixelMap(rdata);
            return  image;
        }
        return  null;
    }
}