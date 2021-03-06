/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common.network.internal;

import io.netty.channel.embedded.EmbeddedChannel;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.World;

import org.apache.logging.log4j.core.helpers.Integers;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLContainer;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.FMLOutboundHandler.OutboundTarget;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.handshake.FMLHandshakeMessage;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.common.network.internal.FMLMessage.CompleteHandshake;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FMLNetworkHandler
{
    public static final int READ_TIMEOUT = Integers.parseInt(System.getProperty("fml.readTimeout","30"),30);
    public static final int LOGIN_TIMEOUT = Integers.parseInt(System.getProperty("fml.loginTimeout","600"),600);
    private static EnumMap<Side, EmbeddedChannel> channelPair;

    /*    private static final int FML_HASH = Hashing.murmur3_32().hashString("FML").asInt();
    private static final int PROTOCOL_VERSION = 0x2;
    private static final FMLNetworkHandler INSTANCE = new FMLNetworkHandler();

    // List of states for connections from clients to server
    static final int LOGIN_RECEIVED = 1;
    static final int CONNECTION_VALID = 2;
    static final int FML_OUT_OF_DATE = -1;
    static final int MISSING_MODS_OR_VERSIONS = -2;

    private Map<NetLoginHandler, Integer> loginStates = Maps.newHashMap();
    private Map<ModContainer, NetworkModHandler> networkModHandlers = Maps.newHashMap();

    private Map<Integer, NetworkModHandler> networkIdLookup = Maps.newHashMap();

    public static void handlePacket250Packet(Packet250CustomPayload packet, INetworkManager network, NetHandler handler)
    {
        String target = packet.field_73630_a;

        if (target.startsWith("MC|"))
        {
            handler.handleVanilla250Packet(packet);
        }
        if (target.equals("FML"))
        {
            instance().handleFMLPacket(packet, network, handler);
        }
        else
        {
            NetworkRegistry.instance().handleCustomPacket(packet, network, handler);
        }
    }

    public static void onConnectionEstablishedToServer(NetHandler clientHandler, INetworkManager manager, Packet1Login login)
    {
        NetworkRegistry.instance().clientLoggedIn(clientHandler, manager, login);
    }

    private void handleFMLPacket(Packet250CustomPayload packet, INetworkManager network, NetHandler netHandler)
    {
        FMLPacket pkt = FMLPacket.readPacket(network, packet.field_73629_c);
        // Part of an incomplete multipart packet
        if (pkt == null)
        {
            return;
        }
        String userName = "";
        if (netHandler instanceof NetLoginHandler)
        {
            userName = ((NetLoginHandler) netHandler).field_72543_h;
        }
        else
        {
            EntityPlayer pl = netHandler.getPlayer();
            if (pl != null)
            {
                userName = pl.func_70005_c_();
            }
        }

        pkt.execute(network, this, netHandler, userName);
    }

    public static void onConnectionReceivedFromClient(NetLoginHandler netLoginHandler, MinecraftServer server, SocketAddress address, String userName)
    {
        instance().handleClientConnection(netLoginHandler, server, address, userName);
    }

    private void handleClientConnection(NetLoginHandler netLoginHandler, MinecraftServer server, SocketAddress address, String userName)
    {
        if (!loginStates.containsKey(netLoginHandler))
        {
            if (handleVanillaLoginKick(netLoginHandler, server, address, userName))
            {
                // No FML on the client
                FMLLog.fine("Connection from %s rejected - no FML packet received from client", userName);
                netLoginHandler.completeConnection("You don't have FML installed, you cannot connect to this server");
                return;
            }
            else
            {
                // Vanilla kicked us for some reason - bye now!
                FMLLog.fine("Connection from %s was closed by vanilla minecraft", userName);
                return;
            }

        }
        switch (loginStates.get(netLoginHandler))
        {
        case LOGIN_RECEIVED:
            // mods can try and kick undesireables here
            String modKick = NetworkRegistry.instance().connectionReceived(netLoginHandler, netLoginHandler.field_72538_b);
            if (modKick != null)
            {
                netLoginHandler.completeConnection(modKick);
                loginStates.remove(netLoginHandler);
                return;
            }
            // The vanilla side wanted to kick
            if (!handleVanillaLoginKick(netLoginHandler, server, address, userName))
            {
                loginStates.remove(netLoginHandler);
                return;
            }
            // Reset the "connection completed" flag so processing can continue
            NetLoginHandler.func_72531_a(netLoginHandler, false);
            // Send the mod list request packet to the client from the server
            netLoginHandler.field_72538_b.func_74429_a(getModListRequestPacket());
            loginStates.put(netLoginHandler, CONNECTION_VALID);
            break;
        case CONNECTION_VALID:
            netLoginHandler.completeConnection(null);
            loginStates.remove(netLoginHandler);
            break;
        case MISSING_MODS_OR_VERSIONS:
            netLoginHandler.completeConnection("The server requires mods that are absent or out of date on your client");
            loginStates.remove(netLoginHandler);
            break;
        case FML_OUT_OF_DATE:
            netLoginHandler.completeConnection("Your client is not running a new enough version of FML to connect to this server");
            loginStates.remove(netLoginHandler);
            break;
        default:
            netLoginHandler.completeConnection("There was a problem during FML negotiation");
            loginStates.remove(netLoginHandler);
            break;
        }
    }

    *//**
     * @param netLoginHandler
     * @param server
     * @param address
     * @param userName
     * @return if the user can carry on
     *//*
    private boolean handleVanillaLoginKick(NetLoginHandler netLoginHandler, MinecraftServer server, SocketAddress address, String userName)
    {
        // Vanilla reasons first
        ServerConfigurationManager playerList = server.func_71203_ab();
        String kickReason = playerList.func_72399_a(address, userName);

        if (kickReason != null)
        {
            netLoginHandler.completeConnection(kickReason);
        }
        return kickReason == null;
    }

    public static void handleLoginPacketOnServer(NetLoginHandler handler, Packet1Login login)
    {
        if (login.field_73561_a == FML_HASH)
        {
            if (login.field_73558_e == PROTOCOL_VERSION)
            {
                FMLLog.finest("Received valid FML login packet from %s", handler.field_72538_b.func_74430_c());
                instance().loginStates.put(handler, LOGIN_RECEIVED);
            }
            else if (login.field_73558_e != PROTOCOL_VERSION)
            {
                FMLLog.finest("Received incorrect FML (%x) login packet from %s", login.field_73558_e, handler.field_72538_b.func_74430_c());
                instance().loginStates.put(handler, FML_OUT_OF_DATE);
            }
        }
        else
        {
            FMLLog.fine("Received invalid login packet (%x, %x) from %s", login.field_73561_a, login.field_73558_e,
                    handler.field_72538_b.func_74430_c());
        }
    }

    static void setHandlerState(NetLoginHandler handler, int state)
    {
        instance().loginStates.put(handler, state);
    }

    public static FMLNetworkHandler instance()
    {
        return INSTANCE;
    }

    public static Packet1Login getFMLFakeLoginPacket()
    {
        // Always reset compat to zero before sending our fake packet
        FMLCommonHandler.instance().getSidedDelegate().setClientCompatibilityLevel((byte) 0);
        Packet1Login fake = new Packet1Login();
        // Hash FML using a simple function
        fake.field_73561_a = FML_HASH;
        // The FML protocol version
        fake.field_73558_e = PROTOCOL_VERSION;
        fake.field_73557_d = EnumGameType.NOT_SET;
        fake.field_73559_b = WorldType.field_77139_a[0];
        return fake;
    }

    public Packet250CustomPayload getModListRequestPacket()
    {
        return PacketDispatcher.getPacket("FML", FMLPacket.makePacket(MOD_LIST_REQUEST));
    }

    public void registerNetworkMod(NetworkModHandler handler)
    {
        networkModHandlers.put(handler.getContainer(), handler);
        networkIdLookup.put(handler.getNetworkId(), handler);
    }
    public boolean registerNetworkMod(ModContainer container, Class<?> networkModClass, ASMDataTable asmData)
    {
        NetworkModHandler handler = new NetworkModHandler(container, networkModClass, asmData);
        if (handler.isNetworkMod())
        {
            registerNetworkMod(handler);
        }

        return handler.isNetworkMod();
    }

    public NetworkModHandler findNetworkModHandler(Object mc)
    {
        if (mc instanceof InjectedModContainer)
        {
            return networkModHandlers.get(((InjectedModContainer)mc).wrappedContainer);
        }
        if (mc instanceof ModContainer)
        {
            return networkModHandlers.get(mc);
        }
        else if (mc instanceof Integer)
        {
            return networkIdLookup.get(mc);
        }
        else
        {
            return networkModHandlers.get(FMLCommonHandler.instance().findContainerFor(mc));
        }
    }

    public Set<ModContainer> getNetworkModList()
    {
        return networkModHandlers.keySet();
    }

    public static void handlePlayerLogin(EntityPlayerMP player, NetServerHandler netHandler, INetworkManager manager)
    {
        NetworkRegistry.instance().playerLoggedIn(player, netHandler, manager);
        GameRegistry.onPlayerLogin(player);
    }

    public Map<Integer, NetworkModHandler> getNetworkIdMap()
    {
        return networkIdLookup;
    }

    public void bindNetworkId(String key, Integer value)
    {
        Map<String, ModContainer> mods = Loader.instance().getIndexedModList();
        NetworkModHandler handler = findNetworkModHandler(mods.get(key));
        if (handler != null)
        {
            handler.setNetworkId(value);
            networkIdLookup.put(value, handler);
        }
    }

    public static void onClientConnectionToRemoteServer(NetHandler netClientHandler, String server, int port, INetworkManager networkManager)
    {
        NetworkRegistry.instance().connectionOpened(netClientHandler, server, port, networkManager);
    }

    public static void onClientConnectionToIntegratedServer(NetHandler netClientHandler, MinecraftServer server, INetworkManager networkManager)
    {
        NetworkRegistry.instance().connectionOpened(netClientHandler, server, networkManager);
    }

    public static void onConnectionClosed(INetworkManager manager, EntityPlayer player)
    {
        NetworkRegistry.instance().connectionClosed(manager, player);
    }


    public static void openGui(EntityPlayer player, Object mod, int modGuiId, World world, int x, int y, int z)
    {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        if (mc == null)
        {
            NetworkModHandler nmh = instance().findNetworkModHandler(mod);
            if (nmh != null)
            {
                mc = nmh.getContainer();
            }
            else
            {
                FMLLog.warning("A mod tried to open a gui on the server without being a NetworkMod");
                return;
            }
        }
        if (player instanceof EntityPlayerMP)
        {
            NetworkRegistry.instance().openRemoteGui(mc, (EntityPlayerMP) player, modGuiId, world, x, y, z);
        }
        else if (FMLCommonHandler.instance().getSide().equals(Side.CLIENT))
        {
            NetworkRegistry.instance().openLocalGui(mc, player, modGuiId, world, x, y, z);
        }
        else
        {
            FMLLog.fine("Invalid attempt to open a local GUI on a dedicated server. This is likely a bug. GUIID: %s,%d", mc.getModId(), modGuiId);
        }
    }

    public static Packet getEntitySpawningPacket(Entity entity)
    {
        EntityRegistration er = EntityRegistry.instance().lookupModSpawn(entity.getClass(), false);
        if (er == null)
        {
            return null;
        }
        if (er.usesVanillaSpawning())
        {
            return null;
        }
        return PacketDispatcher.getPacket("FML", FMLPacket.makePacket(Type.ENTITYSPAWN, er, entity, instance().findNetworkModHandler(er.getContainer())));
    }

    public static void makeEntitySpawnAdjustment(int entityId, EntityPlayerMP player, int serverX, int serverY, int serverZ)
    {
        Packet250CustomPayload pkt = PacketDispatcher.getPacket("FML", FMLPacket.makePacket(Type.ENTITYSPAWNADJUSTMENT, entityId, serverX, serverY, serverZ));
        player.field_71135_a.func_72567_b(pkt);
    }

    public static InetAddress computeLocalHost() throws IOException
    {
        InetAddress add = null;
        List<InetAddress> addresses = Lists.newArrayList();
        InetAddress localHost = InetAddress.getLocalHost();
        for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces()))
        {
            if (!ni.isLoopback() && ni.isUp())
            {
                addresses.addAll(Collections.list(ni.getInetAddresses()));
                if (addresses.contains(localHost))
                {
                    add = localHost;
                    break;
                }
            }
        }
        if (add == null && !addresses.isEmpty())
        {
            for (InetAddress addr: addresses)
            {
                if (addr.getAddress().length == 4)
                {
                    add = addr;
                    break;
                }
            }
        }
        if (add == null)
        {
            add = localHost;
        }
        return add;
    }

    public static Packet3Chat handleChatMessage(NetHandler handler, Packet3Chat chat)
    {
        return NetworkRegistry.instance().handleChat(handler, chat);
    }

    public static void handlePacket131Packet(NetHandler handler, Packet131MapData mapData)
    {
        if (handler instanceof NetServerHandler || mapData.field_73438_a != Item.field_77744_bd.field_77779_bT)
        {
            // Server side and not "map" packets are always handled by us
            NetworkRegistry.instance().handleTinyPacket(handler, mapData);
        }
        else
        {
            // Fallback to the net client handler implementation
            FMLCommonHandler.instance().handleTinyPacket(handler, mapData);
        }
    }
*/
    public static void fmlServerHandshake(ServerConfigurationManager scm, NetworkManager manager, EntityPlayerMP player)
    {
        NetworkDispatcher dispatcher = NetworkDispatcher.allocAndSet(manager, scm);
        dispatcher.serverToClientHandshake(player);
    }

    public static void fmlClientHandshake(NetworkManager field_147393_d)
    {
        NetworkDispatcher dispatcher = NetworkDispatcher.allocAndSet(field_147393_d);
        dispatcher.clientToServerHandshake();
    }

    public static void openGui(EntityPlayer entityPlayer, Object mod, int modGuiId, World world, int x, int y, int z)
    {

    }

    public static void makeEntitySpawnAdjustment(Entity entity, EntityPlayerMP player, int serverX, int serverY, int serverZ)
    {
        EmbeddedChannel embeddedChannel = channelPair.get(Side.SERVER);
        embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.PLAYER);
        embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        embeddedChannel.writeOutbound(new FMLMessage.EntityAdjustMessage(entity, serverX, serverY, serverZ));
    }

    public static Packet getEntitySpawningPacket(Entity entity)
    {
        EntityRegistration er = EntityRegistry.instance().lookupModSpawn(entity.getClass(), false);
        if (er == null)
        {
            return null;
        }
        if (er.usesVanillaSpawning())
        {
            return null;
        }

        EmbeddedChannel embeddedChannel = channelPair.get(Side.SERVER);
        embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.NOWHERE);
        embeddedChannel.writeOutbound(new FMLMessage.EntitySpawnMessage(er, entity, er.getContainer()));
        FMLProxyPacket result = (FMLProxyPacket) embeddedChannel.outboundMessages().poll();
        return result;
    }

    public static String checkModList(FMLHandshakeMessage.ModList modListPacket, Side side)
    {
        Map<String,String> modList = modListPacket.modList();
        List<ModContainer> rejects = Lists.newArrayList();
        for (Entry<ModContainer, NetworkModHolder> networkMod : NetworkRegistry.INSTANCE.registry().entrySet())
        {
            boolean result = networkMod.getValue().check(modList, side);
            if (!result)
            {
                rejects.add(networkMod.getKey());
            }
        }
        if (rejects.isEmpty())
        {
            return null;
        }
        else
        {
            FMLLog.info("Rejecting connection %s: %s", side, rejects);
            return String.format("Mod rejections %s",rejects);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void addClientHandlers()
    {
        channelPair.get(Side.CLIENT).pipeline().addAfter("FMLRuntimeCodec#0", "GuiHandler", new OpenGuiHandler());
        channelPair.get(Side.CLIENT).pipeline().addAfter("FMLRuntimeCodec#0", "EntitySpawnHandler", new EntitySpawnHandler());
    }
    public static void registerChannel(FMLContainer container, Side side)
    {
        channelPair = NetworkRegistry.INSTANCE.newChannel(container, "FML", new FMLRuntimeCodec(), new HandshakeCompletionHandler());
        EmbeddedChannel embeddedChannel = channelPair.get(Side.SERVER);
        embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.NOWHERE);

        if (side == Side.CLIENT)
        {
            addClientHandlers();
        }
    }

    public static List<FMLProxyPacket> forwardHandshake(CompleteHandshake push, NetworkDispatcher target, Side side)
    {
        channelPair.get(side).attr(NetworkDispatcher.FML_DISPATCHER).set(target);
        channelPair.get(side).writeOutbound(push);

        ArrayList<FMLProxyPacket> list = new ArrayList<FMLProxyPacket>();
        for (Object o: channelPair.get(side).outboundMessages())
        {
            list.add((FMLProxyPacket)o);
        }
        channelPair.get(side).outboundMessages().clear();
        return list;
    }

}
