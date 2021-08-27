package cn.com.demo.framework.pojo;

import cn.com.demo.framework.annotation.Extract;
import cn.com.demo.framework.annotation.JsoupDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: TODO
 * @Auther mzli.wesley
 * @Date 2021/8/22
 * @Version V1.0
 **/
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
