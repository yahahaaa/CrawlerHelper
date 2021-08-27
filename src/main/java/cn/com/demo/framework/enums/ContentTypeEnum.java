package cn.com.demo.framework.enums;

/**
 * @Description: TODO
 * @Auther mzli.wesley
 * @Date 2021/8/21
 * @Version V1.0
 **/
public enum ContentTypeEnum {

    // 表示获取标签内的值
    TEXT(1),
    // 表示获取完整的html标签
    HTML(2);
    private int type;

    ContentTypeEnum(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
