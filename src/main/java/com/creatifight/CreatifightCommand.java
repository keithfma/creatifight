package com.creatifight;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** Registers {@code /gamemode creatifight [target]} as a child literal of vanilla {@code /gamemode}. */
@EventBusSubscriber(modid = CreatifightMod.MODID)
public final class CreatifightCommand {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandNode<CommandSourceStack> gamemodeNode = dispatcher.getRoot().getChild("gamemode");
        if (gamemodeNode == null) return;

        LiteralCommandNode<CommandSourceStack> creatifightNode = Commands.literal("creatifight")
                .requires(src -> src.hasPermission(2))
                .executes(ctx -> run(ctx, ctx.getSource().getPlayerOrException()))
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(ctx -> run(ctx, EntityArgument.getPlayer(ctx, "target"))))
                .build();
        gamemodeNode.addChild(creatifightNode);
    }

    // Ordering matters: setGameMode BEFORE setOn, so the /gamemode-creative-exits-Creatifight
    // mixin sees flag=false during our own setGameMode call and doesn't fire.
    private static int run(CommandContext<CommandSourceStack> ctx, ServerPlayer target) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        String name = target.getName().getString();

        if (CreatifightFlag.isOn(target)) {
            source.sendSuccess(() -> Component.literal(name + " is already in Creatifight."), false);
            return 0;
        }

        target.setGameMode(GameType.CREATIVE);
        CreatifightFlag.setOn(target, true);
        target.getAbilities().invulnerable = false;
        target.onUpdateAbilities();

        source.sendSuccess(() -> Component.literal("Put " + name + " in Creatifight."), true);
        if (source.getPlayer() != target) {
            target.sendSystemMessage(Component.literal("You're now in Creatifight."));
        }
        return 1;
    }

    private CreatifightCommand() {}
}
