# Docker Commands Reference Guide

## Table of Contents
- [Docker Image Commands](#docker-image-commands)
- [Docker Container Commands](#docker-container-commands)
- [Docker Network Commands](#docker-network-commands)
- [Docker Volume Commands](#docker-volume-commands)
- [Dockerfile](#dockerfile)
- [Docker Compose](#docker-compose)

---

## Docker Image Commands

### Build Docker Image
```bash
# Build image from current directory
docker build -t {imagename} .

docker build -t myapp:latest .

# Build with specific Dockerfile
docker build -f {dockerfile path} -t myapp:v1.0 .

docker build -f /path/to/Dockerfile -t myapp:v1.0 .

# Build with build arguments
docker build --build-arg VERSION=1.0 -t myapp:latest .
```

**Example:**
```bash
docker build -t node-app:1.0 .
docker build -f Dockerfile.prod -t node-app:production .
```

### List Docker Images
```bash
# List all images
docker image ls
docker images

# List images with specific filter
docker images --filter "dangling=true"
docker images --filter "reference=node*"
```

**Example Output:**
```
REPOSITORY    TAG       IMAGE ID       CREATED         SIZE
node-app      1.0       abc123def456   2 hours ago     150MB
nginx         latest    fed456abc789   3 days ago      142MB
```

### Remove Docker Image
```bash
# Remove by name
docker image rm {image_name}

docker image rm myapp:latest
docker image rm myapp:latest myapp:v1.0

# Remove by ID
docker rmi {imageId}

docker rmi abc123def456

# Remove all unused images
docker image prune -a
```

**Example:**
```bash
docker image rm node-app:1.0
docker rmi abc123def456
docker image prune -a --force
```

---

## Docker Container Commands

### Create Container
```bash
# Create container without starting
docker create --name {container_name} {image_name:version}

docker create --name my-container nginx:latest

# Create with environment variables
docker create --name db-container -e POSTGRES_PASSWORD=secret postgres
```

**Example:**
```bash
docker create --name web-server nginx:latest
```

### Run Container
```bash
# Run in detached mode with port mapping
docker run -d -p 8080:80 --name web nginx

# Run with volume mount
docker run -d -v /host/path:/container/path nginx

# Run with environment variables
docker run -d -e NODE_ENV=production -p 3000:3000 node-app

# Run interactively
docker run -it ubuntu:latest bash

# Run with custom command
docker run nginx:latest nginx -v
```

**Example:**
```bash
# Run Node.js application
docker run -d -p 3000:3000 --name my-app \
  -e NODE_ENV=production \
  -v $(pwd)/logs:/app/logs \
  node-app:latest

# Run MySQL database
docker run -d -p 3306:3306 --name mysql-db \
  -e MYSQL_ROOT_PASSWORD=secret \
  -e MYSQL_DATABASE=mydb \
  mysql:8.0
```

### List Containers
```bash
# List running containers
docker container ls
docker ps

# List all containers (including stopped)
docker ps -a

# List with specific format
docker ps --format "table {{.ID}}\t{{.Names}}\t{{.Status}}"
```

**Example Output:**
```
CONTAINER ID   IMAGE          COMMAND                  STATUS
abc123def456   nginx:latest   "nginx -g 'daemon of…"   Up 2 hours
fed456abc789   mysql:8.0      "docker-entrypoint.s…"   Up 1 hour
```

### Manage Containers
```bash
# Start container
docker start my-container

# Stop container
docker stop my-container

# Restart container
docker restart my-container

# Kill container (force stop)
docker kill my-container

# Pause container
docker pause my-container

# Unpause container
docker unpause my-container
```

**Example:**
```bash
docker start web-server
docker stop web-server
docker restart web-server
```

### Remove Containers
```bash
# Remove stopped container
docker rm my-container

# Remove running container (force)
docker rm -f my-container

# Remove all stopped containers
docker container prune

# Remove all stopped containers (force, no prompt)
docker container prune -f
```

**Example:**
```bash
docker rm web-server
docker rm -f mysql-db
docker container prune --force
```

### Rename Container
```bash
docker rename {old-name} {new-name}
```

**Example:**
```bash
docker rename web-server production-web-server
```

### Execute Commands in Container
```bash
# Execute bash shell
docker exec -it my-container bash

# Execute sh shell (for Alpine-based images)
docker exec -it my-container sh

# Execute specific command
docker exec my-container ls -la /app

# Execute as root user
docker exec -u root -it my-container bash
```

**Example:**
```bash
# Access container shell
docker exec -it web-server bash

# Check logs inside container
docker exec web-server cat /var/log/nginx/access.log

# Run database query
docker exec mysql-db mysql -uroot -psecret -e "SHOW DATABASES;"
```

### Container Logs
```bash
# View logs
docker logs my-container

# Follow logs (live)
docker logs -f my-container

# Show last 100 lines
docker logs --tail 100 my-container

# Show logs with timestamps
docker logs -t my-container
```

**Example:**
```bash
docker logs -f --tail 50 web-server
```

---

## Docker Network Commands

### List Networks
```bash
docker network ls
```

**Example Output:**
```
NETWORK ID     NAME      DRIVER    SCOPE
abc123def456   bridge    bridge    local
fed456abc789   host      host      local
```

### Create Network
```bash
# Create bridge network
docker network create my-network

# Create with specific driver
docker network create --driver bridge my-bridge

# Create with subnet
docker network create --subnet=172.18.0.0/16 my-custom-network
```

**Example:**
```bash
docker network create app-network
docker network create --driver bridge --subnet 192.168.0.0/24 backend-network
```

### Remove Network
```bash
docker network rm my-network

# Remove all unused networks
docker network prune
```

**Example:**
```bash
docker network rm app-network
docker network prune -f
```

### Connect Container to Network
```bash
# Connect container to network
docker network connect my-network my-container

# Disconnect container from network
docker network disconnect my-network my-container
```

**Example:**
```bash
docker network connect backend-network mysql-db
docker run -d --network backend-network --name api node-app
```

### Inspect Network
```bash
docker network inspect my-network
```

---

## Docker Volume Commands

### List Volumes
```bash
docker volume ls
```

**Example Output:**
```
DRIVER    VOLUME NAME
local     mysql-data
local     app-logs
```

### Create Volume
```bash
docker volume create my-volume

# Create with specific driver
docker volume create --driver local my-data
```

**Example:**
```bash
docker volume create postgres-data
docker volume create app-uploads
```

### Remove Volume
```bash
docker volume rm my-volume

# Remove all unused volumes
docker volume prune
```

**Example:**
```bash
docker volume rm old-data
docker volume prune -f
```

### Inspect Volume
```bash
docker volume inspect my-volume
```

### Use Volume with Container
```bash
# Mount named volume
docker run -d -v my-volume:/app/data nginx

# Mount bind mount
docker run -d -v /host/path:/container/path nginx

# Mount as read-only
docker run -d -v my-volume:/app/data:ro nginx
```

**Example:**
```bash
# PostgreSQL with persistent data
docker run -d \
  --name postgres \
  -v postgres-data:/var/lib/postgresql/data \
  -e POSTGRES_PASSWORD=secret \
  postgres:15

# Node.js app with bind mount
docker run -d \
  --name dev-app \
  -v $(pwd):/app \
  -p 3000:3000 \
  node:18
```

---

## Dockerfile

A Dockerfile is a text document that contains instructions for building a Docker image.

### Basic Dockerfile Structure

```dockerfile
# Base image
FROM node:18-alpine

# Set working directory
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install --production

# Copy application code
COPY . .

# Expose port
EXPOSE 3000

# Set environment variables
ENV NODE_ENV=production

# Define the command to run
CMD ["node", "server.js"]
```

### Dockerfile Instructions

#### FROM
Specifies the base image.
```dockerfile
FROM ubuntu:22.04
FROM node:18-alpine
FROM python:3.11-slim
```

#### WORKDIR
Sets the working directory for subsequent instructions.
```dockerfile
WORKDIR /app
WORKDIR /usr/src/app
```

#### COPY
Copies files from host to container.
```dockerfile
COPY package.json .
COPY . .
COPY src/ /app/src/
```

#### ADD
Similar to COPY but can also handle URLs and tar extraction.
```dockerfile
ADD https://example.com/file.tar.gz /app/
ADD archive.tar.gz /app/
```

#### RUN
Executes commands during image build.
```dockerfile
RUN apt-get update && apt-get install -y curl
RUN npm install
RUN pip install -r requirements.txt
```

#### CMD
Specifies the default command to run when container starts.
```dockerfile
CMD ["python", "app.py"]
CMD ["npm", "start"]
CMD ["nginx", "-g", "daemon off;"]
```

#### ENTRYPOINT
Configures container to run as an executable.
```dockerfile
ENTRYPOINT ["python"]
CMD ["app.py"]  # Can be overridden
```

#### EXPOSE
Documents which ports the container listens on.
```dockerfile
EXPOSE 80
EXPOSE 3000
EXPOSE 8080 8443
```

#### ENV
Sets environment variables.
```dockerfile
ENV NODE_ENV=production
ENV APP_PORT=3000
ENV DATABASE_URL=postgresql://localhost/mydb
```

#### ARG
Defines build-time variables.
```dockerfile
ARG VERSION=1.0
ARG BUILD_DATE
RUN echo "Building version $VERSION"
```

#### VOLUME
Creates a mount point for external volumes.
```dockerfile
VOLUME /data
VOLUME ["/var/log", "/var/db"]
```

#### USER
Sets the user for subsequent instructions.
```dockerfile
USER node
USER appuser:appgroup
```

#### LABEL
Adds metadata to the image.
```dockerfile
LABEL version="1.0"
LABEL description="My application"
LABEL maintainer="admin@example.com"
```

### Complete Dockerfile Examples

#### Node.js Application
```dockerfile
# Multi-stage build for Node.js
FROM node:18-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build

# Production stage
FROM node:18-alpine

WORKDIR /app

COPY --from=builder /app/dist ./dist
COPY --from=builder /app/node_modules ./node_modules
COPY package*.json ./

EXPOSE 3000

USER node

CMD ["node", "dist/server.js"]
```

#### Python Application
```dockerfile
FROM python:3.11-slim

WORKDIR /app

# Install system dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends gcc && \
    rm -rf /var/lib/apt/lists/*

# Copy requirements and install Python dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY . .

# Create non-root user
RUN useradd -m -u 1000 appuser && \
    chown -R appuser:appuser /app

USER appuser

EXPOSE 8000

CMD ["python", "app.py"]
```

#### Go Application
```dockerfile
# Build stage
FROM golang:1.21-alpine AS builder

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . .
RUN CGO_ENABLED=0 GOOS=linux go build -o main .

# Production stage
FROM alpine:latest

RUN apk --no-cache add ca-certificates

WORKDIR /root/

COPY --from=builder /app/main .

EXPOSE 8080

CMD ["./main"]
```

---

## Docker Compose

Docker Compose is a tool for defining and running multi-container Docker applications using a YAML file.

### Basic docker-compose.yml Structure

```yaml
version: '3.8'

services:
  web:
    image: nginx:latest
    ports:
      - "80:80"
    volumes:
      - ./html:/usr/share/nginx/html
    networks:
      - frontend

  app:
    build: ./app
    ports:
      - "3000:3000"
    environment:
      - NODE_ENV=production
      - DATABASE_URL=postgresql://db:5432/mydb
    depends_on:
      - db
    networks:
      - frontend
      - backend

  db:
    image: postgres:15
    environment:
      - POSTGRES_PASSWORD=secret
      - POSTGRES_DB=mydb
    volumes:
      - postgres-data:/var/lib/postgresql/data
    networks:
      - backend

networks:
  frontend:
  backend:

volumes:
  postgres-data:
```

### Docker Compose Commands

```bash
# Start services
docker-compose up

# Start in detached mode
docker-compose up -d

# Build and start
docker-compose up --build

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# View logs
docker-compose logs

# Follow logs
docker-compose logs -f

# Scale service
docker-compose up -d --scale app=3

# Execute command in service
docker-compose exec app bash

# List containers
docker-compose ps

# Restart services
docker-compose restart
```

### Complete Docker Compose Examples

#### Full-Stack Web Application
```yaml
version: '3.8'

services:
  # Frontend - React Application
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    ports:
      - "3000:3000"
    environment:
      - REACT_APP_API_URL=http://localhost:5000
    volumes:
      - ./frontend:/app
      - /app/node_modules
    networks:
      - app-network
    depends_on:
      - backend

  # Backend - Node.js API
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    ports:
      - "5000:5000"
    environment:
      - NODE_ENV=development
      - DATABASE_URL=postgresql://postgres:secret@db:5432/myapp
      - REDIS_URL=redis://redis:6379
    volumes:
      - ./backend:/app
      - /app/node_modules
    networks:
      - app-network
    depends_on:
      - db
      - redis
    restart: unless-stopped

  # Database - PostgreSQL
  db:
    image: postgres:15-alpine
    environment:
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=secret
      - POSTGRES_DB=myapp
    volumes:
      - postgres-data:/var/lib/postgresql/data
      - ./db/init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "5432:5432"
    networks:
      - app-network
    restart: unless-stopped

  # Cache - Redis
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    networks:
      - app-network
    restart: unless-stopped

  # Reverse Proxy - Nginx
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
      - ./nginx/ssl:/etc/nginx/ssl
    networks:
      - app-network
    depends_on:
      - frontend
      - backend
    restart: unless-stopped

networks:
  app-network:
    driver: bridge

volumes:
  postgres-data:
  redis-data:
```

#### Microservices Architecture
```yaml
version: '3.8'

services:
  # API Gateway
  gateway:
    build: ./gateway
    ports:
      - "8000:8000"
    environment:
      - SERVICE_USER=http://user-service:3001
      - SERVICE_ORDER=http://order-service:3002
      - SERVICE_PRODUCT=http://product-service:3003
    networks:
      - microservices
    depends_on:
      - user-service
      - order-service
      - product-service

  # User Service
  user-service:
    build: ./services/user
    environment:
      - DATABASE_URL=postgresql://postgres:secret@user-db:5432/users
      - REDIS_URL=redis://redis:6379
    networks:
      - microservices
    depends_on:
      - user-db
      - redis

  # Order Service
  order-service:
    build: ./services/order
    environment:
      - DATABASE_URL=postgresql://postgres:secret@order-db:5432/orders
      - KAFKA_BROKER=kafka:9092
    networks:
      - microservices
    depends_on:
      - order-db
      - kafka

  # Product Service
  product-service:
    build: ./services/product
    environment:
      - MONGODB_URL=mongodb://mongo:27017/products
    networks:
      - microservices
    depends_on:
      - mongo

  # Databases
  user-db:
    image: postgres:15-alpine
    environment:
      POSTGRES_PASSWORD: secret
      POSTGRES_DB: users
    volumes:
      - user-db-data:/var/lib/postgresql/data
    networks:
      - microservices

  order-db:
    image: postgres:15-alpine
    environment:
      POSTGRES_PASSWORD: secret
      POSTGRES_DB: orders
    volumes:
      - order-db-data:/var/lib/postgresql/data
    networks:
      - microservices

  mongo:
    image: mongo:6
    volumes:
      - mongo-data:/data/db
    networks:
      - microservices

  # Message Broker
  kafka:
    image: confluentinc/cp-kafka:latest
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
    networks:
      - microservices
    depends_on:
      - zookeeper

  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    networks:
      - microservices

  # Cache
  redis:
    image: redis:7-alpine
    networks:
      - microservices

networks:
  microservices:
    driver: bridge

volumes:
  user-db-data:
  order-db-data:
  mongo-data:
```

#### Development Environment with Monitoring
```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "3000:3000"
    environment:
      - DATABASE_URL=postgresql://postgres:secret@db:5432/app
    volumes:
      - .:/app
      - /app/node_modules
    networks:
      - monitoring
    labels:
      - "prometheus.scrape=true"
      - "prometheus.port=3000"

  db:
    image: postgres:15
    environment:
      POSTGRES_PASSWORD: secret
      POSTGRES_DB: app
    volumes:
      - db-data:/var/lib/postgresql/data
    networks:
      - monitoring

  # Monitoring
  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus-data:/prometheus
    networks:
      - monitoring
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3001:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - grafana-data:/var/lib/grafana
      - ./grafana/dashboards:/etc/grafana/provisioning/dashboards
    networks:
      - monitoring
    depends_on:
      - prometheus

  # Log Aggregation
  loki:
    image: grafana/loki:latest
    ports:
      - "3100:3100"
    volumes:
      - ./loki/loki-config.yml:/etc/loki/local-config.yaml
    networks:
      - monitoring

networks:
  monitoring:
    driver: bridge

volumes:
  db-data:
  prometheus-data:
  grafana-data:
```

### Advanced Docker Compose Features

#### Environment Variables
```yaml
services:
  app:
    image: myapp
    environment:
      - NODE_ENV=${NODE_ENV:-production}
      - API_KEY=${API_KEY}
    env_file:
      - .env
      - .env.production
```

#### Health Checks
```yaml
services:
  web:
    image: nginx
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
```

#### Resource Limits
```yaml
services:
  app:
    image: myapp
    deploy:
      resources:
        limits:
          cpus: '0.5'
          memory: 512M
        reservations:
          cpus: '0.25'
          memory: 256M
```

#### Profiles
```yaml
services:
  app:
    image: myapp
    profiles:
      - production

  debug:
    image: myapp-debug
    profiles:
      - debug

# Run with: docker-compose --profile production up
```

---

## Best Practices

### Dockerfile Best Practices
1. **Use specific base image versions** - Avoid `latest` tag
2. **Minimize layers** - Combine RUN commands
3. **Use .dockerignore** - Exclude unnecessary files
4. **Multi-stage builds** - Reduce final image size
5. **Run as non-root user** - Enhance security
6. **Order instructions efficiently** - Put changing content last

### Docker Compose Best Practices
1. **Use version control** - Track docker-compose.yml changes
2. **Environment-specific configs** - Use `.env` files
3. **Named volumes** - For persistent data
4. **Health checks** - Ensure service reliability
5. **Resource limits** - Prevent resource exhaustion
6. **Networks** - Isolate services appropriately

---

## Quick Reference

### Common Workflows

**Development:**
```bash
# Start development environment
docker-compose up -d

# View logs
docker-compose logs -f app

# Rebuild after code changes
docker-compose up -d --build

# Execute commands
docker-compose exec app npm test
```

**Production:**
```bash
# Build production images
docker build -t myapp:v1.0 .

# Run with production config
docker-compose -f docker-compose.prod.yml up -d

# Scale services
docker-compose up -d --scale api=3
```

**Cleanup:**
```bash
# Remove stopped containers
docker container prune -f

# Remove unused images
docker image prune -a -f

# Remove unused volumes
docker volume prune -f

# Remove everything
docker system prune -a --volumes -f
```

---

## Troubleshooting

### Common Issues

**Container won't start:**
```bash
docker logs container-name
docker inspect container-name
```

**Port already in use:**
```bash
# Find process using port
lsof -i :3000
# or
netstat -tulpn | grep 3000
```

**Volume permission issues:**
```bash
# Fix ownership
docker exec container-name chown -R user:group /path
```

**Network connectivity:**
```bash
# Inspect network
docker network inspect network-name

# Test connectivity
docker exec container1 ping container2
```

---

This guide covers the essential Docker commands and concepts. For more detailed information, visit the [official Docker documentation](https://docs.docker.com/).
