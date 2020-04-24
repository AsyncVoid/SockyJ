package me.async.sockyj.lib;

import java.io.IOException;
import java.io.OutputStream;

public interface IStreamWritable {
	public void WriteToStream(OutputStream stream) throws IOException;
}
