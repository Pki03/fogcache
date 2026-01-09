import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    Tooltip,
    ResponsiveContainer,
    CartesianGrid,
} from "recharts";

export default function LatencyChart({ data }) {
    return (
        <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6 shadow-xl hover:shadow-2xl transition">
            <div className="flex items-center justify-between mb-4">
                <h2 className="text-white text-lg font-semibold">📈 Latency Trend</h2>
                <span className="text-xs text-slate-400">last 60s</span>
            </div>

            <div className="h-72">
                <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={data}>
                        <CartesianGrid stroke="#1f2937" strokeDasharray="3 3" />
                        <XAxis hide />
                        <YAxis
                            tick={{ fill: "#cbd5f5", fontSize: 12 }}
                            axisLine={false}
                            tickLine={false}
                        />
                        <Tooltip
                            contentStyle={{
                                backgroundColor: "#020617",
                                border: "1px solid #334155",
                                borderRadius: "8px",
                            }}
                            labelStyle={{ color: "#e5e7eb" }}
                        />
                        <Line
                            type="monotone"
                            dataKey="value"
                            stroke="#60A5FA"
                            strokeWidth={3}
                            dot={false}
                            isAnimationActive
                        />
                    </LineChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
}
