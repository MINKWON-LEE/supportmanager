package com.mobigen.snet.supportagent.dao.table.model;

/**
 * <pre>
 *  - 테이블 설명 : 
 *  - 테이블 명 : SNET_ASSET_SW_AUDIT_HISTORY
 *  - PK :   asset_cd, sw_type, sw_nm, sw_info, sw_dir, sw_user, sw_etc, audit_day
 *  - generated in : 2017-03-14 11:17
 * </pre>
 */
public class SnetAssetSwAuditHistoryEntity implements java.io.Serializable{

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
     * 타입 : VARCHAR (50)
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
     * 타입 : VARCHAR (12)
     * PK
     * </pre>
     */
    private String auditDay;

    /**<pre>
     * 설명 : 
     * 컬럼명 : branch_id
     * 타입 : VARCHAR (50)
     * </pre>
     */
    private String branchId;

    /**<pre>
     * 설명 : 
     * 컬럼명 : branch_nm
     * 타입 : VARCHAR (50)
     * </pre>
     */
    private String branchNm;

    /**<pre>
     * 설명 : 
     * 컬럼명 : team_id
     * 타입 : VARCHAR (50)
     * </pre>
     */
    private String teamId;

    /**<pre>
     * 설명 : 
     * 컬럼명 : team_nm
     * 타입 : VARCHAR (50)
     * </pre>
     */
    private String teamNm;

    /**<pre>
     * 설명 : 
     * 컬럼명 : host_nm
     * 타입 : VARCHAR (1024)
     * </pre>
     */
    private String hostNm;

    /**<pre>
     * 설명 : 
     * 컬럼명 : ip_address
     * 타입 : VARCHAR (1024)
     * </pre>
     */
    private String ipAddress;

    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_rate
     * 타입 : DECIMAL (5.2)
     * </pre>
     */
    private String auditRate;

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_ok
     * 타입 : INT (10.0)
     * </pre>
     */
    private String adResultOk;

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_nok
     * 타입 : INT (10.0)
     * </pre>
     */
    private String adResultNok;

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_na
     * 타입 : INT (10.0)
     * </pre>
     */
    private String adResultNa;

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_pass
     * 타입 : INT (10.0)
     * </pre>
     */
    private String adResultPass;

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_total
     * 타입 : INT (10.0)
     * </pre>
     */
    private String adWeightTotal;

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_ok
     * 타입 : INT (10.0)
     * </pre>
     */
    private String adWeightOk;

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_nok
     * 타입 : INT (10.0)
     * </pre>
     */
    private String adWeightNok;

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_pass
     * 타입 : INT (10.0)
     * </pre>
     */
    private String adWeightPass;

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_na
     * 타입 : INT (10.0)
     * </pre>
     */
    private String adWeightNa;

    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_file_cd
     * 타입 : VARCHAR (33)
     * </pre>
     */
    private String auditFileCd;

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_req
     * 타입 : INT (10.0)
     * </pre>
     */
    private String adResultReq;

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_req
     * 타입 : INT (10.0)
     * </pre>
     */
    private String adWeightReq;

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id
     * 타입 : VARCHAR (255)
     * </pre>
     */
    private String userId;

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_nm
     * 타입 : VARCHAR (255)
     * </pre>
     */
    private String userNm;

    /**<pre>
     * 설명 : 
     * 컬럼명 : service_p_nm
     * 타입 : VARCHAR (50)
     * </pre>
     */
    private String servicePNm;

    /**<pre>
     * 설명 : 
     * 컬럼명 : service_c_nm
     * 타입 : VARCHAR (50)
     * </pre>
     */
    private String serviceCNm;


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
     * 타입 : VARCHAR (50)
     * PK
     * </pre>
     */
    public String getSwType(){
        return this.swType;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_type
     * 타입 : VARCHAR (50)
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
     * 타입 : VARCHAR (12)
     * PK
     * </pre>
     */
    public String getAuditDay(){
        return this.auditDay;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_day
     * 타입 : VARCHAR (12)
     * PK
     * </pre>
     */
    public void setAuditDay(String auditDay){
        this.auditDay = auditDay ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : branch_id
     * 타입 : VARCHAR (50)
     * 
     * </pre>
     */
    public String getBranchId(){
        return this.branchId;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : branch_id
     * 타입 : VARCHAR (50)
     * 
     * </pre>
     */
    public void setBranchId(String branchId){
        this.branchId = branchId ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : branch_nm
     * 타입 : VARCHAR (50)
     * 
     * </pre>
     */
    public String getBranchNm(){
        return this.branchNm;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : branch_nm
     * 타입 : VARCHAR (50)
     * 
     * </pre>
     */
    public void setBranchNm(String branchNm){
        this.branchNm = branchNm ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : team_id
     * 타입 : VARCHAR (50)
     * 
     * </pre>
     */
    public String getTeamId(){
        return this.teamId;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : team_id
     * 타입 : VARCHAR (50)
     * 
     * </pre>
     */
    public void setTeamId(String teamId){
        this.teamId = teamId ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : team_nm
     * 타입 : VARCHAR (50)
     * 
     * </pre>
     */
    public String getTeamNm(){
        return this.teamNm;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : team_nm
     * 타입 : VARCHAR (50)
     * 
     * </pre>
     */
    public void setTeamNm(String teamNm){
        this.teamNm = teamNm ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : host_nm
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public String getHostNm(){
        return this.hostNm;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : host_nm
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public void setHostNm(String hostNm){
        this.hostNm = hostNm ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : ip_address
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public String getIpAddress(){
        return this.ipAddress;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ip_address
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_rate
     * 타입 : DECIMAL (5.2)
     * 
     * </pre>
     */
    public String getAuditRate(){
        return this.auditRate;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_rate
     * 타입 : DECIMAL (5.2)
     * 
     * </pre>
     */
    public void setAuditRate(String auditRate){
		// 숫자타입인 경우
		if("".equals(auditRate)){
			this.auditRate = null;
		}else{
	        this.auditRate = auditRate ;
		}
    }
    
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_rate
     * 타입 : DECIMAL (5.2)
     * 
     * </pre>
     */
    public Double getAuditRate_asDouble(){
        if(this.auditRate==null || this.auditRate.equals("")){
        	return null;
        }else{
	        return Double.parseDouble(this.auditRate);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_rate
     * 타입 : DECIMAL (5.2)
     * 
     * </pre>
     */
    public void setAuditRate_asDouble(Double auditRate){
    	if(auditRate==null){
    		this.auditRate = null;
    	}else{
	        this.auditRate = String.valueOf(auditRate) ;
    	}    
    }

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_ok
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAdResultOk(){
        return this.adResultOk;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_ok
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdResultOk(String adResultOk){
		// 숫자타입인 경우
		if("".equals(adResultOk)){
			this.adResultOk = null;
		}else{
	        this.adResultOk = adResultOk ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_ok
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAdResultOk_asLong(){
        if(this.adResultOk==null || this.adResultOk.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.adResultOk);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_ok
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdResultOk_asLong(Long adResultOk){
    	if(adResultOk==null){
    		this.adResultOk = null;
    	}else{
	        this.adResultOk = String.valueOf(adResultOk) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_nok
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAdResultNok(){
        return this.adResultNok;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_nok
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdResultNok(String adResultNok){
		// 숫자타입인 경우
		if("".equals(adResultNok)){
			this.adResultNok = null;
		}else{
	        this.adResultNok = adResultNok ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_nok
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAdResultNok_asLong(){
        if(this.adResultNok==null || this.adResultNok.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.adResultNok);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_nok
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdResultNok_asLong(Long adResultNok){
    	if(adResultNok==null){
    		this.adResultNok = null;
    	}else{
	        this.adResultNok = String.valueOf(adResultNok) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_na
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAdResultNa(){
        return this.adResultNa;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_na
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdResultNa(String adResultNa){
		// 숫자타입인 경우
		if("".equals(adResultNa)){
			this.adResultNa = null;
		}else{
	        this.adResultNa = adResultNa ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_na
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAdResultNa_asLong(){
        if(this.adResultNa==null || this.adResultNa.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.adResultNa);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_na
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdResultNa_asLong(Long adResultNa){
    	if(adResultNa==null){
    		this.adResultNa = null;
    	}else{
	        this.adResultNa = String.valueOf(adResultNa) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_pass
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAdResultPass(){
        return this.adResultPass;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_pass
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdResultPass(String adResultPass){
		// 숫자타입인 경우
		if("".equals(adResultPass)){
			this.adResultPass = null;
		}else{
	        this.adResultPass = adResultPass ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_pass
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAdResultPass_asLong(){
        if(this.adResultPass==null || this.adResultPass.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.adResultPass);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_pass
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdResultPass_asLong(Long adResultPass){
    	if(adResultPass==null){
    		this.adResultPass = null;
    	}else{
	        this.adResultPass = String.valueOf(adResultPass) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_total
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAdWeightTotal(){
        return this.adWeightTotal;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_total
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdWeightTotal(String adWeightTotal){
		// 숫자타입인 경우
		if("".equals(adWeightTotal)){
			this.adWeightTotal = null;
		}else{
	        this.adWeightTotal = adWeightTotal ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_total
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAdWeightTotal_asLong(){
        if(this.adWeightTotal==null || this.adWeightTotal.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.adWeightTotal);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_total
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdWeightTotal_asLong(Long adWeightTotal){
    	if(adWeightTotal==null){
    		this.adWeightTotal = null;
    	}else{
	        this.adWeightTotal = String.valueOf(adWeightTotal) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_ok
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAdWeightOk(){
        return this.adWeightOk;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_ok
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdWeightOk(String adWeightOk){
		// 숫자타입인 경우
		if("".equals(adWeightOk)){
			this.adWeightOk = null;
		}else{
	        this.adWeightOk = adWeightOk ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_ok
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAdWeightOk_asLong(){
        if(this.adWeightOk==null || this.adWeightOk.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.adWeightOk);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_ok
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdWeightOk_asLong(Long adWeightOk){
    	if(adWeightOk==null){
    		this.adWeightOk = null;
    	}else{
	        this.adWeightOk = String.valueOf(adWeightOk) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_nok
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAdWeightNok(){
        return this.adWeightNok;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_nok
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdWeightNok(String adWeightNok){
		// 숫자타입인 경우
		if("".equals(adWeightNok)){
			this.adWeightNok = null;
		}else{
	        this.adWeightNok = adWeightNok ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_nok
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAdWeightNok_asLong(){
        if(this.adWeightNok==null || this.adWeightNok.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.adWeightNok);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_nok
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdWeightNok_asLong(Long adWeightNok){
    	if(adWeightNok==null){
    		this.adWeightNok = null;
    	}else{
	        this.adWeightNok = String.valueOf(adWeightNok) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_pass
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAdWeightPass(){
        return this.adWeightPass;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_pass
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdWeightPass(String adWeightPass){
		// 숫자타입인 경우
		if("".equals(adWeightPass)){
			this.adWeightPass = null;
		}else{
	        this.adWeightPass = adWeightPass ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_pass
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAdWeightPass_asLong(){
        if(this.adWeightPass==null || this.adWeightPass.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.adWeightPass);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_pass
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdWeightPass_asLong(Long adWeightPass){
    	if(adWeightPass==null){
    		this.adWeightPass = null;
    	}else{
	        this.adWeightPass = String.valueOf(adWeightPass) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_na
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAdWeightNa(){
        return this.adWeightNa;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_na
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdWeightNa(String adWeightNa){
		// 숫자타입인 경우
		if("".equals(adWeightNa)){
			this.adWeightNa = null;
		}else{
	        this.adWeightNa = adWeightNa ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_na
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAdWeightNa_asLong(){
        if(this.adWeightNa==null || this.adWeightNa.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.adWeightNa);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_na
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdWeightNa_asLong(Long adWeightNa){
    	if(adWeightNa==null){
    		this.adWeightNa = null;
    	}else{
	        this.adWeightNa = String.valueOf(adWeightNa) ;
    	}    
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
     * 컬럼명 : ad_result_req
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAdResultReq(){
        return this.adResultReq;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_req
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdResultReq(String adResultReq){
		// 숫자타입인 경우
		if("".equals(adResultReq)){
			this.adResultReq = null;
		}else{
	        this.adResultReq = adResultReq ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_req
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAdResultReq_asLong(){
        if(this.adResultReq==null || this.adResultReq.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.adResultReq);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_result_req
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdResultReq_asLong(Long adResultReq){
    	if(adResultReq==null){
    		this.adResultReq = null;
    	}else{
	        this.adResultReq = String.valueOf(adResultReq) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_req
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAdWeightReq(){
        return this.adWeightReq;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_req
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdWeightReq(String adWeightReq){
		// 숫자타입인 경우
		if("".equals(adWeightReq)){
			this.adWeightReq = null;
		}else{
	        this.adWeightReq = adWeightReq ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_req
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAdWeightReq_asLong(){
        if(this.adWeightReq==null || this.adWeightReq.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.adWeightReq);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : ad_weight_req
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAdWeightReq_asLong(Long adWeightReq){
    	if(adWeightReq==null){
    		this.adWeightReq = null;
    	}else{
	        this.adWeightReq = String.valueOf(adWeightReq) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id
     * 타입 : VARCHAR (255)
     * 
     * </pre>
     */
    public String getUserId(){
        return this.userId;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id
     * 타입 : VARCHAR (255)
     * 
     * </pre>
     */
    public void setUserId(String userId){
        this.userId = userId ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_nm
     * 타입 : VARCHAR (255)
     * 
     * </pre>
     */
    public String getUserNm(){
        return this.userNm;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : user_nm
     * 타입 : VARCHAR (255)
     * 
     * </pre>
     */
    public void setUserNm(String userNm){
        this.userNm = userNm ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : service_p_nm
     * 타입 : VARCHAR (50)
     * 
     * </pre>
     */
    public String getServicePNm(){
        return this.servicePNm;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : service_p_nm
     * 타입 : VARCHAR (50)
     * 
     * </pre>
     */
    public void setServicePNm(String servicePNm){
        this.servicePNm = servicePNm ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : service_c_nm
     * 타입 : VARCHAR (50)
     * 
     * </pre>
     */
    public String getServiceCNm(){
        return this.serviceCNm;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : service_c_nm
     * 타입 : VARCHAR (50)
     * 
     * </pre>
     */
    public void setServiceCNm(String serviceCNm){
        this.serviceCNm = serviceCNm ;
    }
    
    
    



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[SnetAssetSwAuditHistoryEntity =>");
	    sb.append("  assetCd=").append(assetCd).append("\n");
	    sb.append(", swType=").append(swType).append("\n");
	    sb.append(", swNm=").append(swNm).append("\n");
	    sb.append(", swInfo=").append(swInfo).append("\n");
	    sb.append(", swDir=").append(swDir).append("\n");
	    sb.append(", swUser=").append(swUser).append("\n");
	    sb.append(", swEtc=").append(swEtc).append("\n");
	    sb.append(", auditDay=").append(auditDay).append("\n");
	    sb.append(", branchId=").append(branchId).append("\n");
	    sb.append(", branchNm=").append(branchNm).append("\n");
	    sb.append(", teamId=").append(teamId).append("\n");
	    sb.append(", teamNm=").append(teamNm).append("\n");
	    sb.append(", hostNm=").append(hostNm).append("\n");
	    sb.append(", ipAddress=").append(ipAddress).append("\n");
	    sb.append(", auditRate=").append(auditRate).append("\n");
	    sb.append(", adResultOk=").append(adResultOk).append("\n");
	    sb.append(", adResultNok=").append(adResultNok).append("\n");
	    sb.append(", adResultNa=").append(adResultNa).append("\n");
	    sb.append(", adResultPass=").append(adResultPass).append("\n");
	    sb.append(", adWeightTotal=").append(adWeightTotal).append("\n");
	    sb.append(", adWeightOk=").append(adWeightOk).append("\n");
	    sb.append(", adWeightNok=").append(adWeightNok).append("\n");
	    sb.append(", adWeightPass=").append(adWeightPass).append("\n");
	    sb.append(", adWeightNa=").append(adWeightNa).append("\n");
	    sb.append(", auditFileCd=").append(auditFileCd).append("\n");
	    sb.append(", adResultReq=").append(adResultReq).append("\n");
	    sb.append(", adWeightReq=").append(adWeightReq).append("\n");
	    sb.append(", userId=").append(userId).append("\n");
	    sb.append(", userNm=").append(userNm).append("\n");
	    sb.append(", servicePNm=").append(servicePNm).append("\n");
	    sb.append(", serviceCNm=").append(serviceCNm).append("\n");
	    sb.append("]\n");
    	return sb.toString();
    }

}
