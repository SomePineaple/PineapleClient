package me.somepineaple.pineapleclient.main.manager;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.render.components.Frame;
import me.somepineaple.pineapleclient.main.guiscreen.render.pinnables.Pinnable;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.DrawnUtil;
import me.somepineaple.pineapleclient.main.util.EnemyUtil;
import me.somepineaple.pineapleclient.main.util.EzMessageUtil;
import me.somepineaple.pineapleclient.main.util.FriendUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static me.somepineaple.pineapleclient.PineapleClient.sendMinecraftLog;

public class ConfigManager {

    // FOLDERS
    private final String MAIN_FOLDER = "PINEAPLECLIENT/";
    private final String CONFIGS_FOLDER = MAIN_FOLDER + "configs/";
    private String ACTIVE_CONFIG_FOLDER = CONFIGS_FOLDER + "default/";

    // STATIC FILES
    private final String CLIENT_FILE = "client.json";
    private final String DRAWN_FILE = "drawn.txt";
    private final String EZ_FILE = "ez.txt";
    private final String ENEMIES_FILE = "enemies.json";
    private final String FRIENDS_FILE = "friends.json";
    private final String HUD_FILE = "hud.json";

    // DIRS
    private final String CLIENT_DIR = MAIN_FOLDER + CLIENT_FILE;
    private final String DRAWN_DIR = MAIN_FOLDER + DRAWN_FILE;
    private final String EZ_DIR = MAIN_FOLDER + EZ_FILE;
    private final String ENEMIES_DIR = MAIN_FOLDER + ENEMIES_FILE;
    private final String FRIENDS_DIR = MAIN_FOLDER + FRIENDS_FILE;
    private final String HUD_DIR = MAIN_FOLDER + HUD_FILE;

    // FOLDER PATHS
    private final Path MAIN_FOLDER_PATH = Paths.get(MAIN_FOLDER);
    private final Path CONFIGS_FOLDER_PATH = Paths.get(CONFIGS_FOLDER);
    private Path ACTIVE_CONFIG_FOLDER_PATH = Paths.get(ACTIVE_CONFIG_FOLDER);

    // FILE PATHS
    private final Path CLIENT_PATH = Paths.get(CLIENT_DIR);
    private final Path DRAWN_PATH = Paths.get(DRAWN_DIR);
    private final Path EZ_PATH = Paths.get(EZ_DIR);
    private final Path HUD_PATH = Paths.get(HUD_DIR);

    public boolean set_active_config_folder(String folder) {
        if (folder.equals(this.ACTIVE_CONFIG_FOLDER)) {
            return false;
        }

        this.ACTIVE_CONFIG_FOLDER = CONFIGS_FOLDER + folder;
        this.ACTIVE_CONFIG_FOLDER_PATH = Paths.get(ACTIVE_CONFIG_FOLDER);

        loadSettings();
        return true;
    }

    // LOAD & SAVE EZ MESSAGE

    private void save_ezmessage() throws IOException {

        FileWriter writer = new FileWriter(EZ_DIR);

        try {
            writer.write(EzMessageUtil.get_message());
        } catch (Exception ignored) {
            writer.write("test message");
        }

        writer.close();

    }

    private void load_ezmessage() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String s : Files.readAllLines(EZ_PATH)) {
            sb.append(s);
        }
        EzMessageUtil.set_message(sb.toString());
    }

    // LOAD & SAVE DRAWN

    private void save_drawn() throws IOException {
        FileWriter writer = new FileWriter(DRAWN_DIR);

        for (String s : DrawnUtil.hidden_tags) {
            writer.write(s + System.lineSeparator());
        }

        writer.close();
    }

    private void load_drawn() throws IOException {
        DrawnUtil.hidden_tags = Files.readAllLines(DRAWN_PATH);
    }

    // LOAD & SAVE PALS

    private void save_friends() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(FriendUtil.friends);
        OutputStreamWriter file;

        file = new OutputStreamWriter(new FileOutputStream(FRIENDS_DIR), StandardCharsets.UTF_8);
        file.write(json);
        file.close();
    }

    private void load_friends() throws IOException {
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get(FRIENDS_DIR));

        FriendUtil.friends = gson.fromJson(reader, new TypeToken<ArrayList<FriendUtil.Friend>>(){
            private static final long serialVersionUID = -5732545611332038008L;
        }.getType());

        reader.close();
    }

    // LOAD & SAVE ENEMIES

    private void save_enemies() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(EnemyUtil.enemies);

        OutputStreamWriter file;

        file = new OutputStreamWriter(new FileOutputStream(ENEMIES_DIR), StandardCharsets.UTF_8);
        file.write(json);
        file.close();
    }

    private void load_enemies() throws IOException {
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get(ENEMIES_DIR));

        EnemyUtil.enemies = gson.fromJson(reader, new TypeToken<ArrayList<EnemyUtil.Enemy>>(){
            private static final long serialVersionUID = -3166080551425623602L;
        }.getType());

        reader.close();
    }

    // LOAD & SAVE HACKS

    private void save_hacks() throws IOException {

        for (Hack hack : PineapleClient.getHackManager().getArrayHacks()) {

            final String file_name = ACTIVE_CONFIG_FOLDER + hack.getTag() + ".txt";
            final Path file_path = Paths.get(file_name);
            delete_file(file_name);
            verify_file(file_path);

            final File file = new File(file_name);
            final BufferedWriter br = new BufferedWriter(new FileWriter(file));

            for (Setting setting : PineapleClient.getSettingManager().get_settings_with_hack(hack)) {
                switch (setting.get_type()) {
                    case "button":
                        br.write(setting.get_tag() + ":" + setting.getValue(true) + "\r\n");
                        break;
                    case "combobox":
                        br.write(setting.get_tag() + ":" + setting.get_current_value() + "\r\n");
                        break;
                    case "label":
                        br.write(setting.get_tag() + ":" + setting.getValue("") + "\r\n");
                        break;
                    case "doubleslider":
                        br.write(setting.get_tag() + ":" + setting.getValue(1.0) + "\r\n");
                        break;
                    case "integerslider":
                        br.write(setting.get_tag() + ":" + setting.getValue(1) + "\r\n");
                        break;
                }
            }

            br.close();
        }
    }

    private void load_hacks() throws IOException {

        for (Hack hack : PineapleClient.getHackManager().getArrayHacks()) {

            final String file_name = ACTIVE_CONFIG_FOLDER + hack.getTag() + ".txt";
            final File file = new File(file_name);
            final FileInputStream fi_stream = new FileInputStream(file.getAbsolutePath());
            final DataInputStream di_stream = new DataInputStream(fi_stream);
            final BufferedReader br = new BufferedReader(new InputStreamReader(di_stream));

            final List<String> bugged_lines = new ArrayList<>();

            String line;
            while ((line = br.readLine()) != null) {

                try {
                    final String colune = line.trim();
                    final String tag = colune.split(":")[0];
                    final String value = colune.split(":")[1];

                    Setting setting = PineapleClient.getSettingManager().getSettingWithTag(hack, tag);

                    // send_minecraft_log("Attempting to assign value '" + value + "' to setting '" + tag + "'");

                    try {
                        switch (setting.get_type()) {
                            case "button":
                                setting.setValue(Boolean.parseBoolean(value));
                                break;
                            case "label":
                                setting.setValue(value);
                                break;
                            case "doubleslider":
                                setting.setValue(Double.parseDouble(value));
                                break;
                            case "integerslider":
                                setting.setValue(Integer.parseInt(value));
                                break;
                            case "combobox":
                                setting.set_current_value(value);
                                break;
                        }
                    } catch (Exception e) {
                        // TODO : FIX BUGGED LINE
                        bugged_lines.add(colune);
                        sendMinecraftLog("Error loading '" + value + "' to setting '" + tag + "'");
                        break;
                    }


                } catch (Exception ignored) {
                } // TODO : figure out what causes this

            }

            br.close();

        }
    }

    // LOAD & SAVE client/gui

    private void save_client() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser parser = new JsonParser();

        JsonObject main_json = new JsonObject();

        JsonObject config = new JsonObject();
        JsonObject gui = new JsonObject();

        config.add("name", new JsonPrimitive(PineapleClient.getName()));
        config.add("version", new JsonPrimitive(PineapleClient.getVersion()));
        config.add("user", new JsonPrimitive(PineapleClient.getActualUser()));
        config.add("prefix", new JsonPrimitive(CommandManager.getPrefix()));

        for (Frame frames_gui : PineapleClient.clickGui.get_array_frames()) {
            JsonObject frame_info = new JsonObject();

            frame_info.add("name", new JsonPrimitive(frames_gui.getName()));
            frame_info.add("tag", new JsonPrimitive(frames_gui.getTag()));
            frame_info.add("x", new JsonPrimitive(frames_gui.getX()));
            frame_info.add("y", new JsonPrimitive(frames_gui.getY()));

            gui.add(frames_gui.getTag(), frame_info);
        }

        main_json.add("configuration", config);
        main_json.add("gui",           gui);

        JsonElement json_pretty = parser.parse(main_json.toString());

        String json = gson.toJson(json_pretty);

        OutputStreamWriter file;

        file = new OutputStreamWriter(new FileOutputStream(CLIENT_DIR), StandardCharsets.UTF_8);
        file.write(json);

        file.close();
    }

    private void load_client() throws IOException {
        InputStream stream = Files.newInputStream(CLIENT_PATH);
        JsonObject  json_client = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
        JsonObject  json_config = json_client.get("configuration").getAsJsonObject();
        JsonObject  json_gui = json_client.get("gui").getAsJsonObject();

        CommandManager.setPrefix(json_config.get("prefix").getAsString());

        for (Frame frames : PineapleClient.clickGui.get_array_frames()) {
            JsonObject frame_info = json_gui.get(frames.getTag()).getAsJsonObject();

            Frame frame_requested = PineapleClient.clickGui.get_frame_with_tag(frame_info.get("tag").getAsString());

            frame_requested.setX(frame_info.get("x").getAsInt());
            frame_requested.setY(frame_info.get("y").getAsInt());
        }

        stream.close();
    }

    // LOAD & SAVE HUD

    private void save_hud() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser parser = new JsonParser();

        JsonObject main_json = new JsonObject();

        JsonObject main_frame = new JsonObject();
        JsonObject main_hud   = new JsonObject();

        main_frame.add("name", new JsonPrimitive(PineapleClient.clickHud.get_frame_hud().get_name()));
        main_frame.add("tag",  new JsonPrimitive(PineapleClient.clickHud.get_frame_hud().get_tag()));
        main_frame.add("x",    new JsonPrimitive(PineapleClient.clickHud.get_frame_hud().get_x()));
        main_frame.add("y",    new JsonPrimitive(PineapleClient.clickHud.get_frame_hud().get_y()));

        for (Pinnable pinnables_hud : PineapleClient.getHudManager().get_array_huds()) {
            JsonObject frame_info = new JsonObject();

            frame_info.add("title", new JsonPrimitive(pinnables_hud.get_title()));
            frame_info.add("tag",   new JsonPrimitive(pinnables_hud.get_tag()));
            frame_info.add("state", new JsonPrimitive(pinnables_hud.is_active()));
            frame_info.add("dock",  new JsonPrimitive(pinnables_hud.get_dock()));
            frame_info.add("x",     new JsonPrimitive(pinnables_hud.get_x()));
            frame_info.add("y",     new JsonPrimitive(pinnables_hud.get_y()));

            main_hud.add(pinnables_hud.get_tag(), frame_info);
        }

        main_json.add("frame", main_frame);
        main_json.add("hud",   main_hud);

        JsonElement pretty_json = parser.parse(main_json.toString());

        String json = gson.toJson(pretty_json);

        delete_file(HUD_DIR);
        verify_file(HUD_PATH);

        OutputStreamWriter file;

        file = new OutputStreamWriter(new FileOutputStream(HUD_DIR), StandardCharsets.UTF_8);
        file.write(json);

        file.close();
    }

    private void load_hud() throws IOException {
        InputStream input_stream  = Files.newInputStream(HUD_PATH);
        JsonObject  main_hud   = new JsonParser().parse(new InputStreamReader(input_stream)).getAsJsonObject();
        JsonObject  main_frame = main_hud.get("frame").getAsJsonObject();
        JsonObject  main_huds  = main_hud.get("hud").getAsJsonObject();

        PineapleClient.clickHud.get_frame_hud().set_x(main_frame.get("x").getAsInt());
        PineapleClient.clickHud.get_frame_hud().set_y(main_frame.get("y").getAsInt());

        for (Pinnable pinnables : PineapleClient.getHudManager().get_array_huds()) {
            JsonObject hud_info = main_huds.get(pinnables.get_tag()).getAsJsonObject();

            Pinnable pinnable_requested = PineapleClient.getHudManager().get_pinnable_with_tag(hud_info.get("tag").getAsString());

            pinnable_requested.set_active(hud_info.get("state").getAsBoolean());
            pinnable_requested.set_dock(hud_info.get("dock").getAsBoolean());

            pinnable_requested.set_x(hud_info.get("x").getAsInt());
            pinnable_requested.set_y(hud_info.get("y").getAsInt());
        }

        input_stream.close();
    }

    // LOAD & SAVE BINDS

    private void save_binds() throws IOException {
        final String file_name = ACTIVE_CONFIG_FOLDER + "BINDS.txt";
        final Path file_path = Paths.get(file_name);

        this.delete_file(file_name);
        this.verify_file(file_path);
        final File file = new File(file_name);
        final BufferedWriter br = new BufferedWriter(new FileWriter(file));
        for (final Hack modules : PineapleClient.getHackManager().getArrayHacks()) {
            br.write(modules.getTag() + ":" + modules.getBind(1) + ":" + modules.isActive() + "\r\n");
        }
        br.close();
    }

    private void load_binds() throws IOException {

        final String file_name = ACTIVE_CONFIG_FOLDER + "BINDS.txt";
        final File file = new File(file_name);
        final FileInputStream fi_stream = new FileInputStream(file.getAbsolutePath());
        final DataInputStream di_stream = new DataInputStream(fi_stream);
        final BufferedReader br = new BufferedReader(new InputStreamReader(di_stream));
        String line;
        while ((line = br.readLine()) != null) {
            try {
                final String colune = line.trim();
                final String tag = colune.split(":")[0];
                final String bind = colune.split(":")[1];
                final String active = colune.split(":")[2];
                final Hack module = PineapleClient.getHackManager().getModuleWithTag(tag);
                module.setBind(Integer.parseInt(bind));
                module.set_active(Boolean.parseBoolean(active));
            } catch (Exception ignored) {}
        }
        br.close();
    }

    // LOAD & SAVE SETTINGS

    public void saveSettings() {
        try {
            verify_dir(MAIN_FOLDER_PATH);
            verify_dir(CONFIGS_FOLDER_PATH);
            verify_dir(ACTIVE_CONFIG_FOLDER_PATH);
            save_hacks();
            save_binds();
            save_friends();
            save_enemies();
            save_client();
            save_drawn();
            save_ezmessage();
            save_hud();
        } catch (IOException e) {
            sendMinecraftLog("Something has gone wrong while saving settings");
            sendMinecraftLog(e.toString());
        }
    }

    public void loadSettings() {

        try {
            load_binds();
            load_client();
            load_drawn();
            load_enemies();
            load_ezmessage();
            load_friends();
            load_hacks();
            load_hud();
        } catch (IOException e) {
            sendMinecraftLog("Something has gone wrong while loading settings");
            sendMinecraftLog(e.toString());
        }
    }

    // GENERAL UTIL

    public boolean delete_file(final String path) throws IOException {
        final File f = new File(path);
        return f.delete();
    }

    public void verify_file(final Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    public void verify_dir(final Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
    }
}
