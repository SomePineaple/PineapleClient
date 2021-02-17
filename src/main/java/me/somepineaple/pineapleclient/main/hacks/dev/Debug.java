package me.somepineaple.pineapleclient.main.hacks.dev;

import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;

public class Debug extends Hack {
    
    public Debug() {
        super(Category.BETA);

		this.name        = "Debug";
		this.tag         = "Debug";
		this.description = "used for debugging";
    }
}
