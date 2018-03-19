package _2018_03_01;

import myprogramers.ddy.CloudExcelUtil;

import java.io.IOException;
/**
 * 思路：对Excel结构进行校验，如果校验不通过，则进行过滤
 * 然后对excel类型与
 *
 *
 * */
public class 按照国调下发要求对厂站一次设备进行治理通过Excel文档进行导入到源数据端 {
    public static void main(String[] args) throws IOException {
        String localpath="D:\\Program Files\\feiq\\AutoRecv Files\\崔柳(E83935460B87)\\吉安\\吉安\\";
        String filename="变电站吉安.xls";
        CloudExcelUtil cloudExcelUtil=new CloudExcelUtil(filename,localpath);
        cloudExcelUtil.setImpExcel();


    }
}
