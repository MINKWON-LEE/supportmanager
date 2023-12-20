package com.mobigen.snet.supportagent.dao.table.model;

/**
 * <pre>
 *  - 테이블 설명 : 
 *  - 테이블 명 : SNET_CONFIG_FEMTO_FIRMWARE
 *  - PK : 
 *  - generated in : 2017-03-28 10:14
 * </pre>
 */
public class SnetConfigFemtoFirmwareEntity implements java.io.Serializable{

    /**<pre>
     * 설명 : 
     * 컬럼명 : manufacturer
     * 타입 : VARCHAR (100)
     * </pre>
     */
    private String manufacturer;

    /**<pre>
     * 설명 : 
     * 컬럼명 : softwareversion
     * 타입 : VARCHAR (100)
     * </pre>
     */
    private String softwareversion;

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
     * 컬럼명 : manufacturer
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public String getManufacturer(){
        return this.manufacturer;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : manufacturer
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public void setManufacturer(String manufacturer){
        this.manufacturer = manufacturer ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : softwareversion
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public String getSoftwareversion(){
        return this.softwareversion;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : softwareversion
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public void setSoftwareversion(String softwareversion){
        this.softwareversion = softwareversion ;
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
        sb.append("[SnetConfigFemtoFirmwareEntity =>");
	    sb.append("  manufacturer=").append(manufacturer).append("\n");
	    sb.append(", softwareversion=").append(softwareversion).append("\n");
	    sb.append(", useYn=").append(useYn).append("\n");
	    sb.append(", createDate=").append(createDate).append("\n");
	    sb.append(", updateDate=").append(updateDate).append("\n");
	    sb.append("]\n");
    	return sb.toString();
    }

}
