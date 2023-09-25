package kr.or.coder.common.util;

public class TextUtil {
    
    /**
	 * 배치명 반환
	 * 
	 * @param btchNm
	 * @return
	 */
	public static String getBatchNm(String jobNm) {
		if (jobNm.endsWith("Job")) {
			return jobNm.substring(0, jobNm.length() - 3);
		}
		return jobNm;
	}

	/**
	 * 배치 Job 명 반환
	 * 
	 * @param btchNm
	 * @return
	 */
	public static String getJobName(String btchNm) {
		return btchNm + "Job";
	}

	/**
	 * 배치 Trigger 명 반환
	 * 
	 * @param btchNm
	 * @return
	 */
	public static String getTriggerName(String btchNm) {
		return btchNm + "Trigger";
	}
}
