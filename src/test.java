
import jxl.*;
import jxl.read.biff.BiffException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;



public class test {

    private String filePath;
    private List list = new ArrayList();
    public person[] p = new person[10000];
    public person[] p_temp = new person[10000];//在获取测试分类信息之后暂时存储相对应的人才信息用于计算
    private String pattern = "(\\d*)(\\.)(\\d*)";//标识得到的分数
    public static String[] test_id = new String[1000];
    public int count;//记录分类后每类中成员的个数
    public int num_testID;
    public test(String filePath) {
        this.filePath = filePath;
    }

    private void read_test() throws IOException,BiffException {
        InputStream stream = new FileInputStream(filePath);         
        Workbook rwb = Workbook.getWorkbook(stream);
        Sheet sheet = rwb.getSheet(0);
        for(int i = 0;i < sheet.getRows();i++){
            String[] str = new String[sheet.getColumns()];
            Cell cell = null;
            for(int j = 0; j < sheet.getColumns();j++){
                cell = sheet.getCell(j,i);
                str[j] = cell.getContents();
            }
            list.add(str);
        }
    }
    
    private void outData()
    {
        for(int i = 1;i<list.size();i++){
            int temp = 0;
            String[] str = (String[])list.get(i);
            p[i] = new person();
            p_temp[i] = new person();
            p[i].person_id = str[0];
            p[i].test_id = str[7];
            for(int j = 8;j <str.length;j ++){
                boolean isMatch  = Pattern.matches(pattern,str[j]);
                if(isMatch){
                    p[i].scores[temp] = Double.parseDouble(str[j]);
                    temp++;
                }
            }
            p[i].count = temp-1;
        } 
    }
    
    private void classify_test(){
        num_testID = 0;
        test_id[num_testID] = new String();
        test_id[num_testID] = p[1].test_id;
        num_testID++;
        for(int i = 2;i < list.size();i++){
            boolean flag = true; 
            test_id[num_testID] = new String();
            for(int j=0;j<=num_testID;j++){
                if(test_id[j].equals(p[i].test_id)){
                    flag = false;
                    break;
                }
            }
            if(flag) {
                test_id[num_testID] = p[i].test_id;
                num_testID++;
            }
        }
        
//        for(int i = 0;i <temp;i++){
//            System.out.println(test_id[i]);
//        }
//        System.out.println(temp);
    }
    
//    private void print_info() {
//        for (int k = 0; k < test_id.length; k++) {
//            for (int i = 1; i < list.size(); i++) {
//                if (p[i].test_id.equals(test_id[k])) {
//                    System.out.println(p[i].person_id + " " + p[i].test_id);
//                    for (int j = 0; j <= p[i].count; j++) {
//                        System.out.print(p[i].scores[j] + "\t");
//                    }
//                    System.out.println();
//                }
//            }
//        }
//    }
//    
    public void init_testID(String test_id){
        count = 0;
        for(int i = 1;i<list.size();i++){
            if(p[i].test_id.equals(test_id)){
                p_temp[count] = p[i];
                count ++;
            }
        }
    }
    
    public void cal_eu_distance(){
        for(int i = 0;i < count;i++){
            for(int j = i+1;j < count;j++) {
                double ans = 0;
                
                for (int k = 0; k <=p_temp[i].count; k++) {
                    ans += (p_temp[i].scores[k] - p_temp[j].scores[k])*(p_temp[i].scores[k] - p_temp[j].scores[k]);
                }
                ans = Math.sqrt(ans);  

                if(ans < 3){
                    System.out.println("the distance between id " + p_temp[i].person_id + " and id " + p_temp[j].person_id + " is " + ans);
                }
            }
        }
    }
    
    public void show(){ 
        for(int i = 0;i <num_testID;i++){
            init_testID(test_id[i]);
            System.out.println("test_id : "+test_id[i]+" the result of talent_similarity judgment: ");
            System.out.println("-----------------------");
     //       print_test();
            cal_eu_distance();
            System.out.println();
        }
    }

//    public void print_test(){
//        for (int i = 0; i < count; i++) {
//            System.out.println(p_temp[i].person_id + " " + p_temp[i].test_id);
//                for (int j = 0; j <= p_temp[i].count; j++) {
//                    System.out.print(p_temp[i].scores[j] + "\t");
//                }
//                System.out.println();
//            }
//        System.out.println(count);
//    }
    
    public static  void main(String[] args) throws  BiffException,IOException {
        test excel = new test("C:\\Users\\shenyun\\Desktop\\big_data.xls");
        excel.read_test();
        excel.outData();
        excel.classify_test();
        excel.show();
        
       // excel.print_info();
    }
}
   
    