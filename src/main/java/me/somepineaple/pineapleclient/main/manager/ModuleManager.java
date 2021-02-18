package me.somepineaple.pineapleclient.main.manager;

import me.somepineaple.pineapleclient.main.util.MessageUtil;
import me.somepineaple.turok.draw.RenderHelp;
import me.somepineaple.pineapleclient.main.event.events.EventRender;
import me.somepineaple.pineapleclient.main.hacks.render.BreakHighlight;
import me.somepineaple.pineapleclient.main.hacks.*;
import me.somepineaple.pineapleclient.main.hacks.chat.*;
import me.somepineaple.pineapleclient.main.hacks.combat.*;
import me.somepineaple.pineapleclient.main.hacks.dev.FakePlayer;
import me.somepineaple.pineapleclient.main.hacks.exploit.*;
import me.somepineaple.pineapleclient.main.hacks.misc.*;
import me.somepineaple.pineapleclient.main.hacks.movement.*;
import me.somepineaple.pineapleclient.main.hacks.render.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;

public class ModuleManager {

	public static ArrayList<Hack> array_hacks = new ArrayList<>();

	public static Minecraft mc = Minecraft.getMinecraft();

	public ModuleManager() {

		// CLick GUI and HUD.
		add_hack(new ClickGUI());
		add_hack(new ClickHUD());

		// Chat.
		add_hack(new ChatSuffix());
		add_hack(new VisualRange());
		add_hack(new Totempop());
		add_hack(new ClearChat());
		add_hack(new ChatMods());
		add_hack(new AutoEz());
		add_hack(new AntiRacist());
		add_hack(new Announcer());

		// Combat.
		add_hack(new Criticals());
		add_hack(new KillAura());
		add_hack(new Surround());
		add_hack(new Velocity());
		add_hack(new AutoCrystal());
		add_hack(new HoleFill());
		add_hack(new Trap());
		add_hack(new Socks());
		add_hack(new SelfTrap());
		add_hack(new AutoArmour());
		add_hack(new Auto32k());
		add_hack(new Webfill());
		add_hack(new AutoWeb());
		add_hack(new BedAura());
		add_hack(new Offhand());
		add_hack(new AutoGapple());
		add_hack(new AutoTotem());
		add_hack(new AutoMine());

		// Exploit.
		add_hack(new XCarry());
		add_hack(new NoSwing());
		add_hack(new PortalGodMode());
		add_hack(new PacketMine());
		add_hack(new EntityMine());
		add_hack(new BuildHeight());
		add_hack(new CoordExploit());
		add_hack(new NoHandshake());
		add_hack(new NoRotate());

		// Movement.
		add_hack(new Strafe());
		add_hack(new Step());
		add_hack(new Sprint());
		add_hack(new Anchor());
		add_hack(new Freecam());
		add_hack(new MCP());
		add_hack(new ReverseStep());
		add_hack(new Scaffold());
		
		// Render.
		add_hack(new Highlight());
		add_hack(new HoleESP());
		add_hack(new ShulkerPreview());
		add_hack(new ViewmodleChanger());
		add_hack(new VoidESP());
		add_hack(new Antifog());
		add_hack(new NameTags());
		add_hack(new FuckedDetector());
		add_hack(new Tracers());
		add_hack(new SkyColour());
		add_hack(new Chams());
		add_hack(new Capes());
		add_hack(new AlwaysNight());
		add_hack(new CityEsp());
		add_hack(new FullBright());
		add_hack(new BreakHighlight());
		add_hack(new NoFire());

		// Misc.
		add_hack(new MiddleClickFriends());
		add_hack(new StopEXP());
		add_hack(new AutoReplenish());
		add_hack(new AutoNomadHut());
		add_hack(new FastUtil());
		add_hack(new Speedmine());

		// Dev
		add_hack(new FakePlayer());

		array_hacks.sort(Comparator.comparing(Hack::get_name));
	}

	public void add_hack(Hack module) {
		array_hacks.add(module);
	}

	public ArrayList<Hack> get_array_hacks() {
		return array_hacks;
	}

	public ArrayList<Hack> get_array_active_hacks() {
		ArrayList<Hack> actived_modules = new ArrayList<>();

		for (Hack modules : get_array_hacks()) {
			if (modules.is_active()) {
				actived_modules.add(modules);
			}
		}

		return actived_modules;
	}

	public Vec3d process(Entity entity, double x, double y, double z) {
		return new Vec3d(
			(entity.posX - entity.lastTickPosX) * x,
			(entity.posY - entity.lastTickPosY) * y,
			(entity.posZ - entity.lastTickPosZ) * z);
	}

	public Vec3d get_interpolated_pos(Entity entity, double ticks) {
		return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(process(entity, ticks, ticks, ticks)); // x, y, z.
	}

	public void render(RenderWorldLastEvent event) {
		mc.profiler.startSection("pineapleclient");
		mc.profiler.startSection("setup");

		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.disableDepth();

		GlStateManager.glLineWidth(1f);

		Vec3d pos = get_interpolated_pos(mc.player, event.getPartialTicks());

		EventRender event_render = new EventRender(RenderHelp.INSTANCE, pos);

		event_render.reset_translation();

		mc.profiler.endSection();

		for (Hack modules : get_array_hacks()) {
			if (modules.is_active()) {
				mc.profiler.startSection(modules.get_tag());
				try {
					modules.render(event_render);
				} catch (Exception e) {
					e.printStackTrace();
					// NotificationUtil.send_notification(new Notification("Error at: " + modules.get_name() + " render method: " + e.getCause().getLocalizedMessage(), 255, 0, 0));
				}
				mc.profiler.endSection();
			}
		}

		mc.profiler.startSection("release");

		GlStateManager.glLineWidth(1f);

		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.enableDepth();
		GlStateManager.enableCull();

		RenderHelp.release_gl();

		mc.profiler.endSection();
		mc.profiler.endSection();
	}

	public void update() {
		for (Hack modules : get_array_hacks()) {
			if (modules.is_active()) {
				try {
					modules.update();
				} catch (Exception e) {
					// MessageUtil.client_message("Error at " + modules.get_name() + " update method " + e.getMessage());
					// NotificationUtil.send_notification(new Notification("Error at " + modules.get_name() + " update method " + e.getCause().getLocalizedMessage(), 255, 0, 0));
					e.printStackTrace();
				}
			}
		}
	}

	public void render() {
		for (Hack modules : get_array_hacks()) {
			if (modules.is_active()) {
				try {
					modules.render();
				} catch (Exception e) {
					if (mc.world != null && mc.player != null) {
						MessageUtil.client_message("Error at " + modules.get_name() + " render method " + e.getMessage());
						// NotificationUtil.send_notification(new Notification("Error at " + modules.get_name() + " render method " + e.getCause().getLocalizedMessage(), 255, 0, 0));
					}
				}
			}
		}
	}

	public void bind(int event_key) {
		if (event_key == 0) {
			return;
		}

		for (Hack modules : get_array_hacks()) {
			if (modules.get_bind(0) == event_key) {
				modules.toggle();
			}
		}
	}

	public Hack get_module_with_tag(String tag) {
		Hack module_requested = null;

		for (Hack module : get_array_hacks()) {
			if (module.get_tag().equalsIgnoreCase(tag)) {
				module_requested = module;
			}
		}

		return module_requested;
	}

	public ArrayList<Hack> get_modules_with_category(Category category) {
		ArrayList<Hack> module_requesteds = new ArrayList<>();

		for (Hack modules : get_array_hacks()) {
			if (modules.get_category().equals(category)) {
				module_requesteds.add(modules);
			}
		}

		return module_requesteds;
	}
}
