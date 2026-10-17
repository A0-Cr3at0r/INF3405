package common.validator;

public class InputValidator {
	
	public static void validateIp(String ip) throws IllegalArgumentException {
		String[] partialIp = ip.split("\\.", -1);
		
		if(partialIp.length != 4) {
			throw new IllegalArgumentException("L’adresse IP doit contenir quatre parties.");
		}
		
		for(String parts : partialIp) {
			if(!parts.matches("[0-9]{1,3}")) {
				throw new IllegalArgumentException("L’adresse IP doit uniquement contenir des chiffres.");
			}
			
			int value = Integer.parseInt(parts);
			
			if (value < 0 || value > 255) {
				throw new IllegalArgumentException("L’adresse IP peut uniquement contenir des chiffres entre 0 et 255.");
			}
		}
	}
	
	public static int validatePort(String port) throws IllegalArgumentException {
		if(!port.matches("[0-9]{4}")) {
			throw new IllegalArgumentException("Le numero de port doit contenir exactement quatre chiffres.");
		}
		
		int value = Integer.parseInt(port);
		
		if (value < 5000 || value > 5050) {
			throw new IllegalArgumentException("Le numero de port doit être compris entre 5000 et 5050 inclusivement.");
		}
		
		return value;
	}
	
	public static void validateMessage(String content) {
	    if (content.isEmpty()) {
	        throw new IllegalArgumentException("Le message est vide.");
	    }

	    if (content.length() > 200) {
	        throw new IllegalArgumentException("Le message ne doit pas dépasser 200 caractères.");
	    }

	    if (content.contains("\n") || content.contains("\r")) {
	        throw new IllegalArgumentException("Le message ne doit pas contenir de saut de ligne.");
	    }
	}
}
