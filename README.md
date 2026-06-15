# Spring Boot Demo — Jenkins CI/CD on EC2

## Project Structure
```
springboot-demo/
├── src/
│   ├── main/
│   │   ├── java/com/demo/app/
│   │   │   ├── Application.java       ← Main class
│   │   │   └── HomeController.java    ← Web page controller
│   │   └── resources/
│   │       ├── templates/index.html   ← HTML page
│   │       └── application.properties
│   └── test/
│       └── java/com/demo/app/
│           └── HomeControllerTest.java ← Unit tests
├── Dockerfile
├── Jenkinsfile                         ← CI/CD pipeline
└── pom.xml
```

## Step 1 — GitHub కి Push చేయి

```bash
cd springboot-demo
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR_USERNAME/springboot-demo.git
git push -u origin main
```

## Step 2 — EC2 Setup

EC2 instance లో (Ubuntu/Amazon Linux) run చేయి:

```bash
# Docker install
sudo apt-get update
sudo apt-get install -y docker.io
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ubuntu   # ubuntu user కి docker permission
```

Security Group లో Port 80 open చేయి (Inbound rule).

## Step 3 — Jenkins Setup

Jenkins లో ఈ plugins install చేయి:
- Git plugin
- Maven Integration
- Docker Pipeline
- SSH Agent

### Credentials add చేయి (Manage Jenkins → Credentials):

| ID | Type | Details |
|----|------|---------|
| `dockerhub-credentials` | Username/Password | Docker Hub login |
| `ec2-ssh-key` | SSH Username with private key | EC2 .pem file |

## Step 4 — Jenkins Pipeline Create చేయి

1. Jenkins → New Item → Pipeline
2. Pipeline Definition: "Pipeline script from SCM"
3. SCM: Git → మీ GitHub repo URL
4. Script Path: `Jenkinsfile`
5. Save → Build Now

## Step 5 — Jenkinsfile లో మార్చవలసినవి

```
DOCKER_IMAGE = "yourdockerhubuser/springboot-demo"   ← మీ Docker Hub username
EC2_HOST     = "ec2-user@YOUR_EC2_IP"                ← మీ EC2 IP
```

Health Check stage లో కూడా:
```
curl -f http://YOUR_EC2_IP/health
```

## Access చేయడం

Deploy అయిన తర్వాత browser లో:
```
http://YOUR_EC2_IP
```
