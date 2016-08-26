package it.poliba.giorgiobasile.markerar.markerdetection;

public class ExtParamException extends Exception{

	private static final long serialVersionUID = 1L;
	private String message;
	
	public ExtParamException(String string){
		message = string;
	}
	
	public String getMessage(){
		return message;
	}
}
