package kr.or.coder.common.log;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class BatchLogStorage {

	public BatchLogStorage() {
		storage = new HashMap<>();
	}

	private Map<String, BatchExeLog> storage;

	/**
	 * 배치 로그 정보 반환
	 * 
	 * @param jobName
	 */
	public BatchExeLog get(String jobName) {
		if (!storage.containsKey(jobName)) { // 신규 생성 처리
			BatchExeLog log = new BatchExeLog();
			storage.put(jobName, log);
			return log;
		}
		return storage.get(jobName);
	}

	/**
	 * 이전 배치 로그 삭제
	 * 
	 * @param jobName
	 */
	public void remove(String jobName) {
		storage.remove(jobName);
	}

	/**
	 * 배치로그 스토리지 초기화
	 * 
	 * @param jobName
	 */
	public void init(String jobName) {

		remove(jobName);

		BatchExeLog log = new BatchExeLog();
		storage.put(jobName, log);
	}
}