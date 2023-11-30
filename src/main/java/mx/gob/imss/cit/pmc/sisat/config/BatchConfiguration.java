package mx.gob.imss.cit.pmc.sisat.config;

import java.util.Objects;
import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import mx.gob.imss.cit.pmc.commons.dto.ArchivoDTO;
import mx.gob.imss.cit.pmc.commons.dto.DetalleRegistroDTO;
import mx.gob.imss.cit.pmc.commons.dto.RegistroDTO;
import mx.gob.imss.cit.pmc.sisat.processor.InsertaArchivoInicialProcessor;
import mx.gob.imss.cit.pmc.sisat.processor.InsertaArchivoProcessor;
import mx.gob.imss.cit.pmc.sisat.reader.ArchivoSISATInicialItemReader;
import mx.gob.imss.cit.pmc.sisat.reader.ArchivoSISATItemReader;
import mx.gob.imss.cit.pmc.sisat.reader.InsertaArchivoInicialItemReader;
import mx.gob.imss.cit.pmc.sisat.reader.InsertaArchivoItemReader;
import mx.gob.imss.cit.pmc.sisat.tasklet.BuscarArchivoTasklet;
import mx.gob.imss.cit.pmc.sisat.tasklet.CifrasControlTasklet;
import mx.gob.imss.cit.pmc.sisat.tasklet.DuplicadosTasklet;
import mx.gob.imss.cit.pmc.sisat.tasklet.EmailArchivoNoExisteTasklet;
import mx.gob.imss.cit.pmc.sisat.tasklet.EmailCargaExistenteTasklet;
import mx.gob.imss.cit.pmc.sisat.tasklet.EmailRegistrosIncorrectosTasklet;
import mx.gob.imss.cit.pmc.sisat.tasklet.ObtenerArchivoTasklet;
import mx.gob.imss.cit.pmc.sisat.tasklet.SusceptiblesTasklet;
import mx.gob.imss.cit.pmc.sisat.utils.ArchivoSISATReadListener;
import mx.gob.imss.cit.pmc.sisat.utils.HeaderLineCallbackHandlerSISAT;
import mx.gob.imss.cit.pmc.sisat.writer.ArchivoSISATWriter;
import mx.gob.imss.cit.pmc.sisat.writer.InsertaArchivoWriter;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "mx.gob.imss.cit.pmc")
public class BatchConfiguration extends DefaultBatchConfigurer {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private HeaderLineCallbackHandlerSISAT headerLineCallbackHandler;

	@Autowired
	private ArchivoSISATReadListener archivoNSSAReadListener;

	@Autowired
	private ArchivoSISATItemReader archivoNSSAItemReader;

	@Autowired
	private ArchivoSISATInicialItemReader archivoNSSAInicialItemReader;

	@Autowired
	private InsertaArchivoProcessor insertaArchivoProcessor;

	@Autowired
	private ArchivoSISATWriter archivoNSSAWriter;

	@Autowired
	private InsertaArchivoWriter insertaArchivoWriter;

	@Autowired
	private ObtenerArchivoTasklet obtenerArchivoTasklet;

	@Autowired
	private EmailRegistrosIncorrectosTasklet emailRegistrosIncorrectosTasklet;

	@Autowired
	private EmailCargaExistenteTasklet emailCargaExistenteTasklet;

	@Autowired
	private EmailArchivoNoExisteTasklet emailArchivoNoExisteTasklet;

	@Autowired
	private SusceptiblesTasklet susceptiblesTasklet;

	@Autowired
	private DuplicadosTasklet duplicadosTasklet;

	@Autowired
	private CifrasControlTasklet cifrasControlTasklet;

	@Autowired
	private BuscarArchivoTasklet buscarArchivoTasklet;

	@Autowired
	private InsertaArchivoItemReader insertaArchivoItemReader;

	@Autowired
	private InsertaArchivoInicialItemReader insertaArchivoInicialItemReader;

	@Autowired
	private InsertaArchivoInicialProcessor insertaArchivoInicialProcessor;

	@Bean
	@NonNull
	public PlatformTransactionManager getTransactionManager() {
		return new ResourcelessTransactionManager();
	}

	@SneakyThrows
	@NonNull
	@Bean
	public JobRepository getJobRepository() {
		return Objects.requireNonNull(new MapJobRepositoryFactoryBean(getTransactionManager()).getObject());
	}

	@Bean
	public Job readSisat() {
		return jobBuilderFactory.get("readSisat").incrementer(new RunIdIncrementer()).start(stepObtieneArchivo())
		.on("FAILED")
		.to(stepEnviaCorreoErrorNoExisteArchivo()).from(stepObtieneArchivo()).on("*")
		.to(validaExistenciaArchivo()).on("FAILED")
		.to(stepEnviaCorreoErrorCargaExiste()).from(validaExistenciaArchivo()).on("*")
		.to(stepValidaEncabezado()).on("FAILED")
		.to(stepEnviaCorreoErrorRegistrosIncorrectos()).from(stepValidaEncabezado()).on("*")
		.to(stepInsertaRegistroArchivo()).on("*")
		.to(stepDuplicados()).on("*")
		.to(stepSusceptibles()).on("*")
		.to(stepCifrasControl())
		.end()
		.build();
	}

	@Bean
	public Job readSisatInicial() {
		return jobBuilderFactory.get("readNSSAInicial").incrementer(new RunIdIncrementer()).start(stepObtieneArchivo())
				.on("FAILED").to(stepEnviaCorreoErrorNoExisteArchivo()).from(stepObtieneArchivo()).on("*")
				.to(validaExistenciaArchivo()).on("FAILED").to(stepEnviaCorreoErrorCargaExiste())
				.from(validaExistenciaArchivo()).on("*").to(stepValidaEncabezadoInicial()).on("FAILED")
				.to(stepEnviaCorreoErrorRegistrosIncorrectos()).from(stepValidaEncabezadoInicial()).on("*")
				.to(stepInsertaRegistroInicialArchivo()).on("*").to(stepDuplicados()).on("*").to(stepSusceptibles())
				.on("*").to(stepCifrasControl()).end().build();
	}

	@Bean
	public Step stepObtieneArchivo() {
		return stepBuilderFactory.get("stepObtieneArchivo").tasklet(obtenerArchivoTasklet).build();
	}

	@Bean
	public Step stepEnviaCorreoErrorRegistrosIncorrectos() {
		return stepBuilderFactory.get("stepEnviaCorreoErrorRegistrosIncorrectos")
				.tasklet(emailRegistrosIncorrectosTasklet).build();
	}

	@Bean
	public Step stepEnviaCorreoErrorCargaExiste() {
		return stepBuilderFactory.get("stepEnviaCorreoErrorCargaExiste").tasklet(emailCargaExistenteTasklet).build();
	}

	@Bean
	public Step stepEnviaCorreoErrorNoExisteArchivo() {
		return stepBuilderFactory.get("stepEnviaCorreoErrorNoExisteArchivo").tasklet(emailArchivoNoExisteTasklet)
				.build();
	}

	@Bean
	public Step stepDuplicados() {
		return stepBuilderFactory.get("stepDuplicados").tasklet(duplicadosTasklet).build();
	}

	@Bean
	public Step stepSusceptibles() {
		return stepBuilderFactory.get("stepSusceptibles").tasklet(susceptiblesTasklet).build();
	}

	@Bean
	public Step stepCifrasControl() {
		return stepBuilderFactory.get("stepCifrasControl").tasklet(cifrasControlTasklet).build();
	}

	@Bean
	public Step validaExistenciaArchivo() {
		return stepBuilderFactory.get("validaExistenciaArchivo").tasklet(buscarArchivoTasklet).build();
	}

	@Bean
	public Step stepValidaEncabezado() {
		return stepBuilderFactory.get("stepValidaEncabezado").listener(headerLineCallbackHandler)
				.<RegistroDTO, ArchivoDTO>chunk(1000000).reader(archivoNSSAItemReader).listener(archivoNSSAReadListener)
				.writer(archivoNSSAWriter).build();
	}

	@Bean
	public Step stepValidaEncabezadoInicial() {
		return stepBuilderFactory.get("stepValidaEncabezado").listener(headerLineCallbackHandler)
				.<RegistroDTO, ArchivoDTO>chunk(1000000).reader(archivoNSSAInicialItemReader)
				.listener(archivoNSSAReadListener).writer(archivoNSSAWriter).build();
	}

	@Bean
	public Step stepInsertaRegistroArchivo() {
		return stepBuilderFactory.get("stepInsertaRegistroArchivo").listener(headerLineCallbackHandler)
				.<RegistroDTO, DetalleRegistroDTO>chunk(100).reader(insertaArchivoItemReader)
				.processor(insertaArchivoProcessor).writer(insertaArchivoWriter).build();
	}

	@Bean
	public Step stepInsertaRegistroInicialArchivo() {
		return stepBuilderFactory.get("stepInsertaRegistroArchivo").listener(headerLineCallbackHandler)
				.<RegistroDTO, DetalleRegistroDTO>chunk(100).reader(insertaArchivoInicialItemReader)
				.processor(insertaArchivoInicialProcessor).writer(insertaArchivoWriter).build();
	}
}