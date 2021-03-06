/* AdvancedChat: A Minecraft Mod to modify the chat.
Copyright (C) 2020 DarkKronicle
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.*/

package net.darkkronicle.advancedchat.config;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.storage.ChatTab;
import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class FilterScreen {

    // Screen for configuring filters.
    public static Screen getScreen(Screen parentScreen) {
        ConfigBuilder builder = ConfigBuilder.create().
                setParentScreen(parentScreen);
        builder.setSavingRunnable(ModMenuImpl::save);
        ModMenuImpl.setBackground(builder);

        builder.alwaysShowTabs();
        ConfigEntryBuilder entry = builder.entryBuilder();

        String[] select = {"1", "2"};

        ConfigCategory filters = builder.getOrCreateCategory(new TranslatableText("config.advancedchat.category.filters"));

        filters.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.filter.createnew"), select, select[0]).setNameProvider((s -> {
            if (s.equalsIgnoreCase("1")) {
                return new TranslatableText("config.advancedchat.click");
            }
            AdvancedChat.configStorage.filters.add(Filter.getDefault());
            ModMenuImpl.save();
            MinecraftClient.getInstance().openScreen(FilterScreen.getScreen(parentScreen));
            return new TranslatableText("config.advancedchat.click");

        })).setTooltip(new TranslatableText("warn.advancedchat.savefirst")).build());



        // Goes through each filter that is saved. Saves it to a /storage/Filter.class
        for (Filter filter : AdvancedChat.configStorage.filters) {

            ConfigCategory category;
            if (builder.hasCategory(new LiteralText(filter.getName()))) {
                // If there is a name conflict, it renames it to the name + 1;
                filter.setName(filter.getName()+"1");
                ModMenuImpl.save();
            }

            category = builder.getOrCreateCategory(new LiteralText(filter.getName()));


            category.addEntry(entry.startStrField(new TranslatableText("config.advancedchat.filter.name"), filter.getName()).setTooltip(new TranslatableText("config.advancedchat.filter.info.name")).setSaveConsumer(filter::setName).build());

            category.addEntry(entry.startBooleanToggle(new TranslatableText("config.advancedchat.filter.active"), filter.isActive()).setTooltip(new TranslatableText("config.advancedchat.filter.info.active")).setSaveConsumer(filter::setActive).build());

            category.addEntry(entry.startStrField(new TranslatableText("config.advancedchat.findstring"), filter.getFindString()).setTooltip(new TranslatableText("config.advancedchat.info.findstring")).setSaveConsumer(filter::setFindString).build());

            category.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.findtype"), Filter.FindType.values(), filter.getFindType()).setTooltip(
                    new TranslatableText("config.advancedchat.info.findtype.literal"),
                    new TranslatableText("config.advancedchat.info.findtype.regex"),
                    new TranslatableText("config.advancedchat.info.findtype.upperlower")
            ).setSaveConsumer(filter::setFindType).build());

            category.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.filter.replacetype"), Filter.ReplaceType.values(), filter.getReplaceType()).setTooltip(
                    new TranslatableText("config.advancedchat.filter.info.replacetype.none"),
                    new TranslatableText("config.advancedchat.filter.info.replacetype.onlymatch"),
                    new TranslatableText("config.advancedchat.filter.info.replacetype.fullline")
            ).setSaveConsumer(filter::setReplaceType).build());

            category.addEntry(entry.startStrField(new TranslatableText("config.advancedchat.filter.replaceto"), filter.getReplaceTo()).setTooltip(new TranslatableText("config.advancedchat.filter.info.replaceto")).setSaveConsumer(val -> filter.setReplaceTo(val)).build());

            ArrayList<AbstractConfigListEntry> sounds = new ArrayList<>();

            sounds.add(entry.startSelector(new TranslatableText("config.advancedchat.filter.notifysound"), Filter.NotifySounds.values(), filter.getNotifySound()).setTooltip(
                    new TranslatableText("config.advancedchat.filter.info.notifysound")
            ).setSaveConsumer(filter::setNotifySound).build());

            sounds.add(entry.startFloatField(new TranslatableText("config.advancedchat.filter.soundpitch"), filter.getSoundPitch()).setTooltip(new TranslatableText("config.advancedchat.filter.info.soundpitch")).setDefaultValue(1).setSaveConsumer(filter::setSoundPitch).setMin(0.5F).setMax(3).build());

            sounds.add(entry.startFloatField(new TranslatableText("config.advancedchat.filter.soundvolume"), filter.getSoundVol()).setTooltip(new TranslatableText("config.advancedchat.filter.info.soundvolume")).setDefaultValue(1).setMin(0.5F).setMax(5).setSaveConsumer(filter::setSoundVol).build());


            category.addEntry(entry.startSubCategory(new TranslatableText("config.advancedchat.subcategory.filter.sound"), sounds).build());


            ArrayList<AbstractConfigListEntry> color = new ArrayList<>();

            color.add(entry.startBooleanToggle(new TranslatableText("config.advancedchat.filter.replacetextcolor"), filter.isReplaceTextColor()).setTooltip(
                    new TranslatableText("config.advancedchat.filter.info.replacetextcolor")
            ).setSaveConsumer(filter::setReplaceTextColor).build());

            color.add(entry.startBooleanToggle(new TranslatableText("config.advancedchat.filter.replacebackground"), filter.isReplaceBackgroundColor()).setTooltip(
                    new TranslatableText("config.advancedchat.filter.info.replacebackground")
            ).setSaveConsumer(filter::setReplaceBackgroundColor).build());

            color.add(entry.startAlphaColorField(new TranslatableText("config.advancedchat.filter.color"), filter.getColor().color()).setSaveConsumer(newval -> {
                filter.setColor(new ColorUtil.SimpleColor(newval));
            }).setTooltip(new TranslatableText("config.advancedchat.filter.info.color")).build());

            category.addEntry(entry.startSubCategory(new TranslatableText("config.advancedchat.subcategory.filter.color"), color).build());




            category.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.filter.delete"), select, select[0]).setNameProvider((s -> {
                if (s.equalsIgnoreCase("1")) {
                    return new TranslatableText("config.advancedchat.click");
                }
                AdvancedChat.configStorage.filters.remove(filter);
                ModMenuImpl.save();
                MinecraftClient.getInstance().openScreen(getScreen(parentScreen));
                return new TranslatableText("config.advancedchat.click");
            })).setTooltip(new TranslatableText("warn.advancedchat.savefirst")).build());

        }

        return builder.build();
    }


}
