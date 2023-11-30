package mx.gob.imss.cit.pmc.sisat.service;

public interface EnviarNotificacionSIST {

	void sendCorreo(long registros, String fecha);
	
	void sendCorreoFalla();
}
