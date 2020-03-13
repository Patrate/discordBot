package discordBot;

/**
 * Helper for discord message formating
 * @author Emmathie
 *
 */
public abstract class DiscordHelper {
	
	/**
	 * make text bold 
	 * @param text
	 * @return
	 */
	public static String bold(String text) {
		return "**" + text.trim() + "**";
	}
	
	/**
	 * make text italic 
	 * @param text
	 * @return
	 */
	public static String italic(String text) {
		return "*" + text.trim() + "*";
	}
	
	/**
	 * make text underlined 
	 * @param text
	 * @return
	 */
	public static String underline(String text) {
		return "__" + text.trim() + "__";
	}
	
	/**
	 * make text striked 
	 * @param text
	 * @return
	 */
	public static String strike(String text) {
		return "~~" + text.trim() + "~~";
	}
	
	/**
	 * make text into a code block 
	 * @param text
	 * @return
	 */
	public static String codeBlock(String text) {
		return "```" + text.trim() + "```";
	}
	
	/**
	 * make text into a code block with "heading" colored in blue 
	 * @param text
	 * @return
	 */
	public static String colorBlue(String text) {
		return "```ini\n[" + text.trim() + "]\n```";
	}
	
	/**
	 * make text into a code block with "heading" colored in blue 
	 * @param text
	 * @return
	 */
	public static String colorRed(String text) {
		return "```css\n[" + text.trim() + "]\n```";
	}
}
