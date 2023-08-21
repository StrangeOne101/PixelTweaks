package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.api.moveskills.MoveSkill;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.stream.Collectors;

public class ExternalMoveTask extends PokemonTask {

    public String move = "forage";

    public ExternalMoveTask(Quest q) {
        super(q);
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putString("move", move);
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        move = nbt.getString("move");
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeString(move);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        move = buffer.readString();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);

        config.addEnum("move", move, (v) -> move = v, NameMap.of("forage", MoveSkill.moveSkills.stream().map(m -> m.id).collect(Collectors.toList()))
                .nameKey(m -> "pixelmon.moveskill." + m)
                .icon(m -> Icon.getIcon(MoveSkill.getMoveSkillByID(m).sprite))
                .create());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Icon getAltIcon() {
        return Icon.getIcon(MoveSkill.getMoveSkillByID(move).sprite);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ITextComponent getAltTitle() {
        TranslationTextComponent title = new TranslationTextComponent("ftbquests.task.pixelmon.external_move.title",
                new TranslationTextComponent(MoveSkill.getMoveSkillByID(move).name));
        if (count > 1) {
            title.appendString(" ");
            title.appendString(count + "x ");
        }
        if (!this.pokemonSpec.isEmpty()) {
            title.appendSibling(getPokemon());
        }
        return title;
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.EXTERNAL_MOVE;
    }

    public void onMove(TeamData teamData, String move, Pokemon pokemon) {
        if (teamData.isCompleted(this)) return;

        if (teamData.file.isServerSide() && (this.pokemonSpec.isEmpty() || this.cachedSpec.matches(pokemon) != this.invert)) {
            teamData.addProgress(this, 1);
        }
    }
}
