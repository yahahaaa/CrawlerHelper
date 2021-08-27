package cn.com.demo.framework.utils;

import cn.com.demo.framework.annotation.Extract;
import cn.com.demo.framework.annotation.JsoupDocument;
import cn.com.demo.framework.enums.ContentTypeEnum;
import cn.com.demo.framework.exception.OriginalStructureException;
import cn.com.demo.framework.exception.OriginalStructureRuntimeException;
import cn.com.demo.framework.pojo.OuterTaskEntity;
import cn.com.demo.framework.pojo.TaskEntity;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: TODO
 * @Auther mzli.wesley
 * @Date 2021/8/21
 * @Version V1.0
 **/
public class CrawlerUtils {

    private CrawlerUtils() {
    }
    public static Connection getConnection(String url) {

        Connection connection = Jsoup.connect(url);
        connection.header("Accept", "");
        connection.header("Accept-Encoding", "");
        connection.header("Accept-Language", "");
        connection.header("Cache-Control", "");
        connection.header("Connection", "");
        connection.header("Cookie", "");
        connection.header("Host", "");
        connection.header("User-Agent", "");
        connection.ignoreHttpErrors(true);
        return connection;
    }

    /**
     * 解析本地url地址中的html文件
     *
     * @param clazz 想要进行html解析并赋值的实体类类对象
     * @param obj   通过判断obj类型是否为List集合决定返回的实体类还是 ArrayList
     */
    public static Object executeLocal(Class<?> clazz, Object obj)
            throws OriginalStructureException, InstantiationException, IllegalAccessException {

        // 1.获取JsoupDocument
        JsoupDocument jsoupDocument = clazz.getAnnotation(JsoupDocument.class);

        // 2.jsoupDocument不能为空，因为jsoupDocument标记为一个爬虫实体类
        if (jsoupDocument == null) {
            throw new OriginalStructureException("非爬虫实体类，爬虫实体类必须标注JsoupDocument注解");
        }

        // 3.localUrl指定了待解析的本地html文件地址，文件地址不能为空
        String localUrl = jsoupDocument.localUrl();
        if (StringUtils.isBlank(localUrl)) {
            throw new OriginalStructureException("未指定本地html文件地址");
        }

        // 4.读取localUrl指定的html文件
        BufferedReader br = null;
        InputStream is = null;
        InputStreamReader isr = null;
        try {
            // 4.1 获取resource目录在本机的绝对路径并生成InputStream流
            is = CrawlerUtils.class.getClassLoader().getResourceAsStream(localUrl);
            if (is == null) {
                throw new OriginalStructureException("未找到LocalUrl中指定的文件");
            }
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            Document document = Jsoup.parse(sb.toString());
            // 5. 解析文件并给obj的字段赋值
            return parseDocument(document, clazz, obj);
        } catch (IOException e) {
            throw new OriginalStructureRuntimeException("读取本地html文件失败", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {}
            }

            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {}
            }

            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {}
            }
        }
    }

    /**
     * 爬取远程url地址并解析
     * @param clazz
     * @param obj
     * @throws OriginalStructureException
     * @throws IOException
     */
    public static void executeRemote(Class<?> clazz, Object obj) throws OriginalStructureException, IOException, InstantiationException, IllegalAccessException {
        // 获取JsoupDocument
        JsoupDocument jsoupDocument = clazz.getAnnotation(JsoupDocument.class);

        // 判断获取的注解不为空
        if (jsoupDocument == null) {
            throw new OriginalStructureException("非爬虫实体类，爬虫实体类必须标注JsoupDocument注解");
        }

        // 其次采用远程url的方式爬取
        String targetUrl = jsoupDocument.targetUrl();
        String domain = jsoupDocument.domain();

        // 如果指定了域名，优先使用域名
        if (StringUtils.isNotBlank(domain)){
            targetUrl = domain;
        }

        if (StringUtils.isBlank(targetUrl)) {
            throw new OriginalStructureRuntimeException("未指定爬取的目标URL");
        }
        // 获取连接对象
        Connection connection = getConnection(targetUrl);
        // 获取爬取方式
        Connection.Method method = jsoupDocument.method();
        Document document = connection.method(method).execute().parse();
        parseDocument(document,TaskEntity.class,obj);
    }

    /**
     * 解析 document 对象
     *
     * @param document jsoup解析html文件生成的document对象
     * @param clazz    爬虫实体类类对象
     * @param obj      obj 用来判断返回的是集合还是一个实体类对象
     */
    private static Object parseDocument(Document document, Class<?> clazz, Object obj)
            throws InstantiationException, IllegalAccessException, OriginalStructureException {

        // 1. 获取爬虫实体类上定义的css选择器
        /**
         * 若想要返回的结果是List集合，jsoupDocument中的css选择器不能为空
         * 若想要返回的结果是实体类对象，jsoupDocument中的css选择器可以为空
         */
        String cssQuery = clazz.getAnnotation(JsoupDocument.class).cssQuery();

        // 获取该类上的所有字段
        Field[] declaredFields = clazz.getDeclaredFields();

        // 2. 通过obj判断我们最后想要返回的是爬虫实体类集合还是爬虫实体类对象
        // 2.1 若我们想要获取爬虫实体类集合
        if (obj instanceof List) {
            if (StringUtils.isBlank(cssQuery)) {
                throw new OriginalStructureRuntimeException("若想要返回爬虫实体类集合，请指定该爬虫实体类上的css选择器");
            }
            Elements entityElements = document.select(cssQuery);
            // 强制类型转换，得到传入的List集合，并将得到的爬虫实体类对象加入集合中
            List result = (List) obj;
            for (Element element : entityElements) {
                Object object = clazz.newInstance();
                // 2.2 通过反射为object对象赋值
                Object o = setFieldValue(declaredFields, element, object);
                result.add(o);
            }
            return result;
        } else {
            // 3. 若我们想要获取爬虫实体类对象
            Object object = clazz.newInstance();
            if (StringUtils.isBlank(cssQuery)) {
                // 3.1 如果返回的对象是实体类对象，而且没有指定类上的css选择器，就直接通过document.selector定位标签位置
                setFieldValue(declaredFields, document, object);
            } else {
                // 3.2 如果返回的对象是实体类对象，但指定了css选择器，就先获取类上的elements对象
                Elements entityElements = document.select(cssQuery);
                // 通过实体类接收,实体类只接收第一个element
                if (entityElements.size() == 0) {
                    return obj;
                }
                Element element = entityElements.get(0);
                // 指定了css选择器，就采用element继续定位标签
                setFieldValue(declaredFields, element, object);
            }
            return object;
        }
    }

    /**
     * 通过element为obj对象上的declaredFields字段赋值
     *
     * @param declaredFields obj对象上的属性值
     * @param element        通过element上的元素信息为declaredFields字段赋值
     * @param obj            需要被赋值的对象
     */
    private static Object setFieldValue(Field[] declaredFields, Element element, Object obj)
            throws InstantiationException, IllegalAccessException, OriginalStructureException {

        // 1. 遍历所有属性，每个字段属性上都有Extract注解，注解中保存了具体的cssQuery信息能够定位到字段的value值
        for (Field field : declaredFields) {
            field.setAccessible(true);
            // 2. 获取实体类属性上的JsoupDocument注解
            // 若字段上标记了JsoupDocument注解，表明该字段的引用要么是一个爬虫实体类要么是一个List集合类
            JsoupDocument fieldJsoupDocument = field.getAnnotation(JsoupDocument.class);
            if (fieldJsoupDocument != null) {
                // 获取该字段对应的类
                Class<?> fieldType = field.getType();
                // 用来接收List集合中的泛型类
                Class<?> actualTypeArgument = null;
                Object childValue;
                // 2.1 如果该字段对应的类是一个List
                if (fieldType.equals(List.class)) {
                    // 2.1.1 获取List中的泛型类型
                    Type genericType = field.getGenericType();
                    // 将上面的type类型转成class类型
                    if (genericType instanceof ParameterizedType) {
                        ParameterizedType pt = (ParameterizedType) genericType;
                        //得到泛型里的class对象
                        actualTypeArgument = (Class<?>) pt.getActualTypeArguments()[0];
                    }
                    // 集合中的泛型不能为空
                    if (actualTypeArgument == null) {
                        throw new OriginalStructureRuntimeException("实体类中定义的List属性必须定义泛型类型");
                    }
                    // 2.1.2 递归执行executeLocal方法（需要传入泛型类对象和一个List集合接收结果值）
                    childValue = executeLocal(actualTypeArgument, new ArrayList<Object>()); // 递归
                } else {
                    // 2.2 如果该字段对应的类是一个爬虫实体类 递归执行executeLocal方法（需要传入字段引用类和一个普通类对象）
                    childValue = executeLocal(fieldType, fieldType.newInstance()); // 递归
                }
                field.set(obj, childValue);
                continue;
            }
            // 3. 通过Extract注解上标记的cssQuery精确定位到具体的值
            Extract extract = field.getAnnotation(Extract.class);
            if (extract != null) {
                // 3.1 获取字段上的cssQuery
                String fieldQuery = extract.cssQuery();
                Elements fieldElement = element.select(fieldQuery);
                // 判断该字段的值是否需要从属性中获取
                String attr = extract.attr();

                // 3.2 检查获取的值是否是标签内的属性值
                if (StringUtils.isNotBlank(attr)) {
                    // 从属性中获取值，赋值给字段
                    String attrValue = fieldElement.attr(attr);
                    // 对获取到的值进行加工
                    attrValue = dealValue(attrValue, extract);
                    field.set(obj, attrValue);
                } else {
                    // 3.3 如果attr为空，获取contentType，判断是获取text还是获取html的值
                    ContentTypeEnum contentTypeEnum = extract.contentType();
                    if (contentTypeEnum == ContentTypeEnum.TEXT) {
                        String fieldText = fieldElement.text();
                        // 对获取到的值进行加工处理
                        fieldText = dealValue(fieldText, extract);
                        // 表示需要将字段设置为text值
                        field.set(obj, fieldText);
                    } else if (contentTypeEnum == ContentTypeEnum.HTML) {
                        String fieldHtml = fieldElement.html();
                        // 对获取到的值进行加工处理
                        fieldHtml = dealValue(fieldHtml, extract);
                        // 表示需要将字段设置为html值
                        field.set(obj, fieldHtml);
                    } else {
                        throw new OriginalStructureRuntimeException("字段抽取类型只能是text和html");
                    }
                }
            }
        }
        return obj;
    }

    /**
     * 加工字符串
     */
    private static String dealValue(String fieldText, Extract extract) {
        fieldText = fieldText.replaceFirst(extract.prefix(), "");
        String[] regex = extract.regex();
        String[] replacement = extract.replacement();
        if (replacement.length != regex.length) {
            throw new OriginalStructureRuntimeException("regex和replacement参数应该一一对应");
        }
        if (regex.length != 0) {
            for (int i = 0; i < regex.length; i++) {
                fieldText = fieldText.replaceAll(regex[i], replacement[i]);
            }
        }
        return fieldText;
    }

    public static void main(String[] args) throws OriginalStructureException, InstantiationException, IllegalAccessException {
        //List<OuterTaskEntity> list = new ArrayList<>();
        OuterTaskEntity o = (OuterTaskEntity) executeLocal(OuterTaskEntity.class, new OuterTaskEntity());
        System.out.println(o.getDrugType());
        System.out.println(o.getTaskType());
        List<TaskEntity> taskEntities = o.getTaskEntities();
        for (TaskEntity taskEntity : taskEntities) {
            System.out.println(taskEntity);
        }
    }
}
