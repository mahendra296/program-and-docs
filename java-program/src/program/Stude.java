package program;

import java.io.Serializable;

public class Stude implements Serializable
{
	int		id;
	String	name;
	public Stude(int id, String name)
	{
		this.id = id;
		this.name = name;
	}
}
