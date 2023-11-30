package mx.gob.imss.cit.pmc.sisat.utils;

import java.util.Map;

import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import mx.gob.imss.cit.pmc.commons.dto.ArchivoDTO;
import mx.gob.imss.cit.pmc.commons.dto.RegistroDTO;

@Component
@StepScope
public class ArchivoSISATProcessListener implements ItemProcessListener<RegistroDTO, ArchivoDTO> {

	@Value("#{jobParameters}")
	private Map<String, JobParameter> jobParameters;

	@Value("#{stepExecution}")
	private StepExecution stepExecution;

	@Override
	public void beforeProcess(RegistroDTO item) {

	}

	@Override
	public void afterProcess(RegistroDTO item, ArchivoDTO result) {

	}

	@Override
	public void onProcessError(RegistroDTO item, Exception e) {

	}

}
