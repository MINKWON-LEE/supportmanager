package com.mobigen.snet.supportagent.models.api;

import lombok.*;

/**
 * resultCode 응답 코드
 *  ㄴ 200(success)
 *  ㄴ 400(Bad request Parameter)
 *  ㄴ 403(Access Denied)
 *  ㄴ 404(Data Not Found)
 *  ㄴ 500(UnknownException)
 * returnType
 *  ㄴ string
 *  ㄴ number
 *  ㄴ object
 *  ㄴ list
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApiResult {
    @Builder.Default
    private int resultCode = 200;
    private String returnType; //string, integer, list, object
    private String resultMessageCode;
    private Object resultData;

}
