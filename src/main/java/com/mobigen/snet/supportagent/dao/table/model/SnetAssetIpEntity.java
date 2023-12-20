package com.mobigen.snet.supportagent.dao.table.model;

/**
 * <pre>
 *  - 테이블 설명 : 
 *  - 테이블 명 : SNET_ASSET_IP
 *  - PK :   asset_cd, ip_address
 *  - generated in : 2017-03-14 11:17
 * </pre>
 */
public class SnetAssetIpEntity implements java.io.Serializable{

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
     * 컬럼명 : ip_address
     * 타입 : VARCHAR (100)
     * PK
     * </pre>
     */
    private String ipAddress;

    /**<pre>
     * 설명 : 
     * 컬럼명 : ip_represent
     * 타입 : INT (10.0)
     * </pre>
     */
    private String ipRepresent;

    /**<pre>
     * 설명 : 
     * 컬럼명 : ip_version
     * 타입 : INT (10.0)
     * </pre>
     */
    private String ipVersion;

    /**<pre>
     * 설명 : 
     * 컬럼명 : sgw_regi
     * 타입 : INT (10.0)
     * </pre>
     */
    private String sgwRegi;

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_regi
     * 타입 : INT (10.0)
     * </pre>
     */
    private String userRegi;


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
     * 컬럼명 : ip_address
     * 타입 : VARCHAR (100)
     * PK
     * </pre>
     */
    public String getIpAddress(){
        return this.ipAddress;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ip_address
     * 타입 : VARCHAR (100)
     * PK
     * </pre>
     */
    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : ip_represent
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getIpRepresent(){
        return this.ipRepresent;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ip_represent
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setIpRepresent(String ipRepresent){
		// 숫자타입인 경우
		if("".equals(ipRepresent)){
			this.ipRepresent = null;
		}else{
	        this.ipRepresent = ipRepresent ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ip_represent
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getIpRepresent_asLong(){
        if(this.ipRepresent==null || this.ipRepresent.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.ipRepresent);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : ip_represent
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setIpRepresent_asLong(Long ipRepresent){
    	if(ipRepresent==null){
    		this.ipRepresent = null;
    	}else{
	        this.ipRepresent = String.valueOf(ipRepresent) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : ip_version
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getIpVersion(){
        return this.ipVersion;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ip_version
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setIpVersion(String ipVersion){
		// 숫자타입인 경우
		if("".equals(ipVersion)){
			this.ipVersion = null;
		}else{
	        this.ipVersion = ipVersion ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : ip_version
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getIpVersion_asLong(){
        if(this.ipVersion==null || this.ipVersion.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.ipVersion);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : ip_version
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setIpVersion_asLong(Long ipVersion){
    	if(ipVersion==null){
    		this.ipVersion = null;
    	}else{
	        this.ipVersion = String.valueOf(ipVersion) ;
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
    



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[SnetAssetIpEntity =>");
	    sb.append("  assetCd=").append(assetCd).append("\n");
	    sb.append(", ipAddress=").append(ipAddress).append("\n");
	    sb.append(", ipRepresent=").append(ipRepresent).append("\n");
	    sb.append(", ipVersion=").append(ipVersion).append("\n");
	    sb.append(", sgwRegi=").append(sgwRegi).append("\n");
	    sb.append(", userRegi=").append(userRegi).append("\n");
	    sb.append("]\n");
    	return sb.toString();
    }

}
