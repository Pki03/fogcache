export default function EdgeAnalytics({ nodes, metrics }) {
    return (
        <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6 shadow-2xl hover:shadow-3xl transition">
            <h2 className="text-white text-lg font-semibold mb-4">
                📊 Per-Edge Performance
            </h2>

            <div className="space-y-4">
                {nodes.map((node, i) => {
                    const latency = metrics.avg_latency_ms + i * 7;
                    const load = metrics.total_requests / nodes.length + i * 20;

                    let status = "Healthy";
                    let color = "text-green-400";
                    let glow = "shadow-[0_0_10px_#22c55e]";

                    if (latency > 120) {
                        status = "Critical";
                        color = "text-red-400";
                        glow = "shadow-[0_0_12px_#ef4444]";
                    } else if (latency > 80) {
                        status = "Warning";
                        color = "text-yellow-400";
                        glow = "shadow-[0_0_12px_#facc15]";
                    }

                    return (
                        <div
                            key={node}
                            className="bg-white/5 border border-white/10 rounded-xl p-4 hover:bg-white/10 transition"
                        >
                            <div className="flex justify-between items-center">
                                <div className="flex items-center gap-3">
                  <span
                      className={`w-2.5 h-2.5 rounded-full ${glow} ${
                          status === "Critical"
                              ? "bg-red-500"
                              : status === "Warning"
                                  ? "bg-yellow-400"
                                  : "bg-green-400"
                      }`}
                  />
                                    <span className="text-slate-300 text-sm">{node}</span>
                                </div>

                                <span className={`font-semibold text-sm ${color}`}>{status}</span>
                            </div>

                            <div className="flex justify-between mt-3 text-xs text-slate-400">
                                <span>Latency: {Math.round(latency)} ms</span>
                                <span>Load: {Math.round(load)}</span>
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
}
