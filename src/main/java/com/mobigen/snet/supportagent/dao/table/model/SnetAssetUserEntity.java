package com.mobigen.snet.supportagent.dao.table.model;

/**
 * <pre>
 *  - 테이블 설명 : 
 *  - 테이블 명 : SNET_ASSET_USER
 *  - PK :   asset_cd, user_id, user_type
 *  - generated in : 2017-03-14 11:17
 * </pre>
 */
public class SnetAssetUserEntity implements java.io.Serializable{

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
     * 컬럼명 : user_id
     * 타입 : VARCHAR (33)
     * PK
     * </pre>
     */
    private String userId;

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_type
     * 타입 : CHAR (2)
     * PK
     * </pre>
     */
    private String userType;

    /**<pre>
     * 설명 : 
     * 컬럼명 : team_id
     * 타입 : VARCHAR (20)
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
     * 컬럼명 : user_nm
     * 타입 : VARCHAR (33)
     * </pre>
     */
    private String userNm;

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_ms
     * 타입 : VARCHAR (30)
     * </pre>
     */
    private String userMs;

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_mail
     * 타입 : VARCHAR (512)
     * </pre>
     */
    private String userMail;

    /**<pre>
     * 설명 : 
     * 컬럼명 : create_date
     * 타입 : DATETIME (19)
     * </pre>
     */
    private java.sql.Timestamp createDate;


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
     * 컬럼명 : user_id
     * 타입 : VARCHAR (33)
     * PK
     * </pre>
     */
    public String getUserId(){
        return this.userId;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id
     * 타입 : VARCHAR (33)
     * PK
     * </pre>
     */
    public void setUserId(String userId){
        this.userId = userId ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_type
     * 타입 : CHAR (2)
     * PK
     * </pre>
     */
    public String getUserType(){
        return this.userType;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : user_type
     * 타입 : CHAR (2)
     * PK
     * </pre>
     */
    public void setUserType(String userType){
        this.userType = userType ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : team_id
     * 타입 : VARCHAR (20)
     * 
     * </pre>
     */
    public String getTeamId(){
        return this.teamId;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : team_id
     * 타입 : VARCHAR (20)
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
     * 컬럼명 : user_nm
     * 타입 : VARCHAR (33)
     * 
     * </pre>
     */
    public String getUserNm(){
        return this.userNm;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : user_nm
     * 타입 : VARCHAR (33)
     * 
     * </pre>
     */
    public void setUserNm(String userNm){
        this.userNm = userNm ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_ms
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public String getUserMs(){
        return this.userMs;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : user_ms
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public void setUserMs(String userMs){
        this.userMs = userMs ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_mail
     * 타입 : VARCHAR (512)
     * 
     * </pre>
     */
    public String getUserMail(){
        return this.userMail;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : user_mail
     * 타입 : VARCHAR (512)
     * 
     * </pre>
     */
    public void setUserMail(String userMail){
        this.userMail = userMail ;
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
    
    
    



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[SnetAssetUserEntity =>");
	    sb.append("  assetCd=").append(assetCd).append("\n");
	    sb.append(", userId=").append(userId).append("\n");
	    sb.append(", userType=").append(userType).append("\n");
	    sb.append(", teamId=").append(teamId).append("\n");
	    sb.append(", teamNm=").append(teamNm).append("\n");
	    sb.append(", userNm=").append(userNm).append("\n");
	    sb.append(", userMs=").append(userMs).append("\n");
	    sb.append(", userMail=").append(userMail).append("\n");
	    sb.append(", createDate=").append(createDate).append("\n");
	    sb.append("]\n");
    	return sb.toString();
    }

}
