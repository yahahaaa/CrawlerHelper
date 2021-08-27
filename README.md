# CrawlerHelper简易爬虫框架使用指南

显示图片需要配置hosts地址

框架大纲来自b站，作者稍微加了一点功能，够平时自己使用了（本简易爬虫不具备任何防反爬功能，基本用于静态页面或本地html）

因为CSS选择器能够定位到一个html文件中的任何数据，所以可以通过框架省略掉具体的查找过程，使用者只需要在实体类的字段上标记注解，并在注解中添加css选择器，由框架对字段赋值

本篇指南中介绍的是本地html目标字段解析流程

## 1. 目标页面

由于该页面采用了一定的反爬机制，所以我是先下载了该页面在本地解析的html

![cde.PNG](https://github.com/yahahaaa/picture/blob/master/crawlerhelper/cde.PNG?raw=true)

该页面中列表部分的html

```html
<table width="714" border="0" cellpadding="10" cellspacing="1" bgcolor="#8AB6E3" align="center">
     <thead>                
         <tr>
             <th width="8%" height="10" align="center" background="/styles/images/news_list_title_bg1.jpg"><strong style="Font-size: 14px; color: #06519A;">序号</strong></th>
             <th width="10%" height="10" align="center" background="/styles/images/news_list_title_bg1.jpg"><strong style="Font-size: 14px; color: #06519A;">受理号</strong></th>
             <th width="12%" height="10" align="center" background="/styles/images/news_list_title_bg1.jpg"><strong style="Font-size: 14px; color: #06519A;">药品名称</strong></th>
             <th width="16%" height="10" align="center" background="/styles/images/news_list_title_bg1.jpg"><strong style="Font-size: 14px; color: #06519A;">进入中心时间</strong></th>
             <th width="12%" height="10" align="center" background="/styles/images/news_list_title_bg1.jpg"><strong style="Font-size: 14px; color: #06519A;">审评状态</strong></th>
             <th width="12%" height="10" align="center" background="/styles/images/news_list_title_bg1.jpg"><strong style="Font-size: 14px; color: #06519A;">药理毒理</strong></th>
             <th width="8%" height="10" align="center" background="/styles/images/news_list_title_bg1.jpg"><strong style="Font-size: 14px; color: #06519A;">临床</strong></th>
             <th width="8%" height="10" align="center" background="/styles/images/news_list_title_bg1.jpg"><strong style="Font-size: 14px; color: #06519A;">药学</strong></th>
             <th width="20%" height="10" align="center" background="/styles/images/news_list_title_bg1.jpg"><strong style="Font-size: 14px; color: #06519A;">备注</strong></th>
         </tr>
     </thead>
     <tbody>
         <tr bgcolor="#f5fafe" class="newsindex">
             <td align="center"> 1 </td>
             <td align="center"> JXHL2101079 </td>
             <td align="center"> LNP023胶囊 </td>
             <td align="center"> 2021-06-03 </td>
             <td align="left"> 排队待审评 </td>
             <td align="center"> <img src="/styles/images/lamp_y.jpg"> </td>
             <td align="center"> <img src="/styles/images/lamp.gif"> </td>
             <td align="center"> <img src="/styles/images/lamp_shut.gif"> </td>
             <td align="left"> </td>
         </tr>

         <tr bgcolor="#f5fafe" class="newsindex">
             <td align="center"> 2 </td>
             <td align="center">CXHL2101175</td>
             <td align="center"> TQA3605片 </td>
             <td align="center"> 2021-06-04 </td>
             <td align="left">排队待审评</td>
             <td align="center"><img src="/styles/images/lamp_shut.gif"></td>
             <td align="center"><img src="/styles/images/lamp_shut.gif"></td>
             <td align="center"><img src="/styles/images/lamp_shut.gif"></td>
             <td align="left"></td>
         </tr>
     </tbody>
</table>
```

## 2. 接口介绍

包下为核心流程代码utils/CrawlerUtils

### 2.1 getConnection(String url)

为模仿浏览器发送请求，需要自己在该方法下定义一些header的变量，可以到目标页面下按F12自行查看

### 2.2 executeRemote(Class<?> clazz, Object obj)

当采用远程爬虫的方式获取数据时，调用该接口，clazz为最外层实体类的类变量，obj主要用于判断该方法返回的是List集合还是一个实体类。如果想要返回的对象是一个List集合时，传入ArrayList对象集合，否则随便传一个Object对象就行

### 2.3 executeLocal(Class<?> clazz,Object obj)

当想要解析本地html文件时（框架上只能解析单个文件，如果想要解析多个文件，可以自行改进代码），采用该接口，本地文件默认读取resource目录

## 3. 流程介绍

### 实体类

首先我们将 1.2.3 定义为一个实体类中的字段接收

1. 

![上](https://github.com/yahahaaa/picture/blob/master/crawlerhelper/%E4%B8%8A.PNG?raw=true)

2. 

![中](https://github.com/yahahaaa/picture/blob/master/crawlerhelper/%E4%B8%AD.PNG?raw=true)

3. 第三部分由于是一个列表，所以第三部分专门再建一个实体类接收列表中的数据，然后用一个集合接收列表中所有数据

![下](https://github.com/yahahaaa/picture/blob/master/crawlerhelper/%E4%B8%8B.PNG?raw=true)

OuterTaskEntity

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsoupDocument(localUrl = "CDE化药审评序列公示.html")
public class OuterTaskEntity {

    @Extract(cssQuery = "#hyID > a")
    private String drugType;

    @Extract(cssQuery = "#applyTypeCde > option:nth-child(3)")
    private String taskType;

    @JsoupDocument()
    private List<TaskEntity> taskEntities;
}
```

TaskEntity

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsoupDocument(localUrl = "CDE化药审评序列公示.html",cssQuery = ".newsindex")
public class TaskEntity {

    @Extract(cssQuery = "td:nth-child(1)")
    private String num;

    @Extract(cssQuery = "td:nth-child(2)")
    private String enName;

    @Extract(cssQuery = "td:nth-child(3)")
    private String cnName;

    @Extract(cssQuery = "td:nth-child(4)")
    private String date;

    @Extract(cssQuery = "td:nth-child(5)")
    private String state;

    // 药理
    @Extract(cssQuery = "td:nth-child(6) > img",attr = "src",prefix = "/styles/images/",
            regex = {"lamp_shut.gif","lamp.gif","lamp_y.jpg"},replacement = {"完成审评","正在审评中","排对待审评"})
    private String pharmacology;

    // 临床
    @Extract(cssQuery = "td:nth-child(7) > img",attr = "src",prefix = "/styles/images/",
            regex = {"lamp_shut.gif","lamp.gif","lamp_y.jpg"},replacement = {"完成审评","正在审评中","排对待审评"})
    private String clinical;

    // 药学
    @Extract(cssQuery = "td:nth-child(8) > img",attr = "src",prefix = "/styles/images/",
            regex = {"lamp_shut.gif","lamp.gif","lamp_y.jpg"},replacement = {"完成审评","正在审评中","排对待审评"})
    private String pharmacy;
}
```

regex和replacement主要对应了str.replaceAll方法，用于替换字段接收的数据



测试结果

```markdown
化药审评序列公示
NDA
TaskEntity(num=1, enName=JXHL2101079, cnName=LNP023胶囊, date=2021-06-03, state=排队待审评, pharmacology=排对待审评, clinical=正在审评中, pharmacy=完成审评)
TaskEntity(num=2, enName=CXHL2101175, cnName=TQA3605片, date=2021-06-04, state=排队待审评, pharmacology=完成审评, clinical=完成审评, pharmacy=完成审评)
TaskEntity(num=3, enName=CXHL2101176, cnName=TQA3605片, date=2021-06-04, state=排队待审评, pharmacology=完成审评, clinical=完成审评, pharmacy=完成审评)
TaskEntity(num=4, enName=CXHL2101177, cnName=HRG2010胶囊, date=2021-06-04, state=排队待审评, pharmacology=完成审评, clinical=完成审评, pharmacy=完成审评)
TaskEntity(num=5, enName=CXHL2101178, cnName=HRG2010胶囊, date=2021-06-04, state=排队待审评, pharmacology=完成审评, clinical=完成审评, pharmacy=完成审评)
TaskEntity(num=6, enName=JXHL2101080, cnName=维托拉生注射液, date=2021-06-04, state=排队待审评, pharmacology=完成审评, clinical=完成审评, pharmacy=完成审评)
TaskEntity(num=7, enName=CXHL2101181, cnName=复达那非片, date=2021-06-07, state=排队待审评, pharmacology=完成审评, clinical=完成审评, pharmacy=完成审评)
TaskEntity(num=8, enName=CXHL2101182, cnName=复达那非片, date=2021-06-07, state=排队待审评, pharmacology=完成审评, clinical=完成审评, pharmacy=完成审评)
TaskEntity(num=9, enName=JXHL2101081, cnName=Etrasimod片, date=2021-06-07, state=排队待审评, pharmacology=完成审评, clinical=完成审评, pharmacy=完成审评)
TaskEntity(num=10, enName=JXHL2101082, cnName=Etrasimod片, date=2021-06-07, state=排队待审评, pharmacology=完成审评, clinical=完成审评, pharmacy=完成审评)
```


