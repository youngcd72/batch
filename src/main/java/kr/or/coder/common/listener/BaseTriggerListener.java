package kr.or.coder.common.listener;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.springframework.beans.factory.annotation.Autowired;

import kr.or.coder.app.cmm.model.BatchManager;
import kr.or.coder.app.cmm.service.BatchManageService;
import kr.or.coder.common.util.TextUtil;

public class BaseTriggerListener implements TriggerListener {
    
	private static final Logger logger = LogManager.getLogger(BaseTriggerListener.class);

	@Autowired
	private BatchManageService batchManageService;

	@Override
	public String getName() {
		return BaseTriggerListener.class.getName();
	}

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		JobKey jobKey = trigger.getJobKey();
		logger.info("triggerFired at {} :: jobKey : {}", trigger.getStartTime(), jobKey);
	}

	/**
	 * Trigger 중단 여부를 확인하는 메소드 Job을 수행하기 전 상태
	 * 
	 * 반환값이 false인 경우, Job 수행 반환값이 true인 경우, Job을 수행하지않고
	 * 'SchedulerListtener.jobExecutionVetoed'로 넘어감
	 * 
	 */
	@Override
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {

		JobKey jobKey = context.getJobDetail().getKey();
		String jobId = TextUtil.getBatchNm(jobKey.getName());

		BatchManager batchManage = batchManageService.selectBatchManageInfo(jobId);

		/* 배치예약중지 확인 */
		if (batchManage.getExecutExclBeginTime() != null && batchManage.getExecutExclEndTime() != null) {

			LocalDateTime today = LocalDateTime.now();

			if (today.compareTo(batchManage.getExecutExclBeginTime()) >= 0
					&& today.compareTo(batchManage.getExecutExclEndTime()) <= 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void triggerMisfired(Trigger trigger) {

		JobKey jobKey = trigger.getJobKey();

		logger.info("triggerMisfired at {} :: jobKey : {}", trigger.getStartTime(), jobKey);
	}

	/**
	 * Step 실행 전 처리
	 * 
	 * @param stepExecution
	 * @return
	 */
	@Override
	public void triggerComplete(Trigger trigger, JobExecutionContext context,
			Trigger.CompletedExecutionInstruction triggerInstructionCode) {
		JobKey jobKey = trigger.getJobKey();
		logger.info("triggerComplete at {} :: jobKey : {}", trigger.getStartTime(), jobKey);
	}
}
