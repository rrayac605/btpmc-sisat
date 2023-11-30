package mx.gob.imss.cit.pmc.sisat.tasklet;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import mx.gob.imss.cit.pmc.commons.enums.IdentificadorArchivoEnum;
import mx.gob.imss.cit.pmc.commons.utils.Utils;
import mx.gob.imss.cit.pmc.services.FtpService;

@Component
@StepScope
public class ObtenerArchivoTasklet implements Tasklet {
	
	@Value("#{stepExecution}")
	private StepExecution stepExecution;

	@Autowired
	private FtpService ftpService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		String nombre = stepExecution.getJobParameters().getString("nombre");
		if (nombre != null && !nombre.trim().equals("")) {
			if (!ftpService.copyFileFromFTP(nombre)) {
				stepExecution.setExitStatus(ExitStatus.FAILED);
			}

		} else {
			if (!ftpService.copyFileFromFTP(
					Utils.obtenerNombreArchivoSISAT(IdentificadorArchivoEnum.ARCHIVO_SISAT.getIdentificador()))) {
				stepExecution.setExitStatus(ExitStatus.FAILED);
			}
		}
		return RepeatStatus.FINISHED;
	}

}
