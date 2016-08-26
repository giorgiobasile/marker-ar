package it.poliba.giorgiobasile.markerar.markerdetection;

/**
 * Eccezione lanciata quando i parametri di calibrazione non sono validi
 *
 */
public class CPException extends Exception{

	private static final long serialVersionUID = 1L;
	private String message;

	public CPException(String string) {
		message = string;
	}
	
	public String getMessage(){
		return message;
	}
}
