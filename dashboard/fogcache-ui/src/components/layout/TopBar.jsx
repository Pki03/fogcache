export default function TopBar({ status }) {
    const statusColor =
        status === "Critical"
            ? "bg-red-500 shadow-[0_0_12px_#ef4444]"
            : status === "Warning"
                ? "bg-yellow-400 shadow-[0_0_12px_#facc15]"
                : "bg-green-400 shadow-[0_0_12px_#22c55e]";

    return (
        <div className="flex justify-between items-center py-4 px-2">
            <div className="flex flex-col">
                <h1 className="text-white text-2xl font-semibold tracking-wide">
                    FogCache Platform
                </h1>
                <span className="text-sm text-slate-400">
          Distributed Edge Control Plane
        </span>
            </div>

            <div className="flex items-center gap-4 bg-white/5 border border-white/10 rounded-xl px-4 py-2 backdrop-blur-md shadow-lg">
                <span className="text-slate-300 text-sm">System Status</span>

                <span className={`w-3 h-3 rounded-full ${statusColor}`} />

                <span className="text-white font-medium">{status}</span>
            </div>
        </div>
    );
}
