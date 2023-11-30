package mx.gob.imss.cit.pmc.sisat.writer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import mx.gob.imss.cit.pmc.commons.dto.ArchivoDTO;
import mx.gob.imss.cit.pmc.commons.dto.DetalleRegistroDTO;
import mx.gob.imss.cit.pmc.services.DetalleRegistroService;

@Component
@StepScope
public class InsertaArchivoWriter implements ItemWriter<DetalleRegistroDTO> {

	private final static Logger logger = LoggerFactory.getLogger(InsertaArchivoWriter.class);

	@Value("#{stepExecution}")
	private StepExecution stepExecution;

	@Autowired
	private DetalleRegistroService detalleRegistroService;

	@Override
	public void write(List<? extends DetalleRegistroDTO> items) throws Exception {
		ArchivoDTO archivoDTO = (ArchivoDTO) stepExecution.getExecutionContext().get("archivoDTO");
		@SuppressWarnings("unchecked")
		int guardados = detalleRegistroService.insertaRegistros((List<DetalleRegistroDTO>) items);
		Integer indice = (Integer) stepExecution.getExecutionContext().get("indice");
		logger.debug("Guardados: {}", guardados);
		logger.info("Guardando: {} de {}", indice, archivoDTO.getCifrasControlDTO().getNumTotalRegistros());
		stepExecution.getExecutionContext().put("archivoDTO", archivoDTO);
		List<DetalleRegistroDTO> detalle = new ArrayList<DetalleRegistroDTO>();
		stepExecution.getExecutionContext().put("detalleRegistroDTO", detalle);
	}

}
