package it.tdlight.tdlight;

import it.tdlight.tdnatives.NativeLog;
import java.util.Objects;

public class Log extends NativeLog {

	static {
		try {
			Init.start();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			System.exit(0);
		}
	}

	private static final FatalErrorCallbackPtr defaultFatalErrorCallbackPtr = System.err::println;
	private static FatalErrorCallbackPtr fatalErrorCallback = defaultFatalErrorCallbackPtr;

	/**
	 * Sets the callback that will be called when a fatal error happens. None of the TDLib methods can be called from the callback. The TDLib will crash as soon as callback returns. By default the callback set to print in stderr.
	 * @param fatalErrorCallback Callback that will be called when a fatal error happens. Pass null to restore default callback.
	 */
	public static void setFatalErrorCallback(FatalErrorCallbackPtr fatalErrorCallback) {
		Log.fatalErrorCallback = Objects.requireNonNullElse(fatalErrorCallback, defaultFatalErrorCallbackPtr);
	}

	private static void onFatalError(String errorMessage) {
		new Thread(() -> Log.fatalErrorCallback.onFatalError(errorMessage)).start();
	}
}