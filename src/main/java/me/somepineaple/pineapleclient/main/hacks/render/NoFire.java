package me.somepineaple.pineapleclient.main.hacks.render;

import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;

public class NoFire extends Hack {

	public NoFire() {
		super(Category.RENDER);
		this.name = "No Fire";
		this.tag = "nofire";
		this.description = "Stops the rendering of the fire overlay";
	}
}
