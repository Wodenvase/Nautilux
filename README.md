# Nautilux: Coral Reef Health Monitoring Platform

Nautilux is an open-source platform for monitoring coral reef health using real-time and historical data from underwater cameras, sensors, and sonar. It’s designed for marine researchers, conservationists, and anyone interested in ocean health.

---

## What’s Inside?
- **Data Ingestion:** Collects live and batch data from ROVs, buoys, and sensor networks.
- **Analysis:** Runs lightweight image and sonar analysis to estimate reef health, bleaching risk, and biodiversity.
- **Dashboard:** JavaFX desktop app for visualizing reef zones, health scores, and live data feeds.
- **APIs:** REST endpoints for data access and integration.
- **Alerting:** Basic notification system for risk conditions.

---

## Quick Start

### Prerequisites
- Java 17+ (or newer)
- Python 3.11+
- Docker & Docker Compose
- Maven
- Git

### Setup
```bash
# Clone the repo
git clone https://github.com/Wodenvase/Nautilux.git
cd Nautilux

# Make the startup script executable
chmod +x start-nautilux.sh

# Start everything (backend, Ray service, DB, etc.)
./start-nautilux.sh start
```

- The backend API will be at [http://localhost:8080](http://localhost:8080)
- The Ray analysis service will be at [http://localhost:8000](http://localhost:8000)
- Prometheus metrics at [http://localhost:9090](http://localhost:9090)

### Stopping
```bash
./start-nautilux.sh stop
```

---

## Project Structure
```
Nautilux/
├── quarkus-app/         # Java backend (REST API, data models, Camel routes)
├── ray-service/         # Python microservice for analysis
├── javafx-dashboard/    # JavaFX desktop dashboard
├── data/                # Sample data, DB init scripts
├── monitoring/          # Prometheus config
├── docker-compose.yml   # Infra services
├── start-nautilux.sh    # Startup script
└── README.md
```

---

## Contributing
Pull requests are welcome! If you spot a bug or want to add a feature, open an issue or PR. This project is for learning, research, and real-world conservation.

---

## License
MIT License. See [LICENSE](LICENSE).

---
