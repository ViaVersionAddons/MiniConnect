package net.lenni0451.miniconnect;

import net.lenni0451.optconfig.ConfigLoader;
import net.lenni0451.optconfig.annotations.Description;
import net.lenni0451.optconfig.annotations.OptConfig;
import net.lenni0451.optconfig.annotations.Option;
import net.lenni0451.optconfig.provider.ConfigProvider;
import net.raphimc.viaproxy.util.logging.Logger;

import java.io.File;

@OptConfig
public class Config {

    @Option("enable-account-login")
    @Description("Enable account login")
    public static boolean EnableAccountLogin = true;

    @Option("enable-proxy-online-mode")
    @Description("Enable the proxy online mode")
    public static boolean EnableProxyOnlineMode = true;

    static {
        try {
            File dataFolder = Main.getInstance().getDataFolder();
            dataFolder.mkdirs();
            ConfigLoader<Config> loader = new ConfigLoader<>(Config.class);
            loader.getConfigOptions().setAddMissingOptions(true)
                    .setRewriteConfig(true)
                    .setRemoveUnknownOptions(true)
                    .setResetInvalidOptions(true);
            loader.loadStatic(ConfigProvider.file(new File(Main.getInstance().getDataFolder(), "config.yml")));
        } catch (Throwable t) {
            Logger.LOGGER.error("Failed to load config.yml. Falling back to default config.", t);
        }
    }

}
