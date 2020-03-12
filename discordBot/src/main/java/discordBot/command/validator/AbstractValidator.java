package discordBot.command.validator;

import discordBot.exceptions.ValidatorException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class AbstractValidator {
	private String failMessage;

	public AbstractValidator(String failMessage) {
		this.failMessage = failMessage;
	}

	public abstract boolean check(MessageReceivedEvent event);

	public ValidatorException getFailException() {
		return new ValidatorException(failMessage);
	}
}
