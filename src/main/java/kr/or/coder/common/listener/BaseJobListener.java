package kr.or.coder.common.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.springframework.beans.factory.annotation.Autowired;

import kr.or.coder.app.cmm.service.BatchManageService;
import kr.or.coder.common.log.BatchExeLog;
import kr.or.coder.common.log.BatchLogStorage;
import kr.or.coder.common.util.TextUtil;

public class BaseJobListener implements JobListener {

    private static final Logger logger = LogManager.getLogger(BaseJobListener.class);

	@Autowired
	private BatchLogStorage logStorage;

	@Autowired
	private BatchManageService batchManageService;

	@Override
	public String getName() {
		return BaseJobListener.class.getName();
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {

		JobKey jobKey = context.getJobDetail().getKey();

		/* Log 정보 기초정보 설정 */
		String btchNm = TextUtil.getBatchNm(jobKey.getName());
		logStorage.init(btchNm);

		BatchExeLog batLog = logStorage.get(btchNm);
		batLog.setJobId(System.currentTimeMillis());
		batLog.setJobName(btchNm);

		logger.info("jobToBeExecuted :: jobKey : {}", jobKey);
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		JobKey jobKey = context.getJobDetail().getKey();
		logger.info("jobExecutionVetoed :: jobKey : {}", jobKey);
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {

		JobKey jobKey = context.getJobDetail().getKey();

		/* 로그정리 */
		String btchNm = TextUtil.getBatchNm(jobKey.getName());
		BatchExeLog btchLog = logStorage.get(btchNm);

		if (btchLog != null) {
			/* 배치로그등록 */
			batchManageService.insertBatchExeLog(btchLog);
		}

		logger.info("jobWasExecuted :: jobKey : {}", jobKey);
	}
}
