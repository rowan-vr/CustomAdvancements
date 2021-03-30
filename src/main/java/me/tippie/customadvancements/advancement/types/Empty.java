package me.tippie.customadvancements.advancement.types;

import me.tippie.customadvancements.CustomAdvancements;

import java.util.logging.Level;

public class Empty extends AdvancementType {
	public Empty() {
		super("");
		CustomAdvancements.getInstance().getLogger().log(Level.WARNING,"One of your configurations contains an invalid advancement type.");
	}

	@Override
	public void onProgress() {

	}
}
