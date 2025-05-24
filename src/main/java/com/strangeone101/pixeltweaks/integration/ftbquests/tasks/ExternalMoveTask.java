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
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.stream.Collectors;

public class ExternalMoveTask extends PokemonTask {

    public String move = "forage";

    public ExternalMoveTask(long id, Quest q) {
        super(id, q);
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putString("move", move);
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        move = nbt.getString("move");
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeUtf(move);
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        move = buffer.readUtf();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);

        config.addEnum("move", move, (v) -> move = v, NameMap.of("forage", MoveSkill.getAllMoveSkills().stream().map(m -> m.id()).collect(Collectors.toList()))
                .nameKey(m -> "pixelmon.moveskill." + m)
                .icon(m -> Icon.getIcon(MoveSkill.getMoveSkillByID(m).sprite()))
                .create());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Icon getAltIcon() {
        return Icon.getIcon(MoveSkill.getMoveSkillByID(move).sprite());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getAltTitle() {
        MutableComponent title = Component.translatable("ftbquests.task.pixelmon.external_move.title",
                Component.translatable(MoveSkill.getMoveSkillByID(move).name()));
        if (count > 1) {
            title.append(" ");
            title.append(count + "x");
        }
        if (this.cachedSpec != null) {
            title.append(" ");
            title.append(getPokemon());
        }
        return title;
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.EXTERNAL_MOVE;
    }

    public void onMove(TeamData teamData, String move, Pokemon pokemon) {
        if (teamData.isCompleted(this)) return;

        if (teamData.getFile().isServerSide() && (this.cachedSpec == null || this.cachedSpec.matches(pokemon) != this.invert)
                && this.move.equals(move)) {
            teamData.addProgress(this, 1);
        }
    }
}
