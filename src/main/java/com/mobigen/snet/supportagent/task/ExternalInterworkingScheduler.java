/**
 * project : SupportAgent
 * package : com.mobigen.snet.supportagent.task
 * company : Mobigen
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 15.
 * Description :
 *
 */
package com.mobigen.snet.supportagent.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mobigen.snet.supportagent.component.FemtoFirmwareProcessor;
import com.mobigen.snet.supportagent.component.IbnNetworkEquipInterworkingComponent;
import com.mobigen.snet.supportagent.component.NetworkEqInterworkingComponent;
import com.mobigen.snet.supportagent.component.WifiFirmwareProcessor;
import com.mobigen.snet.supportagent.service.ExternalInterWorkingService;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.task
 * Company : Mobigen
 * File    : ExternalInterworkingScheduler.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 15.
 * Description :
 *
 */
@Component
public class ExternalInterworkingScheduler {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ExternalInterWorkingService externalInterWorkingService;

	@Autowired
	private NetworkEqInterworkingComponent networkEqInterworkingComponent;

	@Value("${snet.external.service.use}")
	private boolean externalUse;

	@Autowired
	private WifiFirmwareProcessor wifiFirmwareProcessor;

	@Autowired
	private IbnNetworkEquipInterworkingComponent ibnNetworkEquipInterworkingComponent;

	@Autowired
	private FemtoFirmwareProcessor femtoFirmwareProcessor;

	/**
	 * TMS 연동 관련 스케쥴러
	 */
	@Scheduled(cron = "${snet.schedule.tms.itw}")
	public void TMSInterworkingScheduler(){
		try {
			if(externalUse){
				logger.info("*[TMSInterworkingScheduler] start");

				externalInterWorkingService.TMSInterWorkingService();

				logger.info("*[TMSInterworkingScheduler] end");
			}
		} catch (Exception e) {
			logger.error("TMS ExternalInterWorking Exception :: " + e.getMessage(), e);
		}

		//==  WiFi firmware 처리(SNET_CONFIG_WIFI_FIRMWARE)
		wifiFirmwareProcessor.processFromWifiFirmware();
	}

	/**
	 * Network 장비 연동
	 */
	@Scheduled(cron = "${snet.schedule.nw.equip.itw}")
	public void NWEQuipInterworkingScheduler(){
		try {
			if(externalUse) {
				logger.info("*[NWEQuipInterworkingScheduler] start");

				networkEqInterworkingComponent.networkEquipInterworking();

				logger.info("*[NWEQuipInterworkingScheduler] end");
			}
		} catch (Exception e) {
			logger.error("Network Equip ExternalInterWorking Exception :: " + e.getMessage(), e);
		}
	}


	/**
	 * ibn 중앙서버의 Network 장비 연동
	 */
	@Scheduled(cron = "${snet.schedule.ibn.nw.equip.ife}")
	public void ibnNetworkEquipInterfaceScheduler(){
		try {
			if(externalUse){
				logger.info("*[ibnNetworkEquipInterfaceScheduler] start");

				ibnNetworkEquipInterworkingComponent.mainProcess();

				logger.info("*[ibnNetworkEquipInterfaceScheduler] end");
			}
		} catch (Exception e) {
			logger.error("ibnNetworkEquipInterfaceScheduler :: " + e.getMessage(), e);
		}
	}


	/**
	 * FEMTO 장비 연동 (미사용)
	 */
//	@Scheduled(cron = "${snet.schedule.femto.ife}") // 일단 목요일 오전 10시에 돌도록
	public void femtoInterfaceScheduler(){
		try {
			if(externalUse){
				femtoFirmwareProcessor.mainProcess();
			}
		} catch (Exception e) {
			logger.error("femtoInterfaceScheduler :: " + e.getMessage(), e);
		}
	}


}
