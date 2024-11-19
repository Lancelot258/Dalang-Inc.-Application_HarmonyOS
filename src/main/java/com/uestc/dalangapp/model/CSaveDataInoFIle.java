package com.uestc.dalangapp.model;

import ohos.app.Context;
import ohos.global.resource.RawFileEntry;
import ohos.global.resource.Resource;
import org.askerov.dynamicgrid.GridItemInfo;

import java.io.*;
import java.util.*;

public class CSaveDataInoFIle {
    static public void WriteFile(Context context, Map<Integer,List<GridItemInfo> > mpData)
    {
        //save file
        String name="categoryDataConfig.txt";
        String fileName=new File(context.getFilesDir(),name).getPath();
        int idc=0;
        StringBuffer str = new StringBuffer();
        try {
            FileWriter fw=new FileWriter(fileName,false);
            Set set=mpData.entrySet();
            Iterator iter=set.iterator();
            while (iter.hasNext())
            {
                Map.Entry entry=(Map.Entry)iter.next();
                List<GridItemInfo> list= (List<GridItemInfo>) entry.getValue();
                String nameStr="";
                int ns=list.size();
                if(ns<1) continue;
                for (int i=0;i<ns;i++)
                {
                    GridItemInfo data=list.get(i);
                    nameStr=nameStr+data.getIconId()+"#"+data.getIdOrder()+"#"+data.getFileName()+";";
                }
                str.append(entry.getKey()+":"+nameStr).append('\n');
            }
            fw.write(str.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Map<Integer,List<GridItemInfo> > ReadCategoryFileName(Context context)
    {
        String name="categoryDataConfig.txt";
        String fileName=new File(context.getFilesDir(),name).getPath();
        File file=new File(fileName);
        if(!file.exists()) return null;
        //save file
        Map<Integer,List<GridItemInfo>> mpData=new HashMap<>();
        StringBuilder result = new StringBuilder();
        try {

            BufferedReader br=new BufferedReader(new FileReader(file));
            String str=null;
            while ((str=br.readLine())!=null)
            {
                if(str.isEmpty()) continue;
                String strUnit[]=str.split(":");
                int idCatory=Integer.parseInt(strUnit[0]);
                String nameArr[]=strUnit[1].split(";");
                //
                List<GridItemInfo> listData=new ArrayList<>();
                for (int i=0;i<nameArr.length;i++)
                {
                    String strInfo=nameArr[i];
                    if(strInfo.isEmpty()) continue;
                    String dataStr[]=strInfo.split("#");
                    int id=Integer.parseInt(dataStr[0]);
                    int idOrder=Integer.parseInt(dataStr[1]);
                    String picName=dataStr[2];
                    //judge the file name existed
                    File filePic=new File(picName);
                    if(!filePic.exists()) continue;
                    //
                    GridItemInfo data=new GridItemInfo(picName,id,idOrder);
                    listData.add(i,data);
                }
                mpData.put(idCatory,listData);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mpData;
    }
    public static Map<Integer,List<GridItemInfo> > FirstReadCategoryFileName(Context context) throws IOException {
        Map<Integer,List<GridItemInfo>> mpData=new HashMap<>();
        int nArrSize[]={10,6,17,34,25,4,14,28,34};
        //
        for (int idc=0;idc<nArrSize.length;idc++) {
            List<GridItemInfo> listData=new ArrayList<>();
            for (int i = 0; i < nArrSize[idc]; i++) {
                String fileName = String.format("%d_%02d.png",idc, i);
                RawFileEntry fileEntry = context.getResourceManager().getRawFileEntry("resources/rawfile/" + fileName);
                Resource resource = fileEntry.openRawFile();
                int len = resource.available();
                byte[] buffer = new byte[len];
                resource.read(buffer, 0, len);
                //write file
                String newFileName = new File(context.getFilesDir(), fileName).getPath();
                FileOutputStream fileOutputStream = new FileOutputStream(newFileName);
                fileOutputStream.write(buffer, 0, len);
                fileOutputStream.close();
                //
                GridItemInfo data = new GridItemInfo(newFileName, i, i);
                listData.add(data);
            }
            mpData.put(idc, listData);
        }
        //save category data information file
        WriteFile(context,mpData);
        return mpData;
    }
}
