
import jxl.*;
import jxl.read.biff.BiffException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;



public class method {

    private String filePath;
    private List list = new ArrayList();
    private String pattern = "(\\d)(.*)(\\.)(.*)(0)";//标识得到的分数
    private String pattern1 = "(\\d)(.*)(\\_)(.*)(\\d)";//标识ID
    private String num[][] = new String[1010][10000];
    private int count = 0;//记录相关测试中的项目数

    public method(String filePath){
        this.filePath = filePath;
    }

    private void readExcel() throws IOException,BiffException {
        InputStream stream = new FileInputStream(filePath);

        Workbook rwb = Workbook.getWorkbook(stream);
        Sheet sheet = rwb.getSheet(0);
        for (int i = 0;i<sheet.getRows();i++) {
            String[] str = new String[sheet.getColumns()];
            Cell cell = null;
            for (int j = 0; j < sheet.getColumns(); j++) {
                cell = sheet.getCell(j, i);
                str[j] = cell.getContents();
            }
            list.add(str);
        }
    }
    private void outData()
    {
        for(int i = 1;i<list.size();i++){
            int temp = 1;
            String[] str = (String[])list.get(i);
            for(int j = 0;j<str.length;j++){
                boolean isMatch  = Pattern.matches(pattern,str[j]);
                boolean isMatch1 = Pattern.matches(pattern1,str[j]);
                if(isMatch || isMatch1)
                {
                    num[i][temp] = str[j];
                    temp++;
                }
            }
            count = temp - 1;
        }
    }

    private void cal_euclidean_distance()
    {
        int number = 0;
        for(int i = 1;i <list.size();i++){
            for(int j = i+1; j<list.size();j++){
                double ans = 0;
                for(int k = 2;k<=count;k++){
                    double temp_1 = Double.parseDouble(num[i][k]);
                    double temp_2 = Double.parseDouble(num[j][k]);
                    ans += (temp_1 - temp_2) * (temp_1 - temp_2);
                }
                ans = Math.sqrt(ans);
                if(ans < 3){
                    output_info(i,j,ans,"euclidean");
                    number++;
                }
            }
        }
        System.out.println("we all find " + number + " groups by using method euclidean");
    }

    private void cal_manhattan_distance()
    {
        int number = 0;
        for(int i = 1;i < list.size();i++){
            for(int j = i+1;j < list.size();j++){
                double ans = 0;
                for(int k = 2;k<=count;k++){
                    double temp_1 = Double.parseDouble(num[i][k]);
                    double temp_2 = Double.parseDouble(num[j][k]);
                    ans += Math.abs(temp_1 - temp_2);
                }
                if(ans < 5){
                    //        output_info(i,j,ans,"manhattan");
                    number ++;
                }
            }
        }
        System.out.println("we all find " + number + " groups by using method manhattan");
    }

    private void cal_che_distance()//切比雪夫距离
    {
        int number = 0;
        for(int i = 1;i < list.size();i++){
            for(int j = i+1;j < list.size();j++){
                double ans = 0;
                for(int k = 2;k<=count;k++){
                    double temp_1 = Double.parseDouble(num[i][k]);
                    double temp_2 = Double.parseDouble(num[j][k]);
                    if(ans <= Math.abs(temp_1  - temp_2))
                    {
                        ans = Math.abs(temp_1 - temp_2);
                    }
                }
                if(ans < 3){
                    //     output_info(i,j,ans,"chebyshev");
                    number++;
                }
            }
        }
        System.out.println("we all find " + number + " groups by using method chebyshev");
    }

    public void output_info(int i,int j,double ans,String method){
        System.out.println("the " + method + " distance between id " + num[i][1] + " and id " + num[j][1] + " is " + ans);
        System.out.println("the actual statistics are : ");
        for(int l = 2;l <= count;l++){
            System.out.print(num[i][l]+"\t");
        }
        System.out.println();
        for(int l = 2;l <= count;l++){
            System.out.print(num[j][l]+"\t");
        }
        System.out.println();
    }

    public static  void main(String[] args) throws  BiffException,IOException{
        method excel = new method("C:\\Users\\shenyun\\Desktop\\query_result.xls");
        excel.readExcel();
        excel.outData();

        long startTime_1 = System.currentTimeMillis();
        excel.cal_euclidean_distance();
        long endTime_1 = System.currentTimeMillis();
        System.out.println("euclidean : 程序运行时间为 ：" + (endTime_1 - startTime_1) + " ms");

        long startTime_2 = System.currentTimeMillis();
        excel.cal_manhattan_distance();
        long endTime_2 = System.currentTimeMillis();
        System.out.println("manhattan : 程序运行时间为 ：" + (endTime_2 - startTime_2) + " ms");

        long startTime_3 = System.currentTimeMillis();
        excel.cal_che_distance();
        long endTime_3 = System.currentTimeMillis();
        System.out.println("chebyshev : 程序运行时间为 ：" + (endTime_3 - startTime_3) + " ms");
    }
}



 