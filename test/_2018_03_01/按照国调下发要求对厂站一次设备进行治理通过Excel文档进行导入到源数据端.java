package _2018_03_01;

import myprogramers.ddy.CloudExcelUtil;

import java.io.IOException;
/**
 * ˼·����Excel�ṹ����У�飬���У�鲻ͨ��������й���
 * Ȼ���excel������
 *
 *
 * */
public class ���չ����·�Ҫ��Գ�վһ���豸��������ͨ��Excel�ĵ����е��뵽Դ���ݶ� {
    public static void main(String[] args) throws IOException {
        String localpath="D:\\Program Files\\feiq\\AutoRecv Files\\����(E83935460B87)\\����\\����\\";
        String filename="���վ����.xls";
        CloudExcelUtil cloudExcelUtil=new CloudExcelUtil(filename,localpath);
        cloudExcelUtil.setImpExcel();


    }
}
