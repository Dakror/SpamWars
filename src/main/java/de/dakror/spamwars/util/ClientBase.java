package de.dakror.spamwars.util;

import java.util.HashMap;

import com.shephertz.app42.gaming.multiplayer.client.events.AllRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.AllUsersEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.ChatEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveUserInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LobbyData;
import com.shephertz.app42.gaming.multiplayer.client.events.LobbyEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.MatchedRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.MoveEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.UpdateEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ChatRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.LobbyRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.NotifyListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.UpdateRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener;

/**
 * @author Dakror
 */
public class ClientBase implements ZoneRequestListener, RoomRequestListener, ChatRequestListener, NotifyListener, LobbyRequestListener, UpdateRequestListener, ConnectionRequestListener
{
	@Override
	public void onConnectDone(ConnectEvent e)
	{}
	
	@Override
	public void onDisconnectDone(ConnectEvent e)
	{}
	
	@Override
	public void onInitUDPDone(byte b)
	{}
	
	@Override
	public void onSendUpdateDone(byte b)
	{}
	
	@Override
	public void onGetLiveLobbyInfoDone(LiveRoomInfoEvent e)
	{}
	
	@Override
	public void onJoinLobbyDone(LobbyEvent e)
	{}
	
	@Override
	public void onLeaveLobbyDone(LobbyEvent e)
	{}
	
	@Override
	public void onSubscribeLobbyDone(LobbyEvent e)
	{}
	
	@Override
	public void onUnSubscribeLobbyDone(LobbyEvent e)
	{}
	
	@Override
	public void onChatReceived(ChatEvent e)
	{}
	
	@Override
	public void onGameStarted(String e, String arg1, String arg2)
	{}
	
	@Override
	public void onGameStopped(String e, String arg1)
	{}
	
	@Override
	public void onMoveCompleted(MoveEvent e)
	{}
	
	@Override
	public void onPrivateChatReceived(String e, String arg1)
	{}
	
	@Override
	public void onRoomCreated(RoomData e)
	{}
	
	@Override
	public void onRoomDestroyed(RoomData e)
	{}
	
	@Override
	public void onUpdatePeersReceived(UpdateEvent e)
	{}
	
	@Override
	public void onUserChangeRoomProperty(RoomData e, String arg1, HashMap<String, Object> arg2, HashMap<String, String> arg3)
	{}
	
	@Override
	public void onUserJoinedLobby(LobbyData e, String arg1)
	{}
	
	@Override
	public void onUserJoinedRoom(RoomData e, String arg1)
	{}
	
	@Override
	public void onUserLeftLobby(LobbyData e, String arg1)
	{}
	
	@Override
	public void onUserLeftRoom(RoomData e, String arg1)
	{}
	
	@Override
	public void onUserPaused(String e, boolean arg1, String arg2)
	{}
	
	@Override
	public void onUserResumed(String e, boolean arg1, String arg2)
	{}
	
	@Override
	public void onSendChatDone(byte b)
	{}
	
	@Override
	public void onSendPrivateChatDone(byte b)
	{}
	
	@Override
	public void onGetLiveRoomInfoDone(LiveRoomInfoEvent e)
	{}
	
	@Override
	public void onJoinRoomDone(RoomEvent e)
	{}
	
	@Override
	public void onLeaveRoomDone(RoomEvent e)
	{}
	
	@Override
	public void onLockPropertiesDone(byte b)
	{}
	
	@Override
	public void onSetCustomRoomDataDone(LiveRoomInfoEvent e)
	{}
	
	@Override
	public void onSubscribeRoomDone(RoomEvent e)
	{}
	
	@Override
	public void onUnSubscribeRoomDone(RoomEvent e)
	{}
	
	@Override
	public void onUnlockPropertiesDone(byte b)
	{}
	
	@Override
	public void onUpdatePropertyDone(LiveRoomInfoEvent e)
	{}
	
	@Override
	public void onCreateRoomDone(RoomEvent e)
	{}
	
	@Override
	public void onDeleteRoomDone(RoomEvent e)
	{}
	
	@Override
	public void onGetAllRoomsDone(AllRoomsEvent e)
	{}
	
	@Override
	public void onGetLiveUserInfoDone(LiveUserInfoEvent e)
	{}
	
	@Override
	public void onGetMatchedRoomsDone(MatchedRoomsEvent e)
	{}
	
	@Override
	public void onGetOnlineUsersDone(AllUsersEvent e)
	{}
	
	@Override
	public void onSetCustomUserDataDone(LiveUserInfoEvent e)
	{}
}
