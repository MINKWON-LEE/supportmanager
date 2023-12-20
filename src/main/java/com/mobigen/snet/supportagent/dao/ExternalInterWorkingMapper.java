/**
 * project : SupportAgent
 * package : com.mobigen.snet.supportagent.dao
 * company : Mobigen
 * 
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 10.
 * Description : 
 * 
 */
package com.mobigen.snet.supportagent.dao;

import com.mobigen.snet.supportagent.entity.Asset;
import com.mobigen.snet.supportagent.entity.Company;
import com.mobigen.snet.supportagent.entity.FirmWare;
import com.mobigen.snet.supportagent.entity.Group;
import com.mobigen.snet.supportagent.entity.TMSInterworkingEntity;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.dao
 * Company : Mobigen
 * File    : ExternalInterWorkingDao.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 10.
 * Description : 
 * 
 */
public interface ExternalInterWorkingMapper {

	public int TMSFirmwareInsert(FirmWare entity) throws Exception;
	
	public int TMSFirmwareUpdate(TMSInterworkingEntity entity) throws Exception;

	public int TMSCompanyInsert(Company entity) throws Exception;
	
	public int TMSCompanyUpdate(TMSInterworkingEntity entity) throws Exception;
	
	public int TMSGroupInsert(Group entity) throws Exception;
	
	public int TMSGroupUpdate(TMSInterworkingEntity entity) throws Exception;
	
	public int TMSAssetInsert(Asset entity) throws Exception;
	
	public int TMSAssetUpdate(TMSInterworkingEntity entity) throws Exception;
	
}
