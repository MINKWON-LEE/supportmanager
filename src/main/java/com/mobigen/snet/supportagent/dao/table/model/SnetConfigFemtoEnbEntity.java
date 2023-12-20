package com.mobigen.snet.supportagent.dao.table.model;

/**
 * <pre>
 *  - 테이블 설명 : 
 *  - 테이블 명 : SNET_CONFIG_FEMTO_ENB
 *  - PK : 
 *  - generated in : 2017-03-28 10:14
 * </pre>
 */
public class SnetConfigFemtoEnbEntity implements java.io.Serializable{

    /**<pre>
     * 설명 : 
     * 컬럼명 : eci
     * 타입 : VARCHAR (100)
     * </pre>
     */
    private String eci;

    /**<pre>
     * 설명 : 
     * 컬럼명 : femto_nm
     * 타입 : VARCHAR (100)
     * </pre>
     */
    private String femtoNm;

    /**<pre>
     * 설명 : 
     * 컬럼명 : branch_id
     * 타입 : VARCHAR (100)
     * </pre>
     */
    private String branchId;

    /**<pre>
     * 설명 : 
     * 컬럼명 : branch_nm
     * 타입 : VARCHAR (100)
     * </pre>
     */
    private String branchNm;

    /**<pre>
     * 설명 : 
     * 컬럼명 : team_id
     * 타입 : VARCHAR (100)
     * </pre>
     */
    private String teamId;

    /**<pre>
     * 설명 : 
     * 컬럼명 : team_nm
     * 타입 : VARCHAR (100)
     * </pre>
     */
    private String teamNm;

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

    /**<pre>
     * 설명 : 
     * 컬럼명 : enb_type
     * 타입 : VARCHAR (100)
     * </pre>
     */
    private String enbType;


    //=================== getter/setter methods ========================

    /**<pre>
     * 설명 : 
     * 컬럼명 : eci
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public String getEci(){
        return this.eci;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : eci
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public void setEci(String eci){
        this.eci = eci ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : femto_nm
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public String getFemtoNm(){
        return this.femtoNm;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : femto_nm
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public void setFemtoNm(String femtoNm){
        this.femtoNm = femtoNm ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : branch_id
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public String getBranchId(){
        return this.branchId;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : branch_id
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public void setBranchId(String branchId){
        this.branchId = branchId ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : branch_nm
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public String getBranchNm(){
        return this.branchNm;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : branch_nm
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public void setBranchNm(String branchNm){
        this.branchNm = branchNm ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : team_id
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public String getTeamId(){
        return this.teamId;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : team_id
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public void setTeamId(String teamId){
        this.teamId = teamId ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : team_nm
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public String getTeamNm(){
        return this.teamNm;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : team_nm
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public void setTeamNm(String teamNm){
        this.teamNm = teamNm ;
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
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : enb_type
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public String getEnbType(){
        return this.enbType;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : enb_type
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public void setEnbType(String enbType){
        this.enbType = enbType ;
    }
    
    
    



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[SnetConfigFemtoEnbEntity =>");
	    sb.append("  eci=").append(eci).append("\n");
	    sb.append(", femtoNm=").append(femtoNm).append("\n");
	    sb.append(", branchId=").append(branchId).append("\n");
	    sb.append(", branchNm=").append(branchNm).append("\n");
	    sb.append(", teamId=").append(teamId).append("\n");
	    sb.append(", teamNm=").append(teamNm).append("\n");
	    sb.append(", useYn=").append(useYn).append("\n");
	    sb.append(", createDate=").append(createDate).append("\n");
	    sb.append(", updateDate=").append(updateDate).append("\n");
	    sb.append(", enbType=").append(enbType).append("\n");
	    sb.append("]\n");
    	return sb.toString();
    }

}
