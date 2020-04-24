package me.async.sockyj.lib;

import java.io.IOException;
import java.io.InputStream;

public interface IStreamReadable {
	public void ReadFromStream(InputStream stream) throws IOException;
}
