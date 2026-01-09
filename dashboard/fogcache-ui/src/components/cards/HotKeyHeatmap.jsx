export default function HotKeyHeatmap({ data }) {
    const entries = Object.entries(data || {});

    return (
        <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6 shadow-2xl hover:shadow-3xl transition">
            <h2 className="text-white text-lg font-semibold mb-4">
                🔥 Hot-Key Activity
            </h2>

            <div className="grid grid-cols-3 gap-3">
                {entries.map(([key, count]) => {
                    let intensity = "bg-yellow-500/20";
                    let glow = "shadow-[0_0_10px_#eab308]";

                    if (count > 50) {
                        intensity = "bg-red-500/30";
                        glow = "shadow-[0_0_14px_#ef4444]";
                    } else if (count > 20) {
                        intensity = "bg-orange-500/30";
                        glow = "shadow-[0_0_12px_#fb923c]";
                    }

                    return (
                        <div
                            key={key}
                            className={`rounded-xl p-3 text-center border border-white/10 ${intensity} ${glow} transition`}
                        >
                            <div className="text-white font-medium text-sm">{key}</div>
                            <div className="text-xs text-slate-300 mt-1">
                                {count.toLocaleString()} hits
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
}
