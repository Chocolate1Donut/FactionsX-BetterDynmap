package net.donut.fxbetterdynmap;

import net.prosavage.factionsx.command.engine.FCommand;
import net.prosavage.factionsx.command.engine.CommandInfo;
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder;

public class MarkMe extends FCommand {
    public MarkMe(){
        // Alias for the command, so this would do `/f wild`.
        getAliases().add("markme");
        // We can add multiple
        getAliases().add("dyntest");

        // The commandRequirements pre-check common things for you, the official way.
        // For example we could add #asFactionMember(true) if we want to make sure they're a faction member.
        // Here we do not want this executed in console, as the console cannot be teleported.
        this.commandRequirements = new CommandRequirementsBuilder()
                .asPlayer(true)
                .build();
    }

    public boolean execute(CommandInfo info) {
        info.message("Executing MarkMe, because you meet the requirements.");
        info.message("Also, here is the funny word you configured: " + Conf.funnyWordToSayOnCommand);
        EngineBetterDynmap dynEngine = EngineBetterDynmap.getInstance();
        dynEngine.init();
        return true;
    }

    //This is used by the command engine to tell a player what a command does in the help menu
    public String getHelpInfo() {
        return "This is a test command for my plugin that is going to take down prosavage.";
    }

}
