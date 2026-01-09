from flask import Flask, request, jsonify

app = Flask(__name__)

# Health endpoint
@app.route("/health")
def health():
    return jsonify({"status": "ok"})

# Main ML decision endpoint
@app.route("/decide", methods=["POST"])
def decide():
    data = request.get_json(force=True)

    print("ML received:", data)

    key = data.get("key")
    count = data.get("requests", 0)
    latency = data.get("latency", 0)

    # Simple intelligent policy
    if count > 100 and latency > 100:
        decision = "replicate"
    elif count > 30:
        decision = "prefetch"
    else:
        decision = "normal"

    return jsonify({
        "key": key,
        "decision": decision,
        "confidence": round(min(1.0, (count / 150) + (latency / 200)), 2)
    })

if __name__ == "__main__":
    app.run(port=5001)
