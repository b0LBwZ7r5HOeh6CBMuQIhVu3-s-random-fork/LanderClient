/*
 * Decompiled with CFR 0_132.
 */
package cn.Arctic.Commands.commands;

import cn.Arctic.Commands.Command;
import cn.Arctic.Util.Chat.Helper;
import net.minecraft.client.Minecraft;

public class Enchant
extends Command {
    public Enchant() {
        super("Enchant", new String[]{"e"}, "", "enchanth");
    }

    @Override
    public String execute(String[] args) {
        if (args.length < 1) {
            Minecraft.getMinecraft().player.sendChatMessage("/give " + Minecraft.getMinecraft().player.getName() + " diamond_sword 1 0 {ench:[{id:16,lvl:127}]}");
        } else {
            Helper.sendMessage("invalid syntax Valid .enchant");
        }
        return null;
    }
}

