package com.mobigen.snet.supportagent.component;

import com.mobigen.snet.supportagent.concurrents.OneTimeThread;
import com.mobigen.snet.supportagent.dao.DaoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by osujin12 on 2017. 1. 19..
 */
@Component
public class ShadowDecryptComponet extends AbstractComponent {

    @Value("${snet.support.JTR.path}")
    private String shadowFIlePath;

    @Autowired
    private DaoMapper daoMapper;

    private String jtrShell = "john.sh";

    public void init(){
    	try {
    		OneTimeThread worker = new OneTimeThread() {
    			@Override
    			public void task() throws Exception {
    				decryptShadowFile();
    			}
    		};
    		worker.start();
		} catch (Exception e) {
			logger.error("Shadow file Watcher Exception :: {}", e.getMessage(), e.fillInStackTrace());
		}
    }


    public void decryptShadowFile() throws IOException {

        logger.info("Start SHADOW FILE Decrypt.!!");

        HashMap<String,List<String>> pwList = new HashMap<>();
        HashMap<String , String> decryptResult = new HashMap<>();

        FileSystem fs = FileSystems.getDefault();
        Path watchPath =  fs.getPath(shadowFIlePath);
        WatchService watchService = fs.newWatchService();

        watchPath.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE);

        List<String> list = null;
        String assetCd = "";
        String fileFullPath = "";

        while (true) {
            try {

                // 지정된 디렉토리에 변경이되는지 이벤트를 모니터링한다.
                WatchKey changeKey = watchService.take();

                List<WatchEvent<?>> watchEvents = changeKey.pollEvents();

                for (WatchEvent<?> watchEvent : watchEvents) {
                    // Ours are all Path type events:
                    @SuppressWarnings("unchecked")
					WatchEvent<Path> pathEvent = (WatchEvent<Path>) watchEvent;

                    Path path = pathEvent.context();

                    //파일명에서 assetCd 추출

                    assetCd = path.toString().substring(0,path.toString().length()-4);

                    fileFullPath = path.toString();
                    list = doJTRservice(fileFullPath);

                    pwList.put(assetCd,list);

                    logger.info("Decrypt ==> "+assetCd);

                    int countPw = daoMapper.selectPwChk(assetCd);
                    if (countPw >= 1){
                        daoMapper.deletePwChk(assetCd);
                    }

                    //data insert
                    if(pwList.get(assetCd).size() != 0){
                        String crackId = "";
                        decryptResult.put("assetCd",assetCd);
                        for (String userId : pwList.get(assetCd)){
                            //Dao 호출
                            logger.debug(assetCd+" :: "+userId);
                            crackId += userId + " , ";
                        }
                        decryptResult.put("userId",crackId);

                        daoMapper.insertCrackId(decryptResult);

                    }else{
                        logger.info(assetCd + " PassWord is OK.");
                    }

                    pwList.remove(assetCd);

                    //delete file
                    deleteFile(fileFullPath);
                }

                changeKey.reset(); // Important!

            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                pwList.remove(assetCd);
            } catch (IOException e) {
                logger.error(e.getMessage());
                pwList.remove(assetCd);
            }
        }
    }

    @SuppressWarnings("static-access")
	List<String> doJTRservice(String fileNm) throws InterruptedException, IOException {
        List<String> slist = new ArrayList<>();
        String filePath = shadowFIlePath;
        String cmd = filePath + jtrShell + " " + filePath + fileNm;
        Process p = Runtime.getRuntime().exec(cmd);

        Thread.currentThread().sleep(1000);


        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));

        //  read the output from the command
//        System.out.printf("Here is the standard output of the command:\n");
        String s = null;
        Pattern pattern= Pattern.compile("\\((.*?)\\)");
        Matcher m = null;


        while ((s = stdInput.readLine()) != null) {
            if(s.contains("password hashes") || s.contains("--format=crypt") || s.contains("Warning") || s.contains("md5crypt") || s.contains("traditional") || s.contains("descrypt") || s.contains("generic crypt"))
                s="";

            m = pattern.matcher(s);
            while(m.find()) {
                logger.info("find match : " + m.group(1));
                slist.add(m.group(1));
            }

        }

        p.waitFor(); // 0 = 성공 , 1 = 실패
        stdInput.close();
        stdError.close();

        return slist;
    }

    public void deleteFile(String filePath) {
        try {
            System.out.println("file Delete : "+filePath);
            File file = new File(shadowFIlePath+filePath);
            file.delete();
        }catch (NullPointerException e){}
    }
}
