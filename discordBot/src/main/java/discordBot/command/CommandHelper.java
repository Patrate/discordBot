package discordBot.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import discordBot.exceptions.HelperException;

public class CommandHelper {
	private String description;
	private Map<String, String> useCase;
	
	public CommandHelper(String description, String... useCaseList) throws HelperException {
		if (useCaseList.length % 2 == 1) {
			throw new HelperException("UseCase length mismatch");
		}
		this.description = description;
		this.useCase = new HashMap<String, String>();
		for (int i = 0; i < useCaseList.length; i += 2) {
			useCase.put(useCaseList[i], useCaseList[i+ 1]);
		}
	}
	
	public CommandHelper(String... help) throws HelperException {
		this(help[0], Arrays.copyOfRange(help,1,help.length));
	}

	public CommandHelper(String description) throws HelperException {
		this(description, new String[0]);
	}
	
	public String getDescription() {
		return description;
	}

	public Map<String, String> getUseCase() {
		return useCase;
	}
}
