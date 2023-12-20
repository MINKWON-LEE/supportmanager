package com.mobigen.snet.supportagent.dao.table.model;

/**
 * <pre>
 *  - 테이블 설명 : 
 *  - 테이블 명 : SNET_CONNECT_MASTER
 *  - PK :   asset_cd
 *  - generated in : 2017-03-14 11:17
 * </pre>
 */
public class SnetConnectMasterEntity implements java.io.Serializable{

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
     * 컬럼명 : tmp_asset_flag
     * 타입 : INT (10.0)
     * </pre>
     */
    private String tmpAssetFlag;

    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_nm
     * 타입 : VARCHAR (256)
     * </pre>
     */
    private String swNm;

    /**<pre>
     * 설명 : 
     * 컬럼명 : os_bit
     * 타입 : INT (10.0)
     * </pre>
     */
    private String osBit;

    /**<pre>
     * 설명 : 
     * 컬럼명 : update_date
     * 타입 : DATETIME (19)
     * </pre>
     */
    private java.sql.Timestamp updateDate;

    /**<pre>
     * 설명 : 
     * 컬럼명 : update_user_id
     * 타입 : VARCHAR (30)
     * </pre>
     */
    private String updateUserId;

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_os
     * 타입 : VARCHAR (30)
     * </pre>
     */
    private String userIdOs;

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_dba
     * 타입 : VARCHAR (30)
     * </pre>
     */
    private String userIdDba;

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_url
     * 타입 : VARCHAR (30)
     * </pre>
     */
    private String userIdUrl;

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_root
     * 타입 : VARCHAR (30)
     * </pre>
     */
    private String userIdRoot;

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_web
     * 타입 : VARCHAR (30)
     * </pre>
     */
    private String userIdWeb;

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_was
     * 타입 : VARCHAR (30)
     * </pre>
     */
    private String userIdWas;

    /**<pre>
     * 설명 : 
     * 컬럼명 : password_os
     * 타입 : VARCHAR (1024)
     * </pre>
     */
    private String passwordOs;

    /**<pre>
     * 설명 : 
     * 컬럼명 : password_dba
     * 타입 : VARCHAR (1024)
     * </pre>
     */
    private String passwordDba;

    /**<pre>
     * 설명 : 
     * 컬럼명 : password_url
     * 타입 : VARCHAR (1024)
     * </pre>
     */
    private String passwordUrl;

    /**<pre>
     * 설명 : 
     * 컬럼명 : password_web
     * 타입 : VARCHAR (1024)
     * </pre>
     */
    private String passwordWeb;

    /**<pre>
     * 설명 : 
     * 컬럼명 : password_was
     * 타입 : VARCHAR (1024)
     * </pre>
     */
    private String passwordWas;

    /**<pre>
     * 설명 : 
     * 컬럼명 : password_root
     * 타입 : VARCHAR (1024)
     * </pre>
     */
    private String passwordRoot;

    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_os
     * 타입 : INT (10.0)
     * </pre>
     */
    private String accountChkOs;

    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_dba
     * 타입 : INT (10.0)
     * </pre>
     */
    private String accountChkDba;

    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_url
     * 타입 : INT (10.0)
     * </pre>
     */
    private String accountChkUrl;

    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_root
     * 타입 : INT (10.0)
     * </pre>
     */
    private String accountChkRoot;

    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_web
     * 타입 : INT (10.0)
     * </pre>
     */
    private String accountChkWeb;

    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_was
     * 타입 : INT (10.0)
     * </pre>
     */
    private String accountChkWas;

    /**<pre>
     * 설명 : 
     * 컬럼명 : account_desc
     * 타입 : VARCHAR (256)
     * </pre>
     */
    private String accountDesc;

    /**<pre>
     * 설명 : 
     * 컬럼명 : prompt_user_id_os
     * 타입 : VARCHAR (256)
     * </pre>
     */
    private String promptUserIdOs;

    /**<pre>
     * 설명 : 
     * 컬럼명 : prompt_user_id_root
     * 타입 : VARCHAR (256)
     * </pre>
     */
    private String promptUserIdRoot;

    /**<pre>
     * 설명 : 
     * 컬럼명 : prompt_user_id_web
     * 타입 : VARCHAR (256)
     * </pre>
     */
    private String promptUserIdWeb;

    /**<pre>
     * 설명 : 
     * 컬럼명 : prompt_user_id_was
     * 타입 : VARCHAR (256)
     * </pre>
     */
    private String promptUserIdWas;

    /**<pre>
     * 설명 : 
     * 컬럼명 : prompt_user_id_db
     * 타입 : VARCHAR (256)
     * </pre>
     */
    private String promptUserIdDb;

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ssh
     * 타입 : INT (10.0)
     * </pre>
     */
    private String portSsh;

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_sftp
     * 타입 : INT (10.0)
     * </pre>
     */
    private String portSftp;

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_telnet
     * 타입 : INT (10.0)
     * </pre>
     */
    private String portTelnet;

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ftp
     * 타입 : INT (10.0)
     * </pre>
     */
    private String portFtp;

    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_log
     * 타입 : VARCHAR (4000)
     * </pre>
     */
    private String connectLog;

    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_shell_root
     * 타입 : VARCHAR (20)
     * </pre>
     */
    private String connectShellRoot;

    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_shell_os
     * 타입 : VARCHAR (20)
     * </pre>
     */
    private String connectShellOs;

    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_shell_web
     * 타입 : VARCHAR (20)
     * </pre>
     */
    private String connectShellWeb;

    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_shell_was
     * 타입 : VARCHAR (20)
     * </pre>
     */
    private String connectShellWas;

    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_shell_db
     * 타입 : VARCHAR (20)
     * </pre>
     */
    private String connectShellDb;

    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_ip_address
     * 타입 : VARCHAR (100)
     * </pre>
     */
    private String connectIpAddress;

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ssh_alive
     * 타입 : INT (10.0)
     * </pre>
     */
    private String portSshAlive;

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_sftp_alive
     * 타입 : INT (10.0)
     * </pre>
     */
    private String portSftpAlive;

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_telnet_alive
     * 타입 : INT (10.0)
     * </pre>
     */
    private String portTelnetAlive;

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ftp_alive
     * 타입 : INT (10.0)
     * </pre>
     */
    private String portFtpAlive;

    /**<pre>
     * 설명 : 
     * 컬럼명 : nw_get_config_cmd_cd
     * 타입 : VARCHAR (1024)
     * </pre>
     */
    private String nwGetConfigCmdCd;

    /**<pre>
     * 설명 : ????
     * 컬럼명 : dir_dba
     * 타입 : VARCHAR (1024)
     * </pre>
     */
    private String dirDba;

    /**<pre>
     * 설명 : 설치경로
     * 컬럼명 : dir_url
     * 타입 : VARCHAR (1024)
     * </pre>
     */
    private String dirUrl;

    /**<pre>
     * 설명 : 설치경로
     * 컬럼명 : dir_web
     * 타입 : VARCHAR (1024)
     * </pre>
     */
    private String dirWeb;

    /**<pre>
     * 설명 : 설치경로
     * 컬럼명 : dir_was
     * 타입 : VARCHAR (1024)
     * </pre>
     */
    private String dirWas;

    /**<pre>
     * 설명 : DBA설치계정
     * 컬럼명 : inst_account_dba
     * 타입 : VARCHAR (30)
     * </pre>
     */
    private String instAccountDba;

    /**<pre>
     * 설명 : URL설치계정
     * 컬럼명 : inst_account_url
     * 타입 : VARCHAR (30)
     * </pre>
     */
    private String instAccountUrl;

    /**<pre>
     * 설명 : WEB설치계정
     * 컬럼명 : inst_account_web
     * 타입 : VARCHAR (30)
     * </pre>
     */
    private String instAccountWeb;

    /**<pre>
     * 설명 : WAS설치계정
     * 컬럼명 : inst_account_was
     * 타입 : VARCHAR (30)
     * </pre>
     */
    private String instAccountWas;

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_url_adm
     * 타입 : VARCHAR (100)
     * </pre>
     */
    private String userIdUrlAdm;

    /**<pre>
     * 설명 : 
     * 컬럼명 : password_url_adm
     * 타입 : VARCHAR (100)
     * </pre>
     */
    private String passwordUrlAdm;

    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_url_adm
     * 타입 : INT (10.0)
     * </pre>
     */
    private String accountChkUrlAdm;

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_database
     * 타입 : INT (10.0)
     * </pre>
     */
    private String portDatabase;

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_window
     * 타입 : INT (10.0)
     * </pre>
     */
    private String portWindow;

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_window_alive
     * 타입 : INT (10.0)
     * </pre>
     */
    private String portWindowAlive;

    /**<pre>
     * 설명 : CR : 아이디 패스워드, RO : Root ID 로 로그임, RSA: 인증서 파일 로그인, LC : Command Line SU 
     * 컬럼명 : login_type
     * 타입 : VARCHAR (50)
     * </pre>
     */
    private String loginType;

    /**<pre>
     * 설명 : 
     * 컬럼명 : relay_asset_cd
     * 타입 : VARCHAR (35)
     * </pre>
     */
    private String relayAssetCd;

    /**<pre>
     * 설명 : 
     * 컬럼명 : use_sudo
     * 타입 : VARCHAR (2)
     * </pre>
     */
    private String useSudo;


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
     * 컬럼명 : tmp_asset_flag
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getTmpAssetFlag(){
        return this.tmpAssetFlag;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : tmp_asset_flag
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setTmpAssetFlag(String tmpAssetFlag){
		// 숫자타입인 경우
		if("".equals(tmpAssetFlag)){
			this.tmpAssetFlag = null;
		}else{
	        this.tmpAssetFlag = tmpAssetFlag ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : tmp_asset_flag
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getTmpAssetFlag_asLong(){
        if(this.tmpAssetFlag==null || this.tmpAssetFlag.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.tmpAssetFlag);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : tmp_asset_flag
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setTmpAssetFlag_asLong(Long tmpAssetFlag){
    	if(tmpAssetFlag==null){
    		this.tmpAssetFlag = null;
    	}else{
	        this.tmpAssetFlag = String.valueOf(tmpAssetFlag) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_nm
     * 타입 : VARCHAR (256)
     * 
     * </pre>
     */
    public String getSwNm(){
        return this.swNm;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : sw_nm
     * 타입 : VARCHAR (256)
     * 
     * </pre>
     */
    public void setSwNm(String swNm){
        this.swNm = swNm ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : os_bit
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getOsBit(){
        return this.osBit;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : os_bit
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setOsBit(String osBit){
		// 숫자타입인 경우
		if("".equals(osBit)){
			this.osBit = null;
		}else{
	        this.osBit = osBit ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : os_bit
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getOsBit_asLong(){
        if(this.osBit==null || this.osBit.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.osBit);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : os_bit
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setOsBit_asLong(Long osBit){
    	if(osBit==null){
    		this.osBit = null;
    	}else{
	        this.osBit = String.valueOf(osBit) ;
    	}    
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
     * 컬럼명 : update_user_id
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public String getUpdateUserId(){
        return this.updateUserId;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : update_user_id
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public void setUpdateUserId(String updateUserId){
        this.updateUserId = updateUserId ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_os
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public String getUserIdOs(){
        return this.userIdOs;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_os
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public void setUserIdOs(String userIdOs){
        this.userIdOs = userIdOs ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_dba
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public String getUserIdDba(){
        return this.userIdDba;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_dba
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public void setUserIdDba(String userIdDba){
        this.userIdDba = userIdDba ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_url
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public String getUserIdUrl(){
        return this.userIdUrl;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_url
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public void setUserIdUrl(String userIdUrl){
        this.userIdUrl = userIdUrl ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_root
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public String getUserIdRoot(){
        return this.userIdRoot;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_root
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public void setUserIdRoot(String userIdRoot){
        this.userIdRoot = userIdRoot ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_web
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public String getUserIdWeb(){
        return this.userIdWeb;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_web
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public void setUserIdWeb(String userIdWeb){
        this.userIdWeb = userIdWeb ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_was
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public String getUserIdWas(){
        return this.userIdWas;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_was
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public void setUserIdWas(String userIdWas){
        this.userIdWas = userIdWas ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : password_os
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public String getPasswordOs(){
        return this.passwordOs;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : password_os
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public void setPasswordOs(String passwordOs){
        this.passwordOs = passwordOs ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : password_dba
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public String getPasswordDba(){
        return this.passwordDba;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : password_dba
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public void setPasswordDba(String passwordDba){
        this.passwordDba = passwordDba ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : password_url
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public String getPasswordUrl(){
        return this.passwordUrl;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : password_url
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public void setPasswordUrl(String passwordUrl){
        this.passwordUrl = passwordUrl ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : password_web
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public String getPasswordWeb(){
        return this.passwordWeb;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : password_web
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public void setPasswordWeb(String passwordWeb){
        this.passwordWeb = passwordWeb ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : password_was
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public String getPasswordWas(){
        return this.passwordWas;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : password_was
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public void setPasswordWas(String passwordWas){
        this.passwordWas = passwordWas ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : password_root
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public String getPasswordRoot(){
        return this.passwordRoot;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : password_root
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public void setPasswordRoot(String passwordRoot){
        this.passwordRoot = passwordRoot ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_os
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAccountChkOs(){
        return this.accountChkOs;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_os
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAccountChkOs(String accountChkOs){
		// 숫자타입인 경우
		if("".equals(accountChkOs)){
			this.accountChkOs = null;
		}else{
	        this.accountChkOs = accountChkOs ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_os
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAccountChkOs_asLong(){
        if(this.accountChkOs==null || this.accountChkOs.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.accountChkOs);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_os
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAccountChkOs_asLong(Long accountChkOs){
    	if(accountChkOs==null){
    		this.accountChkOs = null;
    	}else{
	        this.accountChkOs = String.valueOf(accountChkOs) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_dba
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAccountChkDba(){
        return this.accountChkDba;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_dba
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAccountChkDba(String accountChkDba){
		// 숫자타입인 경우
		if("".equals(accountChkDba)){
			this.accountChkDba = null;
		}else{
	        this.accountChkDba = accountChkDba ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_dba
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAccountChkDba_asLong(){
        if(this.accountChkDba==null || this.accountChkDba.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.accountChkDba);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_dba
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAccountChkDba_asLong(Long accountChkDba){
    	if(accountChkDba==null){
    		this.accountChkDba = null;
    	}else{
	        this.accountChkDba = String.valueOf(accountChkDba) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_url
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAccountChkUrl(){
        return this.accountChkUrl;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_url
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAccountChkUrl(String accountChkUrl){
		// 숫자타입인 경우
		if("".equals(accountChkUrl)){
			this.accountChkUrl = null;
		}else{
	        this.accountChkUrl = accountChkUrl ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_url
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAccountChkUrl_asLong(){
        if(this.accountChkUrl==null || this.accountChkUrl.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.accountChkUrl);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_url
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAccountChkUrl_asLong(Long accountChkUrl){
    	if(accountChkUrl==null){
    		this.accountChkUrl = null;
    	}else{
	        this.accountChkUrl = String.valueOf(accountChkUrl) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_root
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAccountChkRoot(){
        return this.accountChkRoot;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_root
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAccountChkRoot(String accountChkRoot){
		// 숫자타입인 경우
		if("".equals(accountChkRoot)){
			this.accountChkRoot = null;
		}else{
	        this.accountChkRoot = accountChkRoot ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_root
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAccountChkRoot_asLong(){
        if(this.accountChkRoot==null || this.accountChkRoot.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.accountChkRoot);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_root
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAccountChkRoot_asLong(Long accountChkRoot){
    	if(accountChkRoot==null){
    		this.accountChkRoot = null;
    	}else{
	        this.accountChkRoot = String.valueOf(accountChkRoot) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_web
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAccountChkWeb(){
        return this.accountChkWeb;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_web
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAccountChkWeb(String accountChkWeb){
		// 숫자타입인 경우
		if("".equals(accountChkWeb)){
			this.accountChkWeb = null;
		}else{
	        this.accountChkWeb = accountChkWeb ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_web
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAccountChkWeb_asLong(){
        if(this.accountChkWeb==null || this.accountChkWeb.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.accountChkWeb);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_web
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAccountChkWeb_asLong(Long accountChkWeb){
    	if(accountChkWeb==null){
    		this.accountChkWeb = null;
    	}else{
	        this.accountChkWeb = String.valueOf(accountChkWeb) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_was
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAccountChkWas(){
        return this.accountChkWas;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_was
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAccountChkWas(String accountChkWas){
		// 숫자타입인 경우
		if("".equals(accountChkWas)){
			this.accountChkWas = null;
		}else{
	        this.accountChkWas = accountChkWas ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_was
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAccountChkWas_asLong(){
        if(this.accountChkWas==null || this.accountChkWas.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.accountChkWas);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_was
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAccountChkWas_asLong(Long accountChkWas){
    	if(accountChkWas==null){
    		this.accountChkWas = null;
    	}else{
	        this.accountChkWas = String.valueOf(accountChkWas) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : account_desc
     * 타입 : VARCHAR (256)
     * 
     * </pre>
     */
    public String getAccountDesc(){
        return this.accountDesc;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_desc
     * 타입 : VARCHAR (256)
     * 
     * </pre>
     */
    public void setAccountDesc(String accountDesc){
        this.accountDesc = accountDesc ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : prompt_user_id_os
     * 타입 : VARCHAR (256)
     * 
     * </pre>
     */
    public String getPromptUserIdOs(){
        return this.promptUserIdOs;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : prompt_user_id_os
     * 타입 : VARCHAR (256)
     * 
     * </pre>
     */
    public void setPromptUserIdOs(String promptUserIdOs){
        this.promptUserIdOs = promptUserIdOs ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : prompt_user_id_root
     * 타입 : VARCHAR (256)
     * 
     * </pre>
     */
    public String getPromptUserIdRoot(){
        return this.promptUserIdRoot;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : prompt_user_id_root
     * 타입 : VARCHAR (256)
     * 
     * </pre>
     */
    public void setPromptUserIdRoot(String promptUserIdRoot){
        this.promptUserIdRoot = promptUserIdRoot ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : prompt_user_id_web
     * 타입 : VARCHAR (256)
     * 
     * </pre>
     */
    public String getPromptUserIdWeb(){
        return this.promptUserIdWeb;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : prompt_user_id_web
     * 타입 : VARCHAR (256)
     * 
     * </pre>
     */
    public void setPromptUserIdWeb(String promptUserIdWeb){
        this.promptUserIdWeb = promptUserIdWeb ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : prompt_user_id_was
     * 타입 : VARCHAR (256)
     * 
     * </pre>
     */
    public String getPromptUserIdWas(){
        return this.promptUserIdWas;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : prompt_user_id_was
     * 타입 : VARCHAR (256)
     * 
     * </pre>
     */
    public void setPromptUserIdWas(String promptUserIdWas){
        this.promptUserIdWas = promptUserIdWas ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : prompt_user_id_db
     * 타입 : VARCHAR (256)
     * 
     * </pre>
     */
    public String getPromptUserIdDb(){
        return this.promptUserIdDb;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : prompt_user_id_db
     * 타입 : VARCHAR (256)
     * 
     * </pre>
     */
    public void setPromptUserIdDb(String promptUserIdDb){
        this.promptUserIdDb = promptUserIdDb ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ssh
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getPortSsh(){
        return this.portSsh;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ssh
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortSsh(String portSsh){
		// 숫자타입인 경우
		if("".equals(portSsh)){
			this.portSsh = null;
		}else{
	        this.portSsh = portSsh ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ssh
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getPortSsh_asLong(){
        if(this.portSsh==null || this.portSsh.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.portSsh);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ssh
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortSsh_asLong(Long portSsh){
    	if(portSsh==null){
    		this.portSsh = null;
    	}else{
	        this.portSsh = String.valueOf(portSsh) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_sftp
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getPortSftp(){
        return this.portSftp;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_sftp
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortSftp(String portSftp){
		// 숫자타입인 경우
		if("".equals(portSftp)){
			this.portSftp = null;
		}else{
	        this.portSftp = portSftp ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_sftp
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getPortSftp_asLong(){
        if(this.portSftp==null || this.portSftp.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.portSftp);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_sftp
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortSftp_asLong(Long portSftp){
    	if(portSftp==null){
    		this.portSftp = null;
    	}else{
	        this.portSftp = String.valueOf(portSftp) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_telnet
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getPortTelnet(){
        return this.portTelnet;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_telnet
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortTelnet(String portTelnet){
		// 숫자타입인 경우
		if("".equals(portTelnet)){
			this.portTelnet = null;
		}else{
	        this.portTelnet = portTelnet ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_telnet
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getPortTelnet_asLong(){
        if(this.portTelnet==null || this.portTelnet.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.portTelnet);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_telnet
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortTelnet_asLong(Long portTelnet){
    	if(portTelnet==null){
    		this.portTelnet = null;
    	}else{
	        this.portTelnet = String.valueOf(portTelnet) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ftp
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getPortFtp(){
        return this.portFtp;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ftp
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortFtp(String portFtp){
		// 숫자타입인 경우
		if("".equals(portFtp)){
			this.portFtp = null;
		}else{
	        this.portFtp = portFtp ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ftp
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getPortFtp_asLong(){
        if(this.portFtp==null || this.portFtp.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.portFtp);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ftp
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortFtp_asLong(Long portFtp){
    	if(portFtp==null){
    		this.portFtp = null;
    	}else{
	        this.portFtp = String.valueOf(portFtp) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_log
     * 타입 : VARCHAR (4000)
     * 
     * </pre>
     */
    public String getConnectLog(){
        return this.connectLog;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_log
     * 타입 : VARCHAR (4000)
     * 
     * </pre>
     */
    public void setConnectLog(String connectLog){
        this.connectLog = connectLog ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_shell_root
     * 타입 : VARCHAR (20)
     * 
     * </pre>
     */
    public String getConnectShellRoot(){
        return this.connectShellRoot;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_shell_root
     * 타입 : VARCHAR (20)
     * 
     * </pre>
     */
    public void setConnectShellRoot(String connectShellRoot){
        this.connectShellRoot = connectShellRoot ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_shell_os
     * 타입 : VARCHAR (20)
     * 
     * </pre>
     */
    public String getConnectShellOs(){
        return this.connectShellOs;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_shell_os
     * 타입 : VARCHAR (20)
     * 
     * </pre>
     */
    public void setConnectShellOs(String connectShellOs){
        this.connectShellOs = connectShellOs ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_shell_web
     * 타입 : VARCHAR (20)
     * 
     * </pre>
     */
    public String getConnectShellWeb(){
        return this.connectShellWeb;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_shell_web
     * 타입 : VARCHAR (20)
     * 
     * </pre>
     */
    public void setConnectShellWeb(String connectShellWeb){
        this.connectShellWeb = connectShellWeb ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_shell_was
     * 타입 : VARCHAR (20)
     * 
     * </pre>
     */
    public String getConnectShellWas(){
        return this.connectShellWas;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_shell_was
     * 타입 : VARCHAR (20)
     * 
     * </pre>
     */
    public void setConnectShellWas(String connectShellWas){
        this.connectShellWas = connectShellWas ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_shell_db
     * 타입 : VARCHAR (20)
     * 
     * </pre>
     */
    public String getConnectShellDb(){
        return this.connectShellDb;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_shell_db
     * 타입 : VARCHAR (20)
     * 
     * </pre>
     */
    public void setConnectShellDb(String connectShellDb){
        this.connectShellDb = connectShellDb ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_ip_address
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public String getConnectIpAddress(){
        return this.connectIpAddress;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : connect_ip_address
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public void setConnectIpAddress(String connectIpAddress){
        this.connectIpAddress = connectIpAddress ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ssh_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getPortSshAlive(){
        return this.portSshAlive;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ssh_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortSshAlive(String portSshAlive){
		// 숫자타입인 경우
		if("".equals(portSshAlive)){
			this.portSshAlive = null;
		}else{
	        this.portSshAlive = portSshAlive ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ssh_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getPortSshAlive_asLong(){
        if(this.portSshAlive==null || this.portSshAlive.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.portSshAlive);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ssh_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortSshAlive_asLong(Long portSshAlive){
    	if(portSshAlive==null){
    		this.portSshAlive = null;
    	}else{
	        this.portSshAlive = String.valueOf(portSshAlive) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_sftp_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getPortSftpAlive(){
        return this.portSftpAlive;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_sftp_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortSftpAlive(String portSftpAlive){
		// 숫자타입인 경우
		if("".equals(portSftpAlive)){
			this.portSftpAlive = null;
		}else{
	        this.portSftpAlive = portSftpAlive ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_sftp_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getPortSftpAlive_asLong(){
        if(this.portSftpAlive==null || this.portSftpAlive.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.portSftpAlive);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_sftp_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortSftpAlive_asLong(Long portSftpAlive){
    	if(portSftpAlive==null){
    		this.portSftpAlive = null;
    	}else{
	        this.portSftpAlive = String.valueOf(portSftpAlive) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_telnet_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getPortTelnetAlive(){
        return this.portTelnetAlive;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_telnet_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortTelnetAlive(String portTelnetAlive){
		// 숫자타입인 경우
		if("".equals(portTelnetAlive)){
			this.portTelnetAlive = null;
		}else{
	        this.portTelnetAlive = portTelnetAlive ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_telnet_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getPortTelnetAlive_asLong(){
        if(this.portTelnetAlive==null || this.portTelnetAlive.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.portTelnetAlive);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_telnet_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortTelnetAlive_asLong(Long portTelnetAlive){
    	if(portTelnetAlive==null){
    		this.portTelnetAlive = null;
    	}else{
	        this.portTelnetAlive = String.valueOf(portTelnetAlive) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ftp_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getPortFtpAlive(){
        return this.portFtpAlive;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ftp_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortFtpAlive(String portFtpAlive){
		// 숫자타입인 경우
		if("".equals(portFtpAlive)){
			this.portFtpAlive = null;
		}else{
	        this.portFtpAlive = portFtpAlive ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ftp_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getPortFtpAlive_asLong(){
        if(this.portFtpAlive==null || this.portFtpAlive.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.portFtpAlive);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_ftp_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortFtpAlive_asLong(Long portFtpAlive){
    	if(portFtpAlive==null){
    		this.portFtpAlive = null;
    	}else{
	        this.portFtpAlive = String.valueOf(portFtpAlive) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : nw_get_config_cmd_cd
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public String getNwGetConfigCmdCd(){
        return this.nwGetConfigCmdCd;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : nw_get_config_cmd_cd
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public void setNwGetConfigCmdCd(String nwGetConfigCmdCd){
        this.nwGetConfigCmdCd = nwGetConfigCmdCd ;
    }
    
    
    

    /**<pre>
     * 설명 : ????
     * 컬럼명 : dir_dba
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public String getDirDba(){
        return this.dirDba;
    }
    
    /**<pre>
     * 설명 : ????
     * 컬럼명 : dir_dba
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public void setDirDba(String dirDba){
        this.dirDba = dirDba ;
    }
    
    
    

    /**<pre>
     * 설명 : 설치경로
     * 컬럼명 : dir_url
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public String getDirUrl(){
        return this.dirUrl;
    }
    
    /**<pre>
     * 설명 : 설치경로
     * 컬럼명 : dir_url
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public void setDirUrl(String dirUrl){
        this.dirUrl = dirUrl ;
    }
    
    
    

    /**<pre>
     * 설명 : 설치경로
     * 컬럼명 : dir_web
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public String getDirWeb(){
        return this.dirWeb;
    }
    
    /**<pre>
     * 설명 : 설치경로
     * 컬럼명 : dir_web
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public void setDirWeb(String dirWeb){
        this.dirWeb = dirWeb ;
    }
    
    
    

    /**<pre>
     * 설명 : 설치경로
     * 컬럼명 : dir_was
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public String getDirWas(){
        return this.dirWas;
    }
    
    /**<pre>
     * 설명 : 설치경로
     * 컬럼명 : dir_was
     * 타입 : VARCHAR (1024)
     * 
     * </pre>
     */
    public void setDirWas(String dirWas){
        this.dirWas = dirWas ;
    }
    
    
    

    /**<pre>
     * 설명 : DBA설치계정
     * 컬럼명 : inst_account_dba
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public String getInstAccountDba(){
        return this.instAccountDba;
    }
    
    /**<pre>
     * 설명 : DBA설치계정
     * 컬럼명 : inst_account_dba
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public void setInstAccountDba(String instAccountDba){
        this.instAccountDba = instAccountDba ;
    }
    
    
    

    /**<pre>
     * 설명 : URL설치계정
     * 컬럼명 : inst_account_url
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public String getInstAccountUrl(){
        return this.instAccountUrl;
    }
    
    /**<pre>
     * 설명 : URL설치계정
     * 컬럼명 : inst_account_url
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public void setInstAccountUrl(String instAccountUrl){
        this.instAccountUrl = instAccountUrl ;
    }
    
    
    

    /**<pre>
     * 설명 : WEB설치계정
     * 컬럼명 : inst_account_web
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public String getInstAccountWeb(){
        return this.instAccountWeb;
    }
    
    /**<pre>
     * 설명 : WEB설치계정
     * 컬럼명 : inst_account_web
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public void setInstAccountWeb(String instAccountWeb){
        this.instAccountWeb = instAccountWeb ;
    }
    
    
    

    /**<pre>
     * 설명 : WAS설치계정
     * 컬럼명 : inst_account_was
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public String getInstAccountWas(){
        return this.instAccountWas;
    }
    
    /**<pre>
     * 설명 : WAS설치계정
     * 컬럼명 : inst_account_was
     * 타입 : VARCHAR (30)
     * 
     * </pre>
     */
    public void setInstAccountWas(String instAccountWas){
        this.instAccountWas = instAccountWas ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_url_adm
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public String getUserIdUrlAdm(){
        return this.userIdUrlAdm;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : user_id_url_adm
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public void setUserIdUrlAdm(String userIdUrlAdm){
        this.userIdUrlAdm = userIdUrlAdm ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : password_url_adm
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public String getPasswordUrlAdm(){
        return this.passwordUrlAdm;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : password_url_adm
     * 타입 : VARCHAR (100)
     * 
     * </pre>
     */
    public void setPasswordUrlAdm(String passwordUrlAdm){
        this.passwordUrlAdm = passwordUrlAdm ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_url_adm
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getAccountChkUrlAdm(){
        return this.accountChkUrlAdm;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_url_adm
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAccountChkUrlAdm(String accountChkUrlAdm){
		// 숫자타입인 경우
		if("".equals(accountChkUrlAdm)){
			this.accountChkUrlAdm = null;
		}else{
	        this.accountChkUrlAdm = accountChkUrlAdm ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_url_adm
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getAccountChkUrlAdm_asLong(){
        if(this.accountChkUrlAdm==null || this.accountChkUrlAdm.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.accountChkUrlAdm);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : account_chk_url_adm
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setAccountChkUrlAdm_asLong(Long accountChkUrlAdm){
    	if(accountChkUrlAdm==null){
    		this.accountChkUrlAdm = null;
    	}else{
	        this.accountChkUrlAdm = String.valueOf(accountChkUrlAdm) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_database
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getPortDatabase(){
        return this.portDatabase;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_database
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortDatabase(String portDatabase){
		// 숫자타입인 경우
		if("".equals(portDatabase)){
			this.portDatabase = null;
		}else{
	        this.portDatabase = portDatabase ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_database
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getPortDatabase_asLong(){
        if(this.portDatabase==null || this.portDatabase.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.portDatabase);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_database
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortDatabase_asLong(Long portDatabase){
    	if(portDatabase==null){
    		this.portDatabase = null;
    	}else{
	        this.portDatabase = String.valueOf(portDatabase) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_window
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getPortWindow(){
        return this.portWindow;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_window
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortWindow(String portWindow){
		// 숫자타입인 경우
		if("".equals(portWindow)){
			this.portWindow = null;
		}else{
	        this.portWindow = portWindow ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_window
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getPortWindow_asLong(){
        if(this.portWindow==null || this.portWindow.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.portWindow);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_window
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortWindow_asLong(Long portWindow){
    	if(portWindow==null){
    		this.portWindow = null;
    	}else{
	        this.portWindow = String.valueOf(portWindow) ;
    	}    
    }
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : port_window_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public String getPortWindowAlive(){
        return this.portWindowAlive;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_window_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortWindowAlive(String portWindowAlive){
		// 숫자타입인 경우
		if("".equals(portWindowAlive)){
			this.portWindowAlive = null;
		}else{
	        this.portWindowAlive = portWindowAlive ;
		}
    }
    
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_window_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public Long getPortWindowAlive_asLong(){
        if(this.portWindowAlive==null || this.portWindowAlive.equals("")){
        	return null;
        }else{
	        return Long.parseLong(this.portWindowAlive);
        } 
    }
    /**<pre>
     * 설명 : 
     * 컬럼명 : port_window_alive
     * 타입 : INT (10.0)
     * 
     * </pre>
     */
    public void setPortWindowAlive_asLong(Long portWindowAlive){
    	if(portWindowAlive==null){
    		this.portWindowAlive = null;
    	}else{
	        this.portWindowAlive = String.valueOf(portWindowAlive) ;
    	}    
    }
    

    /**<pre>
     * 설명 : CR : 아이디 패스워드, RO : Root ID 로 로그임, RSA: 인증서 파일 로그인, LC : Command Line SU 
     * 컬럼명 : login_type
     * 타입 : VARCHAR (50)
     * 
     * </pre>
     */
    public String getLoginType(){
        return this.loginType;
    }
    
    /**<pre>
     * 설명 : CR : 아이디 패스워드, RO : Root ID 로 로그임, RSA: 인증서 파일 로그인, LC : Command Line SU 
     * 컬럼명 : login_type
     * 타입 : VARCHAR (50)
     * 
     * </pre>
     */
    public void setLoginType(String loginType){
        this.loginType = loginType ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : relay_asset_cd
     * 타입 : VARCHAR (35)
     * 
     * </pre>
     */
    public String getRelayAssetCd(){
        return this.relayAssetCd;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : relay_asset_cd
     * 타입 : VARCHAR (35)
     * 
     * </pre>
     */
    public void setRelayAssetCd(String relayAssetCd){
        this.relayAssetCd = relayAssetCd ;
    }
    
    
    

    /**<pre>
     * 설명 : 
     * 컬럼명 : use_sudo
     * 타입 : VARCHAR (2)
     * 
     * </pre>
     */
    public String getUseSudo(){
        return this.useSudo;
    }
    
    /**<pre>
     * 설명 : 
     * 컬럼명 : use_sudo
     * 타입 : VARCHAR (2)
     * 
     * </pre>
     */
    public void setUseSudo(String useSudo){
        this.useSudo = useSudo ;
    }
    
    
    



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[SnetConnectMasterEntity =>");
	    sb.append("  assetCd=").append(assetCd).append("\n");
	    sb.append(", tmpAssetFlag=").append(tmpAssetFlag).append("\n");
	    sb.append(", swNm=").append(swNm).append("\n");
	    sb.append(", osBit=").append(osBit).append("\n");
	    sb.append(", updateDate=").append(updateDate).append("\n");
	    sb.append(", updateUserId=").append(updateUserId).append("\n");
	    sb.append(", userIdOs=").append(userIdOs).append("\n");
	    sb.append(", userIdDba=").append(userIdDba).append("\n");
	    sb.append(", userIdUrl=").append(userIdUrl).append("\n");
	    sb.append(", userIdRoot=").append(userIdRoot).append("\n");
	    sb.append(", userIdWeb=").append(userIdWeb).append("\n");
	    sb.append(", userIdWas=").append(userIdWas).append("\n");
	    sb.append(", passwordOs=").append(passwordOs).append("\n");
	    sb.append(", passwordDba=").append(passwordDba).append("\n");
	    sb.append(", passwordUrl=").append(passwordUrl).append("\n");
	    sb.append(", passwordWeb=").append(passwordWeb).append("\n");
	    sb.append(", passwordWas=").append(passwordWas).append("\n");
	    sb.append(", passwordRoot=").append(passwordRoot).append("\n");
	    sb.append(", accountChkOs=").append(accountChkOs).append("\n");
	    sb.append(", accountChkDba=").append(accountChkDba).append("\n");
	    sb.append(", accountChkUrl=").append(accountChkUrl).append("\n");
	    sb.append(", accountChkRoot=").append(accountChkRoot).append("\n");
	    sb.append(", accountChkWeb=").append(accountChkWeb).append("\n");
	    sb.append(", accountChkWas=").append(accountChkWas).append("\n");
	    sb.append(", accountDesc=").append(accountDesc).append("\n");
	    sb.append(", promptUserIdOs=").append(promptUserIdOs).append("\n");
	    sb.append(", promptUserIdRoot=").append(promptUserIdRoot).append("\n");
	    sb.append(", promptUserIdWeb=").append(promptUserIdWeb).append("\n");
	    sb.append(", promptUserIdWas=").append(promptUserIdWas).append("\n");
	    sb.append(", promptUserIdDb=").append(promptUserIdDb).append("\n");
	    sb.append(", portSsh=").append(portSsh).append("\n");
	    sb.append(", portSftp=").append(portSftp).append("\n");
	    sb.append(", portTelnet=").append(portTelnet).append("\n");
	    sb.append(", portFtp=").append(portFtp).append("\n");
	    sb.append(", connectLog=").append(connectLog).append("\n");
	    sb.append(", connectShellRoot=").append(connectShellRoot).append("\n");
	    sb.append(", connectShellOs=").append(connectShellOs).append("\n");
	    sb.append(", connectShellWeb=").append(connectShellWeb).append("\n");
	    sb.append(", connectShellWas=").append(connectShellWas).append("\n");
	    sb.append(", connectShellDb=").append(connectShellDb).append("\n");
	    sb.append(", connectIpAddress=").append(connectIpAddress).append("\n");
	    sb.append(", portSshAlive=").append(portSshAlive).append("\n");
	    sb.append(", portSftpAlive=").append(portSftpAlive).append("\n");
	    sb.append(", portTelnetAlive=").append(portTelnetAlive).append("\n");
	    sb.append(", portFtpAlive=").append(portFtpAlive).append("\n");
	    sb.append(", nwGetConfigCmdCd=").append(nwGetConfigCmdCd).append("\n");
	    sb.append(", dirDba=").append(dirDba).append("\n");
	    sb.append(", dirUrl=").append(dirUrl).append("\n");
	    sb.append(", dirWeb=").append(dirWeb).append("\n");
	    sb.append(", dirWas=").append(dirWas).append("\n");
	    sb.append(", instAccountDba=").append(instAccountDba).append("\n");
	    sb.append(", instAccountUrl=").append(instAccountUrl).append("\n");
	    sb.append(", instAccountWeb=").append(instAccountWeb).append("\n");
	    sb.append(", instAccountWas=").append(instAccountWas).append("\n");
	    sb.append(", userIdUrlAdm=").append(userIdUrlAdm).append("\n");
	    sb.append(", passwordUrlAdm=").append(passwordUrlAdm).append("\n");
	    sb.append(", accountChkUrlAdm=").append(accountChkUrlAdm).append("\n");
	    sb.append(", portDatabase=").append(portDatabase).append("\n");
	    sb.append(", portWindow=").append(portWindow).append("\n");
	    sb.append(", portWindowAlive=").append(portWindowAlive).append("\n");
	    sb.append(", loginType=").append(loginType).append("\n");
	    sb.append(", relayAssetCd=").append(relayAssetCd).append("\n");
	    sb.append(", useSudo=").append(useSudo).append("\n");
	    sb.append("]\n");
    	return sb.toString();
    }

}
