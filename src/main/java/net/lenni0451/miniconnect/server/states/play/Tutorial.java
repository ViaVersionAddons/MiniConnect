package net.lenni0451.miniconnect.server.states.play;

import com.viaversion.viaversion.api.minecraft.item.data.FilterableComponent;
import net.lenni0451.mcstructs.text.components.StringComponent;
import net.lenni0451.miniconnect.server.protocol.ProtocolConstants;
import net.lenni0451.miniconnect.utils.ViaUtils;

import java.util.ArrayList;
import java.util.List;

public class Tutorial {

    public static final FilterableComponent[] TEXT = buildTutorial(
            """
                    §6§l§oMiniConnect
                    
                    Using MiniConnect you can connect to any Minecraft server with any version.
                    Check out the following pages to learn how to use MiniConnect.
                    """,
            """
                    §6§l§oServer address
                    
                    Click on §2Set server address§r (name tag) and enter the server address in the chat.
                    Example: §2example.com§r or §2example.com:25565§r
                    If no port is specified, the default port is used.
                    """,
            """
                    §6§l§oServer version
                    
                    Click on §2Set protocol version§r (anvil) and select the desired version.
                    §1Crafting table -> release version
                    §2Furnace -> beta version
                    §3Dirt -> alpha version
                    §4Bedrock -> bedrock edition
                    """,
            """
                    §6§l§oLogin
                    
                    Click on §2Login§r (key) to log in with your Minecraft account.
                    Click on the URL in the chat and complete the login process.
                    After disconnecting, the account will be logged out.
                    """,
            """
                    §6§l§oProxy online mode
                    
                    If you are using a premium account for joining MiniConnect, you can enable the proxy online mode.
                    When enabled, MiniConnect settings will be saved between sessions.""",
            """
                    §6§l§oConnect
                    
                    After setting all required options (server address, version) you can connect by clicking on §2Connect to server§r.""",
            """
                    §6§l§oDisconnect
                    
                    When on a server, you can type §2/disconnect§r in the chat to disconnect.
                    After disconnecting, you will automatically be placed into the lobby again.
                    All your settings will be retained until you disconnect.
                    """,
            """
                    §6§l§oWildcard domains
                    
                    MiniConnect supports the same wildcard domain format as ViaProxy itself.
                    Example: §2example.com_25565_1.8.viaproxy.example.com
                    This autofills the server address and version automatically."""
    );

    private static FilterableComponent[] buildTutorial(final String... pages) {
        List<FilterableComponent> components = new ArrayList<>();
        for (String page : pages) {
            String[] lines = page.trim().split("\n");
            StringComponent base = new StringComponent();
            for (int i = 0; i < lines.length; i++) {
                if (i != 0) base.append("\n");
                base.append(lines[i]);
            }
            components.add(new FilterableComponent(ViaUtils.convertNbt(ProtocolConstants.TEXT_CODEC.serializeNbtTree(base)), null));
        }
        return components.toArray(new FilterableComponent[0]);
    }

}
