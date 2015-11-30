package common;

import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;

import static java.nio.file.StandardWatchEventKinds.*;
import java.io.*;
import java.util.*;

/**
 * Example to watch a directory (or tree) for changes to files.
 */

public class WatchDir implements Runnable{

	private final WatchService watcher;
	private final Map<WatchKey, Path> keys;

	private List<Watcher> watchers = new ArrayList<Watcher>();
	private boolean stop = false;
	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void register(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE );
		keys.put(key, dir);
	}

	/**
	 * Creates a WatchService and registers the given directory
	 */
	public WatchDir(Path dir) throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, Path>();
		register(dir);
	}

	public void stop(){
		stop = true;
	}
	/**
	 * Process all events for keys queued to the watcher
	 */
	public void run() {
		while ( !stop ) {

			// wait for key to be signalled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				System.err.println("WatchKey not recognized!!");
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				Kind<?> kind = event.kind();
				// TBD - provide example of how OVERFLOW event is handled
				if (kind == OVERFLOW) {
					continue;
				}

				for( Watcher w: watchers ){
					w.listChange();
				}
				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);

				// print out event
				System.out.format("%s: %s\n", event.kind().name(), child);
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty()) {
					break;
				}
			}
		}
	}


	public void addWatcher( Watcher watcher ){
		watchers.add(watcher);
	}
	public void removeWatcher( Watcher watcher ){
		watchers.remove(watcher);
	}
	public static void main(String[] args) throws IOException {
		Path dir = Paths.get("tree");
		new WatchDir(dir).run();
	}
}