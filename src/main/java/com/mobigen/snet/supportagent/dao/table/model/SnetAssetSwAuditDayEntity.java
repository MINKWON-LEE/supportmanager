package com.mobigen.snet.supportagent.dao.table.model;

/**
 * <pre>
 *  - 테이블 설명 : 
 *  - 테이블 명 : SNET_ASSET_SW_AUDIT_DAY
 *  - PK :   asset_cd, sw_type, sw_nm, sw_info, sw_dir, sw_user, sw_etc
 *  - generated in : 2017-03-14 11:17
 * </pre>
 */
public class SnetAssetSwAuditDayEntity implements java.io.Serializable{

    /**<pre>
     * 설명 : 
     * 컬럼명 : asset_cd
     * 타입 : VARCHAR (33)
     * PK
     * </pre>
     */
    private String assetCd;

    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_type
     * 타입 : VARCHAR (20)
     * PK
     * </pre>
     */
    private String swType;

    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_nm
     * 타입 : VARCHAR (100)
     * PK
     * </pre>
     */
    private String swNm;

    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_info
     * 타입 : VARCHAR (100)
     * PK
     * </pre>
     */
    private String swInfo;

    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_dir
     * 타입 : VARCHAR (200)
     * PK
     * </pre>
     */
    private String swDir;

    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_user
     * 타입 : VARCHAR (100)
     * PK
     * </pre>
     */
    private String swUser;

    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_etc
     * 타입 : VARCHAR (100)
     * PK
     * </pre>
     */
    private String swEtc;

    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_day
     * 타입 : VARCHAR (16)
     * </pre>
     */
    private String auditDay;

    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_file_cd
     * 타입 : VARCHAR (33)
     * </pre>
     */
    private String auditFileCd;

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_regi
     * 타입 : INT (10.0)
     * </pre>
     */
    private String userRegi;

    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_info_sort_num
     * 타입 : VARCHAR (100)
     * </pre>
     */
    private String swInfoSortNum;

    /**<pre>
     * 설명 : 
     * 컬럼명 : create_date
     * 타입 : DATETIME (19)
     * </pre>
     */
    private java.sql.Timestamp createDate;

    /**<pre>
     * 설명 : 
     * 컬럼명 : url_service_target
     * 타입 : INT (10.0)
     * </pre>
     */
    private String urlServiceTarget;

    /**<pre>
     * 설명 : 
     * 컬럼명 : url_target_state
     * 타입 : INT (10.0)
     * </pre>
     */
    private String urlTargetState;

    /**<pre>
     * 설명 : 
     * 컬럼명 : closed_due_date
     * 타입 : DATE (10)
     * </pre>
     */
    private java.sql.Date closedDueDate;

    /**<pre>
     * 설명 : 
     * 컬럼명 : except_reason
     * 타입 : VARCHAR (1024)
     * </pre>
     */
    private String exceptReason;

    /**<pre>
     * 설명 : 
     * 컬럼명 : pre_chg_date
     * 타입 : VARCHAR (8)
     * </pre>
     */
    private String preChgDate;

    /**<pre>
     * 설명 : 
     * 컬럼명 : last_chg_date
     * 타입 : VARCHAR (8)
     * </pre>
     */
    private String lastChgDate;

    /**<pre>
     * 설명 : 
     * 컬럼명 : chg_page_cnt
     * 타입 : INT (10.0)
     * </pre>
     */
    private String chgPageCnt;

    /**<pre>
     * 설명 : 
     * 컬럼명 : added_chg_page_cnt
     * 타입 : INT (10.0)
     * </pre>
     */
    private String addedChgPageCnt;

    /**<pre>
     * 설명 : 
     * 컬럼명 : added_chg_cnt
     * 타입 : INT (10.0)
     * </pre>
     */
    private String addedChgCnt;

    /**<pre>
     * 설명 : 
     * 컬럼명 : represent_url
     * 타입 : INT (10.0)
     * </pre>
     */
    private String representUrl;

    /**<pre>
     * 설명 : 
     * 컬럼명 : url_management_cd
     * 타입 : VARCHAR (50)
     * </pre>
     */
    private String urlManagementCd;

    /**<pre>
     * 설명 : 
     * 컬럼명 : url_service_nm
     * 타입 : VARCHAR (200)
     * </pre>
     */
    private String urlServiceNm;

    /**<pre>
     * 설명 : 
     * 컬럼명 : url_person_flag
     * 타입 : VARCHAR (2)
     * </pre>
     */
    private String urlPersonFlag;

    /**<pre>
     * 설명 : 
     * 컬럼명 : url_location_flag
     * 타입 : VARCHAR (2)
     * </pre>
     */
    private String urlLocationFlag;

    /**<pre>
     * 설명 : 
     * 컬럼명 : url_charge_flag
     * 타입 : VARCHAR (2)
     * </pre>
     */
    private String urlChargeFlag;

    /**<pre>
     * 설명 : 
     * 컬럼명 : url_board_flag
     * 타입 : VARCHAR (2)
     * </pre>
     */
    private String urlBoardFlag;

    /**<pre>
     * 설명 : 
     * 컬럼명 : url_server_flag
     * 타입 : VARCHAR (2)
     * </pre>
     */
    private String urlServerFlag;


    //=================== getter/setter methods ========================

    /**<pre>
     * 설명 : 
     * 컬럼명 : asset_cd
     * 타입 : VARCHAR (33)
     * PK
     * </pre>
     */
    public String getAssetCd(){
        return this.assetCd;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : asset_cd
     * 타입 : VARCHAR (33)
     * PK
     * </pre>
     */
    public void setAssetCd(String assetCd){
        this.assetCd = assetCd ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_type
     * 타입 : VARCHAR (20)
     * PK
     * </pre>
     */
    public String getSwType(){
        return this.swType;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_type
     * 타입 : VARCHAR (20)
     * PK
     * </pre>
     */
    public void setSwType(String swType){
        this.swType = swType ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_nm
     * 타입 : VARCHAR (100)
     * PK
     * </pre>
     */
    public String getSwNm(){
        return this.swNm;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_nm
     * 타입 : VARCHAR (100)
     * PK
     * </pre>
     */
    public void setSwNm(String swNm){
        this.swNm = swNm ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_info
     * 타입 : VARCHAR (100)
     * PK
     * </pre>
     */
    public String getSwInfo(){
        return this.swInfo;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_info
     * 타입 : VARCHAR (100)
     * PK
     * </pre>
     */
    public void setSwInfo(String swInfo){
        this.swInfo = swInfo ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_dir
     * 타입 : VARCHAR (200)
     * PK
     * </pre>
     */
    public String getSwDir(){
        return this.swDir;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_dir
     * 타입 : VARCHAR (200)
     * PK
     * </pre>
     */
    public void setSwDir(String swDir){
        this.swDir = swDir ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_user
     * 타입 : VARCHAR (100)
     * PK
     * </pre>
     */
    public String getSwUser(){
        return this.swUser;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_user
     * 타입 : VARCHAR (100)
     * PK
     * </pre>
     */
    public void setSwUser(String swUser){
        this.swUser = swUser ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_etc
     * 타입 : VARCHAR (100)
     * PK
     * </pre>
     */
    public String getSwEtc(){
        return this.swEtc;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_etc
     * 타입 : VARCHAR (100)
     * PK
     * </pre>
     */
    public void setSwEtc(String swEtc){
        this.swEtc = swEtc ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_day
     * 타입 : VARCHAR (16)
     * 
     * </pre>
     */
    public String getAuditDay(){
        return this.auditDay;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_day
     * 타입 : VARCHAR (16)
     * 
     * </pre>
     */
    public void setAuditDay(String auditDay){
        this.auditDay = auditDay ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_file_cd
     * 타입 : VARCHAR (33)
     * 
     * </pre>
     */
    public String getAuditFileCd(){
        return this.auditFileCd;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_file_cd
     * 타입 : VARCHAR (33)
     * 
     * </pre>
     */
    public void setAuditFileCd(String auditFileCd){
        this.auditFileCd = auditFileCd ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_regi
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getUserRegi(){
        return this.userRegi;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : user_regi
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setUserRegi(String userRegi){
		// 숫자타입인 경우
		if("".equals(userRegi)){
			this.userRegi = null;
		}else{
	        this.userRegi = userRegi ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : user_regi
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getUserRegi_asLong(){
        if(this.userRegi==null || this.userRegi.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.userRegi);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : user_regi
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setUserRegi_asLong(Long userRegi){
    	if(userRegi==null){
    		this.userRegi = null;
    	}else{
	        this.userRegi = String.valueOf(userRegi) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_info_sort_num
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public String getSwInfoSortNum(){
        return this.swInfoSortNum;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_info_sort_num
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public void setSwInfoSortNum(String swInfoSortNum){
        this.swInfoSortNum = swInfoSortNum ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : create_date
     * 타입 : DATETIME (19)
     * 
     * </pre>
     */
    public java.sql.Timestamp getCreateDate(){
        return this.createDate;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : create_date
     * 타입 : DATETIME (19)
     * 
     * </pre>
     */
    public void setCreateDate(java.sql.Timestamp createDate){
        this.createDate = createDate ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : url_service_target
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getUrlServiceTarget(){
        return this.urlServiceTarget;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : url_service_target
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setUrlServiceTarget(String urlServiceTarget){
		// 숫자타입인 경우
		if("".equals(urlServiceTarget)){
			this.urlServiceTarget = null;
		}else{
	        this.urlServiceTarget = urlServiceTarget ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : url_service_target
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getUrlServiceTarget_asLong(){
        if(this.urlServiceTarget==null || this.urlServiceTarget.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.urlServiceTarget);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : url_service_target
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setUrlServiceTarget_asLong(Long urlServiceTarget){
    	if(urlServiceTarget==null){
    		this.urlServiceTarget = null;
    	}else{
	        this.urlServiceTarget = String.valueOf(urlServiceTarget) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : url_target_state
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getUrlTargetState(){
        return this.urlTargetState;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : url_target_state
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setUrlTargetState(String urlTargetState){
		// 숫자타입인 경우
		if("".equals(urlTargetState)){
			this.urlTargetState = null;
		}else{
	        this.urlTargetState = urlTargetState ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : url_target_state
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getUrlTargetState_asLong(){
        if(this.urlTargetState==null || this.urlTargetState.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.urlTargetState);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : url_target_state
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setUrlTargetState_asLong(Long urlTargetState){
    	if(urlTargetState==null){
    		this.urlTargetState = null;
    	}else{
	        this.urlTargetState = String.valueOf(urlTargetState) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : closed_due_date
     * 타입 : DATE (10)
     * 
     * </pre>
     */
    public java.sql.Date getClosedDueDate(){
        return this.closedDueDate;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : closed_due_date
     * 타입 : DATE (10)
     * 
     * </pre>
     */
    public void setClosedDueDate(java.sql.Date closedDueDate){
        this.closedDueDate = closedDueDate ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : except_reason
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public String getExceptReason(){
        return this.exceptReason;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : except_reason
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public void setExceptReason(String exceptReason){
        this.exceptReason = exceptReason ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : pre_chg_date
     * 타입 : VARCHAR (8)
     * 
     * </pre>
     */
    public String getPreChgDate(){
        return this.preChgDate;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : pre_chg_date
     * 타입 : VARCHAR (8)
     * 
     * </pre>
     */
    public void setPreChgDate(String preChgDate){
        this.preChgDate = preChgDate ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : last_chg_date
     * 타입 : VARCHAR (8)
     * 
     * </pre>
     */
    public String getLastChgDate(){
        return this.lastChgDate;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : last_chg_date
     * 타입 : VARCHAR (8)
     * 
     * </pre>
     */
    public void setLastChgDate(String lastChgDate){
        this.lastChgDate = lastChgDate ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : chg_page_cnt
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getChgPageCnt(){
        return this.chgPageCnt;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : chg_page_cnt
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setChgPageCnt(String chgPageCnt){
		// 숫자타입인 경우
		if("".equals(chgPageCnt)){
			this.chgPageCnt = null;
		}else{
	        this.chgPageCnt = chgPageCnt ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : chg_page_cnt
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getChgPageCnt_asLong(){
        if(this.chgPageCnt==null || this.chgPageCnt.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.chgPageCnt);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : chg_page_cnt
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setChgPageCnt_asLong(Long chgPageCnt){
    	if(chgPageCnt==null){
    		this.chgPageCnt = null;
    	}else{
	        this.chgPageCnt = String.valueOf(chgPageCnt) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : added_chg_page_cnt
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAddedChgPageCnt(){
        return this.addedChgPageCnt;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : added_chg_page_cnt
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAddedChgPageCnt(String addedChgPageCnt){
		// 숫자타입인 경우
		if("".equals(addedChgPageCnt)){
			this.addedChgPageCnt = null;
		}else{
	        this.addedChgPageCnt = addedChgPageCnt ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : added_chg_page_cnt
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAddedChgPageCnt_asLong(){
        if(this.addedChgPageCnt==null || this.addedChgPageCnt.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.addedChgPageCnt);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : added_chg_page_cnt
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAddedChgPageCnt_asLong(Long addedChgPageCnt){
    	if(addedChgPageCnt==null){
    		this.addedChgPageCnt = null;
    	}else{
	        this.addedChgPageCnt = String.valueOf(addedChgPageCnt) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : added_chg_cnt
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAddedChgCnt(){
        return this.addedChgCnt;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : added_chg_cnt
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAddedChgCnt(String addedChgCnt){
		// 숫자타입인 경우
		if("".equals(addedChgCnt)){
			this.addedChgCnt = null;
		}else{
	        this.addedChgCnt = addedChgCnt ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : added_chg_cnt
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAddedChgCnt_asLong(){
        if(this.addedChgCnt==null || this.addedChgCnt.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.addedChgCnt);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : added_chg_cnt
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAddedChgCnt_asLong(Long addedChgCnt){
    	if(addedChgCnt==null){
    		this.addedChgCnt = null;
    	}else{
	        this.addedChgCnt = String.valueOf(addedChgCnt) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : represent_url
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getRepresentUrl(){
        return this.representUrl;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : represent_url
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setRepresentUrl(String representUrl){
		// 숫자타입인 경우
		if("".equals(representUrl)){
			this.representUrl = null;
		}else{
	        this.representUrl = representUrl ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : represent_url
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getRepresentUrl_asLong(){
        if(this.representUrl==null || this.representUrl.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.representUrl);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : represent_url
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setRepresentUrl_asLong(Long representUrl){
    	if(representUrl==null){
    		this.representUrl = null;
    	}else{
	        this.representUrl = String.valueOf(representUrl) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : url_management_cd
     * 타입 : VARCHAR (50)
     * 
     * </pre>
     */
    public String getUrlManagementCd(){
        return this.urlManagementCd;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : url_management_cd
     * 타입 : VARCHAR (50)
     * 
     * </pre>
     */
    public void setUrlManagementCd(String urlManagementCd){
        this.urlManagementCd = urlManagementCd ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : url_service_nm
     * 타입 : VARCHAR (200)
     * 
     * </pre>
     */
    public String getUrlServiceNm(){
        return this.urlServiceNm;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : url_service_nm
     * 타입 : VARCHAR (200)
     * 
     * </pre>
     */
    public void setUrlServiceNm(String urlServiceNm){
        this.urlServiceNm = urlServiceNm ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : url_person_flag
     * 타입 : VARCHAR (2)
     * 
     * </pre>
     */
    public String getUrlPersonFlag(){
        return this.urlPersonFlag;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : url_person_flag
     * 타입 : VARCHAR (2)
     * 
     * </pre>
     */
    public void setUrlPersonFlag(String urlPersonFlag){
        this.urlPersonFlag = urlPersonFlag ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : url_location_flag
     * 타입 : VARCHAR (2)
     * 
     * </pre>
     */
    public String getUrlLocationFlag(){
        return this.urlLocationFlag;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : url_location_flag
     * 타입 : VARCHAR (2)
     * 
     * </pre>
     */
    public void setUrlLocationFlag(String urlLocationFlag){
        this.urlLocationFlag = urlLocationFlag ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : url_charge_flag
     * 타입 : VARCHAR (2)
     * 
     * </pre>
     */
    public String getUrlChargeFlag(){
        return this.urlChargeFlag;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : url_charge_flag
     * 타입 : VARCHAR (2)
     * 
     * </pre>
     */
    public void setUrlChargeFlag(String urlChargeFlag){
        this.urlChargeFlag = urlChargeFlag ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : url_board_flag
     * 타입 : VARCHAR (2)
     * 
     * </pre>
     */
    public String getUrlBoardFlag(){
        return this.urlBoardFlag;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : url_board_flag
     * 타입 : VARCHAR (2)
     * 
     * </pre>
     */
    public void setUrlBoardFlag(String urlBoardFlag){
        this.urlBoardFlag = urlBoardFlag ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : url_server_flag
     * 타입 : VARCHAR (2)
     * 
     * </pre>
     */
    public String getUrlServerFlag(){
        return this.urlServerFlag;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : url_server_flag
     * 타입 : VARCHAR (2)
     * 
     * </pre>
     */
    public void setUrlServerFlag(String urlServerFlag){
        this.urlServerFlag = urlServerFlag ;
    }
    
    
    



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[SnetAssetSwAuditDayEntity =>");
	    sb.append("  assetCd=").append(assetCd).append("\n");
	    sb.append(", swType=").append(swType).append("\n");
	    sb.append(", swNm=").append(swNm).append("\n");
	    sb.append(", swInfo=").append(swInfo).append("\n");
	    sb.append(", swDir=").append(swDir).append("\n");
	    sb.append(", swUser=").append(swUser).append("\n");
	    sb.append(", swEtc=").append(swEtc).append("\n");
	    sb.append(", auditDay=").append(auditDay).append("\n");
	    sb.append(", auditFileCd=").append(auditFileCd).append("\n");
	    sb.append(", userRegi=").append(userRegi).append("\n");
	    sb.append(", swInfoSortNum=").append(swInfoSortNum).append("\n");
	    sb.append(", createDate=").append(createDate).append("\n");
	    sb.append(", urlServiceTarget=").append(urlServiceTarget).append("\n");
	    sb.append(", urlTargetState=").append(urlTargetState).append("\n");
	    sb.append(", closedDueDate=").append(closedDueDate).append("\n");
	    sb.append(", exceptReason=").append(exceptReason).append("\n");
	    sb.append(", preChgDate=").append(preChgDate).append("\n");
	    sb.append(", lastChgDate=").append(lastChgDate).append("\n");
	    sb.append(", chgPageCnt=").append(chgPageCnt).append("\n");
	    sb.append(", addedChgPageCnt=").append(addedChgPageCnt).append("\n");
	    sb.append(", addedChgCnt=").append(addedChgCnt).append("\n");
	    sb.append(", representUrl=").append(representUrl).append("\n");
	    sb.append(", urlManagementCd=").append(urlManagementCd).append("\n");
	    sb.append(", urlServiceNm=").append(urlServiceNm).append("\n");
	    sb.append(", urlPersonFlag=").append(urlPersonFlag).append("\n");
	    sb.append(", urlLocationFlag=").append(urlLocationFlag).append("\n");
	    sb.append(", urlChargeFlag=").append(urlChargeFlag).append("\n");
	    sb.append(", urlBoardFlag=").append(urlBoardFlag).append("\n");
	    sb.append(", urlServerFlag=").append(urlServerFlag).append("\n");
	    sb.append("]\n");
    	return sb.toString();
    }

}
