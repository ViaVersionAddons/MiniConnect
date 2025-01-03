# MiniConnect
A miniature recreation of the old ViaProxyConnect server as a ViaProxy plugin.

## History
The original ViaProxyConnect was shut down after the public release of ViaProxy because of various reasons.\
ViaProxyConnect was based on BungeeCord reconnecting the player to the lobby or target server.
Hosting it required 3 servers (BungeeCord, Lobby Server, ViaProxy) running at the same time and linking them together.\
The original plugin used injection to modify the code of BungeeCord to make it work for this purpose.
This meant that the plugin needed to be updated for every new BungeeCord version.
On top of that it also added 1.7 support to BungeeCord which made it even more difficult to maintain.

This is a recreation of the most important features of ViaProxyConnect as a single plugin for ViaProxy.
It removes the need for multiple servers and automatically sets everything up for you.\
Since ViaProxy is now responsible for the connection handling and version translation, this plugin is much easier to maintain and should theoretically never break (unless ViaProxy changes something).

## Installation
Download the latest release from the [releases page](https://github.com/ViaVersionAddons/MiniConnect/releases) and put it into the `plugins` folder of your ViaProxy server.\
<b>After installing the plugin, ViaProxy will no longer function as a normal proxy server! The plugin will also change some config options of ViaProxy to make it work properly.</b>\
This can always be reverted by removing the plugin from the `plugins` folder and changing the config options back to their original values.\
The plugin itself does not have any configuration options.

## Usage
After joining the server, a GUI with settings will open:\
![alt text](images/mainui.png)
1. **Set server address**: Enter the address of the server you want to connect to into the chat.
2. **Set protocol version**: Set the protocol version of the server you want to connect to.
3. **Login** (optional): Login with your Minecraft account to join premium servers.
4. **Connect to server**: Connect to the server after setting all options.
5. **How to use**: A small usage guide.
6. **Disconnect**: Disconnect from the lobby (same as closing the GUI).

After setting all required option (server address and protocol version), click on the `Connect to server` button to connect to the server.\
If you disconnect from the server, all settings will be reset and you will have to set them again.

When connected to a server, you can return to the lobby by typing the `/disconnect` command in the chat.
This will retain all settings and you can immediately connect to another server.
