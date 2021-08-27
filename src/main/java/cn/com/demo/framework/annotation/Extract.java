package cn.com.demo.framework.annotation;

import cn.com.demo.framework.enums.ContentTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 抽取爬取结果赋值给字段
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Extract {

    /**
     * css选择器
     * @return
     */
    String cssQuery();

    /**
     * replaceAll的regex参数
     * 采用正则表达式替换抽取的值
     * @return
     */
    String[] regex() default {};

    /**
     * replaceAll的replacement参数
     * @return
     */
    String[] replacement() default {};

    /**
     * 获取值的方式
     * 1 表示获取text
     * 2 表示获取html
     * @return
     */
    ContentTypeEnum contentType() default ContentTypeEnum.TEXT;

    /**
     * 当这个字段不为空，该字段的值会从该属性中获取
     * @return
     */
    String attr() default "";

    /**
     * 清除前缀
     * @return
     */
    String prefix() default "";
}
