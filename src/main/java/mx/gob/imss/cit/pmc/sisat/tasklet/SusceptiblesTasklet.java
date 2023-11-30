package mx.gob.imss.cit.pmc.sisat.tasklet;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import mx.gob.imss.cit.pmc.commons.enums.IdentificadorArchivoEnum;
import mx.gob.imss.cit.pmc.commons.utils.Utils;
import mx.gob.imss.cit.pmc.services.DetalleRegistroService;

@Component
@StepScope
public class SusceptiblesTasklet implements Tasklet {

	@Value("#{stepExecution}")
	private StepExecution stepExecution;

	@Autowired
	protected DetalleRegistroService detalleRegistroService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		StepContext stepContext = chunkContext.getStepContext();
		StepExecution stepExecution = stepContext.getStepExecution();
		JobExecution jobExecution = stepExecution.getJobExecution();
		ExecutionContext jobContext = jobExecution.getExecutionContext();
		@SuppressWarnings("unchecked")
		List<String> nombres = (ArrayList<String>) jobContext.get("nombres");

		String nombre = stepExecution.getJobParameters().getString("nombre");
		nombre = (nombre != null && !nombre.trim().equals("")) ? nombre
				: Utils.obtenerNombreArchivoSISAT(IdentificadorArchivoEnum.ARCHIVO_SISAT.getIdentificador());
		detalleRegistroService.existeSusceptibleNss(nombre, nombres);
		jobContext.put("nombres", nombres);
		return RepeatStatus.FINISHED;
	}

}
