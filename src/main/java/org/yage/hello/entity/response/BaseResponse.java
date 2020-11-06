package org.yage.hello.entity.response;

import lombok.Data;

/**
 * @author 常振亚
 * @version 1.0
 * @date 2020/9/21
 * @date 23:15
 */
@Data
public class BaseResponse {

    private String code;
    private String text;
    private Object data;

    /**
     * @param data
     * @return
     */
    public static BaseResponse success(Object data) {

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode("00000");
        baseResponse.setText("SUCCESS");
        baseResponse.setData(data);

        return baseResponse;
    }
}
