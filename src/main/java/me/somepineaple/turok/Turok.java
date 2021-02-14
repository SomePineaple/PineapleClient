package me.somepineaple.turok;

// Draw.

import me.somepineaple.turok.draw.GL;
import me.somepineaple.turok.task.Font;

// Task.

/**
* @author me
*
* Created by me.
* 27/04/20.
*
*/
public class Turok {
	private Font font_manager;

	public Turok(String tag) {
		
	}

	public void resize(int x, int y, float size) {
		GL.resize(x, y, size);
	}

	public void resize(int x, int y, float size, String tag) {
		GL.resize(x, y, size, "end");
	}

	public Font get_font_manager() {
		return this.font_manager;
	}
}