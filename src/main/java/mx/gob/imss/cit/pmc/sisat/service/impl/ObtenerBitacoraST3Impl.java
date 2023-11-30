package mx.gob.imss.cit.pmc.sisat.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import mx.gob.imss.cit.pmc.commons.utils.DateUtils;
import mx.gob.imss.cit.pmc.sisat.repository.BitacoraConsultaST3Repository;
import mx.gob.imss.cit.pmc.sisat.service.EnviarNotificacionSIST;
import mx.gob.imss.cit.pmc.sisat.service.ObtenerBitacoraST3;

@Component
public class ObtenerBitacoraST3Impl implements ObtenerBitacoraST3 {

    @Value("${rangoInicial}")
    private String rangoInicial;

    @Value("${rangoFinal}")
    private String rangoFinal;
	
	@Autowired
	BitacoraConsultaST3Repository bitacoraRepo;
	
	@Autowired
	EnviarNotificacionSIST enviarNotficacion;
	
	@Override
	public Object obtenerEnviarBitacoraST3(String fecInicio, String fecFin) {
		long count = 0;
		try {
			Date fechaIni = getfechaInicio(fecInicio);
			Date fechaFin = getfechaFin(fecFin);
			
			count = bitacoraRepo.getCountBitacora(fechaIni, fechaFin);
			
			enviarNotficacion.sendCorreo(count, DateUtils.getFechaActual_ddMMYYYY());
			return count;
		} catch (Exception e) {
			enviarNotficacion.sendCorreoFalla();
		}
		return count;
	}
	
	private Date getfechaFin(String fechaFin) {
		if(fechaFin == null || fechaFin.isEmpty()) {
			Date fecFin = DateUtils.SumaRestarFecha(new Date(), Integer.parseInt(rangoFinal), "DAYS");
			return fecFin;
		}
		
		Date fecFin = DateUtils.parserFromString(fechaFin);
		return fecFin;
	}
	
	private Date getfechaInicio(String fecInicio) {
		if(fecInicio == null || fecInicio.isEmpty()) {
			Date fecIni = DateUtils.SumaRestarFecha(new Date(), Integer.parseInt(rangoInicial), "DAYS");
			return fecIni;
		}
		
		Date fecIni = DateUtils.parserFromString(fecInicio);
		return fecIni;
	}

}
