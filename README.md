# Microservice-E-Learning

This project demonstrates a secure, cloud-native microservice-based E-Learning platform using Spring Boot, MongoDB Atlas, and AWS EKS. It follows DevOps and DevSecOps best practices with automated CI/CD pipelines, service discovery (Eureka), and API Gateway routing. This repository contains multiple microservices, including user enrolment, and supporting infrastructure automation using Terraform and Helm.

---

## 🌐 Architecture Overview

- **Infrastructure**: AWS EKS (Elastic Kubernetes Service)
- **Microservices**: Spring Boot (Java 21), MongoDB Atlas
- **Service Discovery**: Eureka Server
- **Routing**: Spring Cloud API Gateway
- **CI/CD**: GitHub Actions + Docker Hub
- **Deployment**: Helm charts and Terraform (Infrastructure as Code)
- **Security**: IAM, Snyk vulnerability scans

---

## 🚀 Deployment Instructions (From Scratch)

### ⚙️ Prerequisites

Ensure the following tools are installed:

- [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html)
- [Terraform](https://developer.hashicorp.com/terraform/downloads)
- [kubectl](https://kubernetes.io/docs/tasks/tools/)
- [Helm](https://helm.sh/docs/intro/install/)
- Docker (for local builds)
- AWS IAM user credentials configured via `aws configure`

---

### 📁 Folder Structure
microservices-e-learning/ ├── terraform-eks/ # Terraform config for EKS cluster ├── micro-services/ # All Spring Boot microservices ├── helm/ # Helm charts for each service └── .github/workflows/ci.yml # GitHub Actions pipeline

---

### 🧱 Step 1: Provision AWS EKS Cluster

bash
cd terraform-eks
terraform init
terraform plan
terraform apply

This creates:

EKS cluster
Node group (t3.micro)
IAM roles and security groups

---

### 🧱 Step 2: Connect kubectl to the Cluster

aws eks --region ap-south-1 update-kubeconfig --name free-tier-cluster

---

### 🧱 Step 3:  Deploy Services Using Helm

cd helm
helm install discovery-server ./discovery-server
helm install api-gateway ./api-gateway
helm install user-enrolment ./user-enrolment-service

Verify all pods and services:
kubectl get pods
kubectl get svc

---

Access Services

Once the EXTERNAL-IP is available:
Eureka Dashboard: http://<discovery-server-external-ip>:8761
API Gateway: http://<api-gateway-external-ip>:8080
