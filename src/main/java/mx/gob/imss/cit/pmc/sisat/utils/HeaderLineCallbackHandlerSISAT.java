package mx.gob.imss.cit.pmc.sisat.utils;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.gob.imss.cit.pmc.commons.callbackhandler.HeaderLineCallbackHandler;
import mx.gob.imss.cit.pmc.commons.dto.ArchivoDTO;
import mx.gob.imss.cit.pmc.commons.dto.CabeceraDTO;
import mx.gob.imss.cit.pmc.commons.enums.IdentificadorArchivoEnum;
import mx.gob.imss.cit.pmc.commons.utils.Utils;
import mx.gob.imss.cit.pmc.services.dao.archivo.ArchivoRepository;

@Component
@StepScope
public class HeaderLineCallbackHandlerSISAT extends HeaderLineCallbackHandler
		implements LineCallbackHandler, StepExecutionListener {

	private StepExecution stepExecution;

	@Autowired
	private ArchivoRepository archivoRepository;

	public StepExecution getStepExecution() {
		return stepExecution;
	}

	public void setStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	@Override
	public void handleLine(String line) {

		stepExecution.getExecutionContext().put("Cabecera", crearCabecera(line));
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		String nombre = stepExecution.getJobParameters().getString("nombre");

		if (!archivoRepository.existeArchivo(nombre != null ? nombre
				: Utils.obtenerNombreArchivoSISAT(IdentificadorArchivoEnum.ARCHIVO_SISAT.getIdentificador()))) {

			CabeceraDTO cabeceraDTO = (CabeceraDTO) stepExecution.getExecutionContext().get("Cabecera");
			ArchivoDTO archivoDTO = null;
			if (cabeceraDTO.getNumRegistros().intValue() != stepExecution.getReadCount()) {
				archivoDTO = crearArchivoSISAT(IdentificadorArchivoEnum.ARCHIVO_SISAT.getIdentificador(),
						stepExecution.getReadCount());
				stepExecution.setExitStatus(ExitStatus.FAILED);
			} else {
				archivoDTO = crearArchivoCorrectoSISAT(IdentificadorArchivoEnum.ARCHIVO_SISAT.getIdentificador(),
						stepExecution.getReadCount());
			}
			if ((nombre != null && !nombre.trim().equals(""))) {
				archivoDTO.setNomArchivo(nombre);
				archivoDTO.setDesIdArchivo(nombre);
			}
			archivoRepository.saveUser(archivoDTO);
		}
		return stepExecution.getExitStatus();
	}

}
