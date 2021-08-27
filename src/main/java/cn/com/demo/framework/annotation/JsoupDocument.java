package cn.com.demo.framework.annotation;

import org.jsoup.Connection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识该实体类是用来接收爬虫结果
 */
@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsoupDocument {

    /**
     * 目标网站的域名
     * @return
     */
    String domain() default "";

    /**
     * 目标url
     * @return
     */
    String targetUrl() default "";

    /**
     * 读取本地html文件
     * 默认存储resources目录下
     * @return
     */
    String localUrl() default "";

    /**
     * CSS选择器（若实体类属性上指定了css选择，优先使用属性上定义的css选择器）
     * @return
     */
    String cssQuery() default "";

    /**
     * 请求方式
     * @return
     */
    Connection.Method method() default Connection.Method.GET;
}
