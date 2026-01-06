
## 🎯 Objective

Convert FogCache from a working system into a **clear, interview-ready engineering artifact** by:

* Explaining *why* the system exists
* Showing *how* it is designed
* Demonstrating *what problems it solves*
* Making it easy for reviewers to understand and run


---

## 🧠 Key Principle

> A strong project is not judged by code alone,
> but by how clearly the engineer explains **trade-offs, decisions, and outcomes**.
---

## 🔷 FogCache — Distributed Intelligent CDN (Java)

### Overview

FogCache is a **distributed CDN-like system** built in Java that simulates how modern content delivery platforms cache, replicate, and intelligently prefetch content under load.

It is designed as a **production-style backend system**, not a toy project.

---

## 🏗️ Architecture Overview

### Core Components

| Component          | Responsibility                        |
| ------------------ | ------------------------------------- |
| Origin Server      | Source of truth for content           |
| Edge Server        | Caches and serves content             |
| Cache Engine       | LRU / LFU pluggable eviction          |
| Replication Layer  | Hot-key based replication             |
| Load Handling      | Rate limiting & backpressure          |
| Intelligence Layer | Pattern analysis & ML-driven prefetch |
| Admin APIs         | Metrics & cluster visibility          |
| Deployment         | Docker + Compose                      |

---

## 🔄 Request Flow

1. Client requests `/content?id=`
2. Edge server:

   * Applies rate limiting
   * Checks local cache
3. On cache miss:

   * Fetches from origin
   * Stores in cache
4. Hot-key detection triggers:

   * Replication
   * Prefetch decisions
5. Metrics and logs recorded asynchronously

---

## 💾 Caching Strategy

* **LRU / LFU caches** via `CacheStore` interface
* Thread-safe access
* Pluggable eviction policies
* Optimized for read-heavy workloads

---

## 🔥 Hot Key Handling

* Tracks access frequency per key
* Automatically detects hot keys
* Triggers adaptive replication
* Prevents single-node overload

---

## 🤖 Intelligence Layer

* Request logs collected at runtime
* Patterns extracted (frequency, hit ratio)
* Optional ML service predicts hot/warm/cold keys
* Prefetching controlled by:

  * Confidence thresholds
  * Cooldowns
  * Budget limits

> ML is **advisory**, never authoritative — system remains safe without it.

---

## 🛡️ Reliability & Fault Tolerance

* Node health awareness
* Graceful shutdown
* Failure simulation tested (Day 25)
* Recovery without data corruption

---

## 📊 Observability

### Admin Endpoints

* `/admin/nodes` — cluster state
* `/admin/metrics` — cache & latency stats
* `/admin/hotkeys` — hot key visibility
* `/admin/ml/decisions` — ML decision snapshot

---

## 🚀 Deployment

* Fully Dockerized
* Multi-service orchestration via Docker Compose
* Configurable via environment variables
* Works locally or in container networks

---

## 📈 Performance Summary

* ~500+ RPS on local machine
* Low average latency
* Tail latency identified and documented
* Further optimizations planned (Day 26)

---

## 🧩 Design Trade-offs

| Decision               | Rationale                      |
| ---------------------- | ------------------------------ |
| Blocking I/O           | Simpler correctness first      |
| Sync per-key locking   | Prevent duplicate origin fetch |
| ML as advisory         | Safety > aggressiveness        |
| Optimizations deferred | Stability before tuning        |

---

## 🔮 Future Improvements

* Async origin fetch
* Connection pooling
* Reduced lock contention
* p99 latency optimization
* Frontend dashboard (optional)

---

## ▶️ How to Run

```bash
docker compose up
```

Then test:

```bash
curl http://localhost:8083/content?id=test
```

---

## 📌 Why This Project Matters

FogCache demonstrates:

* Distributed system design
* Caching strategies
* Reliability engineering
* Performance awareness
* Production-grade thinking

This is **not a student project** — it mirrors real backend systems.

---

