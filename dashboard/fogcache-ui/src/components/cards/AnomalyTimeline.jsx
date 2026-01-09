export default function AnomalyTimeline({ history }) {
    const anomalies = [];

    for (let i = 1; i < history.length; i++) {
        const delta = history[i].value - history[i - 1].value;
        if (delta > 40) {
            anomalies.push({
                time: new Date().toLocaleTimeString(),
                message: `Latency spike detected (+${delta}ms)`
            });
        }
    }

    return (
        <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6 shadow-2xl hover:shadow-3xl transition">
            <h2 className="text-white text-lg font-semibold mb-4">🧬 Incident Timeline</h2>

            {anomalies.length === 0 && (
                <div className="flex items-center gap-2 text-green-400 text-sm">
                    <span className="w-2 h-2 bg-green-400 rounded-full shadow-[0_0_8px_#22c55e]" />
                    <span>No incidents detected</span>
                </div>
            )}

            <div className="space-y-3 mt-2">
                {anomalies.map((a, i) => (
                    <div
                        key={i}
                        className="relative bg-white/5 border border-red-400/30 rounded-xl p-4 text-sm text-slate-200"
                    >
                        <div className="absolute -left-1 top-4 w-2 h-2 bg-red-400 rounded-full shadow-[0_0_8px_#ef4444]" />

                        <div className="flex justify-between items-center mb-1">
                            <span className="text-red-400 font-semibold">{a.time}</span>
                            <span className="text-xs text-red-300">ANOMALY</span>
                        </div>

                        <p className="text-slate-300">{a.message}</p>
                    </div>
                ))}
            </div>
        </div>
    );
}
