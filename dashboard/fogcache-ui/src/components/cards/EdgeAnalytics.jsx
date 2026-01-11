export default function EdgeAnalytics({ nodes, metricsByNode }) {

    return (
        <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6 shadow-2xl">
            <h2 className="text-white text-lg font-semibold mb-4">
                📊 Per-Edge Performance
            </h2>

            <div className="space-y-4">
                {nodes.map((node) => {
                    const nodeMetrics = metricsByNode[node] || {};
                    const latency = nodeMetrics.avg_latency_ms || 0;
                    const load = nodeMetrics.external_requests || 0;

                    let status = "Healthy";
                    let color = "text-green-400";

                    if (latency > 120 || load > 80) {
                        status = "Critical";
                        color = "text-red-400";
                    }
                    else if (latency > 80 || load > 40) {
                        status = "Warning";
                        color = "text-yellow-400";
                    }

                    return (
                        <div key={node} className="bg-white/5 border border-white/10 rounded-xl p-4">
                            <div className="flex justify-between items-center">
                                <span className="text-slate-300 text-sm">{node}</span>
                                <span className={`font-semibold text-sm ${color}`}>{status}</span>
                            </div>

                            <div className="flex justify-between mt-3 text-xs text-slate-400">
                                <span>Latency: {latency} ms</span>
                                <span>Load: {load}</span>
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
}
