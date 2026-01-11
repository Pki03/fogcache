import { useEffect, useState } from "react";
import { motion } from "framer-motion";

import { getMetrics, getHotKeys, getNodes, getMLDecisions } from "../services/api";

import MetricCard from "../components/cards/MetricCard";
import HotKeysCard from "../components/cards/HotKeysCard";
import HotKeyHeatmap from "../components/cards/HotKeyHeatmap";
import NodesCard from "../components/cards/NodesCard";
import MLDecisions from "../components/cards/MLDecisions";
import AlertsPanel from "../components/cards/AlertsPanel";
import RootCausePanel from "../components/cards/RootCausePanel";
import EdgeAnalytics from "../components/cards/EdgeAnalytics";
import AnomalyTimeline from "../components/cards/AnomalyTimeline";
import LatencyChart from "../components/charts/LatencyChart";
import TopBar from "../components/layout/TopBar";

export default function Dashboard() {
    const [metrics, setMetrics] = useState({});
    const [metricsByNode, setMetricsByNode] = useState({});
    const [hotKeys, setHotKeys] = useState({});
    const [nodes, setNodes] = useState([]);
    const [latencyHistory, setLatencyHistory] = useState([]);
    const [mlDecisions, setMlDecisions] = useState({});

    useEffect(() => {
        let cancelled = false;

        const load = async () => {
            if (cancelled) return;

            const [m, h, n, d] = await Promise.all([
                getMetrics(),
                getHotKeys(),
                getNodes(),
                getMLDecisions()
            ]);

            if (cancelled) return;

            setMetrics(m.data);
            setHotKeys(h.data);
            setNodes(n.data);
            setMlDecisions(d.data);

            // 🧠 Build per-node metrics map (order is preserved)
            const perNode = {};
            n.data.forEach((node, i) => {
                perNode[node] = m.list?.[i] || {};
            });
            setMetricsByNode(perNode);

            setLatencyHistory(prev => [
                ...prev.slice(-30),
                { value: m.data.avg_latency_ms || 0 }
            ]);
        };

        load();
        const t = setInterval(load, 2000);

        return () => {
            cancelled = true;
            clearInterval(t);
        };
    }, []);

    let systemStatus = "Healthy";
    if (metrics.errors > 0) systemStatus = "Critical";
    else if (metrics.avg_latency_ms > 100) systemStatus = "Warning";

    return (
        <div className="min-h-screen bg-gradient-to-br from-[#0B1220] via-[#0E1628] to-[#0B1220] p-10">
            <motion.div
                initial={{ opacity: 0, y: 15 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.6 }}
                className="max-w-7xl mx-auto space-y-12"
            >
                <TopBar status={systemStatus} />

                <section className="space-y-4">
                    <h2 className="text-xl font-semibold text-white">System Metrics</h2>
                    <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-6">
                        <MetricCard title="Requests" value={metrics.external_requests} />
                        <MetricCard title="Cache Hits" value={metrics.cache_hits} />
                        <MetricCard title="Misses" value={metrics.cache_misses} />
                        <MetricCard title="Latency (ms)" value={metrics.avg_latency_ms} />
                    </div>
                </section>

                <LatencyChart data={latencyHistory} />

                <section className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                    <AlertsPanel metrics={metrics} />
                    <RootCausePanel metrics={metrics} />
                </section>

                <AnomalyTimeline history={latencyHistory} />

                <section className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    <HotKeysCard data={hotKeys} />
                    <HotKeyHeatmap data={hotKeys} />
                    <MLDecisions decisions={mlDecisions} />
                </section>

                <section className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                    <NodesCard nodes={nodes} metrics={metrics} />
                    <EdgeAnalytics nodes={nodes} metricsByNode={metricsByNode} />
                </section>
            </motion.div>
        </div>
    );
}
