export default function NodesCard({ nodes, metrics }) {
    return (
        <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6 shadow-2xl hover:shadow-3xl transition">
            <h2 className="text-white text-lg font-semibold mb-4">Edge Health</h2>

            <div className="space-y-3">
                {nodes.map((node) => {
                    let status = "Healthy";
                    let color = "text-green-400";
                    let dot = "bg-green-400";
                    let glow = "shadow-[0_0_12px_#22c55e]";

                    if (metrics.errors > 0) {
                        status = "Critical";
                        color = "text-red-400";
                        dot = "bg-red-500";
                        glow = "shadow-[0_0_12px_#ef4444]";
                    } else if (metrics.avg_latency_ms > 100) {
                        status = "Warning";
                        color = "text-yellow-400";
                        dot = "bg-yellow-400";
                        glow = "shadow-[0_0_12px_#facc15]";
                    }

                    return (
                        <div
                            key={node}
                            className="flex justify-between items-center bg-white/5 border border-white/10 rounded-xl px-4 py-3 hover:bg-white/10 transition"
                        >
                            <div className="flex items-center gap-3">
                <span
                    className={`w-2.5 h-2.5 rounded-full ${dot} ${glow}`}
                />
                                <span className="text-slate-300">{node}</span>
                            </div>

                            <span className={`font-semibold ${color}`}>
                {status}
              </span>
                        </div>
                    );
                })}
            </div>
        </div>
    );
}
