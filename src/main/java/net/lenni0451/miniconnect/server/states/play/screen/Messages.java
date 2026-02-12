package net.lenni0451.miniconnect.server.states.play.screen;

import net.lenni0451.mcstructs.text.TextComponent;
import net.lenni0451.mcstructs.text.stringformat.StringFormat;
import net.lenni0451.mcstructs.text.stringformat.handling.ColorHandling;
import net.lenni0451.mcstructs.text.stringformat.handling.DeserializerUnknownHandling;
import net.lenni0451.mcstructs.text.utils.TextUtils;
import net.lenni0451.miniconnect.Main;
import net.lenni0451.optconfig.ConfigLoader;
import net.lenni0451.optconfig.annotations.Description;
import net.lenni0451.optconfig.annotations.OptConfig;
import net.lenni0451.optconfig.annotations.Option;
import net.lenni0451.optconfig.annotations.Section;
import net.lenni0451.optconfig.provider.ConfigProvider;
import net.raphimc.viaproxy.util.logging.Logger;

import java.io.File;

@OptConfig
public class Messages {

    private static final StringFormat LEGACY_FORMAT = StringFormat.vanilla();

    static {
        try {
            File dataFolder = Main.getInstance().getDataFolder();
            dataFolder.mkdirs();
            ConfigLoader<Messages> loader = new ConfigLoader<>(Messages.class);
            loader.getConfigOptions().setAddMissingOptions(true)
                    .setRewriteConfig(true)
                    .setRemoveUnknownOptions(true)
                    .setResetInvalidOptions(true);
            loader.loadStatic(ConfigProvider.file(new File(Main.getInstance().getDataFolder(), "messages.yml")));
        } catch (Throwable t) {
            Logger.LOGGER.error("Failed to load messages.yml. Falling back to default messages.", t);
        }
    }

    public static TextComponent[] format(final String message, final Object... args) {
        String[] lines = message.split("\n");
        TextComponent[] components = new TextComponent[lines.length];
        for (int i = 0; i < lines.length; i++) {
            components[i] = LEGACY_FORMAT.fromString(lines[i], ColorHandling.RESET, DeserializerUnknownHandling.IGNORE);
            components[i] = TextUtils.replace(components[i], "\\{\\d+\\}", original -> {
                String argIndexStr = original.asUnformattedString();
                int argIndex = Integer.parseInt(argIndexStr.substring(1, argIndexStr.length() - 1));
                if (argIndex >= 0 && argIndex < args.length) {
                    Object arg = args[argIndex];
                    TextComponent argComponent;
                    if (arg instanceof TextComponent) {
                        argComponent = (TextComponent) arg;
                    } else {
                        argComponent = TextComponent.of(String.valueOf(arg));
                    }
                    return argComponent.setStyle(original.getStyle().copy());
                } else {
                    return original;
                }
            });
        }
        return components;
    }


    @Section(name = "MainScreen")
    public static class MainScreen {
        @Option
        public static String Title = "§aMiniConnect";

        @Section(name = "SetServerAddress")
        public static class SetServerAddress {
            @Option
            public static String ItemName = "§aSet server address";
            @Option
            public static String ItemLore = "§bClick to set the server address to connect to";
            @Option
            @Description({"{0} = server address", "Also used for the lore of the connect item"})
            public static String ItemLoreAddressSet = "§aAddress: §6{0}";
            @Option
            public static String ItemLoreNoAddressSet = "§cNo address set (required)";
            @Option
            public static String ChatInfo = "§aPlease enter the server ip into the chat (with optional port) (e.g. example.com, example.com:25565)";
            @Option
            public static String ChatCancelled = "§cCancelled input";
            @Option
            public static String ChatInvalidAddress = "§cInvalid server address";
        }

        @Section(name = "SetProtocolVersion")
        public static class SetProtocolVersion {
            @Option
            public static String ItemName = "§aSet protocol version";
            @Option
            public static String ItemLore = "§bClick to set the protocol version to connect with";
            @Option
            @Description({"{0} = protocol version", "Also used for the lore of the connect item"})
            public static String ItemLoreVersionSet = "§aVersion: §6{0}";
            @Option
            public static String ItemLoreNoVersionSet = "§cNo version set (required)";
        }

        @Section(name = "Login")
        public static class Login {
            @Option
            public static String ItemName = "§aLogin";
            @Option
            public static String ItemLore = "§bClick to login with your Microsoft account";
            @Option
            @Description({"{0} = username", "Also used for the lore of the connect item"})
            public static String ItemLoreLoggedIn = "§aLogged in as: §6{0}";
            @Option
            public static String ItemLoreNotLoggedIn = "§cNot logged in";
            @Option
            public static String ChatLoading = "§aLoading, please wait...";
            @Option
            @Description({"{0} = login url", "{1} = login code"})
            public static String ChatCodeLogin = """
                    §ePlease open your browser and visit §9{0}§e and login with your Microsoft account
                    §bIf the code is not inserted automatically, please enter the code: §a{1}""";
            @Option
            public static String ChatLoginSuccess = "§aSuccessfully logged in";
            @Option
            @Description("{0} = error message")
            public static String ChatLoginFailed = "§cLogin failed: {0}";
        }

        @Section(name = "ConnectToServer")
        public static class ConnectToServer {
            @Option
            public static String ItemName = "§aConnect to server";
            @Option
            public static String ItemLore = "§bClick to connect to the server";
            @Option
            public static String ItemLoreNoAddress = "§cNo address set (required)";
            @Option
            public static String ItemLoreNoVersion = "§cNo version set (required)";
            @Option
            public static String ItemLoreMissingRequirements = "§cYou need to set all options before connecting";
        }

        @Section(name = "ProxyOnlineMode")
        public static class ProxyOnlineMode {
            @Option
            public static String ItemNameEnabled = "§aViaProxy online mode §a§lEnabled";
            @Option
            public static String ItemLoreEnabled = """
                    §bOnline mode is enabled
                    §bAll settings are saved between sessions
                    §6Click again to disable online mode""";
            @Option
            public static String ItemNameDisabled = "§aViaProxy online mode §c§lDisabled";
            @Option
            public static String ItemLoreDisabled = """
                    §bRequire a premium account to join ViaProxy
                    §bYou need to reconnect with your account for verification
                    §6If enabled, your settings will be saved between sessions""";
            @Option
            public static String DisconnectMessage = """
                    §aIn order to verify your online mode status, please reconnect within 60 seconds.
                    §aIf you connect with a premium account, storage persistence will be enabled.
                    §aIf you fail the verification, your account will stay in offline mode.
                    §aAfter enabling online mode, you can disable it at any time by clicking the item again.""";
        }

        @Section(name = "HowToUse")
        public static class HowToUse {
            @Option
            public static String ItemName = "§6How to use";
            @Option
            public static String ChatReopenInfo = "§6§lType anything in chat to open the UI again";
        }

        @Section(name = "Disconnect")
        public static class Disconnect {
            @Option
            public static String ItemName = "§cDisconnect";
            @Option
            public static String DisconnectMessage = "Manual Disconnect";
        }
    }

    @Section(name = "VersionSelectorScreen")
    public static class VersionSelectorScreen {
        @Option
        public static String Title = "§aSelect Version";
        @Option
        public static String PreviousPage = "§aPrevious Page";
        @Option
        public static String Back = "§cBack";
        @Option
        public static String NextPage = "§aNext Page";
    }

    @Section(name = "TutorialScreen")
    public static class TutorialScreen {
        @Option
        public static String Title = "MiniConnect Tutorial";
    }

    @Section(name = "Tutorial")
    public static class Tutorial {
        @Option
        public static String Introduction = """
                §6§l§oMiniConnect
                
                Using MiniConnect you can connect to any Minecraft server with any version.
                Check out the following pages to learn how to use MiniConnect.""";
        @Option
        public static String ServerAddress = """
                §6§l§oServer address
                
                Click on §2Set server address§r (name tag) and enter the server address in the chat.
                Example: §2example.com§r or §2example.com:25565§r
                If no port is specified, the default port is used.""";
        @Option
        public static String ServerVersion = """
                §6§l§oServer version
                
                Click on §2Set protocol version§r (anvil) and select the desired version.
                §1Crafting table -> release version
                §2Furnace -> beta version
                §3Dirt -> alpha version
                §4Bedrock -> bedrock edition""";
        @Option
        public static String Login = """
                §6§l§oLogin
                
                Click on §2Login§r (key) to log in with your Minecraft account.
                Click on the URL in the chat and complete the login process.
                After disconnecting, the account will be logged out.""";
        @Option
        public static String ProxyOnlineMode = """
                §6§l§oProxy online mode
                
                If you are using a premium account for joining MiniConnect, you can enable the proxy online mode.
                When enabled, MiniConnect settings will be saved between sessions.""";
        @Option
        public static String Connect = """
                §6§l§oConnect
                
                After setting all required options (server address, version) you can connect by clicking on §2Connect to server§r.""";
        @Option
        public static String Disconnect = """
                §6§l§oDisconnect
                
                When on a server, you can type §2/disconnect§r in the chat to disconnect.
                After disconnecting, you will automatically be placed into the lobby again.
                All your settings will be retained until you disconnect.""";
        @Option
        public static String WildcardDomains = """
                §6§l§oWildcard domains
                
                MiniConnect supports the same wildcard domain format as ViaProxy itself.
                Example: §2example.com_25565_1.8.viaproxy.example.com
                This autofills the server address and version automatically.""";
    }

}
