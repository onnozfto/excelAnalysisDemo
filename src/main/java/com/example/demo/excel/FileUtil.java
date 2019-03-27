package com.example.demo.excel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
	
	/**
	 * 拷贝文件
	 * @param srcPathStr
	 * @param desPathStr
	 */
	public static void copyFile(String srcFile, String destFile) {
        
        FileInputStream fis = null;
        FileOutputStream fos = null;
        // 1.
        try{
            //2.创建输入输出流对象
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);                

            //创建搬运工具
            byte datas[] = new byte[1024*8];
            //创建长度
            int len = 0;
            //循环读取数据
            while((len = fis.read(datas))!=-1){
                fos.write(datas,0,len);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
        	//3.释放资源
        	if(fis!=null)
        	{
        		try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
        	if(fos!=null)
        	{
        		try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
            
        }
    }
}
