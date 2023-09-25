package kr.or.coder.app.cmm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.coder.app.cmm.mapper.system.BatchManageMapper;
import kr.or.coder.app.cmm.model.BatchManager;
import kr.or.coder.app.cmm.service.BatchManageService;
import kr.or.coder.common.log.BatchExeLog;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("BatchManageService")
public class BatchManageServiceImpl implements BatchManageService {

	@Autowired
	private BatchManageMapper batchManageMapper;

	/**
	 * 배치 실행 정보 조회
	 * 
	 * @param jobId
	 * @return BatchManager
	 */
	public BatchManager selectBatchManageInfo(String jobId) {
		return batchManageMapper.selectBatchManageInfo(jobId);
	}

	/**
	 * 배치 실행 정보 조회
	 * 
	 * @return List<BatchManager>
	 */
	public List<BatchManager> selectBatchManageList() {
		return batchManageMapper.selectBatchManageList();
	}

	/**
	 * 배치로그 등록
	 * 
	 * @param log
	 * @return
	 */
	public void insertBatchExeLog(BatchExeLog log) {
		batchManageMapper.insertBatchExeLog(log);
	}
}