/**
 * PythonRTCLauncher.java
 *
 * @author Yuki Suga (ysuga.net)
 * @date 2011/10/11
 * @copyright 2011, ysuga.net allrights reserved.
 *
 */
package net.ysuga.rtsystem.profile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ysuga
 *
 */
public class PythonRTCLauncher implements Runnable {

	private Thread thread;
	
	private File workingDir;
	
	private File pythonFile;

	private ProcessBuilder processBuilder;

	private Process process;

	private InputStream inputStream;
	
	public InputStream getInputStream() {return inputStream;}
	public Process getProcess() {
		return process;
	}
	
	public boolean isAlive() {
		try {
			process.exitValue();
			return false;
		} catch (IllegalThreadStateException e) {
			return true;
		}
	}
	
	
	public PythonRTCLauncher(File workingDir, File file) {
		this.workingDir = workingDir;
		this.pythonFile = file;
	}
	
	
	public void execute() {
		List<String> arg = new ArrayList<String>();
		arg.add("python");
		arg.add("-u");
		arg.add(pythonFile.getAbsolutePath());
		processBuilder = new ProcessBuilder(arg);
		processBuilder.directory(workingDir);
		try {
			process = processBuilder.start();

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	public void terminate() {
		process.destroy();
	}
	
	public void join() throws InterruptedException {
		thread.join();
	}
	
	/*
	 * ここでスレッドを立ち上げて，終了時にはjoinするようにしないとヤバイ．
	 */
	public void run()  {

	}
}
