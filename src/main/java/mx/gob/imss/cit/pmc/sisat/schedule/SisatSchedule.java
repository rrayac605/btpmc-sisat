package mx.gob.imss.cit.pmc.sisat.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import mx.gob.imss.cit.pmc.commons.dto.ErrorResponse;
import mx.gob.imss.cit.pmc.commons.enums.EnumHttpStatus;
import mx.gob.imss.cit.pmc.sisat.service.ObtenerBitacoraST3;

@Component
public class SisatSchedule {

	private final static Logger logger = LoggerFactory.getLogger(SisatSchedule.class);

	private String strVacio = "";
	
	@Autowired
	@Qualifier("jobLauncherScheduled")
	private SimpleJobLauncher jobLauncherScheduled;

	@Autowired
	@Qualifier("readSisat")
	private Job job;

	@Autowired
	private ObtenerBitacoraST3 obtenerBitacora;
	
	@Bean
	public SimpleJobLauncher jobLauncherScheduled(JobRepository jobRepository) {
		SimpleJobLauncher launcher = new SimpleJobLauncher();
		launcher.setJobRepository(jobRepository);
		return launcher;
	}

	@Scheduled(cron = "${cron.expression.sisat}")
	public void startJob() {

		try {
			JobParameters param = new JobParametersBuilder()
					.addString("JobID", String.valueOf(System.currentTimeMillis())).toJobParameters();
			JobExecution execution = jobLauncherScheduled.run(job, param);

			logger.debug("Job finished with status :" + execution.getStatus());
		} catch (JobRestartException e) {
			logger.error(e.getMessage(), e);
		} catch (JobExecutionAlreadyRunningException e) {
			logger.error(e.getMessage(), e);
		} catch (JobInstanceAlreadyCompleteException e) {
			logger.error(e.getMessage(), e);
		} catch (JobParametersInvalidException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	@Scheduled(cron = "${cron.expression.consultaSIST}")
	public void envioConsultarBitacoraSIST() {
		try {
			logger.info("Inicio ejecucion consulta bitacora");
			obtenerBitacora.obtenerEnviarBitacoraST3(strVacio, strVacio);
			logger.info("Finalizo consulta bitacora " + HttpStatus.OK, HttpStatus.OK);
			logger.info("obtenerConsultaST3:returnOk");
		} catch (Exception e) {
			logger.info("Fallo en la ejecucion");
			
			ErrorResponse errorResponse = new ErrorResponse(EnumHttpStatus.SERVER_ERROR_INTERNAL, e.getMessage(),
					"Error de aplicaci\u00F3n");

			int numberHTTPDesired = Integer.parseInt(errorResponse.getCode());

			logger.info("Fallo en la ejecucion:numberHTTPDesired");

		}
	}

}
