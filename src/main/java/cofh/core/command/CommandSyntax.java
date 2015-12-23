package cofh.core.command;

import cofh.lib.util.helpers.StringHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class CommandSyntax implements ISubCommand {

	public static CommandSyntax instance = new CommandSyntax();

	/* ISubCommand */
	@Override
	public String getCommandName() {

		return "syntax";
	}

	@Override
	public int getPermissionLevel() {

		return -1;
	}

	@Override
	public void handleCommand(ICommandSender sender, String[] args) {

		List<String> commandList = new ArrayList<String>(CommandHandler.getCommandList());
		Collections.sort(commandList, String.CASE_INSENSITIVE_ORDER);
		commandList.remove(getCommandName());
		for (int i = 0; i < commandList.size(); ++i) {
			String name = commandList.get(i);
			if (!CommandHandler.canUseCommand(sender, CommandHandler.getCommandPermission(name), name)) {
				commandList.remove(i--);
			}
		}
		final int pageSize = 7;
		int maxPages = (commandList.size() - 1) / pageSize;
		int page;

		try {
			page = args.length == 1 ? 0 : CommandBase.parseIntBounded(sender, args[1], 1, maxPages + 1) - 1;
		} catch (NumberInvalidException numberinvalidexception) {
			String commandName = args[1];
			if (!CommandHandler.getCommandExists(commandName)) {
				throw new CommandNotFoundException("info.cofh.command.notFound");
			}
			sender.addChatMessage(new ChatComponentTranslation("info.cofh.command." + commandName + ".syntax"));
			return;
		}

		int maxIndex = Math.min((page + 1) * pageSize, commandList.size());
		IChatComponent chatcomponenttranslation1 = new ChatComponentTranslation("commands.help.header", page + 1, maxPages + 1);
		chatcomponenttranslation1.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
		sender.addChatMessage(chatcomponenttranslation1);

		for (int i = page * pageSize; i < maxIndex; ++i) {
			IChatComponent chatcomponenttranslation = new ChatComponentText("/cofh " + StringHelper.YELLOW + commandList.get(i));
			chatcomponenttranslation.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/cofh syntax " + commandList.get(i)));
			sender.addChatMessage(chatcomponenttranslation);
		}
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {

		if (args.length == 2) {
			return CommandBase.getListOfStringsFromIterableMatchingLastWord(args, CommandHandler.getCommandList());
		}
		return null;

	}

}
