package com.mobigen.snet.supportagent.component;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mobigen.snet.supportagent.dao.DaoMapper;
import com.mobigen.snet.supportagent.utils.DateUtil;

import jodd.util.StringUtil;

@Component
@SuppressWarnings("unchecked")
public class MakeReportMailHtmlTemplate extends AbstractComponent{

	@Autowired
	private DaoMapper daoMapper;
	
	public String getTextHtmlTemplate(String userId, String textHtml) throws Exception{
		
		/*
		 * TEXT HTML REPLACE 
		 */
		String searchDate = "[#DATE]";
		String searchData1 = "[#DATA_1]";
		String searchData2 = "[#DATA_2]";
		String searchData3 = "[#DATA_3]";
		String searchData4 = "[#DATA_4]";
		String searchData5 = "[#DATA_5]";
		
		String replaceDate  = "";
		String replaceData1  = "";
		String replaceData2  = "";
		String replaceData3  = "";
		String replaceData4  = "";
		String replaceData5  = "";
		
		StringBuilder sb = new StringBuilder();
		String currentDate = DateUtil.getCurrDateBySecond();
		
		sb.append(DateUtil.getYear(currentDate)+"년 ")
		.append(DateUtil.getMonth(currentDate)+"월 ")
		.append(DateUtil.getDay(currentDate)+"일 ")
		.append(DateUtil.getHour(currentDate)+"시 ")
		.append(DateUtil.getCurrDateByStringFormat("mm")+"분 ");
		
		replaceDate = sb.toString();

		List<Map<String, String>> branchList = (List<Map<String, String>>) daoMapper.branchSwAuditRateBranchList();
		if(!branchList.isEmpty()) {
			/**
			 * 서버별 현행화율
			 * @return
			 */
			List<Map<String, String>> branchServerActualizingRateMap = (List<Map<String, String>>) daoMapper.branchServerActualizingRate();
			if(!branchServerActualizingRateMap.isEmpty()) {
				replaceData1 = makeHtmlTextBranchServerActualizingRate(branchList, branchServerActualizingRateMap);
				replaceData4 = makeHtmlTextBranchServerActualizingRate(branchServerActualizingRateMap);
			}
			/**
			 * 부서별 SW별 보안 준수율
			 * @return
			 */
			List<Map<String, String>> branchSwAuditRateMap = (List<Map<String, String>>) daoMapper.branchSwAuditRate();
			if(!branchSwAuditRateMap.isEmpty()) {
				replaceData2 = makeHtmlTextBranchSwAuditRate(branchList, branchSwAuditRateMap);
				replaceData5 = makeHtmlTextBranchSwAuditRate(branchSwAuditRateMap);
			}

			/**
			 * 담당자별 현행화율
			 * @return
			 */
			List<Map<String, String>> branchActualizingRateMap = (List<Map<String, String>>) daoMapper.branchActualizingRate(userId);
			if(!branchActualizingRateMap.isEmpty()) {
				replaceData3 = makeHtmlTextBranchActualizingRate(branchActualizingRateMap);
			}

			textHtml = StringUtil.replace(textHtml, searchDate, replaceDate);
			textHtml = StringUtil.replace(textHtml, searchData1, replaceData1);
			textHtml = StringUtil.replace(textHtml, searchData2, replaceData2);
			textHtml = StringUtil.replace(textHtml, searchData3, replaceData3);
			textHtml = StringUtil.replace(textHtml, searchData4, replaceData4);
			textHtml = StringUtil.replace(textHtml, searchData5, replaceData5);

		}
		return textHtml;
	}
	
	/**
	 * HTML본문 만들기
	 * [#DATA_1]
	 * @param listMap
	 * @return
	 */
	public String makeHtmlTextBranchServerActualizingRate(List<Map<String, String>> branchList, List<Map<String, String>> listMap ){

		StringBuilder result = new StringBuilder();
		StringBuilder template = new StringBuilder();

		double totalArCnt = Double.valueOf(String.valueOf(listMap.get(0).get("TOTAL_AR_CNT")));
		double totalAssetCnt = Double.valueOf(String.valueOf(listMap.get(0).get("TOTAL_ASSET_CNT")));
		double totalArRate = Math.round((totalArCnt/totalAssetCnt)*100);

		// 1-row
		result
		.append("<p style=\"margin-left:0cm; margin-right:0cm\"><strong><span style=\"font-family:&quot;맑은 고딕&quot;\">1. </span></strong><strong><span style=\"font-family:&quot;맑은 고딕&quot;\">서버현행화율")
		.append("(부문 평균):")
		.append(String.valueOf(totalArRate)+"%")
		.append("</span></strong></p>");
		
		template.append(makeBranchTableRow(branchList))
		.append("<tbody style=\"text-align:center;\">")
		// 2 - row
		.append(makeTrActualizing("ASSET_CNT", branchList, listMap))
		// 3 - row
		.append(makeTrActualizing("AR_RATE", branchList, listMap))
		// 4 - row
		.append(makeTrActualizing("SETUP_RATE", branchList, listMap))
		
		.append("</tbody>");
		
		return result.append(makeTableTemplate(template.toString())).toString();
	}

	/**
	 * HTML본문 만들기
	 * [#DATA_4]
	 * @param branchList
	 * @param listMap
	 * @return
	 */
	public String makeHtmlTextBranchServerActualizingRate(List<Map<String, String>> listMap){
		
		StringBuilder result = new StringBuilder();
		
		double totalArCnt = Double.valueOf(String.valueOf(listMap.get(0).get("TOTAL_AR_CNT")));
		double totalAssetCnt = Double.valueOf(String.valueOf(listMap.get(0).get("TOTAL_ASSET_CNT")));
		double totalArRate = Math.round((totalArCnt/totalAssetCnt)*100);
		
		// 1-row
		result
		.append("<p style=\"margin-left:0cm; margin-right:0cm\"><strong><span style=\"font-family:&quot;맑은 고딕&quot;\">1. </span></strong><strong><span style=\"font-family:&quot;맑은 고딕&quot;\">서버현행화율")
		.append("(부문 평균):")
		.append(String.valueOf(totalArRate)+"%")
		.append("</span></strong></p>");
		
		return result.toString();
	}
	
	/**
	 * [#DATA_2]
	 * HTML본문 만들기
	 * @param listMap
	 * @return
	 */
	public String makeHtmlTextBranchSwAuditRate(List<Map<String, String>> branchList, List<Map<String, String>> listMap ){
		
		
		double totalAuditRate = Double.valueOf(String.valueOf(listMap.get(0).get("TOTAL_AUDIT_RATE")));
		StringBuilder result = new StringBuilder();
		StringBuilder template= new StringBuilder();
		// 1-row
		result
		.append("<p style=\"margin-left:0cm; margin-right:0cm\"><strong><span style=\"font-family:&quot;맑은 고딕&quot;\">2. </span></strong><strong><span style=\"font-family:&quot;맑은 고딕&quot;\">보안준수율")
		.append("(부문 평균):")
		.append(String.valueOf(totalAuditRate)+"%")
		.append("</span></strong></p>");
		
		
		template.append(makeBranchTableRow(branchList))
		
		.append(" <tbody style=\"text-align:center;\">")
		// 2 - row 
		.append(makeTrbranchSwAuditRate("OS", branchList, listMap))
		// 3 - row 
		.append(makeTrbranchSwAuditRate("DB", branchList, listMap))
		// 4 - row 
		.append(makeTrbranchSwAuditRate("WEB", branchList, listMap))
		// 5 - row 
		.append(makeTrbranchSwAuditRate("WAS", branchList, listMap))
		// 6 - row 
		.append(makeTrbranchSwAuditRate("NW", branchList, listMap))
		// 7 - row 
		.append(makeTrbranchSwAuditRate("SE", branchList, listMap))
		.append(" </tbody>");
		
		return result.append(makeTableTemplate(template.toString())).toString();
	}

	/**
	 * [#DATA_5]
	 * HTML본문 만들기
	 * @param branchList
	 * @param listMap
	 * @return
	 */
	public String makeHtmlTextBranchSwAuditRate(List<Map<String, String>> listMap ){
		double totalAuditRate = Double.valueOf(String.valueOf(listMap.get(0).get("TOTAL_AUDIT_RATE")));
		StringBuilder result = new StringBuilder();
		// 1-row
		result
		.append("<p style=\"margin-left:0cm; margin-right:0cm\"><strong><span style=\"font-family:&quot;맑은 고딕&quot;\">2. </span></strong><strong><span style=\"font-family:&quot;맑은 고딕&quot;\">보안준수율")
		.append("(부문 평균):")
		.append(String.valueOf(totalAuditRate)+"%")
		.append("</span></strong></p>");
		return result.toString();
	}
	
	/**
	 * [#DATA_3]
	 * HTML본문 만들기
	 * @param listMap
	 * @return
	 */
	public String makeHtmlTextBranchActualizingRate(List<Map<String, String>> listMap ){
		
		StringBuilder template= new StringBuilder();
		
		String teamNm = listMap.get(0).get("TEAM_NM");
				
		template
		.append("<p style=\"margin-left:0cm; margin-right:0cm\"><strong><span style=\"font-family:&quot;맑은 고딕&quot;\">3. </span></strong><strong><span style=\"font-family:&quot;맑은 고딕&quot;\">")
		.append(teamNm)
		.append(" 담당자별 현행화율")
		.append("</span></strong></p>");
		
		// 1 - row
		template.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"MsoNormalTable\" style=\"width:100%; border-top:2px solid #555; border-bottom:1px solid #bbb; margin:5px 0; font-size:13px;\">")
		.append("			<colgroup>")
		.append("			<col width=\"200px\">")
		.append("			</colgroup>")
		.append("			<thead>")
		.append(" 				<tr style=\"background-color:#f3f7f8; height:40px; font-size:14px; font-weight:bold;\">")
		.append(" 				<th>순서</th>")
		.append(" 				<th>운용본부</th>")
		.append(" 				<th>담당자(정)</th>")
		.append(" 				<th>등록대수</th>")
		.append(" 				<th>장비정보현행화</th>")
		.append(" 				<th>에이전트설치율</th>")
		.append(" 				<th>보안준수율</th>")
		.append(" 				<th>불가사유 개수</th>")
		.append(" 				</tr>")
		.append(" 			</thead>")
		.append(" 			<tbody style=\"text-align:center;\">");
		
		// 2- row
		int i = 1;
		int assetCnt	= 0;
		double arCnt	= 0;
		double agentCnt	= 0;
		double cokCnt	= 0;
		double arRate	= 0;
		double setupRate= 0;
		String auditRate= String.valueOf(listMap.get(0).get("TEAM_AUDIT_RATE"));
		
		for(Map<String, String> map : listMap){
			template
			.append("<tr style=\"height:30px;\">")
			.append("<td>")
			.append(i)
			.append("</td>")
			.append("<td>")
			.append(map.get("TEAM_NM"))
			.append("</td>")
			.append("<td>")
			.append(map.get("USER_NM"))
			.append("</td>")
			.append("<td>")
			.append(String.valueOf(map.get("ASSET_CNT")))
			.append("</td>")
			.append("<td>")
			.append(String.valueOf(map.get("AR_RATE"))+"%")
			.append("</td>")
			.append("<td>")
			.append(String.valueOf(map.get("SETUP_RATE"))+"%")
			.append("</td>")
			.append("<td>")
			.append(String.valueOf(map.get("AUDIT_RATE")) +"%")
			.append("</td>")
			.append("<td>")
			.append(String.valueOf(map.get("COK_CNT")))
			.append("</td>")
			.append("</tr>");
			
			assetCnt	= assetCnt	+ Integer.parseInt(String.valueOf(map.get("ASSET_CNT")));
			arCnt		= arCnt		+ Integer.parseInt(String.valueOf(map.get("AR_CNT")));
			agentCnt	= agentCnt	+ Integer.parseInt(String.valueOf(map.get("AGENT_CNT")));
			cokCnt		= cokCnt	+ Integer.parseInt(String.valueOf(map.get("COK_CNT")));
			i++;
		}
		
		/*
		 * 현행화율 
		 * (전체 현행화 장비 / 전체 장비 ) * 100 
		 */
		arRate		= Math.round((arCnt/assetCnt)*100);
		
		/*
		 * 설치율
		 * (에이전트 설치 갯수 / 전체 장비 ) * 100
		 */
		setupRate	= Math.round((agentCnt/assetCnt)*100);
		
		// TOTAL 
		template
		.append("<tr style=\"background-color:#f4f4f4; height:30px; font-weight:bold;\">")
		.append("<td>")
		.append("합계")
		.append("</td>")
		.append("<td>")
		.append(teamNm)
		.append("</td>")
		.append("<td>")
		.append("담당자(정)")
		.append("</td>")
		.append("<td>")
		.append(String.valueOf(assetCnt))
		.append("</td>")
		.append("<td>")
		.append(String.valueOf(arRate)+"%")
		.append("</td>")
		.append("<td>")
		.append(String.valueOf(setupRate)+"%")
		.append("</td>")
		.append("<td>")
		.append(auditRate+"%")
		.append("</td>")
		.append("<td>")
		.append(String.valueOf(cokCnt))
		.append("</td>")
		.append("</tr>")
		.append("</tbody>")
		.append("</table>");
		
		return template.toString();
	}
	
	private String makeTableTemplate(String rowDataTemplate){
		StringBuilder template = new StringBuilder();
		template.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"MsoNormalTable\" style=\"width:100%;  border-top:2px solid #555; border-bottom:1px solid #bbb; margin:5px 0; font-size:13px;\">")
		.append("			<colgroup>")
		.append("			<col width=\"200px\">")
		.append("			</colgroup>")
		.append(rowDataTemplate)
		.append("</table>");
		return template.toString();
	}
	
	private String makeBranchTableRow(List<Map<String, String>> branchList){
		StringBuilder template = new StringBuilder();
		template
		.append("<thead>")
		.append("<tr style=\"background-color:#f3f7f8; height:40px; font-size:14px; font-weight:bold;\">")
		.append(" 				<th>항목</th>");
		
		// Make 1 - row 
		for(Map<String, String> map : branchList){
	        template.append("<th>");
	        template.append(map.get("BRANCH_NM")!=null? map.get("BRANCH_NM") :"-");
	        template.append("</th>");
		}
		
		template
		.append("<th>")
		.append("총계")
		.append("</th>")
		.append("</tr>")
		.append("</thead>");
		
		return template.toString();
	}
	
	private String makeTrActualizing(String type, List<Map<String, String>> branchList, List<Map<String, String>> listMap){
		
		String title = "";
		String total = "";
		switch (type) {
			case "ASSET_CNT":
				title = "장비등록대수";
				total = String.valueOf(listMap.get(0).get("TOTAL_ASSET_CNT"));
				break;
			case "AR_RATE":
				title = "장비정보현행화";
				total = String.valueOf(listMap.get(0).get("TOTAL_AR_RATE"));
				break;
			case "SETUP_RATE":
				title = "에이전트설치율";
				total = String.valueOf(listMap.get(0).get("TOTAL_SETUP_RATE"));
				break;
			default:
				break;
		}
		
		StringBuilder template = new StringBuilder();
		
		template
		.append("<tr style=\"height:30px;\">")
		.append("<td>")
		.append(title)
		.append("</td>")
		.append(makeTdActualizing(type, branchList, listMap))
		.append("<td>")
		.append(total)
		.append("</td>")
		.append("</tr>");

		return template.toString();
	}
	
	private String makeTdActualizing(String type, List<Map<String, String>> branchList, List<Map<String, String>> listMap){
		StringBuilder template = new StringBuilder();
		for(Map<String, String> branch : branchList){
			template.append("<td>");
			String result = "-";
			for(Map<String, String> map : listMap){
				if(map.get("BRANCH_ID")!=null && branch.get("BRANCH_ID").equals(map.get("BRANCH_ID"))){
					if(map.get(type) != null){
						result = String.valueOf(map.get(type));
					}
				}
			}
			template.append(result)
			.append("</td>");
		}
		return template.toString();
	}
	
	public String makeTrbranchSwAuditRate(String swType, List<Map<String, String>> branchList, List<Map<String, String>> listMap){
		
		StringBuilder template = new StringBuilder();
		String totalRate = "";

		template.append("<tr style=\"height:30px;\">")
		.append("<td>")
		.append(swType.toUpperCase())
		.append("</td>");
		
		for(Map<String, String> branch : branchList){
			template.append("<td>");
			String auditRate = "-";
			for(Map<String, String> map : listMap){
				if(map.get("BRANCH_ID")!=null){
					if(branch.get("BRANCH_ID").equals(map.get("BRANCH_ID"))){
						if(map.get("SW_TYPE") != null && map.get("SW_TYPE").equals(swType.toUpperCase())){
							auditRate = String.valueOf(map.get("AUDIT_RATE"))+"%";
							
							if(!totalRate.equals(map.get("TOTAL_RATE")))
								totalRate = String.valueOf(map.get("TOTAL_RATE"));
						}
					}
				}
			}
			
			template.append(auditRate)
			.append("</td>");
		}
		
		template
		.append("<td>")
		.append(!totalRate.equals("-") ? totalRate+"%":totalRate)
		.append("</td>")
		.append("</tr>");
		
		return template.toString();
	}
}
