package kr.or.coder.common.schedule.service.impl;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.or.coder.app.cmm.mapper.system.BatchManageMapper;
import kr.or.coder.app.cmm.model.BatchManager;
import kr.or.coder.common.listener.BaseJobListener;
import kr.or.coder.common.listener.BaseTriggerListener;
import kr.or.coder.common.schedule.service.ScheduleService;
import kr.or.coder.common.schedule.support.AutowiringSpringBeanJobFactory;
import kr.or.coder.common.util.TextUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("SchedulreService")
public class ScheduleServiceImpl implements ScheduleService {

	@Autowired
	private ConfigurableEnvironment environment;

	@Autowired
	private BatchManageMapper batchManageMapper;

	private ApplicationContext context;

	private Scheduler scheduler;

	/**
	 * 최초 배치 Load
	 * 
	 * @param context (ApplicationContext)
	 */
	public void loadQuartzScheduler(ApplicationContext context) {

		this.context = context;

		/* 배치 정보 설정 */
		Properties prop = new Properties();
		prop.put("org.quartz.scheduler.instanceName", environment.getProperty("org.quartz.scheduler.instanceName"));
		prop.put("org.quartz.threadPool.threadCount", environment.getProperty("org.quartz.threadPool.threadCount"));

		try {

			SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
			AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
			jobFactory.setApplicationContext(context);

			factoryBean.setApplicationContext(context);
			factoryBean.setJobFactory(jobFactory);
			factoryBean.setQuartzProperties(prop);
			factoryBean.setWaitForJobsToCompleteOnShutdown(true);
			factoryBean.afterPropertiesSet();

			scheduler = factoryBean.getScheduler();

			ListenerManager listenrManager = scheduler.getListenerManager();
			listenrManager.addJobListener(context.getBean(BaseJobListener.class));
			listenrManager.addTriggerListener(context.getBean(BaseTriggerListener.class));

			scheduler.start();
		} catch (SchedulerException se) {
			log.error("SchedulreServiceImpl.loadQuartzScheduler", se);
			return;
		} catch (Exception ex) {
			log.error("SchedulreServiceImpl.loadQuartzScheduler", ex);
			return;
		}

		List<BatchManager> batchManageList = batchManageMapper.selectBatchManageList();

		/* Quartz 설정 */
		for (BatchManager batchManage : batchManageList) {
			addQuartzScheduler(batchManage);
		}
	}

	/**
	 * 배치정보 추가
	 * 
	 * @param btchNm (배치명)
	 */
	public void addQuartzScheduler(String btchNm) {

		BatchManager batchManage = batchManageMapper.selectBatchManageInfo(btchNm);

		addQuartzScheduler(batchManage);
	}

	/**
	 * 배치정보 추가
	 * 
	 * @param batchManage (배치관리)
	 */
	private void addQuartzScheduler(BatchManager batchManage) {

		String btchNm = batchManage.getBatchNm();
		String jobNm = TextUtil.getJobName(btchNm);
		String triggerNm = TextUtil.getTriggerName(btchNm);

		try {

			Class<? extends Job> jobClass = null;

			try {
				jobClass = (Class<? extends Job>) Class.forName(batchManage.getBatchJobClassNm());
			} catch (ClassNotFoundException ex) {
				log.error("SchedulreServiceImpl.addQuartzScheduler", ex);
			}

			if (jobClass == null)
				return;

			// JobDetail 생성
			JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
			factoryBean.setJobClass(jobClass);
			factoryBean.setName(jobNm);
			factoryBean.setApplicationContext(context);
			factoryBean.afterPropertiesSet();
			JobDetail jobDetail = factoryBean.getObject();

			if (!StringUtils.isEmpty(batchManage.getBatchParamtr())) {

				JobDataMap jobDataMap = jobDetail.getJobDataMap();

				ObjectMapper mapper = new ObjectMapper();
				Map<String, String> paramMap = mapper.readValue(batchManage.getBatchParamtr(), Map.class);

				for (String key : paramMap.keySet()) {
					jobDataMap.put(key, paramMap.get(key));
				}
			}

			// Trigger 생성
			Trigger trigger = newTrigger().withIdentity(triggerNm, Scheduler.DEFAULT_GROUP)
					.withSchedule(cronSchedule(batchManage.getCronExprsn())).build();

			// Scheduler 등록
			scheduler.scheduleJob(jobDetail, trigger);

		} catch (BeansException be) {
			log.error("SchedulreServiceImpl.addQuartzScheduler", be);
		} catch (JsonParseException jpe) {
			log.error("SchedulreServiceImpl.addQuartzScheduler", jpe);
		} catch (JsonMappingException jme) {
			log.error("SchedulreServiceImpl.addQuartzScheduler", jme);
		} catch (IOException ioe) {
			log.error("SchedulreServiceImpl.addQuartzScheduler", ioe);
		} catch (SchedulerException se) {
			log.error("SchedulreServiceImpl.addQuartzScheduler", se);
		}
	}

	/**
	 * 배치정보 변경 (실행시간 변경)
	 * 
	 * @param btchNm (배치명)
	 */
	public void changeQuartzScheduler(String btchNm) {

		BatchManager batchManage = batchManageMapper.selectBatchManageInfo(btchNm);

		if (batchManage != null && !StringUtils.isEmpty(batchManage.getBatchNm())) {

			String triggerNm = TextUtil.getTriggerName(btchNm);

			// Trigger 생성
			Trigger trigger = newTrigger().withIdentity(triggerNm, Scheduler.DEFAULT_GROUP)
					.withSchedule(cronSchedule(batchManage.getCronExprsn())).build();

			try {
				scheduler.rescheduleJob(new TriggerKey(triggerNm), trigger);
			} catch (SchedulerException se) {
				log.error("SchedulreServiceImpl.changeQuartzScheduler", se);
			}
		}
	}

	/**
	 * 특정 배치 일시정지
	 * 
	 * @param btchNm (배치명)
	 */
	public void pauseQuartzScheduler(String btchNm) {

		String triggerNm = TextUtil.getTriggerName(btchNm);

		try {
			scheduler.pauseTrigger(new TriggerKey(triggerNm, Scheduler.DEFAULT_GROUP));
		} catch (SchedulerException se) {
			log.error("SchedulreServiceImpl.removeQuartzScheduler", se);
		}
	}

	/**
	 * 특정 배치 재실행
	 * 
	 * @param btchNm (배치명)
	 */
	public void resumeQuartzScheduler(String btchNm) {

		String triggerNm = TextUtil.getTriggerName(btchNm);

		try {
			scheduler.resumeTrigger(new TriggerKey(triggerNm, Scheduler.DEFAULT_GROUP));
		} catch (SchedulerException se) {
			log.error("SchedulreServiceImpl.resumeQuartzScheduler", se);
		}
	}

	/**
	 * 다음배치 실행시간 조회 (배치상태조회)
	 * 
	 * @param btchNm (배치명)
	 */
	public void nextExeTime(List<String> btchNmList) {

		for (String btchNm : btchNmList) {

			String triggerNm = TextUtil.getTriggerName(btchNm);

			try {
				Trigger trigger = scheduler.getTrigger(new TriggerKey(triggerNm));
				Date nextFireTime = trigger.getFireTimeAfter(new Date());
			} catch (SchedulerException se) {
				log.error("SchedulreServiceImpl.removeQuartzScheduler", se);
			}
		}
	}

	/**
	 * 배치 실행
	 * 
	 * @param btchNm (배치명)
	 */
	public void execQuartzScheduler(String btchNm) {

		String jobNm = TextUtil.getJobName(btchNm);
		JobKey jobKey = new JobKey(jobNm);

		try {
			scheduler.triggerJob(jobKey);
		} catch (SchedulerException se) {
			log.error("SchedulreServiceImpl.execQuartzScheduler", se);
		}
	}

	/**
	 * 배치 중지
	 */
	public void stopQuartzScheduler() {
		try {
			scheduler.shutdown(true);
		} catch (SchedulerException se) {
			log.error("SchedulreServiceImpl.stopQuartzScheduler");
		}
	}
}