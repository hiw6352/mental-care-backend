// 차후 사용할 예정 샘플용으로 보관중
pipeline {
  agent any
  options {
    timestamps()
    disableConcurrentBuilds()
  }

  parameters {
    booleanParam(name: 'DOCKER_BUILD', defaultValue: true, description: 'Build Docker image')
    booleanParam(name: 'DOCKER_PUSH',  defaultValue: false, description: 'Push Docker image to registry')
  }

  environment {
    APP_NAME = 'mental-care-backend'
    // Jenkins 관리 > Global Tool 에 등록한 JDK 이름 (예: temurin-17)
    // 없다면 tools 블록 대신 container/agent에 jdk가 있어야 함.
  }

  tools {
    jdk 'temurin-17' // Jenkins에 미리 등록한 JDK 이름으로 수정
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
        sh 'git log -1 --oneline || true'
      }
    }

    stage('Prepare') {
      steps {
        sh 'chmod +x gradlew || true'
      }
    }

    stage('Build (Gradle)') {
      steps {
        sh './gradlew clean bootJar -x test'
      }
      post {
        success {
          archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
        }
      }
    }

    stage('Docker Build') {
      when { expression { return params.DOCKER_BUILD } }
      steps {
        script {
          def shortSha = sh(returnStdout: true, script: "git rev-parse --short HEAD").trim()
          env.IMAGE_TAG = "${APP_NAME}:${shortSha}"
        }
        sh 'docker build -t ${IMAGE_TAG} .'
      }
    }

    stage('Docker Push') {
      when { allOf { expression { return params.DOCKER_BUILD }; expression { return params.DOCKER_PUSH } } }
      steps {
        // 예시: 사설 레지스트리 사용 시 로그인 필요
        // withCredentials([usernamePassword(credentialsId: 'REGISTRY_CREDS', passwordVariable: 'REG_PW', usernameVariable: 'REG_USER')]) {
        //   sh 'echo $REG_PW | docker login -u $REG_USER --password-stdin registry.example.com'
        //   sh 'docker tag ${IMAGE_TAG} registry.example.com/${IMAGE_TAG}'
        //   sh 'docker push registry.example.com/${IMAGE_TAG}'
        // }
        echo 'Configure registry login & push here'
      }
    }
  }

  post {
    always { echo "Build finished: ${currentBuild.currentResult}" }
  }
}
