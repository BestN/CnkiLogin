
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
ps:其实我并不知道这里面要写什么
------这是一个模拟知网学者库的登陆功能------
 */
public class CnkiLoginApater {
    //你要请求的网址
    public static String LOGIN_URL = "http://expert.cnki.net/Account/LogOnByHome";
    //user_Agent,一个不能缺少的东西，在Network里面的Headers里（我并不知道他是干什么的）
    public static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36";

    public static String AdFind_URL = "http://expert.cnki.net/Search/AdvFind";

    public static void main(String[] args) throws IOException, InterruptedException {

        //这一大段是设置一个htmlunit浏览器
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        WebClient wc = new WebClient(BrowserVersion.FIREFOX_52);
        wc.getOptions().setJavaScriptEnabled(true);
        wc.getOptions().setCssEnabled(false);
//        wc.setAjaxController(new NicelyResynchronizingAjaxController());
        wc.getOptions().setThrowExceptionOnScriptError(false);
        wc.getOptions().setTimeout(10000);

        //将需要向url发送的值存在一个map里（）
        Map<String, String> mapParamsData = new HashMap<String, String>();
        mapParamsData.put("username", "1667273995@qq.com");
        mapParamsData.put("password", "1667273995Njy");
        /*
        突然发现这三条消息不重要，注释掉还是能完成登陆
         */
        //mapParamsData.put("refRmbUser","true");
        //mapParamsData.put("Ecp_ClientId","4190326144101537998");
        //mapParamsData.put("Ecp_IpLoginFail","190326182.50.124.211");

        //这两条是重要信息，没有将无法登陆
        mapParamsData.put("LID","WEEvREcwSlJHSldTTEYzWEp4QllqYUpBazF1MVpJU21zcmJJclB1OWpzND0=$9A4hF_YAuvQ5obgVAqNKPCYcEjKensW4IQMovwHtwkF4VYPoHbKxJw!!");
        mapParamsData.put("c_m_LinID","LinID=WEEvREcwSlJHSldTTEYzWEp4QllqYUpBazF1MVpJU21zcmJJclB1OWpzND0=$9A4hF_YAuvQ5obgVAqNKPCYcEjKensW4IQMovwHtwkF4VYPoHbKxJw!!&ot=03/27/2019 10:20:43");

        //向url发送请求
        Connection connect = Jsoup.connect(LOGIN_URL);
        //必需
        connect.ignoreContentType(true);

        connect.userAgent(USER_AGENT);
        connect.data(mapParamsData);

        connect.method(Connection.Method.POST);
        Connection.Response response = connect.execute();

        //获得传回来的cookie，存入map中，一段时间内点开下一个页面直接输入cookie就实现登录状态
        Map<String, String> cookies = response.cookies();
        System.out.println("获得cookie值"+cookies);

        //url里现在存放的是学者库首页，用来验证cookie登录是否成功 成功可以看到自己账号信息
        Document doc = Jsoup.connect("http://expert.cnki.net/Search/AdvFind")
                        .cookies(cookies)
                            .get();
        System.out.println("-----------------------登陆后的首页为：-------------------------"+doc);
        Element form0 = doc.getElementById("form0");
        //System.out.println(elementById);
//--------------------------------------------------------------------------
        /*

        存储要查询的条件

         */
//        Map<String, String> FormDate = new HashMap<String, String>();
//
//        FormDate.put("fieldParam","E*");
//        FormDate.put("name_pcni_select","name_0");
//        FormDate.put("name_0","");
//        FormDate.put("name_match_0","1");
//        FormDate.put("unit_relation_0","11");
//        FormDate.put("unit_0","医院");
//        FormDate.put("unit_match_0","1");
//        FormDate.put("name_relation_1","11");
//        FormDate.put("name_pcni_select","name_1");
//        FormDate.put("name_1","");
//        FormDate.put("name_match_1","1");
//        FormDate.put("unit_relation_1","11");
//        FormDate.put("unit_1","");
//        FormDate.put("unit_match_1","1");
//        FormDate.put("name_relation_2","11");
//        FormDate.put("name_pcni_select","name_2");
//        FormDate.put("name_2","");
//        FormDate.put("name_match_2","1");
//        FormDate.put("unit_relation_2","11");
//        FormDate.put("unit_2","");
//        FormDate.put("unit_match_2","1");
//        FormDate.put("name_relation_3","11");
//        FormDate.put("name_pcni_select","name_3");
//        FormDate.put("name_3","");
//        FormDate.put("name_match_3","1");
//        FormDate.put("unit_relation_3","11");
//        FormDate.put("unit_3","");
//        FormDate.put("unit_match_3","1");
//        FormDate.put("name_relation_4","11");
//        FormDate.put("name_pcni_select","name_4");
//        FormDate.put("name_4","");
//        FormDate.put("name_match_4","1");
//        FormDate.put("unit_relation_4","11");
//        FormDate.put("unit_4","");
//        FormDate.put("unit_match_4","1");
//        FormDate.put("keyword_0","");
//        FormDate.put("keyword_match_0","1");
//        FormDate.put("researcharea_relation_0","11");
//        FormDate.put("researcharea_0","");
//        FormDate.put("keyword_relation_1","11");
//        FormDate.put("keyword_1","");
//        FormDate.put("keyword_match_1","1");
//        FormDate.put("researcharea_relation_1","11");
//        FormDate.put("researcharea_1","");
//        FormDate.put("keyword_relation_2","11");
//        FormDate.put("keyword_2","");
//        FormDate.put("keyword_match_2","1");
//        FormDate.put("researcharea_relation_2","11");
//        FormDate.put("researcharea_2","");
//        FormDate.put("keyword_relation_3","11");
//        FormDate.put("keyword_3","");
//        FormDate.put("keyword_match_3","1");
//        FormDate.put("researcharea_relation_3","11");
//        FormDate.put("researcharea_3","");
//        FormDate.put("keyword_relation_4","11");
//        FormDate.put("keyword_4","");
//        FormDate.put("keyword_match_4","1");
//        FormDate.put("researcharea_relation_4","11");
//        FormDate.put("researcharea_4","");
//        FormDate.put("fund_match_0","1");
//        FormDate.put("fundcodes","");
//        FormDate.put("statNum_field_0","9");
//        FormDate.put("statNum_match_0","3");
//        FormDate.put("statNum_0","");
//        FormDate.put("statNum_relation_1","11");
//        FormDate.put("statNum_field_1","9");
//        FormDate.put("statNum_match_1","3");
//        FormDate.put("statNum_1","");
//        FormDate.put("statNum_relation_2","11");
//        FormDate.put("statNum_field_2","9");
//        FormDate.put("statNum_match_2","3");
//        FormDate.put("statNum_2","");
//        FormDate.put("statNum_relation_3","11");
//        FormDate.put("statNum_field_3","9");
//        FormDate.put("statNum_match_3","3");
//        FormDate.put("statNum_3","");
//        FormDate.put("statNum_relation_4","11");
//        FormDate.put("statNum_field_4","9");
//        FormDate.put("statNum_match_4","3");
//        FormDate.put("statNum_4","");
//        FormDate.put("X-Requested-With","XMLHttpRequest");
//
//
//
//        //向url发送请求
//        Connection connection = Jsoup.connect("http://expert.cnki.net/Search/AdvFindResult");
//        connection
//                .header("Accept","*/*")
//                .header("Accept-Encoding","gzip, deflate")
//                .header("Accept-Language","zh-CN,zh;q=0.9")
//                .userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36")
//                .cookies(cookies)
//                .data(FormDate)
//                .method(Connection.Method.POST);
//        Connection.Response execute = connection.execute();
//
//        //Map<String, String> listcookies = execute.cookies();
//        //        System.out.println("获得cookie值"+listcookies);
//        Map<String,String> newcookies = execute.cookies();
//        System.out.println("-----------------------------新的cookies值为：--------------------------------"+newcookies);
//
//        Document list = Jsoup.connect("http://expert.cnki.net/Search/AdvFind")
//                .cookies(newcookies)
//                .get();
//        System.out.println("---------------------------传入选择条件后的页面--------------------------------"+list);
//        System.out.println("-------------------------------------------------------------------------------------");
//
//        Map<String, String> PageDate = new HashMap<String, String>();
//        PageDate.put("q","D819C3AAF7D0DE000E81535EA6F3649B2CA2D0E611AE5185DB1A296B91A2FAD4E9EE1E3C51D54EFF65189B8F1DE5507E2FC11F7EBB0A2945BD3462C72B7C661F65A2612D3E86C7271E2EA3BD0556FB9F");
//        PageDate.put("h","10");
//        PageDate.put("pIdx","1");
//        PageDate.put("pSize","10");
//        PageDate.put("order","0");
//
//        Connection Pagecon = Jsoup.connect("http://expert.cnki.net/Search/AdvFindResult");
//        Pagecon
//                .header("Accept","*/*")
//                .header("Accept-Encoding","gzip, deflate")
//                .header("Accept-Language","zh-CN,zh;q=0.9")
//                .userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36")
//                .cookies(cookies)
//                .data(PageDate)
//                .method(Connection.Method.POST);
//        Pagecon.execute();
//
//        Document document = Jsoup.connect("http://expert.cnki.net/Search/AdvFind").get();
//        System.out.println(document);


    }
}
