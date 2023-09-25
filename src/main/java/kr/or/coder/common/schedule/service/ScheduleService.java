package kr.or.coder.common.schedule.service;

import java.util.List;

import org.springframework.context.ApplicationContext;

public interface ScheduleService {

    /**
	 * 최초 배치 Load
	 *  
	 * @param context (ApplicationContext)
	 */
	public void loadQuartzScheduler(ApplicationContext context);

	/**
	 * 배치정보 추가
	 *  
	 * @param btchNm (배치명)
	 */
	public void addQuartzScheduler(String btchNm);
	
	/**
	 * 배치정보 변경 (실행시간 변경)
	 *  
	 * @param btchNm (배치명)
	 */
	public void changeQuartzScheduler(String btchNm);

	/**
	 * 특정 배치 일시정지
	 *  
	 * @param btchNm (배치명)
	 */
	public void pauseQuartzScheduler(String btchNm);

	/**
	 * 특정 배치 재실행
	 *  
	 * @param btchNm (배치명)
	 */
	public void resumeQuartzScheduler(String btchNm);

	/**
	 * 다음배치 실행시간 조회 (배치상태조회)
	 *  
	 * @param btchNm (배치명)
	 */
	public void nextExeTime(List<String> btchNmList);
	
	/**
	 * 특정 배치 즉시실행
	 *  
	 * @param btchNm (배치명)
	 */
	public void execQuartzScheduler(String btchNm);
	
	/**
	 * 배치 중지
	 */
	public void stopQuartzScheduler();
}
