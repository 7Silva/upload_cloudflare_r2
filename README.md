# CDN R2 API

RESTful API for uploading files to Cloudflare R2, built with Spring Boot.

## 🚀 Features

- File uploads to Cloudflare R2
- Automatic folder organization by file type
- Upload logging in MongoDB
- API Key authentication
- Monitoring via Spring Actuator
- Docker containerization

## 📋 Requirements

- Java 21
- MongoDB
- Cloudflare R2 Account
- Maven

## 🔧 Configuration

1. Clone the repository
2. Copy the `.env.example` file to `.env`:

```bash
cp .env.example .env
```

3. Configure environment variables in the `.env` file:

```properties
MONGODB_DATABASE=your_database
MONGODB_USERNAME=your_username
MONGODB_PASSWORD=your_password

R2_ACCESS_KEY_ID=your_key
R2_SECRET_ACCESS_KEY=your_secret
R2_ACCOUNT_ID=your_account_id
R2_BUCKET_NAME=your_bucket
R2_ENDPOINT=https://xxxxx.r2.cloudflarestorage.com

API_KEY=your_api_key
CDN_URL=https://your-cdn.com/
```

## 🚀 Running

### Local

```bash
./mvnw spring-boot:run
```

### Docker

```bash
docker-compose up -d
```

## 📡 Endpoints

### File Upload
`POST /cdn/v1/upload`

**Headers:**
```
Authorization: Bearer {API_KEY}
Content-Type: multipart/form-data
```

**Body:**
- `file`: File to upload (max: 30MB)

**Success Response:**
```json
{
    "success": true,
    "message": "File uploaded successfully",
    "data": "https://your-cdn.com/images/123456_file.jpg",
    "timestamp": "2024-01-20T10:30:00"
}
```

### Health Check
`GET /cdn/actuator/health`

## 📁 Folder Structure

Files are automatically organized into the following folders in R2:

- `/images/` - Image files
- `/videos/` - Video files
- `/audios/` - Audio files
- `/others/` - Other file types

## 🔒 Security

- API Key authentication in `Authorization` header
- File type validation
- IP-based rate limiting
- Maximum file size: 30MB

## 🛠️ Technologies

- Spring Boot 3.4
- Spring Security
- MongoDB
- AWS SDK S3 (for R2)
- Docker
- Maven

## 📊 Monitoring

Actuator endpoints available at `/cdn/actuator`:

- `/health` - Application status
- `/metrics` - Application metrics
- `/prometheus` - Prometheus format metrics

## ⚙️ Docker Configuration

```yml
services:
  api:
    build: .
    ports:
      - "3006:3006"
    environment:
      - MONGODB_DATABASE=${MONGODB_DATABASE}
      - MONGODB_USERNAME=${MONGODB_USERNAME}
      - MONGODB_PASSWORD=${MONGODB_PASSWORD}
      # ... other variables

  mongodb:
    image: mongo:latest
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_ROOT_USERNAME: ${MONGODB_USERNAME}
      MONGO_INITDB_ROOT_PASSWORD: ${MONGODB_PASSWORD}
```

## 📝 Logs

Upload logs are stored in MongoDB in the `upload_logs` collection with the following information:

- Upload ID
- File name
- Size
- File type
- Status
- Error message (if any)
- Client IP
- Upload date/time