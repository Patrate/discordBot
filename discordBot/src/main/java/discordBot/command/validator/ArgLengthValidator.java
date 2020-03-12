package discordBot.command.validator;

import discordBot.command.AbstractCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class ArgLengthValidator extends AbstractValidator {
	private int minLength;
	private int maxLength;

	public ArgLengthValidator(int length) {
		super("arg length should be " + length);
		this.minLength = length;
		this.maxLength = length;
	}

	public ArgLengthValidator(int minLength, int maxLength) {
		super("arg length should be between " + minLength + " and " + maxLength);
		this.minLength = minLength;
		this.maxLength = maxLength;
	}

	public boolean check(MessageReceivedEvent event) {
		String[] params = AbstractCommand.getParams(event);
		return (params.length >= minLength && params.length <= maxLength);
	}
}
