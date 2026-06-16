pipeline {
    agent any

    environment {
        // మీ Docker Hub username మార్చుకోండి
        DOCKER_IMAGE = "saikumarbathala/springboot-demo"
        // Jenkins లో save చేసిన credentials ID లు
        DOCKER_CREDS  = "dockerhub-credentials"
        EC2_SSH_CREDS = "ec2-ssh-key"
        // మీ EC2 public IP మార్చుకోండి
        EC2_HOST      = "15.206.124.135"
    }

    stages {

        // ────────────────────────────────────────
        // Stage 1: Git నుండి code తెచ్చుకో
        // ────────────────────────────────────────
        stage('Checkout') {
            steps {
                echo "📥 Code checkout చేస్తున్నాం..."
                checkout scm
            }
        }

        // ────────────────────────────────────────
        // Stage 2: Maven తో Build చేయి
        // ────────────────────────────────────────
        stage('Build') {
            steps {
                echo "🔨 Maven build చేస్తున్నాం..."
                sh 'mvn clean package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        // ────────────────────────────────────────
        // Stage 3: Unit Tests run చేయి
        // ────────────────────────────────────────
        stage('Test') {
            steps {
                echo "🧪 Tests run చేస్తున్నాం..."
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
                failure {
                    echo "❌ Tests fail అయ్యాయి! Deploy ఆగిపోయింది."
                }
            }
        }

        // ────────────────────────────────────────
        // Stage 4: Docker Image Build & Push
        // ────────────────────────────────────────
        stage('Docker Build & Push') {
            steps {
                echo "🐳 Docker image build చేస్తున్నాం..."
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKER_CREDS) {
                        def img = docker.build("${DOCKER_IMAGE}:${BUILD_NUMBER}")
                        img.push()
                        img.push('latest')
                        echo "✅ Image push అయింది: ${DOCKER_IMAGE}:${BUILD_NUMBER}"
                    }
                }
            }
        }

        // ────────────────────────────────────────
        // Stage 5: EC2 కి Deploy చేయి
        // ────────────────────────────────────────
        stage('Deploy to EC2') {
            steps {
                echo "🚀 EC2 కి deploy చేస్తున్నాం..."
                sshagent([EC2_SSH_CREDS]) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${EC2_HOST} '
                            # Docker install అయుందా check చేయి
                            docker --version

                            # Old container stop చేయి (ఉంటే)
                            docker stop springboot-demo || true
                            docker rm   springboot-demo || true

                            # Latest image pull చేయి
                            docker pull ${DOCKER_IMAGE}:${BUILD_NUMBER}

                            # New container start చేయి
                            docker run -d \\
                                --name springboot-demo \\
                                --restart always \\
                                -p 80:8080 \\
                                ${DOCKER_IMAGE}:${BUILD_NUMBER}

                            echo "Container started!"
                        '
                    """
                }
            }
        }

        // ────────────────────────────────────────
        // Stage 6: Health Check
        // ────────────────────────────────────────
        stage('Health Check') {
            steps {
                echo "🏥 Application live అయిందా check చేస్తున్నాం..."
                sh """
                    sleep 15
                    curl -f http://YOUR_EC2_IP/health || exit 1
                    echo "✅ Application live గా ఉంది!"
                """
            }
        }
    }

    // ────────────────────────────────────────
    // Post Build Notifications
    // ────────────────────────────────────────
    post {
        success {
            echo """
            ✅ PIPELINE SUCCESS!
            Build   : #${BUILD_NUMBER}
            Image   : ${DOCKER_IMAGE}:${BUILD_NUMBER}
            App URL : http://YOUR_EC2_IP
            """
        }
        failure {
            echo "❌ PIPELINE FAILED! Build #${BUILD_NUMBER} — logs చూడండి."
        }
        always {
            cleanWs()
        }
    }
}
