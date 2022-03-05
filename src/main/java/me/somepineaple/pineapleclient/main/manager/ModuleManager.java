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

	public static ArrayList<Hack> arrayHacks = new ArrayList<>();

	public static Minecraft mc = Minecraft.getMinecraft();

	public ModuleManager() {

		// CLick GUI and HUD.
		addHack(new ClickGUI());
		addHack(new ClickHUD());

		// Chat.
		addHack(new ChatSuffix());
		addHack(new VisualRange());
		addHack(new Totempop());
		addHack(new ClearChat());
		addHack(new ChatMods());
		addHack(new AutoEz());
		addHack(new AntiRacist());
		addHack(new Announcer());

		// Combat.
		addHack(new Criticals());
		addHack(new KillAura());
		addHack(new Surround());
		addHack(new Velocity());
		addHack(new AutoCrystal());
		addHack(new HoleFill());
		addHack(new Trap());
		addHack(new Socks());
		addHack(new SelfTrap());
		addHack(new AutoArmour());
		addHack(new Auto32k());
		addHack(new Webfill());
		addHack(new AutoWeb());
		addHack(new BedAura());
		addHack(new Offhand());
		addHack(new AutoGapple());
		addHack(new AutoTotem());
		addHack(new AutoMine());

		// Exploit.
		addHack(new XCarry());
		addHack(new NoSwing());
		addHack(new PortalGodMode());
		addHack(new PacketMine());
		addHack(new EntityMine());
		addHack(new BuildHeight());
		addHack(new CoordExploit());
		addHack(new NoHandshake());
		addHack(new NoRotate());

		// Movement.
		addHack(new Strafe());
		addHack(new Step());
		addHack(new Sprint());
		addHack(new Anchor());
		addHack(new Freecam());
		addHack(new MCP());
		addHack(new ReverseStep());
		addHack(new Scaffold());
		addHack(new BlockLag());
		addHack(new Blink());
		addHack(new InstantBurrow());
		addHack(new Timer());
		
		// Render.
		addHack(new Highlight());
		addHack(new HoleESP());
		addHack(new ShulkerPreview());
		addHack(new ViewmodleChanger());
		addHack(new VoidESP());
		addHack(new Antifog());
		addHack(new NameTags());
		addHack(new FuckedDetector());
		addHack(new Tracers());
		addHack(new SkyColour());
		addHack(new ESP());
		addHack(new Capes());
		addHack(new AlwaysNight());
		addHack(new CityEsp());
		addHack(new FullBright());
		addHack(new BreakHighlight());
		addHack(new NoFire());
		addHack(new Chams());

		// Misc.
		addHack(new MiddleClickFriends());
		addHack(new StopEXP());
		addHack(new AutoReplenish());
		addHack(new AutoNomadHut());
		addHack(new FastUtil());
		addHack(new Speedmine());

		// Dev
		addHack(new FakePlayer());

		arrayHacks.sort(Comparator.comparing(Hack::get_name));
	}

	public void addHack(Hack module) {
		arrayHacks.add(module);
	}

	public ArrayList<Hack> getArrayHacks() {
		return arrayHacks;
	}

	public ArrayList<Hack> getArrayActiveHacks() {
		ArrayList<Hack> actived_modules = new ArrayList<>();

		for (Hack modules : getArrayHacks()) {
			if (modules.isActive()) {
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

	public Vec3d getInterpolatedPos(Entity entity, double ticks) {
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

		Vec3d pos = getInterpolatedPos(mc.player, event.getPartialTicks());

		EventRender eventRender = new EventRender(RenderHelp.INSTANCE, pos);

		eventRender.resetTranslation();

		mc.profiler.endSection();

		for (Hack modules : getArrayHacks()) {
			if (modules.isActive()) {
				mc.profiler.startSection(modules.getTag());
				try {
					modules.render(eventRender);
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

		RenderHelp.releaseGl();

		mc.profiler.endSection();
		mc.profiler.endSection();
	}

	public void update() {
		for (Hack modules : getArrayHacks()) {
			if (modules.isActive()) {
				try {
					modules.update();
				} catch (Exception e) {
					MessageUtil.clientMessage("Error at " + modules.get_name() + " update method " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	public void render() {
		for (Hack modules : getArrayHacks()) {
			if (modules.isActive()) {
				try {
					modules.render();
				} catch (Exception e) {
					if (mc.world != null && mc.player != null) {
						MessageUtil.clientMessage("Error at " + modules.get_name() + " render method " + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void bind(int eventKey) {
		if (eventKey == 0) {
			return;
		}

		for (Hack modules : getArrayHacks()) {
			if (modules.getBind(0) == eventKey) {
				modules.toggle();
			}
		}
	}

	public Hack getModuleWithTag(String tag) {
		Hack moduleRequested = null;

		for (Hack module : getArrayHacks()) {
			if (module.getTag().equalsIgnoreCase(tag)) {
				moduleRequested = module;
			}
		}

		return moduleRequested;
	}

	public ArrayList<Hack> getModulesWithCategory(Category category) {
		ArrayList<Hack> moduleRequested = new ArrayList<>();

		for (Hack modules : getArrayHacks()) {
			if (modules.getCategory().equals(category)) {
				moduleRequested.add(modules);
			}
		}

		return moduleRequested;
	}
}
