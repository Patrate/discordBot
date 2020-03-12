package discordBot;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.reflections.Reflections;

import discordBot.command.AbstractCommand;
import discordBot.command.HelpCommand;
import discordBot.exceptions.CommandException;
import discordBot.exceptions.CommandNotFoundException;
import discordBot.exceptions.HelperException;
import discordBot.exceptions.ValidatorException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class AbstractBot extends ListenerAdapter {

	private static final String DEFAULTIDENTIFIER = "!";

	private Map<String, AbstractCommand> commandList;
	private String commandIdentifier;

	/***
	 * Abstract discord bot. Initialize the commands and the connection. Add a
	 * default help command, you can overload it by just creating another command
	 * named "help"
	 * 
	 * @param packageName       Name of the package containing the commands. Use
	 *                          reflection to load them
	 * @param commandIdentifier String used to identify a command. Default is "!"
	 *                          (eg: "!test" would be the command "test", "/test"
	 *                          isn't identified as a command)
	 * @throws InstantiationException          Thrown if a command can't be
	 *                                         instancied
	 * @throws IllegalAccessException          Thrown if a command can't be
	 *                                         instancied
	 * @throws IllegalArgumentExceptionThrown  if a command can't be instancied
	 * @throws InvocationTargetExceptionThrown if a command can't be instancied
	 * @throws NoSuchMethodExceptionThrown     if a command can't be instancied
	 * @throws SecurityException               Thrown if a command can't be
	 *                                         instancied
	 * @throws LoginException                  Thrown if the bot can't connect to
	 *                                         discord
	 */
	public AbstractBot(String packageName, String commandIdentifier)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, LoginException {
		commandList = new HashMap<String, AbstractCommand>();
		try {
			addCommand(new HelpCommand(this));
		} catch (HelperException e) {
			System.err.println("There is a helper error in the help command lol: " + e.getMessage());
		}
		initCommands(packageName);
		initConnexion();
		this.commandIdentifier = commandIdentifier;
	}

	/**
	 * Abstract discord bot. Initialize the commands and the connection with the
	 * default commandIdentifier "!"
	 * 
	 * @param packageName Name of the package containing the commands. Use
	 *                    reflection to load them
	 * @throws InstantiationException          Thrown if a command can't be
	 *                                         instancied
	 * @throws IllegalAccessException          Thrown if a command can't be
	 *                                         instancied
	 * @throws IllegalArgumentExceptionThrown  if a command can't be instancied
	 * @throws InvocationTargetExceptionThrown if a command can't be instancied
	 * @throws NoSuchMethodExceptionThrown     if a command can't be instancied
	 * @throws SecurityException               Thrown if a command can't be
	 *                                         instancied
	 * @throws LoginException                  Thrown if the bot can't connect to
	 *                                         discord
	 */
	public AbstractBot(String packageName)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, LoginException {
		this(packageName, DEFAULTIDENTIFIER);
	}

	/**
	 * Initialize the connection to discord
	 * 
	 * @throws LoginException If the bot can't connect to discord
	 */
	private void initConnexion() throws LoginException {
		JDABuilder builder = new JDABuilder(AccountType.BOT);
		String token = "";
		token = getToken();
		builder.setToken(token);
		builder.addEventListeners(this);
		builder.build();
	}

	/**
	 * Load the bot token of the discord bot. The token must be in a file named
	 * "token" at the root of the project. Don't hesitate to overload this to get
	 * the token from elsewhere !
	 * 
	 * @return A String containing the token
	 */
	public String getToken() {
		try {
			return new String(Files.readAllBytes(Paths.get("token")));
		} catch (IOException e) {
			System.err.println("Erreur lors de lecture du token");
		}
		return null;
	}

	/**
	 * Use reflection to initialize the command. It will instantiate all the classes
	 * extending AbstractCommand that are contained in the "packageName" package and
	 * it's sub-package
	 * 
	 * @param packageName The root package of the commands
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private void initCommands(String packageName) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Reflections reflections = new Reflections(packageName);
		Set<Class<? extends AbstractCommand>> allCommands = reflections.getSubTypesOf(AbstractCommand.class);
		for (Class<? extends AbstractCommand> command : allCommands) {
			if (Modifier.isAbstract(command.getModifiers())) {
				continue;
			}
			AbstractCommand newCommand = command.getDeclaredConstructor().newInstance();
			addCommand(newCommand);
		}
	};

	private void addCommand(AbstractCommand command) {
		commandList.put(command.getName(), command);
	}

	/**
	 * Return the command named commandName from the commandList
	 * 
	 * @param commandName The name of the command
	 * @return the command named commandName or null if it doesn't exist
	 */
	public final AbstractCommand getCommand(String commandName) {
		return commandList.get(commandName);
	}

	/**
	 * Executed whenever there is a new message in a channel the bot is in. If the
	 * author of the message is the bot itself, return. If the message doesn't start
	 * with the commandIdentifier (default is "!"), return. Otherwise, get the
	 * command name and execute the command associated. If the command is not found,
	 * execute the commandNotFoundException function If the command throw an error,
	 * execute the commandException function
	 */
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot()) {
			return;
		}
		String message = event.getMessage().getContentRaw();
		if (!message.startsWith(commandIdentifier)) {
			return;
		}
		message = message.substring(1);
		String command = message.split(" ", 0)[0];
		try {
			executeCommand(command, event);
		} catch (CommandNotFoundException e) {
			commandNotFoundException(event.getChannel(), e);
		} catch (CommandException e) {
			commandException(event.getChannel(), e);
		} catch (ValidatorException e) {
			validatorException(event.getChannel(), e);
		}
	}

	/**
	 * Execute the function commandName within the context of event
	 * 
	 * @param commandName Name of the command to execute
	 * @param event       Context of the function
	 * @throws CommandNotFoundException If the command is not found in the
	 *                                  commandList
	 * @throws CommandException         If the command throw an exception
	 * @throws ValidatorException       If the command syntax is wrong
	 */
	public void executeCommand(String commandName, MessageReceivedEvent event)
			throws CommandNotFoundException, CommandException, ValidatorException {
		AbstractCommand command = commandList.get(commandName);
		if (command == null) {
			throw new CommandNotFoundException(commandName);
		}
		command.run(event);
	}

	/**
	 * Send a message from the bot to the channel specified
	 * 
	 * @param channel the channel to use
	 * @param message the message to send
	 */
	public static void message(MessageChannel channel, String message) {
		channel.sendMessage(message).queue();
	}

	/**
	 * Command executed whenever a command is not found.
	 * 
	 * @param e
	 */
	protected abstract void commandNotFoundException(MessageChannel channel, CommandNotFoundException e);

	/**
	 * Command executed whenever a command throw an exception
	 * 
	 * @param e
	 */
	protected abstract void commandException(MessageChannel channel, CommandException e);
	
	/**
	 * Command executed whenever a command fail the validation check
	 * 
	 * @param e
	 */
	protected abstract void validatorException(MessageChannel channel, ValidatorException e);
}
