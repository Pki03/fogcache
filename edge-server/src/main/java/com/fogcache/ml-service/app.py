from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route("/")
def health():
    return "ML Service OK", 200

@app.route("/predict", methods=["POST"])
def predict():
    data = request.get_json(force=True)

    count = data.get("count", 0)
    access_rate = data.get("accessRate", 0)

    if count > 100 and access_rate > 20:
        return jsonify({"clazz": "HOT", "confidence": 0.9})
    elif count > 30:
        return jsonify({"clazz": "WARM", "confidence": 0.7})
    else:
        return jsonify({"clazz": "COLD", "confidence": 0.4})

if __name__ == "__main__":
    app.run(port=5001)
