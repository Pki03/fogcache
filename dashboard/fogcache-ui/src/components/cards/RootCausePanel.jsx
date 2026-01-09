export default function RootCausePanel({ metrics }) {
    let cause = "System healthy";
    let tone = "text-green-300";
    let glow = "shadow-[0_0_12px_#22c55e]";

    if (metrics.errors > 0) {
        cause = "Upstream service instability";
        tone = "text-red-300";
        glow = "shadow-[0_0_12px_#ef4444]";
    } else if (metrics.avg_latency_ms > 100) {
        cause = "Edge congestion or overload";
        tone = "text-yellow-300";
        glow = "shadow-[0_0_12px_#facc15]";
    } else if (metrics.cache_misses > metrics.cache_hits) {
        cause = "Cache inefficiency / cold traffic";
        tone = "text-indigo-300";
        glow = "shadow-[0_0_12px_#6366f1]";
    }

    return (
        <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6 shadow-2xl hover:shadow-3xl transition">
            <h2 className="text-white text-lg font-semibold mb-3">🧠 Root Cause Analysis</h2>

            <div
                className={`inline-block px-4 py-2 rounded-xl bg-white/5 border border-white/10 ${glow}`}
            >
                <p className={`text-sm font-medium ${tone}`}>{cause}</p>
            </div>

            <p className="mt-4 text-xs text-slate-400">
                Based on live traffic patterns & cache metrics
            </p>
        </div>
    );
}
