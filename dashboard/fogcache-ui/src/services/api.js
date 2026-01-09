import axios from "axios";

const edges = [
    "http://localhost:8082",
    "http://localhost:8083"
];

const createClient = (baseURL) =>
    axios.create({ baseURL, timeout: 5000 });

const clients = edges.map(createClient);

const aggregate = async (path) => {
    const responses = await Promise.allSettled(
        clients.map((c) => c.get(path))
    );

    const data = responses
        .filter((r) => r.status === "fulfilled")
        .map((r) => r.value.data);

    return data;
};

export const getMetrics = async () => {
    const list = await aggregate("/admin/metrics");

    return {
        data: {
            total_requests: list.reduce((a, b) => a + b.total_requests, 0),
            cache_hits: list.reduce((a, b) => a + b.cache_hits, 0),
            cache_misses: list.reduce((a, b) => a + b.cache_misses, 0),
            errors: list.reduce((a, b) => a + b.errors, 0),
            origin_calls: list.reduce((a, b) => a + b.origin_calls, 0),
            avg_latency_ms:
                list.length === 0
                    ? 0
                    : Math.round(
                        list.reduce((a, b) => a + b.avg_latency_ms, 0) / list.length
                    )
        }
    };
};

export const getHotKeys = async () => {
    const list = await aggregate("/admin/hotkeys");

    const combined = {};
    list.forEach((map) => {
        Object.entries(map).forEach(([k, v]) => {
            combined[k] = (combined[k] || 0) + v;
        });
    });

    return { data: combined };
};

export const getNodes = async () => {
    return { data: edges };
};

export const getMLDecisions = async () => {
    const list = await aggregate("/admin/ml/decisions");

    return {
        data: Object.assign({}, ...list)
    };
};
