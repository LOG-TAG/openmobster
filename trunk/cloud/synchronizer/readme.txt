Some Notes:

Conflict Resolution Policy:

1/ Idea is to keep conflict resolution as automatic as possible, one that does not require any manual user 
intervention. The down side of the approach is data overriding in some scenarios. 
Here is the adopted Conflict Resolution Policy

In a TwoWay Sync:

	The device state wins over the server state

In a SlowSync:

	The device state wins over the server state

In a OneWayClient Sync:

	The device state wins over the server state
	
In a OneWayServer Sync:

	The server state wins over the device state
	
Note: This is not the most effective and should be replaced by a "Rule based" customizable approach
