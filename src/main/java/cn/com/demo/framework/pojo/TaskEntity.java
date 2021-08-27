package cn.com.demo.framework.pojo;

import cn.com.demo.framework.annotation.Extract;
import cn.com.demo.framework.annotation.JsoupDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @Description: TODO
 * @Auther mzli.wesley
 * @Date 2021/8/21
 * @Version V1.0
 **/
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
