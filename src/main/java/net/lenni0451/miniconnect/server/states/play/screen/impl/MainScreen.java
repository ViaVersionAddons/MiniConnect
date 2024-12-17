package net.lenni0451.miniconnect.server.states.play.screen.impl;

import com.google.common.net.HostAndPort;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.PlatformTask;
import net.lenni0451.mcstructs.core.TextFormatting;
import net.lenni0451.mcstructs.text.ATextComponent;
import net.lenni0451.mcstructs.text.components.StringComponent;
import net.lenni0451.mcstructs.text.events.click.ClickEvent;
import net.lenni0451.mcstructs.text.events.click.ClickEventAction;
import net.lenni0451.miniconnect.Main;
import net.lenni0451.miniconnect.server.model.PlayerConfig;
import net.lenni0451.miniconnect.server.protocol.packets.play.s2c.S2CSystemChatPacket;
import net.lenni0451.miniconnect.server.protocol.packets.play.s2c.S2CTransferPacket;
import net.lenni0451.miniconnect.server.states.play.screen.ItemList;
import net.lenni0451.miniconnect.server.states.play.screen.Items;
import net.lenni0451.miniconnect.server.states.play.screen.Screen;
import net.lenni0451.miniconnect.server.states.play.screen.ScreenHandler;
import net.lenni0451.miniconnect.utils.ChannelUtils;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode;
import net.raphimc.netminecraft.packet.impl.play.S2CPlayDisconnectPacket;
import net.raphimc.viaproxy.saves.impl.accounts.MicrosoftAccount;

import java.util.concurrent.TimeoutException;

import static net.lenni0451.miniconnect.server.states.play.screen.ItemBuilder.item;

public class MainScreen extends Screen {

    public MainScreen() {
        super(new StringComponent("§aMiniConnect"), 3);
    }

    @Override
    public void init(ScreenHandler screenHandler, ItemList itemList) {
        PlayerConfig playerConfig = screenHandler.getStateHandler().getHandler().getPlayerConfig();
        boolean hasAddress = playerConfig.serverAddress != null;
        boolean hasVersion = playerConfig.targetVersion != null;
        boolean hasAccount = playerConfig.account != null;

        itemList.set(10, item(Items.NAMETAG).named(new StringComponent("§aSet server address")).setGlint(hasAddress).calculate(builder -> {
            builder.lore(new StringComponent("§bClick to set the server address to connect to"));
            if (hasAddress) {
                builder.lore(new StringComponent("§aAddress: §6" + playerConfig.serverAddress + (playerConfig.serverPort == null || playerConfig.serverPort == -1 ? "" : (":" + playerConfig.serverPort))));
            } else {
                builder.lore(new StringComponent("§cNo address set (required)"));
            }
        }).get(), () -> {
            screenHandler.closeScreen();
            screenHandler.getStateHandler().send(new S2CSystemChatPacket(new StringComponent("§aPlease enter the server ip into the chat (with optional port) (e.g. example.com, example.com:25565"), false));
            playerConfig.chatListener = s -> {
                try {
                    HostAndPort hostAndPort = HostAndPort.fromString(s);
                    if (hostAndPort.getHost().isBlank()) throw new IllegalStateException();
                    playerConfig.serverAddress = hostAndPort.getHost();
                    playerConfig.serverPort = hostAndPort.getPortOrDefault(-1);
                } catch (Throwable t) {
                    screenHandler.getStateHandler().send(new S2CSystemChatPacket(new StringComponent("§cInvalid server address"), false));
                    return false;
                }
                screenHandler.openScreen(this);
                return true;
            };
        });
        itemList.set(11, item(Items.WRITABLE_BOOK).named(new StringComponent("§aSet protocol version")).setGlint(hasVersion).calculate(builder -> {
            builder.lore(new StringComponent("§bClick to set the protocol version to connect with"));
            if (hasVersion) {
                builder.lore(new StringComponent("§aVersion: §6" + playerConfig.targetVersion.getName()));
            } else {
                builder.lore(new StringComponent("§cNo version set (required)"));
            }
        }).get(), () -> {
            screenHandler.openScreen(new VersionSelectorScreen(0));
        });
        itemList.set(12, item(Items.TRIAL_KEY).named(new StringComponent("§aLogin")).setGlint(hasAccount).calculate(builder -> {
            builder.lore(new StringComponent("§bClick to login with your Microsoft account"));
            if (hasAccount) {
                builder.lore(new StringComponent("§aLogged in as: §6" + playerConfig.account.getDisplayString()));
            } else {
                builder.lore(new StringComponent("§cNot logged in"));
            }
        }).get(), () -> {
            screenHandler.closeScreen();
            screenHandler.getStateHandler().send(new S2CSystemChatPacket(new StringComponent("§aLoading, please wait..."), false));
            PlatformTask<?> task = Via.getPlatform().runAsync(() -> {
                try {
                    playerConfig.account = new MicrosoftAccount(MicrosoftAccount.DEVICE_CODE_LOGIN.getFromInput(MinecraftAuth.createHttpClient(), new StepMsaDeviceCode.MsaDeviceCodeCallback(code -> {
                        ATextComponent component = new StringComponent("Please open your browser and visit ").modifyStyle(style -> style.setFormatting(TextFormatting.YELLOW));
                        component.append(new StringComponent(code.getDirectVerificationUri()).modifyStyle(style -> style.setFormatting(TextFormatting.BLUE).setClickEvent(new ClickEvent(ClickEventAction.OPEN_URL, code.getDirectVerificationUri()))));
                        component.append(new StringComponent(" and login with your Microsoft account"));
                        screenHandler.getStateHandler().send(new S2CSystemChatPacket(component, false));
                        screenHandler.getStateHandler().send(new S2CSystemChatPacket(new StringComponent("§bIf the code is not inserted automatically, please enter the code: §a" + code.getUserCode()), false));
                    })));
                    screenHandler.getStateHandler().send(new S2CSystemChatPacket(new StringComponent("§aSuccessfully logged in"), false));
                } catch (InterruptedException e) {
                    return;
                } catch (Throwable t) {
                    if (!(t instanceof TimeoutException)) {
                        t.printStackTrace();
                    }
                    screenHandler.getStateHandler().send(new S2CSystemChatPacket(new StringComponent("§cLogin failed: " + t.getMessage()), false));
                }
                screenHandler.openScreen(this);
            });
            screenHandler.getStateHandler().getChannel().closeFuture().addListener(future -> task.cancel());
        });
//        itemList.set(13, item(Items.LEVER).named(new StringComponent("§aSettings")).get(), () -> {
//            //TODO
//        });
        itemList.set(15, item(Items.OAK_DOOR).named(new StringComponent("§a§lConnect to server")).setGlint(hasAddress && hasVersion).calculate(builder -> {
            builder.lore(new StringComponent("§bClick to connect to the server"));
            if (!hasAddress) builder.lore(new StringComponent("§cNo address set (required)"));
            if (!hasVersion) builder.lore(new StringComponent("§cNo version set (required)"));
            if (!hasAddress || !hasVersion) {
                builder.lore(new StringComponent("§cYou need to set all options before connecting"));
                return;
            }
            builder.lore(new StringComponent("§aAddress: §6" + playerConfig.serverAddress + (playerConfig.serverPort == null || playerConfig.serverPort == -1 ? "" : (":" + playerConfig.serverPort))));
            builder.lore(new StringComponent("§aVersion: §6" + playerConfig.targetVersion.getName()));
            if (hasAccount) builder.lore(new StringComponent("§aLogged in as: §6" + playerConfig.account.getDisplayString()));
        }).get(), () -> {
            if (hasAddress && hasVersion) {
                Main.getInstance().getStateRegistry().getConnectionTargets().put(
                        ChannelUtils.getChannelAddress(screenHandler.getStateHandler().getChannel()),
                        playerConfig.toConnectionInfo()
                );
                screenHandler.getStateHandler().send(new S2CTransferPacket(playerConfig.handshakeAddress, playerConfig.handshakePort));
            } else {
                screenHandler.getStateHandler().send(new S2CSystemChatPacket(new StringComponent("§cYou need to set all options before connecting"), false));
            }
        });
        itemList.set(26, item(Items.BARRIER).named(new StringComponent("§cDisconnect")).get(), () -> {
            screenHandler.getStateHandler().sendAndClose(new S2CPlayDisconnectPacket(new StringComponent("Manual Disconnect")));
        });
    }

    @Override
    public void close(ScreenHandler screenHandler) {
        //Count closing the screen as a disconnect
        screenHandler.getStateHandler().sendAndClose(new S2CPlayDisconnectPacket(new StringComponent("Manual Disconnect")));
    }

}
