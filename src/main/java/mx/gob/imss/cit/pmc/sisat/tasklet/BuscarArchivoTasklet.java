package mx.gob.imss.cit.pmc.sisat.tasklet;

import java.util.Optional;

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

import mx.gob.imss.cit.pmc.commons.dto.ArchivoDTO;
import mx.gob.imss.cit.pmc.commons.enums.EstadoArchivoEnum;
import mx.gob.imss.cit.pmc.commons.enums.IdentificadorArchivoEnum;
import mx.gob.imss.cit.pmc.commons.utils.Utils;
import mx.gob.imss.cit.pmc.services.dao.archivo.ArchivoRepository;

@Component
@StepScope
public class BuscarArchivoTasklet implements Tasklet {

	@Value("#{stepExecution}")
	private StepExecution stepExecution;

	@Autowired
	private ArchivoRepository archivoRepository;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		String nombre = stepExecution.getJobParameters().getString("nombre");
		Optional<ArchivoDTO> archivoDTO = archivoRepository
				.findOneByName((nombre != null && !nombre.trim().equals("")) ? nombre
						: Utils.obtenerNombreArchivoSISAT(IdentificadorArchivoEnum.ARCHIVO_SISAT.getIdentificador()));

		if (archivoDTO.isPresent()) {
			stepExecution.setExitStatus(ExitStatus.FAILED);
			archivoDTO.get().setCveEstadoArchivo("1");
			archivoDTO.get().setDesEstadoArchivo(EstadoArchivoEnum.NO_PROCESADO.getDesEstadoArchivo());
			archivoDTO.get().setObjectIdArchivo(null);
			archivoDTO.get().setDetalleRegistroDTO(null);
			archivoDTO.get().setNumTotalRegistros("0");
			archivoDTO.get().setCifrasControlDTO(null);
			archivoDTO.get().setDesError("\"Existe un archivo con el mismo nombre  con estatus de Procesado");
			archivoRepository.saveUser(archivoDTO.get());
		}
		return RepeatStatus.FINISHED;
	}

}
