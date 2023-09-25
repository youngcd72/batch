package kr.or.coder.app.cmm.service;

import java.util.List;

import kr.or.coder.app.cmm.model.BatchManager;
import kr.or.coder.common.log.BatchExeLog;


public interface BatchManageService {

	/**
	 * 배치 실행 정보 조회
	 * 
	 * @param jobId
	 * @return BatchManager
	 */
	public BatchManager selectBatchManageInfo(String jobId);

	/**
	 * 배치 실행 정보 조회
	 * 
	 * @return BatchManager
	 */
	public List<BatchManager> selectBatchManageList();

	/**
	 * 배치로그 등록
	 * 
	 * @param log
	 * @return
	 */
	public void insertBatchExeLog(BatchExeLog log);
}
