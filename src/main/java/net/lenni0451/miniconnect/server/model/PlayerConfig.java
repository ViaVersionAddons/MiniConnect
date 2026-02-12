package net.lenni0451.miniconnect.server.model;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.lenni0451.commons.gson.GsonParser;
import net.lenni0451.commons.gson.elements.GsonObject;
import net.lenni0451.miniconnect.Main;
import net.lenni0451.miniconnect.model.ConnectionInfo;
import net.lenni0451.miniconnect.utils.AESEncryption;
import net.lenni0451.miniconnect.utils.UUIDUtils;
import net.raphimc.viaproxy.saves.impl.accounts.Account;
import net.raphimc.viaproxy.saves.impl.accounts.MicrosoftAccount;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;
import java.util.function.Function;

public class PlayerConfig {

    private static final AESEncryption ENCRYPTION = new AESEncryption();

    public static File baseDir() {
        return new File(Main.getInstance().getDataFolder(), "online_mode_settings");
    }


    public final UUID uuid;
    @Nullable
    public String serverAddress;
    @Nullable
    public ProtocolVersion targetVersion;
    @Nullable
    public Account account;

    public transient String handshakeAddress;
    public transient int handshakePort;
    public transient ProtocolVersion clientVersion;
    public transient Function<String, Boolean> chatListener;
    public transient boolean isSaved;

    public PlayerConfig(final UUID uuid) {
        this.uuid = uuid;
    }

    public void applyConnectionInfo(final ConnectionInfo connectionInfo) {
        this.serverAddress = connectionInfo.address();
        this.targetVersion = connectionInfo.protocolVersion();
        this.account = connectionInfo.account();
    }

    public ConnectionInfo toConnectionInfo() {
        return new ConnectionInfo(this.handshakeAddress, this.handshakePort, this.serverAddress, this.targetVersion, this.account);
    }

    public void load() throws Exception {
        String hashedUUID = UUIDUtils.hash(this.uuid);
        File settingsFile = new File(PlayerConfig.baseDir(), hashedUUID + ".dat");
        if (!settingsFile.exists()) return;
        this.isSaved = true;

        byte[] key = UUIDUtils.toBytes(this.uuid);
        byte[] data = Files.readAllBytes(settingsFile.toPath());
        byte[] decryptedData = ENCRYPTION.decrypt(key, data);
        String json = new String(decryptedData, StandardCharsets.UTF_8);
        GsonObject object = GsonParser.parse(json).asObject();
        this.serverAddress = object.getString("serverAddress", null);
        this.targetVersion = object.optPrimitive("targetVersion").map(p -> ProtocolVersion.getProtocol(p.asInt())).orElse(null);
        this.account = object.optObject("account").map(GsonObject::getJsonObject).map(MicrosoftAccount::new).orElse(null);
    }

    public void save() throws Exception {
        String hashedUUID = UUIDUtils.hash(this.uuid);
        File settingsFile = new File(PlayerConfig.baseDir(), hashedUUID + ".dat");
        settingsFile.getParentFile().mkdirs();
        this.isSaved = true;

        GsonObject object = new GsonObject();
        if (this.serverAddress != null) object.add("serverAddress", this.serverAddress);
        if (this.targetVersion != null) object.add("targetVersion", this.targetVersion.getOriginalVersion());
        if (this.account != null) object.add("account", this.account.toJson());
        byte[] key = UUIDUtils.toBytes(this.uuid);
        byte[] data = object.toString().getBytes(StandardCharsets.UTF_8);
        byte[] encryptedData = ENCRYPTION.encrypt(key, data);
        Files.write(settingsFile.toPath(), encryptedData);
    }

    public void delete() {
        String hashedUUID = UUIDUtils.hash(this.uuid);
        File settingsFile = new File(PlayerConfig.baseDir(), hashedUUID + ".dat");
        if (settingsFile.exists()) settingsFile.delete();
        this.isSaved = false;
    }

}
