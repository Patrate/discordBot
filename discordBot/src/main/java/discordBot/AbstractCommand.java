package discordBot;

import java.util.Arrays;

import discordBot.exceptions.CommandException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class AbstractCommand {
	private String name;
	
	public AbstractCommand(String name) {
		this.name = name;
	}
	
	public String getName() {return name;}
	
	public abstract void execute(MessageReceivedEvent event) throws CommandException;
	
	public String[] getParams(MessageReceivedEvent event) {
		String message = event.getMessage().getContentRaw();
		String[] splitted = message.split(" ");
		return Arrays.copyOfRange(splitted, 1, splitted.length);
	}
}
