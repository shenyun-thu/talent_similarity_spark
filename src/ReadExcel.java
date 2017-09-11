
import jxl.*;
import jxl.read.biff.BiffException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;



public class ReadExcel {

    private String filePath;
    private List list = new ArrayList();
    public static int N = 5000000;//最大记录限制
    public person[] p = new person[N];
    public person[] p_temp = new person[N];//在获取测试分类信息之后暂时存储相对应的人才信息用于计算
    private String pattern = "(\\d*)(\\.)(\\d*)";//标识得到的分数
    public static String[] test_id = new String[10000];
    public int count;//记录分类后每类中成员的个数
    public int num_testID;
    public ReadExcel(String filePath) {
        this.filePath = filePath;
    }
    public int count_ans_all = 0;
    public static double similarity_limit = 1.5; //相似度的限制
    public static double low_score_limit = 80.0; //各维度平均分最低值限制
    public static double max_minus_limit = 2.0;//各维度上的差值的最大限制
    
    
    private void read_excel() throws IOException,BiffException 
    {
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
            for(int j = 0;j <= p[i].count;j++){
                p[i].average += p[i].scores[j];
            }
            p[i].average = p[i].average / (p[i].count+1);
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
    }

    
    public void init_testID(String test_id){
        count = 0;
        for(int i = 1;i<list.size();i++){
            if(p[i].test_id.equals(test_id)&&p[i].average > low_score_limit){
                p_temp[count] = p[i];
                count ++;
            }
        }
    }

    public void cal_eu_distance(){
        boolean have_ans = false;
        int count_ans = 0;
        double max_score_minus = 0;
        for(int i = 0;i < count;i++){
            for(int j = i+1;j < count;j++) {
                double ans = 0;
                boolean can_cal = true;
                for(int k =0; k <= p_temp[i].count;k++){
                    if((p_temp[i].scores[k] - p_temp[j].scores[k]) > max_score_minus){  
                        max_score_minus = (p_temp[i].scores[k] - p_temp[j].scores[k]);
                        if(max_score_minus > max_minus_limit) {
                           can_cal = false;
                           break;
                       }
                    }
                }
                if(can_cal){
                    for (int k = 0; k <=p_temp[i].count; k++) {
                        ans += (p_temp[i].scores[k] - p_temp[j].scores[k])*(p_temp[i].scores[k] - p_temp[j].scores[k]);
                    }
                    ans = Math.sqrt(ans);
    
                    if(ans < similarity_limit){
                        have_ans = true;
                        System.out.println("the distance between id " + p_temp[i].person_id + " and id " + p_temp[j].person_id + " is " + ans);
                        System.out.println("the actual statistics are : ");
                        for(int m = 0;m <= p_temp[i].count;m++){
                            System.out.print(p_temp[i].scores[m] + " ");
                        }
                        System.out.println("the average : " + p_temp[i].average);
                        System.out.println();
                        for(int m = 0;m <= p_temp[j].count;m++){
                            System.out.print(p_temp[j].scores[m] + " ");
                        }
                        System.out.println("the average : " + p_temp[j].average);
                        System.out.println();
                        count_ans ++;
                    }
                }
            }
        }
        count_ans_all += count_ans;
        System.out.println("in this test we find " + count_ans + " groups");
        if(!have_ans) System.out.println("No answer for your request");
    }

    public void show(){
        for(int i = 0;i <num_testID;i++){
            init_testID(test_id[i]);
            System.out.println("test_id : "+test_id[i]+" the result of talent_similarity judgment: ");
            System.out.println("-----------------------");
            cal_eu_distance();
            System.out.println();
        }
        System.out.println("in this tenant we all find "+count_ans_all+" groups");
        System.out.println("in this tenant we classify "+num_testID+" groups");
    }

    public static  void main(String[] args) throws  BiffException,IOException {
        long start_time = System.currentTimeMillis();
        ReadExcel excel = new ReadExcel("C:\\Users\\shenyun\\Desktop\\store\\tenantid_110006.xls");
        excel.read_excel();
        excel.outData();
        excel.classify_test();
        excel.show();
        long end_time = System.currentTimeMillis();
        System.out.println("程序运行时间为 : "+(end_time - start_time)+" ms");
    }
}
   
    