import CountUp from "react-countup";

export default function MetricCard({ title, value }) {
    return (
        <div className="rounded-2xl bg-white/5 backdrop-blur-xl border border-white/10 p-6 shadow-xl hover:shadow-2xl hover:scale-[1.02] transition-all">
            <p className="text-sm text-slate-400">{title}</p>

            <p className="mt-2 text-3xl font-bold text-white">
                <CountUp
                    end={Number(value) || 0}
                    duration={0.6}
                    preserveValue
                />
            </p>
        </div>
    );
}
