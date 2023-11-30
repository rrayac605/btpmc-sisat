package mx.gob.imss.cit.pmc.sisat.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import mx.gob.imss.cit.pmc.commons.dto.ErrorResponse;
import mx.gob.imss.cit.pmc.commons.enums.EnumHttpStatus;
import mx.gob.imss.cit.pmc.sisat.model.ModelVersion;
import mx.gob.imss.cit.pmc.sisat.processor.InsertaArchivoServiceProcessor;
import mx.gob.imss.cit.pmc.sisat.service.ObtenerBitacoraST3;

@RestController
@Api(value = "Ejecución batch PMC", tags = { "Ejecución batch PMC Rest" })
@RequestMapping("/msbatchsisat/v1")
public class SisatController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("jobLauncherController")
	private SimpleJobLauncher jobLauncherController;

	@Autowired
	@Qualifier("readSisat")
	private Job job;

	@Autowired
	@Qualifier("readSisatInicial")
	private Job jobInicial;
	
	@Autowired
	private ObtenerBitacoraST3 obtenerBitacora;
	
	@Autowired
	private InsertaArchivoServiceProcessor insertaArchivoServiceProcessor;

	private final static String version_service = "btpmc-sisat-1.0.1";
	
	private final static String folio_service = "WO22646";
	
	private final static String nota_service = "Envio noticiacion de consulta dictamen ST3";
	
	@ApiOperation(value = "version", nickname = "version", notes = "version", response = Object.class, responseContainer = "binary", tags = {})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Respuesta exitosa", response = ResponseEntity.class, responseContainer = "byte"),
			@ApiResponse(code = 204, message = "Sin resultados", response = ResponseEntity.class),
			@ApiResponse(code = 500, message = "Describe un error general del sistema", response = ErrorResponse.class) })
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping(value = "/version")
	public ModelVersion version() throws Exception {
		return new ModelVersion(version_service, folio_service, nota_service);
	}	
	
	@RequestMapping("/health/ready")
	@ResponseStatus(HttpStatus.OK)
	public void ready() {
	}

	@RequestMapping("/health/live")
	@ResponseStatus(HttpStatus.OK)
	public void live() {
	}

	@Bean
	public SimpleJobLauncher jobLauncherController(JobRepository jobRepository) {
		SimpleJobLauncher launcher = new SimpleJobLauncher();
		launcher.setJobRepository(jobRepository);
		return launcher;
	}

	@ApiOperation(value = "Ejecución batch carga", nickname = "ejecutar", notes = "Ejecución batch carga", response = Object.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Respuesta exitosa", response = Object.class),
			@ApiResponse(code = 500, message = "Describe un error general del sistema", response = ErrorResponse.class) })
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/ejecutar", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object ejecutar(@RequestParam(required = false) String nombre) {
		Object resultado = null;
		try {
			logger.info("ejecutarBatch carga inicial");
			JobParameters param = new JobParametersBuilder()
					.addString("JobID", String.valueOf(System.currentTimeMillis())).addString("nombre", nombre)
					.toJobParameters();
			JobExecution execution = jobLauncherController.run(job, param);

			logger.debug("Job finished with status :" + execution.getStatus());
			resultado = new ResponseEntity<Object>("El resultado de la ejecución es: " + execution.getStatus(),
					HttpStatus.OK);
			logger.info("ejecutarBatch:returnOk");
		} catch (Exception be) {
			logger.info("ejecutarBatch:catch");
			ErrorResponse errorResponse = new ErrorResponse(EnumHttpStatus.SERVER_ERROR_INTERNAL, be.getMessage(),
					"Error de aplicaci\u00F3n");

			int numberHTTPDesired = Integer.parseInt(errorResponse.getCode());

			resultado = new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.valueOf(numberHTTPDesired));
			logger.info("ejecutarBatch:numberHTTPDesired");

		}

		logger.info("ejecutarBatch:FinalReturn");
		return resultado;
	}

	@ApiOperation(value = "Ejecución batch carga inicial", nickname = "ejecutarInicial", notes = "Ejecución batch carga inicial", response = Object.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Respuesta exitosa", response = Object.class),
			@ApiResponse(code = 500, message = "Describe un error general del sistema", response = ErrorResponse.class) })
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/ejecutarInicial", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object ejecutarInicial(@RequestParam(required = false) String nombre) {
		Object resultado = null;
		try {
			logger.info("ejecutarBatch carga inicial");
			JobParameters param = new JobParametersBuilder()
					.addString("JobID", String.valueOf(System.currentTimeMillis())).addString("nombre", nombre)
					.toJobParameters();
			JobExecution execution = jobLauncherController.run(jobInicial, param);

			logger.debug("Job finished with status :" + execution.getStatus());
			resultado = new ResponseEntity<Object>("El resultado de la ejecución es: " + execution.getStatus(),
					HttpStatus.OK);
			logger.info("ejecutarBatch:returnOk");
		} catch (Exception be) {
			logger.info("ejecutarBatch:catch");
			ErrorResponse errorResponse = new ErrorResponse(EnumHttpStatus.SERVER_ERROR_INTERNAL, be.getMessage(),
					"Error de aplicaci\u00F3n");

			int numberHTTPDesired = Integer.parseInt(errorResponse.getCode());

			resultado = new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.valueOf(numberHTTPDesired));
			logger.info("ejecutarBatch:numberHTTPDesired");

		}

		logger.info("ejecutarBatch:FinalReturn");
		return resultado;
	}
	
	@ApiOperation(value = "Actualizar campos modificados", nickname = "actualizacionCamposModificados", notes = "Actualizar campos modificado", response = Object.class, responseContainer = "List", tags = {})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Respuesta exitosa", response = Object.class, responseContainer = "List"),
			@ApiResponse(code = 204, message = "Sin resultados", response = Object.class),
			@ApiResponse(code = 500, message = "Describe un error general del sistema", response = Object.class) })
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/ejecutarValidacion", produces = { "application/json" })
	public Object ejecutarValidacion(@RequestBody String item) {
		Object resultado = null;
		try {
			logger.info("ejecutarBatch carga inicial");
			insertaArchivoServiceProcessor.process(item);

			resultado = new ResponseEntity<Object>("El resultado de la ejecución es: ", HttpStatus.OK);
			logger.info("ejecutarBatch:returnOk");
		} catch (Exception be) {
			logger.info("ejecutarBatch:catch");
			ErrorResponse errorResponse = new ErrorResponse(EnumHttpStatus.SERVER_ERROR_INTERNAL, be.getMessage(),
					"Error de aplicaci\u00F3n");

			int numberHTTPDesired = Integer.parseInt(errorResponse.getCode());

			resultado = new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.valueOf(numberHTTPDesired));
			logger.info("ejecutarBatch:numberHTTPDesired");

		}

		logger.info("ejecutarBatch:FinalReturn");
		return resultado;
	}
	
	@ApiOperation(value = "Ejecución batch bitacoraConsultaST3", nickname = "ejecutar", notes = "Ejecución batch bitacoraConsultaST3", response = Object.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Respuesta exitosa", response = Object.class, responseContainer = "List"),
			@ApiResponse(code = 204, message = "Sin resultados", response = Object.class),
			@ApiResponse(code = 500, message = "Describe un error general del sistema", response = Object.class) })
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/obtenerConsultaST3", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object obtenerConsultaST3(@RequestParam(required = false) String fecInicio, 
			@RequestParam(required = false) String fecFin) {
		Object resultado = null;

		try {
			logger.info("Inicio ejecucion consulta bitacora");
			obtenerBitacora.obtenerEnviarBitacoraST3(fecInicio, fecFin);
			logger.info("Finalizo consulta bitacora");
			resultado = new ResponseEntity<Object>("El resultado de la ejecución es: " + HttpStatus.OK,
					HttpStatus.OK);
			logger.info("obtenerConsultaST3:returnOk");
		} catch (Exception e) {
			logger.info("Fallo en la ejecucion");
			
			ErrorResponse errorResponse = new ErrorResponse(EnumHttpStatus.SERVER_ERROR_INTERNAL, e.getMessage(),
					"Error de aplicaci\u00F3n");

			int numberHTTPDesired = Integer.parseInt(errorResponse.getCode());

			resultado = new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.valueOf(numberHTTPDesired));
			logger.info("Fallo en la ejecucion:numberHTTPDesired");

		}
		
		return resultado;
	}

}
