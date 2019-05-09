
import java.io.*;
import java.sql.*;
import java.util.ArrayList;


public class DataMapping {

    public static final String url = "jdbc:mysql://localhost:3306/hcr_physician";
    public static final String name = "com.mysql.jdbc.Driver";
    public static final String user = "root";
    public static final String password = "root";

    public static Connection conn = null;
    public static void main(String[] args) throws IOException, SQLException {
        DBHelper();
        String fileName="C:"+ File.separator+"doc_name.txt";
        File f=new File(fileName);
        InputStream in=new FileInputStream(f);
        byte[] b=new byte[(int)f.length()];
        for (int i = 0; i < b.length; i++) {
            b[i]=(byte)in.read();
        }
        in.close();
        //获取到文件中的所有字符，存在str字符串中
        String str = new String(b);
        String doc_name = "";
        ArrayList<String> doc_names = new ArrayList<String>();
        //for循环遍历string字符串中的每个字符
        for(int i = 0; i < str.length(); i++){
            //判断两个空格之间的为一个医生的姓名，然后输出
            if(str.charAt(i) != ' ' && str.charAt(i) != '\n' && str.charAt(i) != '\r'){
                    doc_name = doc_name + str.charAt(i);
            } else if(doc_name.length()>=2 && (str.charAt(i) == ' ' || str.charAt(i) == '\n' || str.charAt(i) == '\r')){
                doc_names.add(doc_name);
                doc_name = "";
            }
        }
        //先定义两个文件，一个存放找到的医生信息，一个存放没找到的医生名字
        String NewDoc_name="C:"+File.separator+"NewDoc_name(Id).txt";
        String NotFind_Doc="C:"+File.separator+"NotFind.txt";

        File File_New = new File(NewDoc_name);
        File File_Not = new File(NotFind_Doc);

        Writer out_New =new FileWriter(File_New,true);
        Writer out_Not =new FileWriter(File_Not,true);
        int allDoc = 0;//记录查询的医生总数
        int successDoc = 0;//记录查询成功写到文件中的医生总数
        int failFind = 0;//记录数据库中没查询到的医生总数
        for(String s : doc_names){
            Boolean isCheck = false;
            //先输出一下该医生名字
            System.out.println(s);
            //allDoc为查找总医生数量
            allDoc++;
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id,doctor_name,section,hospital,career FROM hcp_doctor WHERE doctor_name = '" + s + " '");//' and career like '%中华医学会%'
            //System.out.println(rs.next());
            while (rs.next()){
                String doc_career = rs.getString("career");
                //匹配字段
                if(doc_career != null && doc_career.contains("中华医学会")){
                    String doctor_id = rs.getString("id");
                    String doctor_name = rs.getString("doctor_name");
                    String doc_section = rs.getString("section");
                    String doc_hospital = rs.getString("hospital");
                    System.out.println("id：" + doctor_id + " " + "姓名："+ doctor_name + " " + "医院:" +doc_hospital + " " + "科室:" + doc_section +" ");
                    out_New.write("id：" + doctor_id + " " + "姓名：" + doctor_name + " " + "医院：" + doc_hospital + " " + "科室:" + doc_section + "\r\n");
                    out_New.flush();
                    isCheck = true;
                    successDoc++;
                }
            }
            if(isCheck == false){
                System.out.println(s + " 医生无个人简介，或个人简介不包含“中华医学会”字段现将，该医生存入NotFind文本文档中");
                //没有个人简介或个人简介不包含中华医学会，将其存入NotFind
                out_Not.write(s+" ");
                out_Not.flush();
                //同时将isCheck属性设置回false
                System.out.println("测试该条件是否执行");
                failFind ++;
            }
        }
        System.out.println("总人数："+allDoc+"人");
        System.out.println("成功："+successDoc+"人");
        System.out.println("失败："+failFind+"人");

        out_New.write("成功："+successDoc+"人\r\n");
        out_Not.write("失败："+failFind+"人\r\n");
        out_Not.close();
        out_New.close();
    }
    //数据库操作
    public static void DBHelper(){
        try {
            Class.forName(name);
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
