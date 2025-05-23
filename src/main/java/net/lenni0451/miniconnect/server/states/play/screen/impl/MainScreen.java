package net.lenni0451.miniconnect.server.states.play.screen.impl;

import com.google.common.net.HostAndPort;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.data.StructuredDataContainer;
import com.viaversion.viaversion.api.minecraft.data.StructuredDataKey;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.item.StructuredItem;
import com.viaversion.viaversion.api.minecraft.item.data.FilterableString;
import com.viaversion.viaversion.api.minecraft.item.data.WrittenBook;
import com.viaversion.viaversion.api.platform.PlatformTask;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.lenni0451.mcstructs.text.TextComponent;
import net.lenni0451.mcstructs.text.TextFormatting;
import net.lenni0451.mcstructs.text.components.StringComponent;
import net.lenni0451.mcstructs.text.events.click.types.OpenUrlClickEvent;
import net.lenni0451.miniconnect.Main;
import net.lenni0451.miniconnect.server.model.PlayerConfig;
import net.lenni0451.miniconnect.server.protocol.ProtocolConstants;
import net.lenni0451.miniconnect.server.protocol.packets.play.s2c.S2CContainerSetContentPacket;
import net.lenni0451.miniconnect.server.protocol.packets.play.s2c.S2COpenBookPacket;
import net.lenni0451.miniconnect.server.protocol.packets.play.s2c.S2CSystemChatPacket;
import net.lenni0451.miniconnect.server.protocol.packets.play.s2c.S2CTransferPacket;
import net.lenni0451.miniconnect.server.states.play.Tutorial;
import net.lenni0451.miniconnect.server.states.play.screen.ItemList;
import net.lenni0451.miniconnect.server.states.play.screen.Items;
import net.lenni0451.miniconnect.server.states.play.screen.Screen;
import net.lenni0451.miniconnect.server.states.play.screen.ScreenHandler;
import net.lenni0451.miniconnect.utils.ChannelUtils;
import net.lenni0451.miniconnect.utils.InetUtils;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode;
import net.raphimc.netminecraft.packet.impl.play.S2CPlayDisconnectPacket;
import net.raphimc.viaproxy.saves.impl.accounts.MicrosoftAccount;
import net.raphimc.viaproxy.util.logging.Logger;

import java.net.InetAddress;
import java.util.concurrent.TimeoutException;

import static net.lenni0451.miniconnect.server.states.play.screen.ItemBuilder.item;

public class MainScreen extends Screen {

    public MainScreen() {
        super(new StringComponent("§aMiniConnect"), 4);
    }

    @Override
    public void init(ScreenHandler screenHandler, ItemList itemList) {
        PlayerConfig playerConfig = screenHandler.getStateHandler().getHandler().getPlayerConfig();
        boolean hasAddress = playerConfig.serverAddress != null;
        boolean hasVersion = playerConfig.targetVersion != null;
        boolean hasAccount = playerConfig.account != null;

        itemList.set(11, item(Items.NAMETAG).named(new StringComponent("§aSet server address")).setGlint(hasAddress).calculate(builder -> {
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
                if (s.startsWith("/")) {
                    screenHandler.getStateHandler().send(new S2CSystemChatPacket(new StringComponent("§cCancelled input"), false));
                } else {
                    try {
                        HostAndPort hostAndPort = HostAndPort.fromString(s);
                        if (hostAndPort.getHost().isBlank()) throw new IllegalArgumentException();
                        if (InetUtils.isLocal(InetAddress.getByName(hostAndPort.getHost()))) throw new IllegalArgumentException();
                        playerConfig.serverAddress = hostAndPort.getHost();
                        playerConfig.serverPort = hostAndPort.getPortOrDefault(-1);
                    } catch (Throwable t) {
                        screenHandler.getStateHandler().send(new S2CSystemChatPacket(new StringComponent("§cInvalid server address"), false));
                        return false;
                    }
                }
                screenHandler.openScreen(this);
                return true;
            };
        });
        itemList.set(12, item(Items.ANVIL).named(new StringComponent("§aSet protocol version")).setGlint(hasVersion).calculate(builder -> {
            builder.lore(new StringComponent("§bClick to set the protocol version to connect with"));
            if (hasVersion) {
                builder.lore(new StringComponent("§aVersion: §6" + playerConfig.targetVersion.getName()));
            } else {
                builder.lore(new StringComponent("§cNo version set (required)"));
            }
        }).get(), () -> {
            screenHandler.openScreen(new VersionSelectorScreen(0));
        });
        itemList.set(13, item(Items.TRIAL_KEY).named(new StringComponent("§aLogin")).setGlint(hasAccount).calculate(builder -> {
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
                        TextComponent component = new StringComponent("Please open your browser and visit ").styled(style -> style.setFormatting(TextFormatting.YELLOW));
                        component.append(new StringComponent(code.getDirectVerificationUri()).styled(style -> style.setFormatting(TextFormatting.BLUE).setClickEvent(new OpenUrlClickEvent(code.getDirectVerificationUri()))));
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
        itemList.set(15, item(Items.OAK_DOOR).named(new StringComponent("§aConnect to server")).setGlint(hasAddress && hasVersion).calculate(builder -> {
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
                if (playerConfig.isSaved) {
                    try {
                        playerConfig.save();
                    } catch (Exception e) {
                        Logger.LOGGER.error("Failed to save player config", e);
                    }
                }
                InetAddress channelAddress = ChannelUtils.getChannelAddress(screenHandler.getStateHandler().getChannel());
                Main.getInstance().getStateRegistry().getConnectionTargets().put(channelAddress, playerConfig.toConnectionInfo());
                Main.getInstance().getStateRegistry().getChangeHandshakeIntent().add(channelAddress);
                screenHandler.getStateHandler().send(new S2CTransferPacket(playerConfig.handshakeAddress, playerConfig.handshakePort));
            } else {
                screenHandler.getStateHandler().send(new S2CSystemChatPacket(new StringComponent("§cYou need to set all options before connecting"), false));
            }
        });
        itemList.set(20, item(Items.ENDER_CHEST).setGlint(playerConfig.isSaved).calculate(builder -> {
            if (playerConfig.isSaved) {
                builder.named(new StringComponent("§aViaProxy online mode §a§lEnabled"));
                builder.lore(new StringComponent("§bOnline mode is enabled"));
                builder.lore(new StringComponent("§bAll settings are saved between sessions"));
                builder.lore(new StringComponent("§6Click again to disable online mode"));
            } else {
                builder.named(new StringComponent("§aViaProxy online mode §c§lDisabled"));
                builder.lore(new StringComponent("§bRequire a premium account to join ViaProxy"));
                builder.lore(new StringComponent("§bYou need to reconnect with your account for verification"));
                builder.lore(new StringComponent("§6If enabled, your settings will be saved between sessions"));
            }
        }).get(), () -> {
            if (playerConfig.isSaved) {
                playerConfig.delete();
                screenHandler.openScreen(new MainScreen());
            } else {
                Main.getInstance().getStateRegistry().getVerificationQueue().add(playerConfig.uuid);
                screenHandler.getStateHandler().sendAndClose(new S2CPlayDisconnectPacket(new StringComponent("""
                        §aIn order to verify your online mode status, please reconnect within 60 seconds.
                        §aIf you connect with a premium account, storage persistence will be enabled.
                        §aIf you fail the verification, your account will stay in offline mode.
                        §aAfter enabling online mode, you can disable it at any time by clicking the item again.""")));
            }
        });

        itemList.set(27, item(Items.BOOK).named(new StringComponent("§6How to use")).get(), () -> {
            if (playerConfig.clientVersion.newerThanOrEqualTo(ProtocolVersion.v1_19)) {
                screenHandler.openScreen(new TutorialScreen());
            } else {
                screenHandler.closeScreen();
                screenHandler.getStateHandler().send(new S2CSystemChatPacket(new StringComponent("§6§lType anything in chat to open the UI again"), false));
                StructuredItem book = new StructuredItem(ProtocolConstants.ITEMS.indexOf(Items.WRITTEN_BOOK), 1, new StructuredDataContainer());
                book.dataContainer().setIdLookup(ProtocolConstants.VIA_PROTOCOL, false);
                book.dataContainer().set(StructuredDataKey.WRITTEN_BOOK_CONTENT, new WrittenBook(
                        new FilterableString("How to use MiniConnect", null),
                        "MiniConnect",
                        0,
                        Tutorial.TEXT,
                        true
                ));
                Item[] items = StructuredItem.emptyArray(45);
                for (int i = 36; i < 45; i++) items[i] = book;
                screenHandler.getStateHandler().send(new S2CContainerSetContentPacket(0, 0, items, StructuredItem.empty()));
                screenHandler.getStateHandler().send(new S2COpenBookPacket(0));
                screenHandler.getStateHandler().send(new S2CContainerSetContentPacket(0, 0, StructuredItem.emptyArray(45), StructuredItem.empty()));
                playerConfig.chatListener = s -> {
                    screenHandler.openScreen(new MainScreen());
                    return true;
                };
            }
        });
        itemList.set(35, item(Items.BARRIER).named(new StringComponent("§cDisconnect")).get(), () -> {
            screenHandler.getStateHandler().sendAndClose(new S2CPlayDisconnectPacket(new StringComponent("Manual Disconnect")));
        });
    }

    @Override
    public void close(ScreenHandler screenHandler) {
        //Count closing the screen as a disconnect
        screenHandler.getStateHandler().sendAndClose(new S2CPlayDisconnectPacket(new StringComponent("Manual Disconnect")));
    }

}
