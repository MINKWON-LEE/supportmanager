package com.mobigen.snet.supportagent.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CaseFormat;

import jodd.util.StringUtil;

/**
 * Created by osujin12 on 2016. 2. 18..
 */
public class CommonUtils {

	private static Logger logger = LoggerFactory.getLogger(CommonUtils.class);

	public static String makeFileName() {
		Random random = new Random();
		long rand = random.nextLong();

		if (rand < 0)
			rand = rand * -1;

		return String.valueOf(rand);
	}

	public static String printError(Exception e) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(os));

		return new String(os.toByteArray());
	}

	public static void runProcess(String cmd) {
		Process p;
		String line;
		try {
			logger.info("[ShellCommandHandler] cmd = " + cmd);
			p = Runtime.getRuntime().exec(cmd);
			logger.info("*[p] : {}", p);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = br.readLine()) != null) {
				logger.debug(line);
			}
			p.waitFor();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Run Process Exception :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	public static ArrayList<String> getRunProcess(String cmd) {
		Process p;
		String line;
		ArrayList<String> result = new ArrayList<String>();
		try {
			logger.info("[ShellCommandHandler] cmd = " + cmd);
			p = Runtime.getRuntime().exec(cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = br.readLine()) != null) {
				logger.debug(line);
				result.add(line);
			}
			p.waitFor();

		} catch (Exception e) {
			logger.error("Run Process Exception :: {}", e.getMessage(), e.fillInStackTrace());
		}
		return result;
	}

	public static ArrayList<String> getRunProcess(String[] cmd) {
		Process p;
		String line;
		ArrayList<String> result = new ArrayList<String>();
		try {
			logger.info("[ShellCommandHandler] cmd = " + StringUtil.join(cmd, " "));
			p = Runtime.getRuntime().exec(cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				result.add(line);
			}
			p.waitFor();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Run Process Exception :: {}", e.getMessage(), e.fillInStackTrace());
		}
		return result;
	}

	public static boolean fileCopy(String inFileName, String outFileName) {
		try {
			FileInputStream fis = new FileInputStream(inFileName);
			FileOutputStream fos = new FileOutputStream(outFileName);

			int data = 0;
			while ((data = fis.read()) != -1) {
				fos.write(data);
			}
			fis.close();
			fos.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * return HHmm
	 **/
	@SuppressWarnings("static-access")
	public static boolean compareDiagnosisDate(String start, String end) {

		DateUtil dateUtil = new DateUtil();
		String nowYYYYmmdd = dateUtil.getCurrDate();

		start = nowYYYYmmdd + start;
		end = nowYYYYmmdd + end;

		long nowT = dateUtil.getCurTimeInMilis();
		long startT = 0;
		long endT = 0;

		startT = dateUtil.StringToMili(start);
		endT = dateUtil.StringToMili(end);

		if (startT >= endT) {
			endT += 60 * 60 * 1000 * 24;
		}

		if (startT <= nowT && nowT <= endT) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean moveFile(String orgiFile, String movePath) {

		File uploadFile = new File(orgiFile);
		File moveFile = new File(movePath);

		return uploadFile.renameTo(moveFile);
	}

	public static boolean isOpenPort(String host, int port) {
		boolean result = false;
		try {
			Socket socket = new Socket();
			logger.debug("Test connection Ip :: {} :: {}", host, port);
			socket.connect(new InetSocketAddress(host, port), 1000);
			result = true;
			logger.debug("open Ip :: {} :: open port :: {}", host, port);
			socket.close();
		} catch (Exception e) {
			logger.debug("Exception Ip :: {} :: open port :: {}  Exception :: [ {} ]", host, port, e.getMessage());
			return false;
		}
		return result;
	}

	public static String findFileEncoding(String fileFullPath) throws IOException {
		byte[] buf = new byte[4096];

		@SuppressWarnings("resource")
		java.io.FileInputStream fis = new java.io.FileInputStream(fileFullPath);

		// (1)
		UniversalDetector detector = new UniversalDetector(null);

		// (2)
		int nread;
		while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
			detector.handleData(buf, 0, nread);
		}
		// (3)
		detector.dataEnd();

		// (4)
		String encoding = detector.getDetectedCharset();
		if (encoding != null) {
			// System.out.println("Detected encoding = " + encoding);
		} else {
			// System.out.println("No encoding detected.");
		}

		// (5)
		detector.reset();

		if (encoding == null)
			encoding = "";

		return encoding;
	}

	public static String camelToDbStyle(String str) {
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
	}

	public static void mkdir(String path) {
		try {
			File file = new File(path);
			if (!file.exists() && !file.isDirectory()) {
				logger.debug("mkdir path:: {}",path);
				file.mkdirs();
				Runtime.getRuntime().exec("chown -R sgweb:sgweb " + path);
			}
		} catch (Exception e) {
			logger.error("Mkdir Exception :: " + e.getMessage(), e);
		}
	}
	
	
	public static void deleteFile(File f){
		try {
			if(f.isFile() && f.exists()){
				f.delete();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void deleteFile(String path){
		try {
			File f = new File(path);
			if(f.isFile() && f.exists()){
				f.delete();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void close(Closeable s){
		if(s!=null){
			try {
				s.close();
			} catch (Throwable e) {
				logger.error("[ignore] close : " + e.getMessage(), e);
			}
		}
	}
	
	
	public static void sleep(int second){
		try {
			Thread.sleep(second * 1000);
		} catch (Throwable e) {
			logger.error("[ignore] sleep : " + e.toString());
		}
	}
	
	public static interface SafeRunnable{
		public void run() throws Exception;
	}
	
	public static void runSafely(SafeRunnable r){
		runSafely(r, false);
	}
	
	public static void runSafely(SafeRunnable r, boolean depressErrorLog){
		try {
			r.run();
		} catch (Throwable e) {
			if(!depressErrorLog){
				logger.error("ignored exception", e);
			}
		}
	}
}
