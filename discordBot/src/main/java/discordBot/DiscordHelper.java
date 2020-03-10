package discordBot;

public abstract class DiscordHelper {
	
	public static String bold(String text) {
		return "**" + text.trim() + "**";
	}
	
	public static String italic(String text) {
		return "*" + text.trim() + "*";
	}
	
	public static String underline(String text) {
		return "__" + text.trim() + "__";
	}
	
	public static String strike(String text) {
		return "~~" + text.trim() + "~~";
	}
	
	public static String codeBlock(String text) {
		return "```" + text.trim() + "```";
	}
	
	public static String colorBlueHeading(String heading, String text) {
		return "```ini\n[" + heading.trim() + "] " + text + "\n```";
	}
}
