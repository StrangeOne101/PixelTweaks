package com.strangeone101.pixeltweaks.music;

import com.strangeone101.pixeltweaks.pixelevents.Event;
import com.strangeone101.pixeltweaks.pixelevents.IValidator;

public class MusicEvent extends Event implements IValidator {

    public Music music;

    @Override
    public boolean isClientSide() {
        return true;
    }

    @Override
    public boolean validate() {
        return music != null;
    }

    @Override
    public String getError() {
        return "Music must be provided!";
    }

    public static class BGM extends MusicEvent {}

    public static class Battle extends MusicEvent {}
}
