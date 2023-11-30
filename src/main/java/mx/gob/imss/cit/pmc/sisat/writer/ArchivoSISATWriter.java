package mx.gob.imss.cit.pmc.sisat.writer;

import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import mx.gob.imss.cit.pmc.commons.dto.ArchivoDTO;

@Component
@StepScope
public class ArchivoSISATWriter implements ItemWriter<ArchivoDTO> {

	@Override
	public void write(List<? extends ArchivoDTO> items) throws Exception {
		
	}

}
