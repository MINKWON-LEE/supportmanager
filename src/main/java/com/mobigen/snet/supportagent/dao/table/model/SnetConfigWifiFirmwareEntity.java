package com.mobigen.snet.supportagent.dao.table.model;

/**
 * <pre>
 *  - 테이블 설명 : 
 *  - 테이블 명 : SNET_CONFIG_WIFI_FIRMWARE
 *  - PK :   company_id, firmware_ver
 *  - generated in : 2017-03-14 11:44
 * </pre>
 */
public class SnetConfigWifiFirmwareEntity implements java.io.Serializable{

    /**<pre>
     * 설명 : 
     * 컬럼명 : company_id
     * 타입 : VARCHAR (33)
     * PK
     * </pre>
     */
    private String companyId;

    /**<pre>
     * 설명 : 
     * 컬럼명 : firmware_ver
     * 타입 : VARCHAR (100)
     * PK
     * </pre>
     */
    private String firmwareVer;

    /**<pre>
     * 설명 : 
     * 컬럼명 : company_nm
     * 타입 : VARCHAR (33)
     * </pre>
     */
    private String companyNm;

    /**<pre>
     * 설명 : 
     * 컬럼명 : use_yn
     * 타입 : VARCHAR (2)
     * </pre>
     */
    private String useYn;

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


    //=================== getter/setter methods ========================

    /**<pre>
     * 설명 : 
     * 컬럼명 : company_id
     * 타입 : VARCHAR (33)
     * PK
     * </pre>
     */
    public String getCompanyId(){
        return this.companyId;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : company_id
     * 타입 : VARCHAR (33)
     * PK
     * </pre>
     */
    public void setCompanyId(String companyId){
        this.companyId = companyId ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : firmware_ver
     * 타입 : VARCHAR (100)
     * PK
     * </pre>
     */
    public String getFirmwareVer(){
        return this.firmwareVer;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : firmware_ver
     * 타입 : VARCHAR (100)
     * PK
     * </pre>
     */
    public void setFirmwareVer(String firmwareVer){
        this.firmwareVer = firmwareVer ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : company_nm
     * 타입 : VARCHAR (33)
     * 
     * </pre>
     */
    public String getCompanyNm(){
        return this.companyNm;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : company_nm
     * 타입 : VARCHAR (33)
     * 
     * </pre>
     */
    public void setCompanyNm(String companyNm){
        this.companyNm = companyNm ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : use_yn
     * 타입 : VARCHAR (2)
     * 
     * </pre>
     */
    public String getUseYn(){
        return this.useYn;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : use_yn
     * 타입 : VARCHAR (2)
     * 
     * </pre>
     */
    public void setUseYn(String useYn){
        this.useYn = useYn ;
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
    
    
    



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[SnetConfigWifiFirmwareEntity =>");
	    sb.append("  companyId=").append(companyId).append("\n");
	    sb.append(", firmwareVer=").append(firmwareVer).append("\n");
	    sb.append(", companyNm=").append(companyNm).append("\n");
	    sb.append(", useYn=").append(useYn).append("\n");
	    sb.append(", createDate=").append(createDate).append("\n");
	    sb.append(", updateDate=").append(updateDate).append("\n");
	    sb.append("]\n");
    	return sb.toString();
    }

}
