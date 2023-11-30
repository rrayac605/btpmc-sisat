package mx.gob.imss.cit.pmc.sisat.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.gob.imss.cit.pmc.commons.dto.ParametroDTO;
import mx.gob.imss.cit.pmc.commons.to.MailTO;
import mx.gob.imss.cit.pmc.commons.to.PlantillaEmailTO;
import mx.gob.imss.cit.pmc.commons.utils.DateUtils;
import mx.gob.imss.cit.pmc.services.EmailService;
import mx.gob.imss.cit.pmc.services.dao.archivo.ParametroRepository;
import mx.gob.imss.cit.pmc.services.enums.PlantillaEmailEnum;
import mx.gob.imss.cit.pmc.services.exception.EmailException;
import mx.gob.imss.cit.pmc.sisat.service.EnviarNotificacionSIST;

@Component
public class EnviarNotificacionSISTImpl implements EnviarNotificacionSIST {

	private static final String COMA = ",";
	
	@Autowired
	private ParametroRepository parametroRepository;	
	
	@Autowired
	private EmailService emailService;
	
	private static int MENOS_UNO = -1;
	
	private static String VACIO = "";
	
	@Override
	public void sendCorreo(long registros, String fecha)  {
		try {
			PlantillaEmailTO plantilla = getParametros(registros);
			MailTO mail = llenaParametros(plantilla, registros, fecha);
			
			if(registros > 0) {
				emailService.sendEmail(mail, PlantillaEmailEnum.BITACORA_CONSULTA_SIST);
			}else {
				emailService.sendEmail(mail, PlantillaEmailEnum.BITACORA_CONSULTA_SIST_SIN_INFO);
			}
				
		} catch (Exception e) {

		}	
	}
	
	public void sendCorreoFalla()  {
		try {
			PlantillaEmailTO plantilla = getParametros(MENOS_UNO);
			MailTO mail = llenaParametros(plantilla, MENOS_UNO, VACIO);
			emailService.sendEmail(mail, PlantillaEmailEnum.BITACORA_CONSULTA_SIST_FALLA);			
		} catch (Exception e) {
			
		}

	}
	
	private PlantillaEmailTO getParametros(long registros) {
		String sistemaOrigen = "sist";
		PlantillaEmailTO plantilla = new PlantillaEmailTO();
		
		Optional<ParametroDTO> mailFrom = parametroRepository
				.findOneByCve(sistemaOrigen.concat(".mailFrom"));
		Optional<ParametroDTO> mailTo = parametroRepository
				.findOneByCve(sistemaOrigen.concat(".mailTo"));
		Optional<ParametroDTO> mailCc = parametroRepository
				.findOneByCve(sistemaOrigen.concat(".mailCc"));
		
		String strSubject = sistemaOrigen.concat(".mailNoExistSubject"); 
		if(registros > 0) {
			strSubject = sistemaOrigen.concat(".mailSubject");
		}
		if(registros == -1) {
			strSubject = sistemaOrigen.concat(".mailSubjectError");
		}
		Optional<ParametroDTO> subject = parametroRepository
				.findOneByCve(strSubject);
		
		
		if (mailFrom.isPresent()) {
			plantilla.setMailFrom(mailFrom.get().getDesParametro());
		}
		if (mailCc.isPresent()) {
			plantilla.setMailCc(mailCc.get().getDesParametro().split(COMA));
		}
		if (mailTo.isPresent()) {
			plantilla.setMailTo(mailTo.get().getDesParametro().split(COMA));
		}
		if (subject.isPresent()) {
			plantilla.setSubject(subject.get().getDesParametro());
		}
		
		plantilla.setFechaEnvio(DateUtils.getFechaActual());
		plantilla.setHoraEnvio(DateUtils.getHoraActual());
		return plantilla;
	}

	public MailTO llenaParametros(PlantillaEmailTO plantilla, long registros, String fecha) {
		MailTO mail = new MailTO();
		try {
			Map<String, Object> model;
			
			mail = new MailTO();
			mail.setMailFrom(plantilla.getMailFrom());
			mail.setMailTo(plantilla.getMailTo());
			mail.setMailCc(plantilla.getMailCc());
			mail.setMailSubject(plantilla.getSubject());

			model = new HashMap<String, Object>();
			if(registros > 0) {
				model.put("fechaEnvio", fecha);
				model.put("TotalRegistros", registros);
			}
			mail.setModel(model);
			
		} catch (Exception e) {
			
		}
		return mail;

	}
}
