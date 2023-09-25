package kr.or.coder.common.log;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class BatchExeLog {

	private static final int MAX_LOG_LEN = 2500;

	private long jobId;
	private String jobName;

	private boolean hasErrLog;
	private List<String> exeLogs; // 실행로그
	private List<String> errLogs; // 오류로그

	/**
	 * 실행로그 적재
	 * 
	 * @param log
	 * @return BatchExeLog
	 */
	public BatchExeLog addExeLog(String log) {
		if (exeLogs == null) {
			exeLogs = new ArrayList<>();
		}
		exeLogs.add(log);
		return this;
	}

	/**
	 * 실행로그 리스트 반환
	 * 
	 * @return List<String>
	 */
	public List<String> getExeLogs() {
		return exeLogs;
	}

	/**
	 * 실행로그 리스트 반환
	 * 
	 * @return String
	 */
	public String getExeLog() {
		if (exeLogs == null || exeLogs.isEmpty()) {
			return "";
		}
		String exeLog = String.join("\r\n", exeLogs);
		if (exeLog.length() > MAX_LOG_LEN) {
			return exeLog.substring(0, MAX_LOG_LEN);
		}
		return exeLog;
	}

	/**
	 * 오류로그 적재
	 * 
	 * @param log
	 * @return BatchExeLog
	 */
	public BatchExeLog addErrLog(String log) {
		if (errLogs == null) {
			errLogs = new ArrayList<>();
		}
		hasErrLog = true;
		errLogs.add(log);

		return this;
	}

	/**
	 * 에러로그 리스트 반환
	 * 
	 * @return List<String>
	 */
	public List<String> getErrLogs() {
		return errLogs;
	}

	/**
	 * 에러로그 문자열 반환
	 * 
	 * @return String
	 */
	public String getErrLog() {
		if (errLogs == null || errLogs.isEmpty()) {
			return "";
		}
		String errLog = String.join("\r\n", errLogs);
		if (errLog.length() > MAX_LOG_LEN) {
			errLog.substring(0, MAX_LOG_LEN);
		}
		return errLog;
	}

	/**
	 * 오류발생건수
	 * 
	 * @param log
	 * @return BatchExeLog
	 */
	public long getErrCnt() {
		if (errLogs == null || errLogs.isEmpty()) {
			return 0;
		}
		return errLogs.size();
	}

	/**
	 * 로그존재 여부
	 * 
	 * @param log
	 * @return BatchExeLog
	 */
	public boolean hasErrLog() {
		return hasErrLog;
	}

	/**
	 * 오류발생여부
	 * 
	 * @return String (Y/N)
	 */
	public String getErrYn() {
		return hasErrLog ? "Y" : "N";
	}
}