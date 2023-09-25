package kr.or.coder.app.cmm.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.coder.common.schedule.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/batch")
public class BatchController {

	@Autowired
	private ScheduleService scheduleService;

	/**
	 * 배치추가
	 * 
	 * @param btchNm (배치명)
	 */
	@GetMapping(value = "/add/{btchNm}")
	public void add(@PathVariable String btchNm) {
		scheduleService.addQuartzScheduler(btchNm);
	}

	/**
	 * 배치정보 변경 (실행시간 변경)
	 * 
	 * @param btchNm (배치명)
	 */
	@GetMapping(value = "/change/{btchNm}")
	public void change(@PathVariable String btchNm) {
		scheduleService.changeQuartzScheduler(btchNm);
	}

	/**
	 * 배치 일치정지
	 * 
	 * @param btchNm (배치명)
	 */
	@GetMapping(value = "/pause/{btchNm}")
	public void pause(@PathVariable String btchNm) {
		scheduleService.pauseQuartzScheduler(btchNm);
	}

	/**
	 * 배치 재실행
	 * 
	 * @param btchNm (배치명)
	 */
	@GetMapping(value = "/resume/{btchNm}")
	public void resume(@PathVariable String btchNm) {
		scheduleService.resumeQuartzScheduler(btchNm);
	}

	/**
	 * 배치 상태 조회
	 * 
	 * @param btchNm (배치명)
	 */
	@GetMapping(value = "/nextExeTime/{btchNms}")
	public void nextExeTime(@PathVariable String btchNms) {
		String[] batchNmArr = btchNms.split(",");
		scheduleService.nextExeTime(Arrays.asList(batchNmArr));
	}

	/**
	 * 배치 실행
	 */
	@GetMapping(value = "/execute/{btchNm}")
	@ResponseBody
	public Map<String, String> execute(@PathVariable String btchNm) {

		scheduleService.execQuartzScheduler(btchNm);

		/* 배치실행 결과 반환 */
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("executeYn", String.format("[%s] 실행되었습니다.", btchNm));

		return resultMap;
	}
}
