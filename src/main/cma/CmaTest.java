import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class CmaTest {
    //好大夫搜索链接
    public static String HaodfSearch = "https://so.haodf.com/index/search";
    public static int sta = 0;
    public static void main(String[] args) throws IOException {
        //这一大段是设置一个htmlunit浏览器
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        WebClient wc = new WebClient(BrowserVersion.FIREFOX_52);
        wc.setCssErrorHandler(new SilentCssErrorHandler());
        wc.getOptions().setCssEnabled(false);
        wc.getOptions().setJavaScriptEnabled(true);
        wc.setAjaxController(new NicelyResynchronizingAjaxController());
        wc.getOptions().setThrowExceptionOnScriptError(false);
        wc.getOptions().setTimeout(10000);
        wc.getOptions().setRedirectEnabled(true);
        //还没添加文件读取
        String[] strings = {"姓名","姓名","姓名","姓名"};
        String[] Departments ={"科室"} ;

        HtmlPage page1 = wc.getPage(HaodfSearch);
        HtmlForm form1 = (HtmlForm) page1.getElementById("form1");
        //获取name为“kw”的输入框
        HtmlTextInput kw = form1.getInputByName("kw");
        for(int index = 0; index < strings.length; index++){
            //在输入框中输入医生姓名和科室
            kw.setValueAttribute(strings[index]+"+"+Departments[0]);
            //获取没有id的提交按钮
            HtmlSubmitInput input = (HtmlSubmitInput)form1.getByXPath("//*[@class='sh-btn']").get(0);
            wc.waitForBackgroundJavaScript(3000);
            HtmlPage refPage = input.click();
            //等待js驱动
            wc.waitForBackgroundJavaScript(10000);
            //得到按下按钮后的界面
            Document document = Jsoup.parse(refPage.asXml());
            //System.out.println(document);
            //先找到名字所在的div，再找这个div里的链接
            Elements selects = document.select("div.sl-i-right");
            wc.waitForBackgroundJavaScript(3000);
            //System.out.println(selects);
            //第一页该姓名医生个人简介的标签
            Elements links = selects.select("a[href]");
            ArrayList<String> Links = new ArrayList<String>();
            ArrayList<HtmlPage> PageLinks = new ArrayList<HtmlPage>();
            ArrayList<Document> parseLinks = new ArrayList<Document>();
            String l;
            for(Element e : links){
                //链接有两种，如果包含http头的则直接添加到数组中，如果不包含http头则要手动添加http头
                if(e.attr("href").indexOf("http") != -1){
                    l = e.attr("href");
                    System.out.println(l);
                    Links.add(l);
                } else {
                    l = "http:"+e.attr("href");
                    System.out.println(l);
                    Links.add(l);
                }
            }
            System.out.println("本次数组中存了"+links.size()+"个链接");
            if(Links.size() > 3){
                sta = 3;
            } else if(Links.size() <= 3 && Links.size() > 0){
                sta = Links.size();
            }
            //每个医生的个人介绍链接(取搜索结果的前三位)
            for(int i = 0; i < sta; i++){
                if(Links.get(i) != null){
                    wc.waitForBackgroundJavaScript(10000);
                    HtmlPage pagettt = wc.getPage(Links.get(i));
                    wc.waitForBackgroundJavaScript(10000);
                    PageLinks.add((pagettt));

                } else {
                    System.out.println("第" + i +"次循环链接为空");
                }
            }
            for(int i = 0; i < PageLinks.size(); i++) {
                parseLinks.add(Jsoup.parse(PageLinks.get(i).asXml()));
                wc.waitForBackgroundJavaScript(3000);
            }
            for(Document parseOne:parseLinks){
                int time = 0;
                /*
                这里分两种情况，
                1.有人认领的医生页面
                2.无人认领的医生页面
                    无人认领分有没有完整介绍按钮
                    有完整介绍按钮，就有id为full的标签
                    无完整介绍的按钮，就看id为truncate的标签
                        没有id为truncate的标签，最后看td[colspan='3']这个属性
                */
                //查找医生完整简介的链接
                Elements select = parseOne.select("a.popupwindow.a_hover");
                wc.waitForBackgroundJavaScript(3000);
                if(select.toString() == null || select.toString().equals("") || select.toString() == "" || select.toString() == " ") {
                    /*
                    这是没人认领的医生页面操作
                    */
                    System.out.println("进入没人认领界面操作");
                    //查看是否有完整介绍按钮
                    Elements select1 = parseOne.select("a.blue.cp.J_display_intro");
                    wc.waitForBackgroundJavaScript(3000);
                    System.out.println("测试"+select1.text());
                    if(select1.text().indexOf("完整介绍") != -1){
                        System.out.println("该医生有完整介绍按钮");
                        //只要有蓝色“完整介绍”按钮，就有full id
                        Elements ce = parseOne.select("div#full");
                        wc.waitForBackgroundJavaScript(3000);
                        if(ce.text() == null || ce.text() == "" || ce.text().equals(" ") || ce.text() == " " || ce.text().equals("")){
                            System.out.println("完整介绍的链接是空的，查看基本介绍");
                            if(parseOne.select("div#truncate").text().indexOf("中华医学会") != -1){
                                System.out.println("找到该医生:" + strings[index] + parseOne.select("div.lt").select("td[width='231']").select("a[href]").text());
                                wc.waitForBackgroundJavaScript(3000);
                                break;
                            } else {
                                System.out.println("有truncate的id的基本介绍中不包括“中华医学会”关键字。");
                            }
                        } else if(ce.text().indexOf("中华医学会") != -1){
                            System.out.println("找到该医生:" + strings[index] + parseOne.select("div.lt").select("td[width='231']").select("a[href]").text());
                            wc.waitForBackgroundJavaScript(3000);
                            break;
                        } else {
                            System.out.println("详细介绍中不包含“中华医学会”关键字。");
                        }
                    } else if(select1.text() == null || select1.text() == "" || select1.text().equals(" ") || select1.text().equals("") || select1.text() == " "){
                        System.out.println("该医生没有完整介绍按钮，请查看基本介绍。");
                        Elements ne = parseOne.select("div#truncate");
                        wc.waitForBackgroundJavaScript(3000);
                        //先判断有没有truncate id，如果为空说明没有该标签
                        if(ne.text() == null || ne.text() == " " || ne.text().equals(" ") || ne.text().equals("") || ne == null){
                            System.out.println("没有truncate的id标签");
                            //没有id 为 truncate的标签，就找td[colspan='3']
                            Elements Base = parseOne.select("td[colspan='3']");
                            wc.waitForBackgroundJavaScript(3000);
                            if(Base.text() != null){
                                if(Base.text().indexOf("中华医学会") != -1){
                                    System.out.println("找到该医生" + strings[index] + parseOne.select("div.lt").select("td[width='231']").select("a[href]").text());
                                    wc.waitForBackgroundJavaScript(3000);
                                    break;
                                } else {
                                    System.out.println("第"+ time+ "次循环" + strings[index] + "基本介绍中不包括“中华医学会”关键字。");
                                }
                            } else {
                                System.out.println("该医生没有基本介绍");
                            }
                        } else {
                            if(ne.text().indexOf("中华医学会") != -1){
                                System.out.println("找到该医生:" + strings[index] + parseOne.select("div.lt").select("td[width='231']").select("a[href]").text());
                                break;
                            } else {
                                System.out.println("有truncate的id的基本介绍中不包括“中华医学会”关键字。");
                            }
                        }
                    } else {
                        System.out.println("发生了意料之外的情况，请解决");
                    }
                } else if(select.toString().equals("#")){
                    System.out.println("该医生有人认领但无详细简介，请查看基本介绍");
                } else {
                    /*
                    这是有人认领的医生页面的操作
                    */
                    //查看完整简介的链接
                    System.out.println("有人认领的界面的操作");
                    String Complete = "https://" + parseOne.select("span.space_b_url").text()+select.attr("href");
                    System.out.println(Complete);
                    HtmlPage ComPage = wc.getPage(Complete);
                    wc.waitForBackgroundJavaScript(3000);
                    String asText = ComPage.asText();
                    //如果在简介中找到中华医学会相关字眼，则表示找到该医生
                    if(asText.indexOf("中华医学会") != -1){
                        System.out.println("找到该医生:" + strings[index] +" "+ parseOne.select("div.clearfix.pt5.bb_d.pb5").select("div.hh").text());
                        break;
                    } else {
                        System.out.println("无中华医学会相关字眼，第"+ time+1 +"次循环没找到，开启下一次循环");
                    }
                    //System.out.println(asText);
                }
                time++;
            }
        }
    }
    //有人认领的操作界面
    public static void IsTrue(ArrayList<String> Links,Elements select){
    }
    public static void IsFalse(){
    }
}

