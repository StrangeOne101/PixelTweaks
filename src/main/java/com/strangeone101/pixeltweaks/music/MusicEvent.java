package com.strangeone101.pixeltweaks.music;


import com.strangeone101.pixeltweaks.pixelevents.Event;

public class MusicEvent extends Event {

    public Music music;

    public static class BGM extends MusicEvent {}

    public static class Battle extends MusicEvent {}
}
