export default function AlertsPanel({ metrics }) {
    const alerts = [];

    if (metrics.avg_latency_ms > 100) {
        alerts.push({ type: "CRITICAL", msg: "High latency detected" });
    }
    if (metrics.cache_misses > metrics.cache_hits) {
        alerts.push({ type: "WARNING", msg: "Cache miss ratio elevated" });
    }
    if (metrics.errors > 0) {
        alerts.push({ type: "CRITICAL", msg: "Errors observed in traffic" });
    }

    return (
        <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6 shadow-2xl hover:shadow-3xl transition">
            <h2 className="text-white text-lg font-semibold mb-4">Active Alerts</h2>

            {alerts.length === 0 && (
                <div className="flex items-center gap-2 text-green-400">
                    <span className="w-2.5 h-2.5 bg-green-400 rounded-full shadow-[0_0_10px_#22c55e]" />
                    <span>All systems operational</span>
                </div>
            )}

            <div className="space-y-3">
                {alerts.map((a, i) => {
                    const style =
                        a.type === "CRITICAL"
                            ? "bg-red-500/20 text-red-300 border-red-400/30 shadow-[0_0_12px_#ef4444]"
                            : "bg-yellow-500/20 text-yellow-300 border-yellow-400/30 shadow-[0_0_12px_#facc15]";

                    return (
                        <div
                            key={i}
                            className={`border rounded-xl px-4 py-3 text-sm font-medium ${style}`}
                        >
              <span className="uppercase tracking-wide text-xs opacity-80">
                {a.type}
              </span>
                            <div className="mt-1">{a.msg}</div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
}
