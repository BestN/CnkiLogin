import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Set;

public class TestData {

    //请求登陆的网址
    public static String LOGIN_URL = "http://expert.cnki.net/Account/LogOnByHome";
    //知网学者库高级搜索网址
    public static String AdFind_URL = "http://expert.cnki.net/Search/AdvFind";

    public static final String url = "jdbc:mysql://localhost:3306/mysql";
    public static final String name = "com.mysql.jdbc.Driver";
    public static final String user = "root";
    public static final String password = "root";

    public static Connection conn = null;

    public static void main(String[] args) throws IOException, InterruptedException {
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        //模拟一个浏览器

        WebClient wc = new WebClient(BrowserVersion.FIREFOX_52);
        // 设置webClient的相关参数
        wc.setCssErrorHandler(new SilentCssErrorHandler());
        //设置支持js
        wc.getOptions().setJavaScriptEnabled(true);
        //设置css渲染禁止
        wc.getOptions().setCssEnabled(false);
        //设置ajax
        wc.setAjaxController(new NicelyResynchronizingAjaxController());
        wc.getOptions().setThrowExceptionOnScriptError(false);
        //设置超时时间
        wc.getOptions().setTimeout(50000);
        //设置js抛出异常:false
        wc.getOptions().setThrowExceptionOnScriptError(false);
        //允许重定向
        wc.getOptions().setRedirectEnabled(true);
        //允许cookie
        wc.getCookieManager().setCookiesEnabled(true);
        //先得到目标页面的初始内容
        HtmlPage page = wc.getPage(AdFind_URL);
        // 等待JS驱动dom完成获得还原后的网页
        wc.waitForBackgroundJavaScript(10000*3);

        //设置登陆
        HtmlTextInput username = (HtmlTextInput)page.getElementById("username");
        username.setValueAttribute("1667273995@qq.com");
        HtmlPasswordInput password = (HtmlPasswordInput) page.getElementById("password");
        password.setValueAttribute("1667273995Njy");
        HtmlButtonInput htmlButtonInput = (HtmlButtonInput)page.getElementById("submittext");
        htmlButtonInput.click();

        //获取cookies
        Set<Cookie> cookies = wc.getCookieManager().getCookies();
        System.out.println("得到的cookies为："+cookies);
        wc.waitForBackgroundJavaScript(10000*3);

        HtmlPage page1 = wc.getPage(AdFind_URL);
        //得到id为form0的form表单
        HtmlForm form = (HtmlForm) page1.getElementById("form0");
        //String pageXml = form.asXml();
        //这里可以选择输出一下该表单查看表单内容
        //System.out.println(Jsoup.parse(pageXml));

        //通过Xpath(Xml路径语言)定位到form表单里没有id的提交按钮
        HtmlAnchor button = (HtmlAnchor)page1.getByXPath("//*[@class='mainbtn']").get(0);
        //System.out.println(button);

        //需要先点击清除按钮
        DomElement clearAll = page1.getElementById("clearAll");
        clearAll.click();

        //点击选择取自己需要的复选框
        HtmlCheckBoxInput CheckBoxInput = (HtmlCheckBoxInput)form.getByXPath("/html/body/div[position()=3]/form/div/div[position()=2]/dl/dd[position()=2]/ul[position()=1]/li[position()=5]/input[position()=1]").get(0);
        //将该复选框设置为选定
        CheckBoxInput.setChecked(true);
        //boolean checked = CheckBoxInput.isChecked();
        //System.out.println("是否已经选择医药卫生科技"+checked);

        //获取form表单中自己需要的输入框
        HtmlTextInput unit_0 = form.getInputByName("unit_0");
        //输入查询的内容
        unit_0.setValueAttribute("医院");

        //获取按钮点击后的网页
        HtmlPage retPage = button.click();
        // 等待JS驱动dom完成获得还原后的网页
        wc.waitForBackgroundJavaScript(10000*3);
        //数据库操作
        DBHelper();
        //输出页面内容并插入数据库
        //print(retPage);
        wc.waitForBackgroundJavaScript(10000*3);

        HtmlPage htmlPage = ReturnPage(page1);

        //wc.waitForBackgroundJavaScript(10000*3);
        if(htmlPage != null){
            System.out.println(Jsoup.parse(htmlPage.asXml()));
            //print(htmlPage);
        }
    }

    private  static HtmlPage ReturnPage(HtmlPage Page) throws IOException {
        HtmlAnchor ButtonForNext = (HtmlAnchor)Page.getByXPath("//*[@class='next']").get(0);
        HtmlPage retPage = ButtonForNext.click();
        //wc.waitForBackgroundJavaScript(10000*3);
        return retPage;
    }

    /*
     将页面上的信息输出到控制台
     */
    private  static void print(HtmlPage retPage){
        //创建一个医生对象
        TestDoctor testDoctor = new TestDoctor();


        //输出跳转网页的地址
        System.out.println("跳转的网址为："+retPage.getUrl().toString());
        //输出跳转网页的内容
        System.out.println(Jsoup.parse(retPage.asXml()));
        Document document = Jsoup.parse(retPage.asXml());
        //先获取目标div
        Elements selects = document.select("div#findSearchPager");
        //System.out.println("目标div:"+selects);

        //获取医生姓名
        Elements names = selects.select("span.el-name-name");
        //System.out.println("医生姓名："+names.text());

        //获取所属医院
        Elements hospitals = selects.select("p.txt2");
        //System.out.println("所属医院"+hospitals.text());

        //获取医生详情页链接
        Elements elements = selects.select("span.el-link-text");
        Elements links = elements.select("a");

        //获取当前页码
        String text = document.select("span.current").text();
        System.out.println("----------------------当前页码为："+text);

        ArrayList<String> Names = new ArrayList<String>();
        ArrayList<String> Hospitals = new ArrayList<String>();
        ArrayList<String> Links = new ArrayList<String>();
        for(Element e : names){
            Names.add(e.text());
        }
        for(Element a : hospitals){
            Hospitals.add(a.text());
        }
        for(Element s : links){
            String l = "http://expert.cnki.net"+s.attr("href");
            Links.add(l);
        }
        //插入的sql语句
        String sql = "insert into test0409(name, hospital, link, nownumber) value(?,?,?,?) ";

        for(int i = 0; i < Names.size() ; i++){
            System.out.println("医生姓名: "+Names.get(i)+"  所属医院: "+Hospitals.get(i)+ "\n" +"链接: "+Links.get(i));
            testDoctor.setName(Names.get(i));
            testDoctor.setHospital(Hospitals.get(i));
            testDoctor.setLink(Links.get(i));
            testDoctor.setNownumber(text);
            try {
                PreparedStatement preStmt=conn.prepareStatement(sql);
                preStmt.setString(1,testDoctor.getName());
                preStmt.setString(2,testDoctor.getHospital());
                preStmt.setString(3,testDoctor.getLink());
                preStmt.setString(4,text);
                preStmt.executeUpdate();
                System.out.println("插入到数据库成功");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        /*try {
            //String sql="insert into test0409(start_mileage,end_mileage) values(?,?,?)";//会抛出异常
            //PreparedStatement preStmt=conn.prepareStatement(sql);//conn是上一个程序的conn
            //preStmt.setLong(1, km);
            //preStmt.setLong(2, km);
            //preStmt.executeUpdate();
            //System.out.println("插入到数据库成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }
    public static void DBHelper(){
        try {
            Class.forName(name);
            conn = DriverManager.getConnection(url, user, password);
            //Statement st = conn.createStatement();
            /*ResultSet rs = st.executeQuery("SELECT * FROM test0409 WHERE VINID LIKE '-%'");
            while (rs.next()){
                int i = 0;
                System.out.println(i);
                i++;
            }*/
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void close(){
        try {
            this.conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }
    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }*/

}
/*
        此段可实现登陆，但无法获取cookies
         */
        /*System.out.println("向服务器发送数据，然后获取网页内容：");
        //建立一个WebConversation实例
        WebConversation webConversation = new WebConversation();
        //向指定的URL发出请求
        WebRequest req = new PostMethodWebRequest(LOGIN_URL);
        //给请求加上参数
        req.setParameter("USER_AGENT", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36");
        req.setParameter("username", "1667273995@qq.com");
        req.setParameter("password", "1667273995Njy");
        req.setParameter("LID", "WEEvREcwSlJHSldTTEYzWEp4QllqYUpBazF1MVpJU21zcmJJclB1OWpzND0=$9A4hF_YAuvQ5obgVAqNKPCYcEjKensW4IQMovwHtwkF4VYPoHbKxJw!!");
        req.setParameter("c_m_LinID", "LinID=WEEvREcwSlJHSldTTEYzWEp4QllqYUpBazF1MVpJU21zcmJJclB1OWpzND0=$9A4hF_YAuvQ5obgVAqNKPCYcEjKensW4IQMovwHtwkF4VYPoHbKxJw!!&ot=03/27/2019 10:20:43");
        //获取响应对象
        WebResponse resp;
        try {
            resp = webConversation.getResponse(req);

            //用getText方法获取相应的全部内容
            //用System.out.println将获取的内容打印在控制台上
            System.out.println("将获取的内容打印出来---------------------" + resp.getText());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }*/


