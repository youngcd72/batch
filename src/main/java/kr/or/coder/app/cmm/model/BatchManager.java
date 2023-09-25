package kr.or.coder.app.cmm.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BatchManager {

	private String batchId; // 배치아이디
	private String batchNm; // 배치명
	private String batchDc; // 배치설명

	private String batchJobClassNm;
	private String cronExprsn; // 크론 표현식
	private String batchParamtr; // 배치파라미터

	private LocalDateTime executExclBeginTime; // 배치실행제외시작시간
	private LocalDateTime executExclEndTime; // 배치실행제외종료시간

	private String errorTrnsmisMthCode; // 오류전송방법

}
