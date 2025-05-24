package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.config.Tristate;
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

public class BattleMoveTask extends PokemonTask {

    public enum MoveType {
        ANY, NORMAL, Z_MOVE, DYNAMAX
    }

    public String attack = "";
    public Tristate crit = Tristate.DEFAULT;
    public MoveType moveType = MoveType.ANY;

    public BattleMoveTask(long id, Quest q) {
        super(id, q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.BATTLE_MOVE;
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putString("attack", attack);
        crit.write(nbt, "crit");
        nbt.putByte("moveType", (byte) moveType.ordinal());
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        attack = nbt.getString("attack");
        crit = Tristate.read(nbt, "crit");
        moveType = MoveType.values()[nbt.getByte("moveType")];
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeUtf(attack);
        crit.write(buffer);
        buffer.writeByte(moveType.ordinal());
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        attack = buffer.readUtf();
        crit = Tristate.read(buffer);
        moveType = MoveType.values()[buffer.readByte()];
    }

    @Override
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addString("attack", attack, v -> attack = v.toLowerCase().replace(' ', '_'), "");
        config.addTristate("crit", crit, v -> crit = v, Tristate.DEFAULT);
        config.addEnum("moveType", moveType, v -> moveType = v, NameMap.of(MoveType.ANY, MoveType.values())
                .nameKey(v -> "pixeltweaks.battle_move_type." + v.toString().toLowerCase())
                .create());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getAltTitle() {
        MutableComponent title = Component.translatable("ftbquests.task.pixelmon.battle_move.title",
                Component.translatable("attack." + this.attack), Component.literal(this.count > 1 ? " x" + this.count : ""));

        if (this.attack.isEmpty()) title = Component.translatable("ftbquests.task.pixelmon.battle_move");

        if (this.cachedSpec != null) {
            title = Component.translatable("ftbquests.task.pixelmon.battle_move.title_specific",
                    Component.translatable("attack." + this.attack),
                    Component.literal(this.count > 1 ? " x" + this.count : ""),
                    getPokemon());
        }

        return title;
    }

    public void onBattleMove(TeamData team, Pokemon pokemon, Attack move) {
        if (!team.isCompleted(this) && (this.cachedSpec == null || this.cachedSpec.matches(pokemon))
                && (this.attack.isEmpty() || this.attack.equalsIgnoreCase(move.getMove().getAttackName().toLowerCase().replace(' ', '_')))
                && (this.crit == Tristate.DEFAULT || this.crit.isTrue() == move.didCrit)
        && (this.moveType == MoveType.ANY || (move.isMax ? this.moveType == MoveType.DYNAMAX : move.isZ ? this.moveType == MoveType.Z_MOVE : this.moveType == MoveType.NORMAL))) {
            team.addProgress(this, 1L);
        }
    }
}
