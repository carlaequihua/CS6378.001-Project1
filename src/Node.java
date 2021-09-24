//Object to reprsents a node in the distributed system
class Node
{
	// node identifier
	private NodeID identifier;
	
	// constructor
	public Node(NodeID identifier, String configFile, Listener listener)
	{
		//Your code goes here
	}

	// methods
	public NodeID[] getNeighbors()
	{
		//Your code goes here
	}

	public void send(Message message, NodeID destination)
	{
		//Your code goes here
	}

	public void sendToAll(Message message)
	{
		//Your code goes here
	}
	
	public void tearDown()
	{
		//Your code goes here
	}
}
