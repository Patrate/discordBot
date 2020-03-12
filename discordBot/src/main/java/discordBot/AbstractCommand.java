package discordBot;

import java.util.Arrays;

import discordBot.exceptions.CommandException;
import discordBot.exceptions.HelperException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class AbstractCommand {
	private String name;
	private CommandHelper help;

	/**
	 * A command with a helper
	 * 
	 * @param name Name of the command
	 * @param help the commandHelper. Check how its built, or use the other
	 *             constructors
	 */
	public AbstractCommand(String name, CommandHelper help) {
		this.name = name;
		this.help = help;
	}

	/**
	 * A command with a helper.
	 * The helper is an array of string. The structure is as follow:
	 * [0] = description
	 * [x] = command syntax
	 * [x+1] = syntax description
	 * (eg: for the help command, 
	 * 		[0] = "Display the help of a command"
	 * 		[1] = "help"
	 * 		[2] = "display this message"
	 * 		[3] = "help <command>"
	 * 		[4] = "display the help of the command specified")   
	 * @param name
	 * @param help
	 * @throws HelperException
	 */
	public AbstractCommand(String name, String... help) {
		this(name, buildHelper(help));
	}

	private static CommandHelper buildHelper(String... help) {
		if (help.length == 0) {
			return null;
		}
		try {
			return new CommandHelper(help);
		} catch (HelperException e) {
			try {
				System.err.println("Error building helper useCase:" + e.getMessage());
				return new CommandHelper(help[0]);
			} catch (HelperException e2) {
				System.err.println("Error building helper totally:" + e2.getMessage());
			}
		}
		return null;
	}

	/**
	 * @return The name of the command
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The help of the command
	 */
	public CommandHelper getHelp() {
		return help;
	}

	/**
	 * Execute the command withing the context event
	 * 
	 * @param event context of the command
	 * @throws CommandException if an exception occur during the execution
	 */
	public abstract void execute(MessageReceivedEvent event) throws CommandException;

	/**
	 * Get the params of the command as an array of string containing all the
	 * keywords passed to the command
	 * 
	 * @param event the context of the command
	 * @return a String array with all the keywords after the command name
	 */
	public String[] getParams(MessageReceivedEvent event) {
		String message = event.getMessage().getContentRaw();
		String[] splitted = message.split(" ");
		return Arrays.copyOfRange(splitted, 1, splitted.length);
	}
}
