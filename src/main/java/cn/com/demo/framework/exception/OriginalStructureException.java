package cn.com.demo.framework.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: TODO
 * @Auther mzli.wesley
 * @Date 2021/8/21
 * @Version V1.0
 **/
public class OriginalStructureException extends Exception{

    public OriginalStructureException(String message) {
        super(message);
    }
}
