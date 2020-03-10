package discordBot.exceptions;

public class CommandNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CommandNotFoundException(String string) {
		super(string);
	}
}
