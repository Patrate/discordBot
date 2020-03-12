package discordBot.command;

import discordBot.AbstractBot;
import discordBot.exceptions.CommandException;
import discordBot.exceptions.HelperException;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpCommand extends AbstractCommand {
	private static final String DEFAULT_NOCOMMAND = "Command not found: ", DEFAULT_HELP = "No help provided for: ";

	private AbstractBot bot;

	public HelpCommand(AbstractBot bot) throws HelperException {
		super("help", "Display the help of a command", "help", "display this message", "help <command>",
				"display the help of the command specified");
		this.bot = bot;
	}

	public void execute(MessageReceivedEvent event) throws CommandException {
		String[] params = getParams(event);
		if (params.length == 0) {
			getHelp(event.getChannel(), this, getName());
		} else {
			String commandName = params[0];
			getHelp(event.getChannel(), commandName);
		}
	}

	private void getHelp(MessageChannel chan, String commandName) {
		AbstractCommand command = bot.getCommand(commandName);
		getHelp(chan, command, commandName);
	}

	private void getHelp(MessageChannel chan, AbstractCommand command, String commandName) {
		StringBuilder message = new StringBuilder();
		if (command == null) {
			message.append(DEFAULT_NOCOMMAND + commandName);
		} else if (command.getHelp() == null) {
			message.append(DEFAULT_HELP + commandName);
		} else {
			CommandHelper helper = command.getHelp();
			message.append(helper.getDescription());
			for (String useCase : helper.getUseCase().keySet()) {
				message.append("\n\t");
				message.append(useCase + ": " + helper.getUseCase().get(useCase));
			}
		}
		printHelp(chan, message.toString());
	}

	private void printHelp(MessageChannel chan, String message) {
		AbstractBot.message(chan, message);
	}
}
