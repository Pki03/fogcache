export default function MLDecisions({ decisions }) {
    const items = Object.entries(decisions || {});

    return (
        <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6 shadow-2xl hover:shadow-3xl transition">
            <h2 className="text-white text-lg font-semibold mb-4">🧠 ML Decisions</h2>

            <div className="space-y-3">
                {items.map(([key, decision]) => (
                    <div
                        key={key}
                        className="flex justify-between items-center bg-white/5 border border-white/10 rounded-xl px-4 py-2 hover:bg-white/10 transition"
                    >
                        <span className="text-slate-300 text-sm">{key}</span>

                        <span className="text-indigo-400 font-semibold text-sm">
              {decision}
            </span>
                    </div>
                ))}
            </div>
        </div>
    );
}
