# MessagingService 🚀

**A Spring Boot microservice for sending emails with API key security and Prometheus metrics monitoring.**

## 📝 Overview  
The **MessagingService** handles email delivery using SendGrid and tracks custom metrics for monitoring. Built with **Spring Boot 3**, it integrates with **Prometheus** and **Grafana** for performance insights.

---

## 📌 Features  
- ✅ **Email Service:** Send single and bulk emails via SendGrid  
- ✅ **Secure API:** API Key-based authentication using `ApiKeyFilter`  
- ✅ **Monitoring:** Tracks email attempts, successes, and failures  
- ✅ **Health Checks:** Spring Boot Actuator for `/actuator/health`  
- ✅ **Prometheus Metrics:** Custom counters for email operations  

---

## 🏛️ Architecture  
```
   [Frontend] → [MessagingService] → [SendGrid]
                    ↓
               [Prometheus] → [Grafana]
```
- **Spring Boot:** Core framework  
- **Micrometer:** Collects custom metrics  
- **Prometheus:** Stores metrics  
- **Grafana:** Visualizes metrics  
- **Docker:** Manages containerized services  

---

## 🛠️ Installation

### ✅ Prerequisites:  
- Java 17+  
- Maven  
- Docker (for Prometheus & Grafana)  
- SendGrid API Key  

### 💻 Clone the Repository  
```bash
git clone https://github.com/DOLUWAMU-TAIWO/MessagingService.git
cd MessagingService
```

### ⚙️ Configure Environment Variables  
Create an `.env` file or set environment variables:  
```env
SENDGRID_API_KEY=your_sendgrid_api_key
SERVICE_PASSWORD=your_api_key_password
```

### 🚀 Run the Service  
```bash
./mvnw spring-boot:run
```

---

## 📊 API Endpoints  

### 💌 **Send Email:**  
**POST `/api/email/send`**  
- Headers:  
  `Authorization: Bearer <SERVICE_PASSWORD>`  
- Request Body:  
```json
{
  "to": "recipient@example.com",
  "subject": "Hello",
  "content": "This is a test email"
}
```

- Response:  
```json
{
  "message": "Email sent successfully to recipient@example.com"
}
```

---

### ❤️ **Health Check:**  
**GET `/actuator/health`**  
- Response:  
```json
{
  "status": "UP"
}
```

---

### 📈 **Prometheus Metrics:**  
**GET `/actuator/prometheus`**  
Custom Metrics:  
- `email_attempts_total` - Total email attempts  
- `email_success_total` - Total successful emails  
- `email_failures_total` - Total failed emails  

---

## 📂 Project Structure  
```
MessagingService/
├── src/main/java/dev/dolu/messaging/
│   ├── Config/            # Security and Prometheus configuration
│   ├── Controller/        # API endpoints
│   ├── Metrics/           # Custom email metrics class
│   ├── Service/           # Email service logic
│   └── Filter/            # ApiKeyFilter for security
└── application.properties  # App configuration
```

---

## 🚀 Deployment  
- 🐳 **Docker:** Package as a Docker image using `Dockerfile`.  
- ☁️ **Cloud:** Deploy on AWS ECS, Heroku, or Azure App Service.  

---

## 🛡️ Security Notes  
- Store secrets in `.env` or environment variables.  
- Use `@Value` with Spring `application.yml` for sensitive properties.  
- Implement rate-limiting to prevent abuse.  

---

## 👨‍💻 Author  
**Doluwa Taiwo** 🚀  
- GitHub: [DOLUWAMU-TAIWO](https://github.com/DOLUWAMU-TAIWO)  
- LinkedIn: [Doluwa Taiwo](https://linkedin.com/in/doluwa-taiwo)  

---

## 💪 Contributing  
Pull requests are welcome! For major changes, please open an issue to discuss what you would like to change.  

---

## 📜 License  
This project is licensed under the MIT License.

