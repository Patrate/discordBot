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

import discordBot.exceptions.CommandException;
import discordBot.exceptions.CommandNotFoundException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class AbstractBot extends ListenerAdapter {

	private Map<String, AbstractCommand> commandList;
	
	public AbstractBot(String packageName) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, LoginException {
		initCommands(packageName);
		initConnexion();
	}
	
	private void initConnexion() throws LoginException {
		JDABuilder builder = new JDABuilder(AccountType.BOT);
		String token = "";
		token = getToken();
		builder.setToken(token);
		builder.addEventListeners(this);
		builder.build();
	}
	
	public String getToken() {
		try {
			return new String(Files.readAllBytes(Paths.get("token")));
		} catch (IOException e) {
			System.err.println("Erreur lors de lecture du token");
		}
		return null;
	}
	
	private void initCommands(String packageName) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		commandList = new HashMap<String, AbstractCommand>();
		Reflections reflections = new Reflections(packageName);
		Set<Class<? extends AbstractCommand>> allCommands =
				reflections.getSubTypesOf(AbstractCommand.class);
		for(Class<? extends AbstractCommand> command:allCommands) {
			if(Modifier.isAbstract(command.getModifiers())) {
				continue;
			}
			AbstractCommand newCommand = command.getDeclaredConstructor().newInstance();
			commandList.put(newCommand.getName(), newCommand);
		}
	};
	
	public AbstractCommand getCommand(String commandName) {
		return commandList.get(commandName);
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot()) {
			return;
		}
		String message = event.getMessage().getContentRaw();
		if (!message.startsWith("!")) {
			return;
		}
		message = message.substring(1);
		String command = message.split(" ", 0)[0];
		try {
			executeCommand(command, event);
		} catch (CommandNotFoundException e) {
			commandNotFoundException(e);
		} catch (CommandException e) {
			commandException(e);
		}
	}
	
	public void executeCommand(String commandName, MessageReceivedEvent event) throws CommandNotFoundException, CommandException {
		AbstractCommand command = commandList.get(commandName);
		if(command == null) {
			throw new CommandNotFoundException(commandName);
		}
		command.execute(event);
	}
	
	protected abstract void commandNotFoundException(CommandNotFoundException e);
	protected abstract void commandException(CommandException e);
}
