package net.nausicaea.excentric;

/// Staggers a call to [EveryN#run(Runnable)] to an interval, calling
/// [Runnable] only once every `n` ticks on [EveryN].
public final class EveryN {
	public final int interval;
	private int counter;

	public EveryN(int interval) {
		this(interval, 0);
	}

	/// Offset multiple instances to not fall on the same tick phase.
	public EveryN(int interval, int offset) {
		this.interval = interval;
		this.counter = Math.floorMod(offset, interval);
	}

	/// Call a [Runnable] only once every [EveryN#interval].
	public void run(Runnable tickFn) {
		counter += 1;
		if (counter >= interval) {
			counter = 0;
			tickFn.run();
		}
	}
}
