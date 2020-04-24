package me.async.sockyj.lib.packet;

import me.async.sockyj.lib.IStreamReadable;
import me.async.sockyj.lib.IStreamWritable;

public abstract class Packet implements IStreamWritable, IStreamReadable
{
	public Packet() { }
}