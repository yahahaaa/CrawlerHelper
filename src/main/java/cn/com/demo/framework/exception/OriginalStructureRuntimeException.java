package cn.com.demo.framework.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @Description: TODO
 * @Auther mzli.wesley
 * @Date 2021/8/21
 * @Version V1.0
 **/
public class OriginalStructureRuntimeException extends RuntimeException{
    public OriginalStructureRuntimeException(String message) {
        super(message);
    }

    public OriginalStructureRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
