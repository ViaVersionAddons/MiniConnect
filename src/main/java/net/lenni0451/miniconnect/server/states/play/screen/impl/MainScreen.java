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
import net.lenni0451.mcstructs.text.components.StringComponent;
import net.lenni0451.mcstructs.text.events.click.types.OpenUrlClickEvent;
import net.lenni0451.miniconnect.Main;
import net.lenni0451.miniconnect.server.model.PlayerConfig;
import net.lenni0451.miniconnect.server.protocol.ProtocolConstants;
import net.lenni0451.miniconnect.server.protocol.packets.model.CommonPlayerSpawnInfo;
import net.lenni0451.miniconnect.server.protocol.packets.play.s2c.*;
import net.lenni0451.miniconnect.server.states.play.Tutorial;
import net.lenni0451.miniconnect.server.states.play.screen.*;
import net.lenni0451.miniconnect.utils.ChannelUtils;
import net.lenni0451.miniconnect.utils.GeyserAPI;
import net.lenni0451.miniconnect.utils.InetUtils;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.java.JavaAuthManager;
import net.raphimc.minecraftauth.msa.model.MsaDeviceCode;
import net.raphimc.minecraftauth.msa.service.impl.DeviceCodeMsaAuthService;
import net.raphimc.netminecraft.packet.impl.play.S2CPlayDisconnectPacket;
import net.raphimc.viaproxy.saves.impl.accounts.MicrosoftAccount;
import net.raphimc.viaproxy.util.logging.Logger;

import java.net.InetAddress;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static net.lenni0451.miniconnect.server.states.play.screen.ItemBuilder.item;

public class MainScreen extends Screen {

    public MainScreen() {
        super(new StringComponent(Messages.MainScreen.Title), 4);
    }

    @Override
    public void init(ScreenHandler screenHandler, ItemList itemList) {
        PlayerConfig playerConfig = screenHandler.getStateHandler().getHandler().getPlayerConfig();
        boolean hasAddress = playerConfig.serverAddress != null;
        boolean hasVersion = playerConfig.targetVersion != null;
        boolean hasAccount = playerConfig.account != null;

        itemList.set(11, item(Items.NAMETAG).named(new StringComponent(Messages.MainScreen.SetServerAddress.ItemName)).setGlint(hasAddress).calculate(builder -> {
            builder.lore(Messages.format(Messages.MainScreen.SetServerAddress.ItemLore));
            if (hasAddress) {
                String address = playerConfig.serverAddress + (playerConfig.serverPort == null || playerConfig.serverPort == -1 ? "" : (":" + playerConfig.serverPort));
                builder.lore(Messages.format(Messages.MainScreen.SetServerAddress.ItemLoreAddressSet, address));
            } else {
                builder.lore(Messages.format(Messages.MainScreen.SetServerAddress.ItemLoreNoAddressSet));
            }
        }).get(), () -> {
            screenHandler.closeScreen();
            for (TextComponent component : Messages.format(Messages.MainScreen.SetServerAddress.ChatInfo)) {
                screenHandler.getStateHandler().send(new S2CSystemChatPacket(component, false));
            }
            playerConfig.chatListener = s -> {
                if (s.startsWith("/")) {
                    for (TextComponent component : Messages.format(Messages.MainScreen.SetServerAddress.ChatCancelled)) {
                        screenHandler.getStateHandler().send(new S2CSystemChatPacket(component, false));
                    }
                } else {
                    try {
                        HostAndPort hostAndPort = HostAndPort.fromString(s);
                        if (hostAndPort.getHost().isBlank()) throw new IllegalArgumentException();
                        if (InetUtils.isLocal(InetAddress.getByName(hostAndPort.getHost()))) throw new IllegalArgumentException();
                        playerConfig.serverAddress = hostAndPort.getHost();
                        playerConfig.serverPort = hostAndPort.getPortOrDefault(-1);
                        if (GeyserAPI.isGeyserPlayer(playerConfig.uuid)) {
                            //Respawn the player two times to force the chat to close
                            //This only needs to be done because bedrock doesn't allow closing the chat otherwise
                            CommonPlayerSpawnInfo spawnInfo = ProtocolConstants.DEFAULT_SPAWN_INFO.copy();
                            spawnInfo.dimension = 2;
                            spawnInfo.world = "minecraft:the_nether";
                            spawnInfo.previousGamemode = 3;
                            screenHandler.getStateHandler().send(new S2CRespawnPacket(spawnInfo, S2CRespawnPacket.KEEP_ALL_DATA));
                            ProtocolConstants.sendSpawnInfo(screenHandler.getStateHandler());
                            screenHandler.getStateHandler().send(new S2CRespawnPacket(ProtocolConstants.DEFAULT_SPAWN_INFO, S2CRespawnPacket.KEEP_ALL_DATA));
                            ProtocolConstants.sendSpawnInfo(screenHandler.getStateHandler());
                        }
                    } catch (Throwable t) {
                        for (TextComponent component : Messages.format(Messages.MainScreen.SetServerAddress.ChatInvalidAddress)) {
                            screenHandler.getStateHandler().send(new S2CSystemChatPacket(component, false));
                        }
                        return false;
                    }
                }
                screenHandler.openScreen(this);
                return true;
            };
        });
        itemList.set(12, item(Items.ANVIL).named(new StringComponent(Messages.MainScreen.SetProtocolVersion.ItemName)).setGlint(hasVersion).calculate(builder -> {
            builder.lore(Messages.format(Messages.MainScreen.SetProtocolVersion.ItemLore));
            if (hasVersion) {
                builder.lore(Messages.format(Messages.MainScreen.SetProtocolVersion.ItemLoreVersionSet, playerConfig.targetVersion.getName()));
            } else {
                builder.lore(Messages.format(Messages.MainScreen.SetProtocolVersion.ItemLoreNoVersionSet));
            }
        }).get(), () -> {
            screenHandler.openScreen(new VersionSelectorScreen(0));
        });
        itemList.set(13, item(Items.TRIAL_KEY).named(new StringComponent(Messages.MainScreen.Login.ItemName)).setGlint(hasAccount).calculate(builder -> {
            builder.lore(Messages.format(Messages.MainScreen.Login.ItemLore));
            if (hasAccount) {
                builder.lore(Messages.format(Messages.MainScreen.Login.ItemLoreLoggedIn, playerConfig.account.getDisplayString()));
            } else {
                builder.lore(Messages.format(Messages.MainScreen.Login.ItemLoreNotLoggedIn));
            }
        }).get(), () -> {
            screenHandler.closeScreen();
            for (TextComponent component : Messages.format(Messages.MainScreen.Login.ChatLoading)) {
                screenHandler.getStateHandler().send(new S2CSystemChatPacket(component, false));
            }
            PlatformTask<?> task = Via.getPlatform().runAsync(() -> {
                try {
                    playerConfig.account = new MicrosoftAccount(JavaAuthManager.create(MinecraftAuth.createHttpClient()).login(DeviceCodeMsaAuthService::new, (Consumer<MsaDeviceCode>) code -> {
                        for (TextComponent component : Messages.format(
                                Messages.MainScreen.Login.ChatCodeLogin,
                                new StringComponent(code.getDirectVerificationUri()).styled(style -> style.setClickEvent(new OpenUrlClickEvent(code.getDirectVerificationUri()))),
                                code.getUserCode()
                        )) {
                            screenHandler.getStateHandler().send(new S2CSystemChatPacket(component, false));
                        }
                    }));
                    for (TextComponent component : Messages.format(Messages.MainScreen.Login.ChatLoginSuccess)) {
                        screenHandler.getStateHandler().send(new S2CSystemChatPacket(component, false));
                    }
                } catch (InterruptedException e) {
                    return;
                } catch (Throwable t) {
                    if (!(t instanceof TimeoutException)) {
                        t.printStackTrace();
                    }
                    for (TextComponent component : Messages.format(Messages.MainScreen.Login.ChatLoginFailed, t.getMessage())) {
                        screenHandler.getStateHandler().send(new S2CSystemChatPacket(component, false));
                    }
                }
                screenHandler.openScreen(this);
            });
            screenHandler.getStateHandler().getChannel().closeFuture().addListener(future -> task.cancel());
        });
        itemList.set(15, item(Items.OAK_DOOR).named(new StringComponent(Messages.MainScreen.ConnectToServer.ItemName)).setGlint(hasAddress && hasVersion).calculate(builder -> {
            builder.lore(Messages.format(Messages.MainScreen.ConnectToServer.ItemLore));
            if (!hasAddress) builder.lore(Messages.format(Messages.MainScreen.ConnectToServer.ItemLoreNoAddress));
            if (!hasVersion) builder.lore(Messages.format(Messages.MainScreen.ConnectToServer.ItemLoreNoVersion));
            if (!hasAddress || !hasVersion) {
                builder.lore(Messages.format(Messages.MainScreen.ConnectToServer.ItemLoreMissingRequirements));
                return;
            }
            builder.lore(Messages.format(Messages.MainScreen.SetServerAddress.ItemLoreAddressSet, playerConfig.serverAddress + (playerConfig.serverPort == null || playerConfig.serverPort == -1 ? "" : (":" + playerConfig.serverPort))));
            builder.lore(Messages.format(Messages.MainScreen.SetProtocolVersion.ItemLoreVersionSet, playerConfig.targetVersion.getName()));
            if (hasAccount) builder.lore(Messages.format(Messages.MainScreen.Login.ItemLoreLoggedIn, playerConfig.account.getDisplayString()));
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
                for (TextComponent component : Messages.format(Messages.MainScreen.ConnectToServer.ItemLoreMissingRequirements)) {
                    screenHandler.getStateHandler().send(new S2CSystemChatPacket(component, false));
                }
            }
        });
        itemList.set(20, item(Items.ENDER_CHEST).setGlint(playerConfig.isSaved).calculate(builder -> {
            if (playerConfig.isSaved) {
                builder.named(new StringComponent(Messages.MainScreen.ProxyOnlineMode.ItemNameEnabled));
                builder.lore(Messages.format(Messages.MainScreen.ProxyOnlineMode.ItemLoreEnabled));
            } else {
                builder.named(new StringComponent(Messages.MainScreen.ProxyOnlineMode.ItemNameDisabled));
                builder.lore(Messages.format(Messages.MainScreen.ProxyOnlineMode.ItemLoreDisabled));
            }
        }).get(), () -> {
            if (playerConfig.isSaved) {
                playerConfig.delete();
                screenHandler.openScreen(new MainScreen());
            } else {
                Main.getInstance().getStateRegistry().getVerificationQueue().add(playerConfig.uuid);
                screenHandler.getStateHandler().sendAndClose(new S2CPlayDisconnectPacket(new StringComponent(Messages.MainScreen.ProxyOnlineMode.DisconnectMessage)));
            }
        });

        itemList.set(27, item(Items.BOOK).named(new StringComponent(Messages.MainScreen.HowToUse.ItemName)).get(), () -> {
            if (playerConfig.clientVersion.newerThanOrEqualTo(ProtocolVersion.v1_19)) {
                screenHandler.openScreen(new TutorialScreen());
            } else {
                screenHandler.closeScreen();
                for (TextComponent component : Messages.format(Messages.MainScreen.HowToUse.ChatReopenInfo)) {
                    screenHandler.getStateHandler().send(new S2CSystemChatPacket(component, false));
                }
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
        itemList.set(35, item(Items.BARRIER).named(new StringComponent(Messages.MainScreen.Disconnect.ItemName)).get(), () -> {
            screenHandler.getStateHandler().sendAndClose(new S2CPlayDisconnectPacket(new StringComponent(Messages.MainScreen.Disconnect.DisconnectMessage)));
        });
    }

    @Override
    public void close(ScreenHandler screenHandler) {
        //Count closing the screen as a disconnect
        screenHandler.getStateHandler().sendAndClose(new S2CPlayDisconnectPacket(new StringComponent(Messages.MainScreen.Disconnect.DisconnectMessage)));
    }

}
