package net.lenni0451.miniconnect.server.states.play;

import com.viaversion.viaversion.api.minecraft.item.data.FilterableComponent;
import net.lenni0451.mcstructs.text.components.StringComponent;
import net.lenni0451.mcstructs.text.stringformat.StringFormat;
import net.lenni0451.mcstructs.text.stringformat.handling.ColorHandling;
import net.lenni0451.mcstructs.text.stringformat.handling.DeserializerUnknownHandling;
import net.lenni0451.miniconnect.server.protocol.ProtocolConstants;
import net.lenni0451.miniconnect.server.states.play.screen.Messages;
import net.lenni0451.miniconnect.utils.ViaUtils;

import java.util.ArrayList;
import java.util.List;

public class Tutorial {

    private static final StringFormat LEGACY_FORMAT = StringFormat.vanilla();
    public static final FilterableComponent[] TEXT = buildTutorial(
            Messages.Tutorial.Introduction,
            Messages.Tutorial.ServerAddress,
            Messages.Tutorial.ServerVersion,
            Messages.Tutorial.Login,
            Messages.Tutorial.ProxyOnlineMode,
            Messages.Tutorial.Connect,
            Messages.Tutorial.Disconnect,
            Messages.Tutorial.WildcardDomains
    );

    private static FilterableComponent[] buildTutorial(final String... pages) {
        List<FilterableComponent> components = new ArrayList<>();
        for (String page : pages) {
            String[] lines = page.trim().split("\n");
            StringComponent base = new StringComponent();
            for (int i = 0; i < lines.length; i++) {
                if (i != 0) base.append("\n");
                base.append(LEGACY_FORMAT.fromString(lines[i], ColorHandling.RESET, DeserializerUnknownHandling.IGNORE));
            }
            components.add(new FilterableComponent(ViaUtils.convertNbt(ProtocolConstants.TEXT_CODEC.serializeNbtTree(base)), null));
        }
        return components.toArray(new FilterableComponent[0]);
    }

}
