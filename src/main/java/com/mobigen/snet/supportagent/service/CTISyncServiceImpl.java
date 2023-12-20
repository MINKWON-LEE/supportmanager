package com.mobigen.snet.supportagent.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mobigen.snet.supportagent.dao.DaoMapper;
import com.mobigen.snet.supportagent.entity.CTiSyncTarget;
import com.mobigen.snet.supportagent.utils.JsonUtil;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CTISyncServiceImpl extends AbstractService implements CTISyncService {

    @Value("${snet.ctisync.url}")
    private String syncUrl;
    @Value("${snet.ctisync.apikey}")
    private String apiKey;
    @Autowired
    private DaoMapper daoMapper;
    @Autowired
    JsonUtil jsonUtil;

    /**
     * 'sg_supprotmanager 프로젝트 - 스케줄러'
     */
    public void runSync()
    {
        this.logger.info("Sync CTi CVE Info start...");

        initSSL();
        try
        {
            this.logger.info("CTI REST Api is enabled. Sync start.");

            // CTI 연동 대상 조회
            List<CTiSyncTarget> cTiSyncTargetList = this.daoMapper.selectCTiSyncTartgetList();

            cTiSyncTargetList.sort(Comparator.comparing(CTiSyncTarget::getSwNm));

            for (CTiSyncTarget target : cTiSyncTargetList) {
                int count = 0;
                int page = 0;
                int length = 1000;
                int pageLength = 0;
                String url;
                String result;
                JsonObject obj;
                String cveCodes = "";
                String ctiCd = target.getCtiCd();
                String vendor = target.getSwType();
                String product = target.getSwNm();
                String version = target.getSwInfo();
                String orgSwType = target.getOrgSwType();
                String orgSwNm = target.getOrgSwNm();
                String orgSwInfo = target.getOrgSwInfo();

                String majorVersion = target.getMajorSwInfo();

                // CTI API를 통해 대상의 CVE 취약점 조회
                //String apiUrl = this.syncUrl + "/v1.1/sync/insight/cve/cpe/?format=json&length=" + length + "&apikey=" + this.apiKey + "&vendor=" + vendor + "&product=" + product + "&version=" + version;

                do {
                    page++;
                    String apiUrl = this.syncUrl + "/v1.1/sync/insight/cve/cpe/?format=json&length=" + length + "&apikey=" + this.apiKey + "&vendor=" + vendor + "&product=" + product + "&page=" + page;
                    //String apiUrl = "https://cti.igloosec.com//api/v1.1/sync/insight/cve/cpe/?format=json&length=1000&apikey=8d38617b9faf4ebf945ddb468db29a68&vendor=microsoft&product=internet_information_services&page=" + page;
                    url = apiUrl + "&version=" + version;
                    result = getResponse(url);

                    /*
                    if(result.equals("400")) {
                        url = apiUrl + "&version=" + majorVersion;
                        result = getResponse(url);
                        if(result.equals("400")) {
                            url = apiUrl + "&version=";
                            result = getResponse(url);
                         }
                    }*/

                    if (result.equals("400") || result.equals("500")) {
                        continue;
                    }

                    HashMap<String, Object> data = jsonUtil.convertMap(result);

                    if (data.get("errorCode") != null) {
                        if (data.get("errorCode").toString().equals("SE40"))
                            continue;
                        else
                            break;
                    }

                    if (!"".equals(String.valueOf(data.get("count")))) {
                        count = Integer.parseInt(String.valueOf(data.get("count")));
                        if (count <= 0) {
                            break;
                        }
                    }

                    List<HashMap<String, Object>> results = (List) data.get("results");
                    if (results != null && results.size() > 0) {
                        SimpleDateFormat formatInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        SimpleDateFormat formatOutput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        for (int i = 0; i < results.size(); i++) {
                            String cveId = "";
                            HashMap<String, Object> param = results.get(i);
                            String cveCode = String.valueOf(param.get("cveCode"));
                            target.setCveCode(cveCode);
                            target.setCveId(cveId);
                            target.setCweCode(String.valueOf(param.get("cweCode")));
                            target.setDescription(String.valueOf(param.get("description")));
                            target.setId(Integer.valueOf(String.format("%.0f", param.get("id"))));
                            target.setPublishDate(formatOutput.format(formatInput.parse(String.valueOf(param.get("publishDate")))));
                            target.setReference(String.valueOf(param.get("reference")).replaceAll("\"", "").replaceAll("\\[", "").replaceAll("]", ""));
                            target.setTimeRegDate(formatOutput.format(formatInput.parse(String.valueOf(param.get("timeRegDate")))));
                            target.setUpdateDate(formatOutput.format(formatInput.parse(String.valueOf(param.get("updateDate")))));
                            target.setV2Score(String.valueOf(param.get("v2Score")));
                            target.setV2Severity(String.valueOf(param.get("v2Severity")));
                            target.setV3Score(String.valueOf(param.get("v3Score")));
                            target.setV3Severity(String.valueOf(param.get("v3Severity")));

                            String cpeUriList = "";
                            List<HashMap<String, Object>> cpeList = (List) param.get("cpe");
                            if (cpeList != null && cpeList.size() > 0) {
                                for (Map<String, Object> cpe : cpeList) {
                                    logger.info("cpe_match: " + cpe.get("cpe_match"));

                                    List<HashMap<String, Object>> cpeMatchList = (List) cpe.get("cpe_match");

                                    if (cpeMatchList != null && cpeMatchList.size() > 0) {
                                        for (Map<String, Object> cpeMatch : cpeMatchList) {
                                            if (!"".equals(cpeUriList))
                                                cpeUriList += "," + cpeMatch.get("cpe23Uri");
                                            else
                                                cpeUriList += cpeMatch.get("cpe23Uri");

                                        }
                                    }
                                }
                            }

                            cveId = daoMapper.selectCTICveCodeData(target);

                            target.setCpe(cpeUriList);

                            if (cveId != null && !"".equals(cveId)) {
                                daoMapper.updateCTICveCodeData(target);
                            } else {
                                daoMapper.insertCTICveCodeData(target);
                            }

                            if (!"".equals(cveCodes))
                                cveCodes += "," + cveCode;
                            else
                                cveCodes += cveCode;
                        }
                    }
                    pageLength = page * length;
                } while (count > pageLength);

                // 조회결과 CVE 취약점을 업데이트
                if (!"".equals(cveCodes)) {
                    CTiSyncTarget param = new CTiSyncTarget();

                    param.setSwType(orgSwType);
                    param.setSwNm(orgSwNm);
                    param.setSwInfo(orgSwInfo);

                    param.setCtiCd(ctiCd);

                    param.setCveCodes(cveCodes.replaceAll("\"", ""));
                    param.setCveCount(cveCodes.split(",").length);

                    String assetSwCd = daoMapper.selectCTICveCode(param);
                    if (assetSwCd != null && !"".equals(assetSwCd)) {
                        param.setAssetSwCd(assetSwCd);
                        daoMapper.updateCTICveCode(param);
                    } else {
                        param.setAssetSwCd(makeAsserSwCd());
                        daoMapper.insertCTICveCode(param);
                    }

                    String cveCodeCd = daoMapper.selectCTICveCodeMapp(param);
                    if (cveCodeCd != null && !"".equals(cveCodeCd)) {
                        param.setCveCodeCd(cveCodeCd);
                        daoMapper.updateCTICveCodeMapp(param);
                    } else {
                        daoMapper.insertCTICveCodeMapp(param);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error(e.getMessage(), e);
        }
        this.logger.info("Sync CTi CVE Info end...");
    }

    private String makeAsserSwCd() {
        String assetSwCd = "";
        try {
            assetSwCd = daoMapper.makeAsserSwCd();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return assetSwCd;
    }

    public void initSSL() {
        TrustManager[] trustAllCerts = {new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception localException) {
        }
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    private HashMap<String, String> parseCPE(String data)
    {
        HashMap<String, String> map = new HashMap();
        data = data.replaceAll(" ", "").replaceAll("-", "").replaceAll("_", "").toUpperCase();
        String[] tmp = data.split(":");
        if (tmp.length == 5) {
            try
            {
                map.put("manufacturer", tmp[2]);
                map.put("product", tmp[3]);
                String version = tmp[4].split("\\|")[0];
                map.put("version", version);
                map.put("op", tmp[4].split("\\|")[1]);
                this.logger.info("manufacturer : " + tmp[2]);
                this.logger.info("product : " + tmp[3]);
                this.logger.info("version : " + version);
                this.logger.info("op : " + tmp[4].split("\\|")[1]);
            }
            catch (Exception e)
            {
                this.logger.error("parsing CPE error occured. " + data);
            }
        } else {
            return null;
        }
        return map;
    }

    public String getResponse(String url)
    {
        HttpsURLConnection conn = null;
        BufferedReader reader = null;
        try
        {
            this.logger.info("request url : " + url);
            URL listUrl = new URL(url);

            conn = (HttpsURLConnection)listUrl.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(10000);
//            String key = this.apiKey;
//            this.logger.info("key string : " + key);
//            this.logger.info("base64 string : Basic " + new String(Base64.encodeBase64(key.getBytes("UTF-8"))));
//            conn.setRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64(key.getBytes("UTF-8"))));
//            conn.setRequestProperty("Accept", "Accept: application/json");

            conn.connect();

            if (conn.getResponseCode() == 400) {
                return "400";
            } else if (conn.getResponseCode() == 500) {
                return "500";
            }

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuffer buffer = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\r\n");
            }
            return buffer.toString();
        }
        catch (Exception e)
        {
            this.logger.error(e.getMessage(), e);
        }
        finally
        {
            try
            {
                if (reader != null) {
                    reader.close();
                    conn.disconnect();
                }
            }
            catch (IOException e)
            {
                this.logger.error(e.getMessage(), e);
            }
        }
        return "";
    }

    public static void main(String args[]) {
        /*ApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:applicationContext.xml");
        CTISyncServiceImpl syncService = new CTISyncServiceImpl();
        syncService.runSync();*/
        /*CTISyncServiceImpl syncService = new CTISyncServiceImpl();

        String uri = "https://cti.igloosec.com/api/v1/sync/nvd-cve/?apikey=88fe2f05bb2e4cc3900e5679f35455de&format=json&page=1&length=1000";
        HashMap<String, String> productMap = new HashMap<>();
        JsonObject obj;
        syncService.initSSL();
        String result;

        do {
            result = syncService.getResponse(uri);
            obj = new JsonParser().parse(result).getAsJsonObject();
            int cnt = obj.get("count").getAsInt();
            if (cnt <= 0)
            {
                System.out.println("sync count is zero. end.");
                return;
            }
            JsonArray arr = obj.get("results").getAsJsonArray();
            for (int i = 0; i < arr.size(); i++) {
//                System.out.println("========================" + arr.get(i).getAsJsonObject().get("cveCode").toString());
                JsonArray vendors = arr.get(i).getAsJsonObject().get("vendor").getAsJsonArray();
                if (vendors.size() > 0) {
                    for (int j = 0; j < vendors.size(); j++) {
                        JsonArray products = vendors.get(j).getAsJsonObject().get("product").getAsJsonObject().get("product_data").getAsJsonArray();
                        for (int k = 0; k < products.size(); k++) {
//                            System.out.println(products.get(k).getAsJsonObject().get("product_name").toString());
                            productMap.put(products.get(k).getAsJsonObject().get("product_name").toString(), "");
                        }
                    }
                }
            }
            if (!obj.get("next").isJsonNull())
                uri = obj.get("next").getAsString();
        }while(!obj.get("next").isJsonNull());

//        System.out.println("======================== Start print product list ================================================");
        for(String product : productMap.keySet()){
            System.out.println(product);
        }*/
    }
}
