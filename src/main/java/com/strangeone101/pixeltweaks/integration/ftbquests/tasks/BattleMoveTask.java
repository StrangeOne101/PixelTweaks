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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BattleMoveTask extends PokemonTask {

    public enum MoveType {
        ANY, NORMAL, Z_MOVE, DYNAMAX
    }

    public String attack = "";
    public Tristate crit = Tristate.DEFAULT;
    public MoveType moveType = MoveType.ANY;

    public BattleMoveTask(Quest q) {
        super(q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.BATTLE_MOVE;
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putString("attack", attack);
        crit.write(nbt, "crit");
        nbt.putByte("moveType", (byte) moveType.ordinal());
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        attack = nbt.getString("attack");
        crit = Tristate.read(nbt, "crit");
        moveType = MoveType.values()[nbt.getByte("moveType")];
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeString(attack);
        crit.write(buffer);
        buffer.writeByte(moveType.ordinal());
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        attack = buffer.readString();
        crit = Tristate.read(buffer);
        moveType = MoveType.values()[buffer.readByte()];
    }

    @Override
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);
        config.addString("attack", attack, v -> attack = v.toLowerCase().replace(' ', '_'), "");
        config.addTristate("crit", crit, v -> crit = v, Tristate.DEFAULT);
        config.addEnum("moveType", moveType, v -> moveType = v, NameMap.of(MoveType.ANY, MoveType.values())
                .nameKey(v -> "pixeltweaks.battle_move_type." + v.toString().toLowerCase())
                .create());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ITextComponent getAltTitle() {
        TranslationTextComponent title = new TranslationTextComponent("ftbquests.task.pixelmon.battle_move.title",
                new TranslationTextComponent("attack." + this.attack), new StringTextComponent(this.count > 1 ? " x" + this.count : ""));

        if (this.attack.isEmpty()) title = new TranslationTextComponent("ftbquests.task.pixelmon.battle_move");

        if (!this.pokemonSpec.isEmpty()) {
            title = new TranslationTextComponent("ftbquests.task.pixelmon.battle_move.title_specific",
                    new TranslationTextComponent("attack." + this.attack),
                    new StringTextComponent(this.count > 1 ? " x" + this.count : ""),
                    getPokemon());
        }

        return title;
    }

    public void onBattleMove(TeamData team, Pokemon pokemon, Attack move) {
        if (!team.isCompleted(this) && (this.pokemonSpec.isEmpty() || this.cachedSpec.matches(pokemon))
                && (this.attack.isEmpty() || this.attack.equalsIgnoreCase(move.getMove().getAttackName().toLowerCase().replace(' ', '_')))
                && (this.crit == Tristate.DEFAULT || this.crit.isTrue() == move.didCrit)
        && (this.moveType == MoveType.ANY || (move.isMax ? this.moveType == MoveType.DYNAMAX : move.isZ ? this.moveType == MoveType.Z_MOVE : this.moveType == MoveType.NORMAL))) {
            team.addProgress(this, 1L);
        }
    }
}
