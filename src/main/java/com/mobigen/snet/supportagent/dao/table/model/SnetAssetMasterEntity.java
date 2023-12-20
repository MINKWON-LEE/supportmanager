package com.mobigen.snet.supportagent.dao.table.model;

/**
 * <pre>
 *  - 테이블 설명 : 
 *  - 테이블 명 : SNET_ASSET_MASTER
 *  - PK :   asset_cd
 *  - generated in : 2017-03-14 11:17
 * </pre>
 */
public class SnetAssetMasterEntity implements java.io.Serializable{

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
     * 컬럼명 : agent_cd
     * 타입 : VARCHAR (33)
     * </pre>
     */
    private String agentCd;

    /**<pre>
     * 설명 : 
     * 컬럼명 : branch_id
     * 타입 : VARCHAR (8)
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
     * 타입 : VARCHAR (8)
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
     * 컬럼명 : ip_address
     * 타입 : VARCHAR (1025)
     * </pre>
     */
    private String ipAddress;

    /**<pre>
     * 설명 : 
     * 컬럼명 : host_nm
     * 타입 : VARCHAR (1024)
     * </pre>
     */
    private String hostNm;

    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_day
     * 타입 : VARCHAR (12)
     * </pre>
     */
    private String auditDay;

    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_rate
     * 타입 : DECIMAL (6.2)
     * </pre>
     */
    private String auditRate;

    /**<pre>
     * 설명 : 
     * 컬럼명 : sgw_regi
     * 타입 : INT (10.0)
     * </pre>
     */
    private String sgwRegi;

    /**<pre>
     * 설명 : 
     * 컬럼명 : alive_chk
     * 타입 : INT (10.0)
     * </pre>
     */
    private String aliveChk;

    /**<pre>
     * 설명 : 
     * 컬럼명 : svr_room_id
     * 타입 : VARCHAR (12)
     * </pre>
     */
    private String svrRoomId;

    /**<pre>
     * 설명 : 
     * 컬럼명 : personal_data
     * 타입 : INT (10.0)
     * </pre>
     */
    private String personalData;

    /**<pre>
     * 설명 : 
     * 컬럼명 : gov_flag
     * 타입 : INT (10.0)
     * </pre>
     */
    private String govFlag;

    /**<pre>
     * 설명 : 
     * 컬럼명 : create_date
     * 타입 : DATETIME (19)
     * </pre>
     */
    private java.sql.Timestamp createDate;

    /**<pre>
     * 설명 : 
     * 컬럼명 : update_date
     * 타입 : DATETIME (19)
     * </pre>
     */
    private java.sql.Timestamp updateDate;

    /**<pre>
     * 설명 : 
     * 컬럼명 : get_day
     * 타입 : VARCHAR (20)
     * </pre>
     */
    private String getDay;

    /**<pre>
     * 설명 : 
     * 컬럼명 : infra_yn
     * 타입 : VARCHAR (1)
     * </pre>
     */
    private String infraYn;

    /**<pre>
     * 설명 : 
     * 컬럼명 : ddd
     * 타입 : DATETIME (19)
     * </pre>
     */
    private java.sql.Timestamp ddd;

    /**<pre>
     * 설명 : 
     * 컬럼명 : auto_audit_n_desc
     * 타입 : VARCHAR (1024)
     * </pre>
     */
    private String autoAuditNDesc;

    /**<pre>
     * 설명 : 
     * 컬럼명 : auto_audit_yn
     * 타입 : VARCHAR (1)
     * </pre>
     */
    private String autoAuditYn;

    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_speed
     * 타입 : INT (10.0)
     * </pre>
     */
    private String auditSpeed;

    /**<pre>
     * 설명 : 
     * 컬럼명 : service_id
     * 타입 : INT (10.0)
     * </pre>
     */
    private String serviceId;

    /**<pre>
     * 설명 : 
     * 컬럼명 : isms_yn
     * 타입 : VARCHAR (1)
     * </pre>
     */
    private String ismsYn;


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
     * 컬럼명 : agent_cd
     * 타입 : VARCHAR (33)
     * 
     * </pre>
     */
    public String getAgentCd(){
        return this.agentCd;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : agent_cd
     * 타입 : VARCHAR (33)
     * 
     * </pre>
     */
    public void setAgentCd(String agentCd){
        this.agentCd = agentCd ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : branch_id
     * 타입 : VARCHAR (8)
     * 
     * </pre>
     */
    public String getBranchId(){
        return this.branchId;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : branch_id
     * 타입 : VARCHAR (8)
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
     * 타입 : VARCHAR (8)
     * 
     * </pre>
     */
    public String getTeamId(){
        return this.teamId;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : team_id
     * 타입 : VARCHAR (8)
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
     * 컬럼명 : ip_address
     * 타입 : VARCHAR (1025)
     * 
     * </pre>
     */
    public String getIpAddress(){
        return this.ipAddress;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ip_address
     * 타입 : VARCHAR (1025)
     * 
     * </pre>
     */
    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress ;
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
     * 컬럼명 : audit_day
     * 타입 : VARCHAR (12)
     * 
     * </pre>
     */
    public String getAuditDay(){
        return this.auditDay;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_day
     * 타입 : VARCHAR (12)
     * 
     * </pre>
     */
    public void setAuditDay(String auditDay){
        this.auditDay = auditDay ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_rate
     * 타입 : DECIMAL (6.2)
     * 
     * </pre>
     */
    public String getAuditRate(){
        return this.auditRate;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_rate
     * 타입 : DECIMAL (6.2)
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
     * 타입 : DECIMAL (6.2)
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
     * 타입 : DECIMAL (6.2)
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
     * 컬럼명 : sgw_regi
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getSgwRegi(){
        return this.sgwRegi;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : sgw_regi
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setSgwRegi(String sgwRegi){
		// 숫자타입인 경우
		if("".equals(sgwRegi)){
			this.sgwRegi = null;
		}else{
	        this.sgwRegi = sgwRegi ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : sgw_regi
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getSgwRegi_asLong(){
        if(this.sgwRegi==null || this.sgwRegi.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.sgwRegi);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : sgw_regi
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setSgwRegi_asLong(Long sgwRegi){
    	if(sgwRegi==null){
    		this.sgwRegi = null;
    	}else{
	        this.sgwRegi = String.valueOf(sgwRegi) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : alive_chk
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAliveChk(){
        return this.aliveChk;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : alive_chk
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAliveChk(String aliveChk){
		// 숫자타입인 경우
		if("".equals(aliveChk)){
			this.aliveChk = null;
		}else{
	        this.aliveChk = aliveChk ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : alive_chk
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAliveChk_asLong(){
        if(this.aliveChk==null || this.aliveChk.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.aliveChk);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : alive_chk
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAliveChk_asLong(Long aliveChk){
    	if(aliveChk==null){
    		this.aliveChk = null;
    	}else{
	        this.aliveChk = String.valueOf(aliveChk) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : svr_room_id
     * 타입 : VARCHAR (12)
     * 
     * </pre>
     */
    public String getSvrRoomId(){
        return this.svrRoomId;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : svr_room_id
     * 타입 : VARCHAR (12)
     * 
     * </pre>
     */
    public void setSvrRoomId(String svrRoomId){
        this.svrRoomId = svrRoomId ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : personal_data
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getPersonalData(){
        return this.personalData;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : personal_data
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPersonalData(String personalData){
		// 숫자타입인 경우
		if("".equals(personalData)){
			this.personalData = null;
		}else{
	        this.personalData = personalData ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : personal_data
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getPersonalData_asLong(){
        if(this.personalData==null || this.personalData.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.personalData);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : personal_data
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPersonalData_asLong(Long personalData){
    	if(personalData==null){
    		this.personalData = null;
    	}else{
	        this.personalData = String.valueOf(personalData) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : gov_flag
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getGovFlag(){
        return this.govFlag;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : gov_flag
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setGovFlag(String govFlag){
		// 숫자타입인 경우
		if("".equals(govFlag)){
			this.govFlag = null;
		}else{
	        this.govFlag = govFlag ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : gov_flag
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getGovFlag_asLong(){
        if(this.govFlag==null || this.govFlag.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.govFlag);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : gov_flag
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setGovFlag_asLong(Long govFlag){
    	if(govFlag==null){
    		this.govFlag = null;
    	}else{
	        this.govFlag = String.valueOf(govFlag) ;
    	}    
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
     * 컬럼명 : update_date
     * 타입 : DATETIME (19)
     * 
     * </pre>
     */
    public java.sql.Timestamp getUpdateDate(){
        return this.updateDate;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : update_date
     * 타입 : DATETIME (19)
     * 
     * </pre>
     */
    public void setUpdateDate(java.sql.Timestamp updateDate){
        this.updateDate = updateDate ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : get_day
     * 타입 : VARCHAR (20)
     * 
     * </pre>
     */
    public String getGetDay(){
        return this.getDay;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : get_day
     * 타입 : VARCHAR (20)
     * 
     * </pre>
     */
    public void setGetDay(String getDay){
        this.getDay = getDay ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : infra_yn
     * 타입 : VARCHAR (1)
     * 
     * </pre>
     */
    public String getInfraYn(){
        return this.infraYn;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : infra_yn
     * 타입 : VARCHAR (1)
     * 
     * </pre>
     */
    public void setInfraYn(String infraYn){
        this.infraYn = infraYn ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : ddd
     * 타입 : DATETIME (19)
     * 
     * </pre>
     */
    public java.sql.Timestamp getDdd(){
        return this.ddd;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ddd
     * 타입 : DATETIME (19)
     * 
     * </pre>
     */
    public void setDdd(java.sql.Timestamp ddd){
        this.ddd = ddd ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : auto_audit_n_desc
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public String getAutoAuditNDesc(){
        return this.autoAuditNDesc;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : auto_audit_n_desc
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public void setAutoAuditNDesc(String autoAuditNDesc){
        this.autoAuditNDesc = autoAuditNDesc ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : auto_audit_yn
     * 타입 : VARCHAR (1)
     * 
     * </pre>
     */
    public String getAutoAuditYn(){
        return this.autoAuditYn;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : auto_audit_yn
     * 타입 : VARCHAR (1)
     * 
     * </pre>
     */
    public void setAutoAuditYn(String autoAuditYn){
        this.autoAuditYn = autoAuditYn ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_speed
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAuditSpeed(){
        return this.auditSpeed;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_speed
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAuditSpeed(String auditSpeed){
		// 숫자타입인 경우
		if("".equals(auditSpeed)){
			this.auditSpeed = null;
		}else{
	        this.auditSpeed = auditSpeed ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_speed
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAuditSpeed_asLong(){
        if(this.auditSpeed==null || this.auditSpeed.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.auditSpeed);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : audit_speed
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAuditSpeed_asLong(Long auditSpeed){
    	if(auditSpeed==null){
    		this.auditSpeed = null;
    	}else{
	        this.auditSpeed = String.valueOf(auditSpeed) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : service_id
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getServiceId(){
        return this.serviceId;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : service_id
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setServiceId(String serviceId){
		// 숫자타입인 경우
		if("".equals(serviceId)){
			this.serviceId = null;
		}else{
	        this.serviceId = serviceId ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : service_id
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getServiceId_asLong(){
        if(this.serviceId==null || this.serviceId.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.serviceId);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : service_id
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setServiceId_asLong(Long serviceId){
    	if(serviceId==null){
    		this.serviceId = null;
    	}else{
	        this.serviceId = String.valueOf(serviceId) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : isms_yn
     * 타입 : VARCHAR (1)
     * 
     * </pre>
     */
    public String getIsmsYn(){
        return this.ismsYn;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : isms_yn
     * 타입 : VARCHAR (1)
     * 
     * </pre>
     */
    public void setIsmsYn(String ismsYn){
        this.ismsYn = ismsYn ;
    }
    
    
    



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[SnetAssetMasterEntity =>");
	    sb.append("  assetCd=").append(assetCd).append("\n");
	    sb.append(", agentCd=").append(agentCd).append("\n");
	    sb.append(", branchId=").append(branchId).append("\n");
	    sb.append(", branchNm=").append(branchNm).append("\n");
	    sb.append(", teamId=").append(teamId).append("\n");
	    sb.append(", teamNm=").append(teamNm).append("\n");
	    sb.append(", ipAddress=").append(ipAddress).append("\n");
	    sb.append(", hostNm=").append(hostNm).append("\n");
	    sb.append(", auditDay=").append(auditDay).append("\n");
	    sb.append(", auditRate=").append(auditRate).append("\n");
	    sb.append(", sgwRegi=").append(sgwRegi).append("\n");
	    sb.append(", aliveChk=").append(aliveChk).append("\n");
	    sb.append(", svrRoomId=").append(svrRoomId).append("\n");
	    sb.append(", personalData=").append(personalData).append("\n");
	    sb.append(", govFlag=").append(govFlag).append("\n");
	    sb.append(", createDate=").append(createDate).append("\n");
	    sb.append(", updateDate=").append(updateDate).append("\n");
	    sb.append(", getDay=").append(getDay).append("\n");
	    sb.append(", infraYn=").append(infraYn).append("\n");
	    sb.append(", ddd=").append(ddd).append("\n");
	    sb.append(", autoAuditNDesc=").append(autoAuditNDesc).append("\n");
	    sb.append(", autoAuditYn=").append(autoAuditYn).append("\n");
	    sb.append(", auditSpeed=").append(auditSpeed).append("\n");
	    sb.append(", serviceId=").append(serviceId).append("\n");
	    sb.append(", ismsYn=").append(ismsYn).append("\n");
	    sb.append("]\n");
    	return sb.toString();
    }

}
