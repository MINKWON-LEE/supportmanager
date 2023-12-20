/**
 * project : SupportAgent
 * package : com.mobigen.snet.supportagent.service
 * company : Mobigen
 * 
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 10.
 * Description : 
 * 
 */
package com.mobigen.snet.supportagent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mobigen.snet.supportagent.component.TMSInterWorkingComponent;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Mobigen
 * File    : ExternalInterWorkingServiceImpl.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 10.
 * Description : 
 * 
 */
@Service
public class ExternalInterWorkingServiceImpl extends AbstractService implements ExternalInterWorkingService {

	@Autowired
	private TMSInterWorkingComponent tmsInterWorkingComponent;
	
	
	/* (non-Javadoc)
	 * @see com.mobigen.snet.supportagent.service.ExternalInterWorkingService#getExternalData()
	 */
	@Override
	public void TMSInterWorkingService() throws Exception {
		
		if(tmsInterWorkingComponent.TMSInterworkingSyncronizing())
			logger.info("TMS ExternalInterworking Success!");
		else
			logger.warn("TMS ExternalInterworking Failed!");
			

	}


	@Override
	public void NetWorkEquipDiagService() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
