package com.sktelecom.mf.common.util;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TemplateUtils {

    /**
     * @param srcTemplate
     *            원본 템플릿
     * @param targetDataMap
     *            요청건에 대한 예약어 바인딩용 데이터 맵
     * @return 바인딩용 데이터가 적용된 결과 템플릿
     */
    public static String bindSimpleData(String srcTemplate, Map<String, String> targetDataMap) {
        String destTemlplate = srcTemplate;
        Iterator<String> iterator = targetDataMap.keySet().iterator();
        while (iterator.hasNext()) {
            String currentKey = iterator.next();
            destTemlplate = StringUtils.replace(destTemlplate, currentKey, verifyData(targetDataMap.get(currentKey)));
        }
        return destTemlplate;
    }

    public static String bindSimpleData(String srcTemplate, String targetKey, String targetValue) {
        String destTemlplate = StringUtils.replace(srcTemplate, targetKey, verifyData(targetValue));
        return destTemlplate;
    }

    private static String verifyData(String target) {
        String verifyString = null;
        if (StringUtils.isEmpty(target)) {
            verifyString = "<span style='background-color:#ff0000;'>자동맵핑정보 없음</span>";
        } else {
            verifyString = target;
        }
        return verifyString;
    }

    /**
     * @param srcTemplate
     *            원본 템플릿
     * @param targetDataStr
     *            요청건에 대한 예약어 바인딩용 문자열
     * @param tHeader
     *            테이블 헤더
     * @param tBodyList
     *            테이블 바디 목록
     * @return 바인딩용 그리드가 적용된 결과 템플릿
     */
    public static String bindGridData(String srcTemplate, String targetDataStr, List<String> tHeader, List<List<String>> tBodyList) {
        String destTemplate = null;
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1'>");
        sb.append("    <thead>");
        sb.append("        <tr>");
        sb.append("            " + generateDataSet(tHeader, "background-color:#999999"));
        sb.append("        </tr>");
        sb.append("    </thead>");
        sb.append("    <tbody>");
        int rowNum = 1;
        for (List<String> row: tBodyList) {
            sb.append("        <tr>");
            sb.append("            " + generateDataSet(row, ""));
            sb.append("        </tr>");
        }
        sb.append("    </tbody>");
        sb.append("</table>");
        destTemplate = StringUtils.replace(srcTemplate, targetDataStr, verifyData(sb.toString()));
        return destTemplate;
    }

    /**
     *
     * @param list 컬럼값
     * @param styleString 컬럼 스타일 문자열
     * @return 컬럼값이 맵핑된 테이블로우
     */
    public static String generateDataSet(List<String> list, String cssAttribute) {
        StringBuilder sb = new StringBuilder();
        for (String data : list) {
            sb.append(String.format("<td style='text-align: center;%s'>%s</td>", cssAttribute, nullToEmpty(data)));
        }
        return sb.toString();
    }

    private static String nullToEmpty(String str) {
        String result = StringUtils.stripToEmpty(str);
        if (result.equalsIgnoreCase("null")) {
            result  = StringUtils.EMPTY;
        }
        return result;
    }

    public static String[] arrayCollectionToQueryArr(List selectedItemArray) {
        String[] queryArr = null;
        if (selectedItemArray.size() > 0)
            queryArr = new String[selectedItemArray.size()];

        String query = null;
        for (int i = 0; i < selectedItemArray.size(); i++) {
            HashMap<String, Object> asObject = (HashMap<String, Object>) selectedItemArray.get(i);
            query = objectToString(asObject.get("assetCd"));
            query += objectToString(asObject.get("swType"));
            query += objectToString(asObject.get("swNm"));
            query += objectToString(asObject.get("swInfo"));
            query += objectToString(asObject.get("swDir"));
            query += objectToString(asObject.get("swUser"));
            query += objectToString(asObject.get("swEtc"));
            queryArr[i] = query;
        }
        return queryArr;
    }

    public static String objectToString(Object asObject) {
        String result = null;

        if (asObject != null) {
            result = String.valueOf(asObject);
        } else {
            result = "";
        }
        return result;
    }
}
