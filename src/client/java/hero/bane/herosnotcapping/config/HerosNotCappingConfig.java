package hero.bane.herosnotcapping.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class HerosNotCappingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger("herosnotcapping");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/herosnotcapping.json");

    public boolean keybindMode = false;

    public static HerosNotCappingConfig load() {
        try {
            if (!CONFIG_FILE.exists()) {
                HerosNotCappingConfig cfg = new HerosNotCappingConfig();
                cfg.save();
                return cfg;
            }

            FileReader reader = new FileReader(CONFIG_FILE);
            HerosNotCappingConfig cfg = GSON.fromJson(reader, HerosNotCappingConfig.class);
            reader.close();
            return cfg;

        } catch (Exception e) {
            LOGGER.error("Failed to load HerosNotCapping config", e);
            return new HerosNotCappingConfig();
        }
    }

    public void save() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(CONFIG_FILE);
            GSON.toJson(this, writer);
            writer.close();

        } catch (Exception e) {
            LOGGER.error("Failed to save HerosNotCapping config", e);
        }
    }
}
